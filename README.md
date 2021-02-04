# Exercicio 1

### executando a aplicação
-- compilar e gerar o executavel da aplicação
./gradlew build

-- executando a app dentro da VM
java -jar build/libs/sample-app-0.0.1-SNAPSHOT.jar

acesse http://172.0.2.32:30001/hello

### criando a imagem e executando a aplicação com docker 

-- opções para o build
docker build --help

-- gera uma imagem, tagueia como user/sample-app
docker build --build-arg JAR_FILE=build/libs/\*.jar -t user/sample-app:1 .

-- executa a aplicação
docker run user/sample-app:1

-- executa a aplicação em segundo plano
docker run -d user/sample-app:1

docker ps

Por que não funciona http://172.0.2.32:30001/hello ?

-- executa a aplicação expondo a porta definida em EXPOSE do Buildfile

docker run -P -d user/sample-app:1

docker ps

acesse http://172.0.2.32:{PORT}/hello

-- passando parametros para a app

docker run -P -d -e HELLO_MESSAGE=ola user/sample-app:1

-- reexecutando um container

docker stop $(docker ps -aq)
docker ps -a
docker start container_id

-- dando nome ao seu container

docker run -P -d -e HELLO_MESSAGE=ola --name sample-app user/sample-app:1

docker ps

docker stop sample-app

docker start sample-app

docker inspect sample-app

-- logs da aplicação

docker logs -f sample-app

-- executando um teste de carga com apache bench

docker inspect sample-app

anote o ip da instancia

curl http://{instance_ip}/hello

-- entrando dentro do container que já esta executando

docker exec -it sample-app /bin/bash



