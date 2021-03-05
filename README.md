# Exercício 9 - orquestrando containers


Iniciando o minikube:

```console
minikube start
```

Habilitamos metrics-server

```console
minikube addons enable metrics-server
```

Iniciando k8s dashboard

```console
minikube dashboard
```

Proxy para acesso ao dashboard:

```console
kubectl proxy --address 0.0.0.0 --accept-hosts '.*'
```

Acesse no browser: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/

### Subindo uma imagem simples

Vamos subir um servidor apache httpd:

```console
kubectl create deployment apache-httpd --image=httpd

kubectl expose deployment apache-httpd --type=LoadBalancer --port=80
```

Acesse: http://172.0.2.32:8001/api/v1/namespaces/default/services/apache-httpd/proxy/

```console
kubectl get services

minikube tunnel --cleanup

kubectl get services

curl <external_ip>
```

### Subindo serviço de postal code

##### Criando a imagem

Vamos simular o serviço usando mockserver, mas como não podemos depender de subir o serviço e configurar o mock para o endpoint, iremos gerar uma imagem para nosso serviço, extendendo mockserver, já configurando o endpoint mockado:

```console
cd postalcode-app/image/

docker build -t postalcode-app:1 .
```

Geramos a imagem, porém ela não está em um registry que o minikube usa, então precisamos fazer push da imagem para ele poder usar. Existem várias [formas](https://minikube.sigs.k8s.io/docs/handbook/pushing/) de fazer isso, vamos usar a seguinte:


```console
minikube cache add postalcode-app:1
```

Veja as imagens já disponíveis:

```console
minikube cache list
```

##### deploy da aplicação

Vamos criar uma [namespace](https://kubernetes.io/docs/concepts/overview/working-with-objects/namespaces/) para nossos serviços:

```console
kubectl create namespace sample-ns
```

Veja que a namespace já aparece no dashboard: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/?namespace=sample-ns#/overview?namespace=sample-ns

A forma mais comum para se criar recursos no k8s é usar arquivos que descrevem os recursos que queremos criar. Visualize o arquivo [postalcode-app-pod.yaml](postalcode-svc/deploy/postalcode-app-pod.yaml), ele cria um [pod](https://kubernetes.io/docs/concepts/workloads/pods/) chamado **postalcode-app-pod**, usando a imagem **postalcode-app:1**.

Aplique a configuração do arquivo, usando a namespace criada anteriormente:

```console
kubectl apply -n sample-ns -f postalcode-app-pod.yaml
```

Verifique se o pod foi criado: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/?namespace=sample-ns#/pod?namespace=sample-ns

Agora, como acessar esse serviço? A API do k8s nos da acesso e podemos usar o mesmo proxy que usamos para acessar o dashboard para acessar um endpoint do nosso pod: http://172.0.2.32:8001/api/v1/namespaces/sample-ns/pods/postalcode-app-pod:1080/proxy/postalcodes

Podemos acompanhar o log do nosso pod via linha de comando:

```console
kubectl logs -f -n sample-ns postalcode-app-pod
```

Ou dashboard: http://172.0.2.32:8001/api/v1/namespaces/kubernetes-dashboard/services/kubernetes-dashboard/proxy/?namespace=sample-ns#/log/sample-ns/postalcode-app-pod/pod?namespace=sample-ns&container=postalcode-app

Mas o que ocorre se nosso serviço parar? Na aba de pods do dashboard, excluia o pod, ou se preferir via linha de comando:

```console
kubectl delete -n sample-ns pod postalcode-app-pod
```

Tente acessar novamente o endpoint do serviço. 

Como fazer para que o k8s mantenha os serviços rodando? 

Ao invés de criar um pod, vamos criar um [deployment](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/) aplicando as configurações do arquivo [postalcode-app-deployment.yaml](postalcode-app/deploy/postalcode-app-deployment.yaml).

Nesse arquivo, nós descrevemos o deploy, nesse caso, criamos o deployment de nome **postalcode-app-deployment** indicando que devem subir 3 pods.

```console
kubectl apply -n sample-ns -f postalcode-app-deployment.yaml
```

Verifique no dashboard os 3 pods executando. Agora, se você tentar excluir um pod, outro ira subir, tente fazer isso

Mas agora temos 3 pods rodando, criados com nomes aleatórios, como fazemos para acessar? 

Vamos criar um [service](https://kubernetes.io/docs/concepts/services-networking/service/) que vai permitir acessar os pods do nosso deployment. Verifique o arquivo [postalcode-app-service.yaml](postalcode-app/deploy/postalcode-app-service.yaml) e aplique as configurações:

```console
kubectl apply -n sample-ns -f postalcode-app-service.yaml
```

Verifique no dashboard que o service foi criado e acesse no seu browser: http://172.0.2.32:8001/api/v1/namespaces/sample-ns/services/postalcode-app-service:1099/proxy/postalcodes

Dentro da VM:

```console
kubectl get -n sample-ns service
minikube tunnel
curl http://CLUSTER-IP:1099/postalcodes
```

Ou, execute:

```console
kubectl port-forward -n sample-ns service/postalcode-app-service 40123:1099 --address 0.0.0.0
```

E acesse no browswer: http://172.0.2.32:40123/postalcodes

E como outros pods acessam o serviço? O k8s tem um serviço [DNS](https://kubernetes.io/docs/concepts/services-networking/dns-pod-service/), é por ele que os pods vão se comunicar. Então vamos executar um novo pod e testar o acesso ao nosso serviço a partir dele: 

```console
kubectl run -it --tty pingtest --rm --image=busybox --restart=Never -- /bin/sh
wget -qO- http://postalcode-app-service.sample-ns.svc.cluster.local:1099/postalcodes
```

##### escalando a aplicação

Em algumas momentos, podemos querer ter mais ou menos pods respondendo pelo nosso serviço, para isso utilize: 

```console
kubectl scale -n sample-ns --replicas=1 deployment/postalcode-app-deployment
```

Podemos também criar regras de [autoscaling](https://kubernetes.io/docs/tasks/run-application/horizontal-pod-autoscale-walkthrough/):

```console
kubectl autoscale -n sample-ns deployment/postalcode-app-deployment --min=1 --max=5 --cpu-percent=5
```

E assim como os demais recursos, podemos criar regras para autoscaling definidas em arquivos, verifique [postalcode-app-hpa.yaml](postalcode-app/deploy/postalcode-app-hpa.yaml) e aplique as configurações:

```console
kubectl apply -n sample-ns -f postalcode-app-hpa.yaml
```

Veja detalhes do auto scaling:

```console
kubectl describe -n sample-ns hpa postalcode-app-hpa
```

Ou nos detalhes do deployment no dashboard.

Agora vamos forçar um autoscaling disparando um teste de carga contra a aplicação:

```console
kubectl port-forward -n sample-ns service/postalcode-app-service 40123:1099 --address 0.0.0.0
ab -n 10000 -c 200 http://localhost:40123/postalcodes
```

### Subindo sample app

O minikube roda num docker na network minikube. Suba a aplicação sample-app no kubernetes, na namespace **sample-ns**, acessando o banco de dados MySQL rodando fora do k8s e o postal code service rodando no k8s. Exponha o aplicação em um serviço **sample-app-service** na porta **25123**.

### Expondo a aplicação para fora

A forma como damos acesso ao nossos pods é via services, que podem ser desses tipos: ClusterIP (default), NodePort, LoadBalancer, ExternalName.

Mas, ao invés de expor para fora todos seus services, podemos usar o conceito de [ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/):

```console
minikube addons enable ingress
```

Ingress permite que criamos regras de roteamento para serviços num cluster.

```console
kubectl apply -f sample-ingress.yaml
```

Veja o IP Address do ingress:

```console
kubectl get -n sample-ns ingress
```

Tente executar:

```console
curl -i http://{ingress-ip}/postalcode-app/postalcodes
```

O que apareceu? Veja que na nossa regra de ingress define um host, então:

```console
sudo vim /etc/hosts
```

Edite incluindo o IP do ingress para o host **sample.info** e tente novamente:

```console
curl -i http://sample.info/postalcode-app/postalcodes
curl -i http://sample.info/sample-app/swagger-ui.html
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Para discutir, dado que ingress é por namespace, qual seria a estratégia para ter um IP para aplicações de vários namespaces?
