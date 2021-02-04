# Exercicio 2

## adaptações para rodar no wildfly

Aplicação: WebApplication.java

E nas dependencias: build.gradle

## gerando o war

./gradlew bootWar

ls build/libs/

## criando a imagem e executando a aplicação com docker 

Veja as alterações em Dockerfile

-- gera uma imagem

docker build --build-arg WAR_FILE=build/libs/\*.war -t user/sample-app:2 .

-- executa a aplicação

docker run user/sample-app:2

-- executa a aplicação em segundo plano

docker run -d -p 8080:8080 --name sample-app user/sample-app:2

docker ps

Acesse http://172.0.2.32:8080/sample-app-0.0.2-SNAPSHOT/hello