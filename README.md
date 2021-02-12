# Exercício 7 - impacto das dependências externas

Vamos subir um [mockserver](https://www.mock-server.com/where/docker.html) para simular uma dependência com outro sistema

```
docker run -d --rm -p 1080:1080 --net=my-net --name mockserver mockserver/mockserver
```

No mockserver, vamos configurar um endpoint **/postalcodes**, para responder um endereço mockado e demorar 200 ms para dar essa resposra:

```
curl -v -X PUT "http://localhost:1080/expectation" -d '{
  "httpRequest" : {
    "method" : "GET",
    "path" : "/postalcodes"
  },
  "httpResponse" : {
    "body" : "{\"address\": \"rua mockada\", \"city\": \"Sao Paulo\", \"uf\": \"SP\"}",
    "statusCode": 200,
    "headers": [ { "name": "Content-Type", "values": ["application/json; charset=utf-8"] } ],
    "delay": { "timeUnit": "MILLISECONDS", "value": 200 }
  }}'
```

Veja mais opções: https://5-1.mock-server.com/mock_server/creating_expectations.html

Teste a resposta do mockserver (subimos o mock server fazendo bind na porta 1080, então pode acessar tanto na vm com localhost quando no seu browser via 172.0.2.32):

```
curl http://localhost:1080/postalcodes
```

Veja agora as alterações em [UserCreateService](src/main/java/web/core/user/UserCreateService.java), incluimos uma regra de negócio que valida se o endereço informado do estado de São Paulo, para isso usamos uma chamada a um serviço de postalcode.

```
docker network create my-net

docker run --rm -p 3306:3306 --name mysql --net=my-net -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51

./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*.jar -t sample-app:7 .

docker run --rm -p 8080:30001 -e MYSQL_HOST=mysql -e POSTALCODE_HOST=mockserver:1080 --name sample-app --net=my-net sample-app:7
```

Execute novamente o teste de carga com os parâmetros encontrados do exerício 6.

O que aconteceu? Por que a aplicação perdeu tanta escalabilidade?
