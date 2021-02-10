# Exercicio 8 - orquestrando containers


Iniciando o minikube:

```
minikube start
```

Habilitamos metrics-server

```
minikube addons enable metrics-server
```

Iniciando k8s dashboard

```
minikube dashboard
```

Proxy para acesso ao dashboard:

```
kubectl proxy --address 0.0.0.0 --accept-hosts '.*'
```

Acesse no browser: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/

### Subindo uma imagem simples

Vamos subir um servidor apache httpd:

```
kubectl create deployment apache-httpd --image=httpd
kubectl expose deployment apache-httpd --type=LoadBalancer --port=80
```

Acesse: http://172.0.2.32:8001/api/v1/namespaces/default/services/apache-httpd/proxy/

```
kubectl get services
minikube tunnel --cleanup
kubectl get services
curl <external_ip>
```

### Subindo serviço de postal code

##### Criando a imagem

Vamos simular o serviço usando mockserver, mas como não podemos depender de subir o serviço e configurar o mock para o endpoint, iremos gerar uma imagem para nosso serviço, extendendo mockserver, já configurando o endpoint mockado:

```
cd postalcode-srv/image/

docker build -t postalcode-srv:1 .
```

Geramos a imagem, porém ela não está em um registry que o minikube usa, então precisamos fazer push da imagem para ele poder usar. Existem várias [formas](https://minikube.sigs.k8s.io/docs/handbook/pushing/) de fazer isso, vamos usar a seguinte:


```
minikube cache add postalcode-srv:1
```

Veja as imagens já disponíveis:

```
minikube cache list
```

##### deploy da aplicação

Vamos criar uma [namespace](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/) para nosso serviço:

```
kubectl create namespace postalcode-srv-ns
```

Veja que a namespace já aparece no dashboard: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/?namespace=postalcode-srv-ns#/overview?namespace=postalcode-srv-ns

A forma mais comum para se criar recursos no k8s é usar arquivos que descrevem os recursos que queremos criar. Visualize o arquivo [postalcode-srv-pod.yaml](postalcode-svc/deploy/postalcode-srv-pod.yaml), ele cria um [pod](https://kubernetes.io/docs/concepts/workloads/pods/) chamado **postalcode-srv-pod**, usando a imagem **postalcode-srv:1**.

Aplique a configuração do arquivo, usando a namespace criada anteriormente:

```
kubectl apply -n postalcode-srv-ns -f postalcode-srv-pod.yaml
```

Verifique se o pod foi criado: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/?namespace=postalcode-srv-ns#/pod?namespace=postalcode-srv-ns

Agora, como acessar esse serviço? A API do k8s nos da acesso e podemos usar o mesmo proxy que usamos para acessar o dashboard para acessar um endpoint do nosso pod: http://172.0.2.32:8001/api/v1/namespaces/postalcode-srv-ns/pods/postalcode-srv-pod:1080/proxy/postalcodes

Podemos acompanhar o log do nosso pod via linha de comando:

```
kubectl logs -f -n postalcode-srv-ns postalcode-srv-pod
```

Ou dashboard: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/?namespace=postalcode-srv-ns#/log/postalcode-srv-ns/postalcode-srv-pod/pod?namespace=postalcode-srv-ns&container=postalcode-srv

Mas o que ocorre se nosso serviço parar? Na aba de pods do dashboard, excluia o pod, ou se preferir via linha de comando:

```
kubectl delete -n postalcode-srv-ns pod postalcode-srv-pod
```

Tente acessar novamente o endpoint do serviço. 

Como fazer para que o k8s mantenha os serviços rodando? 

Ao invés de criar um pod, vamos criar um [deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) aplicando as configurações do arquivo [postalcode-srv-deployment.yaml](postalcode-srv/deploy/postalcode-srv-deployment.yaml).

Nesse arquivo, nós descrevemos o deploy, nesse caso, criamos o deployment de nome **postalcode-srv-deployment** indicando que devem subir 3 pods.

```
kubectl apply -n postalcode-srv-ns -f postalcode-srv-deployment.yaml
```

Verifique no dashboard os 3 pods executando. Agora, se você tentar excluir um pod, outro ira subir, tente fazer isso

Mas agora temos 3 pods rodando, criados com nomes aleatórios, como fazemos para acessar? 

Vamos criar um [service](https://kubernetes.io/docs/concepts/services-networking/service/) que vai permitir acessar os pods do nosso deployment. Verifique o arquivo [postalcode-srv-service.yaml](postalcode-srv/deploy/postalcode-srv-service.yaml) e aplique as configurações:

```
kubectl apply -n postalcode-srv-ns -f postalcode-srv-service.yaml
```

Verifique no dashboard que o service foi criado e acesse no seu browser: http://172.0.2.32:8001/api/v1/namespaces/postalcode-srv-ns/services/postalcode-srv-service:1099/proxy/postalcodes

Dentro da VM:

```
kubectl get -n postalcode-srv-ns service
minikube tunnel
curl http://CLUSTER-IP:1099/postalcodes
```

Ou, execute:

```
kubectl port-forward -n postalcode-srv-ns service/postalcode-srv-service 40123:1099 --address 0.0.0.0
```

E acesse no browswer: http://172.0.2.32:40123/postalcodes

E como outros pods acessam o serviço? O k8s tem um serviço DNS interno, é por ele que os pods vão se comunicar. Então vamos executar um novo pod e testar o acesso ao nosso serviço a partir dele: 

```
kubectl run -it --tty pingtest --rm --image=busybox --restart=Never -- /bin/sh
wget -qO- http://postalcode-srv-service.postalcode-srv-ns.svc.cluster.local:1099/postalcodes
```

##### escalando a aplicação

Em algumas momentos, podemos querer ter mais ou menos pods respondendo pelo nosso serviço, para isso utilize: 

```
kubectl scale -n postalcode-srv-ns --replicas=1 deployment/postalcode-srv-deployment
```

Podemos também criar regras de [autoscaling](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale-walkthrough/):

```
kubectl autoscale -n postalcode-srv-ns deployment/postalcode-srv-deployment --min=1 --max=5 --cpu-percent=5
```

E assim como os demais recursos, podemos criar regras para autoscaling definidas em arquivos, verifique [postalcode-srv-hpa.yaml](postalcode-srv/deploy/postalcode-srv-hpa.yaml) e aplique as configurações:

```
kubectl apply -n postalcode-srv-ns -f postalcode-srv-hpa.yaml
```

Veja detalhes do auto scaling:

```
kubectl describe -n postalcode-srv-ns hpa postalcode-srv-hpa
```

Ou nos detalhes do deployment no dashboard.

Agora vamos forçar um autoscaling disparando um teste de carga contra a aplicação:

```
kubectl port-forward -n postalcode-srv-ns service/postalcode-srv-service 40123:1099 --address 0.0.0.0
ab -n 10000 -c 200 http://localhost:40123/postalcodes
```
