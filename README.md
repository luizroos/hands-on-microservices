# Exercício 2 - explorando outras possibilidades
[Setup do ambiente](https://github.com/luizroos/hands-on-microservices)

---

Você não precisa executar apenas aplicações standalone. As vezes você já tem uma aplicação legada, rodando dentro de algum servidor de aplicação, no caso do java, servidores como jboss, websphere, weblogic, etc.

É possível gerar imagens e criar containers de aplicações que não são rodam standalone. Vamos ver como podemos rodar a aplicação do execício 1 dentro de um [Wildfly](https://www.wildfly.org/).

### Adaptações na aplicação

Fizemos algumas alterações na aplicação, por exemplo dentro do [WebApplication.java](sample-app/src/main/java/web/WebApplication.java). No java, podemos gerar arquivos [war](https://en.wikipedia.org/wiki/WAR_(file_format)), esse formato segue um padrão que é interpretado por servidores de aplicações como wildfly. 

Entre no diretório do repositório e faça checkout desse branch:

```console
cd ~/hands-on-microservices

git checkout e2
```

Agora compile novamente a aplicação, dessa vez gerando o arquivo war ao invés de jar (o arquivo war pode ser lido por servidores web java, como Wildfly).

```console
cd ~/hands-on-microservices/sample-app

./gradlew clean bootWar

ls build/libs/
```

Veja que agora tempos um arquivo **war**, esse formato é reconhecido por servidores web java (como tomcat).

#### Nova imagem

Alteramos nossa imagem, agora ela não herda apenas de uma imagem com java mas sim de uma imagem criada com [Wildfly instalado](https://hub.docker.com/r/jboss/wildfly). Veja as alterações em [Dockerfile](sample-app/Dockerfile) e vamos gerar a nova imagem e rodar um container dela:

```console
docker build --build-arg WAR_FILE=build/libs/\*.war -t sample-app:2 .

docker run --rm sample-app:2
```

Repare que o log já mudou completamente se comparado ao exercício 1, pois agora a aplicação está sendo executada de dentro do wildfly. A opção **--rm** indica que o container deve ser removido depois que ele parar de executar, isso pode ser útil para organização.

Pare a aplicação e vamos executar em segundo plano, mas dessa vez, ao invés de expor uma porta aleatória para o host, vamos mapear uma porta específica usando a opção -p :

```console
docker run --rm -d -p 8001:8080 --name sample-app sample-app:2
```

Caso tenha conflito no nome com outros containers previamente iniciados, você pode remove-los usando:

```console
docker rm sample-app
```

Ou então simplesmente mude o nome do container que está rodando.

Agora a porta exposta pelo container é a porta que você informou com paramêtro 'p' (o windfly esta subindo na porta 8080):

```console
docker ps 
```

Porém, quando criamos o container, mapeamos para a porta 8001 da máquina virtual para encaminhar requisições para dentro da porta 8080 do container, então se acessarmos http://localhost:8001 de dentro da máquina virtual, a requisição será encaminhada para a nossa aplicação.

Quando subimos nossa máquina virtual, fizemos também um mapeamento de portas entre nosso computador e a máquina virtual. Uma das portas mapeadas foi justamente a porta 8001, então se acessarmos http://localhost:8001 no nosso computador, a requisição será encaminhada para dentro da máquina virtual na porta 8001, que por sua vez encaminhara para o container. Por isso tente acessar a partir de um browser: http://localhost:8001/sample-app/hello