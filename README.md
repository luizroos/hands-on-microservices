# Exercício 2 - explorando outras possibilidades

Você não precisa só executar aplicações standalone. As vezes você já tem uma aplicação legada, rodando dentro de algum servidor de aplicação, no caso do java, servidores como jboss, websphere, weblogic, etc.

É possível gerar imagens e criar containers de aplicações que não são standalone. Vamos ver como podemos rodar a aplicação do execício 1 dentro de um [Wildfly](https://www.wildfly.org/).

### Adaptações na aplicação

Fizemos algumas alterações na aplicação, removemos sua dependência com [tomcat](sample-app/build.gradle) e fizemos alterações dentro do [WebApplication.java](sample-app/src/main/java/web/WebApplication.java). No java, podemos gerar arquivos [war](https://en.wikipedia.org/wiki/WAR_(file_format)), esse formato segue um padrão que é interpretado por servidores de aplicações como wildfly. Então, ao invés de gerar um jar, vamos compilar a aplicação e gerar agora um arquivo war:

```
./gradlew clean bootWar

ls build/libs/
```

#### Nova imagem

Alteramos nossa imagem, agora ela não herda apenas de uma imagem com java mas sim de uma imagem criada com [Wildfly instalado](https://hub.docker.com/r/jboss/wildfly). Veja as alterações em [Dockerfile](sample-app/Dockerfile) e vamos gerar a nova imagem:

```
docker build --build-arg WAR_FILE=build/libs/\*.war -t sample-app:2 .
```

Vamos executar o container:

```
docker run --rm sample-app:2
```

Veja que o log já mudou completamente se comparado ao exercício 1, pois agora a aplicação está sendo executada de dentro do wildfly. A opção **--rm** indica que o container deve ser removido depois que ele parar de executar, isso pode ser útil para organização.

Pare a aplicação e vamos executar em segundo plano, mas dessa vez, ao invés de expor uma porta aleatória para o host, vamos mapear uma porta específica usando a opção -p :

```
docker run --rm -d -p 8080:8080 --name sample-app sample-app:2
```

Caso tenha conflito no nome com outros containers previamente iniciado, você pode remove-los usando:

```
docker rm sample-app
```

Ou então simplesmente mude o nome do container que está rodando.

Veja que agora a porta que você informou, está mapeada para a porta exposta do container (o windfly esta subindo na porta 8080):

```
docker ps 
```

Como já mapeamos uma porta para o host, podemos acessar através do ip da nossa vm http://172.0.2.32:8080/sample-app/hello
