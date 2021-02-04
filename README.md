# Exercicio 1

### executando a aplicação

Compilar e gerar o executavel da aplicação

```
./gradlew build
```

Executar a applicação dentro da VM

```
java -jar build/libs/sample-app-0.0.1-SNAPSHOT.jar
```

Acesse http://172.0.2.32:30001/hello no seu browser

### criando a imagem e executando a aplicação com docker 

Veja opções para o build

```
docker build --help
```

Gera uma imagem, tagueando como user/sample-app

```
docker build --build-arg JAR_FILE=build/libs/\*.jar -t user/sample-app:1 .
```

Executando a aplicação

```
docker run user/sample-app:1
```

Executando a aplicação em segundo plano

```
docker run -d user/sample-app:1

docker ps
```

Por que não funciona http://172.0.2.32:30001/hello ?

Executando a aplicação expondo a porta definida em EXPOSE do Buildfile

```
docker run -P -d user/sample-app:1

docker ps
```

Veja a porta que foi exposta e acesse http://172.0.2.32:{PORT}/hello

Passando parametros para a app

```
docker run -P -d -e HELLO_MESSAGE=ola user/sample-app:1
```

Reexecutando um container

```
docker stop $(docker ps -aq)
docker ps -a
docker start container_id
```

Dando nome ao seu container

```
docker run -P -d -e HELLO_MESSAGE=ola --name sample-app user/sample-app:1

docker ps

docker stop sample-app

docker start sample-app

docker inspect sample-app
```

Acessando os logs da aplicação

```
docker logs -f sample-app
```

Acessando a aplicação direto

```
docker inspect sample-app

anote o ip da instancia
```

curl http://{instance_ip}/hello

Entrando dentro do container que já esta executando

```
docker exec -it sample-app /bin/bash
```

