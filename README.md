# Exercicio 7 - impacto das dependências externas

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

Teste nossa resposta mockada

```
curl http://localhost:1080/postalcodes
```

Veja agora as alterações em UserCreateService

```
docker network create my-net

docker run -p 3306:3306 --name mysql --net=my-net -v ~/temp/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51

./gradlew build

docker build --build-arg JAR_FILE=build/libs/*.jar -t user/sample-app:7 .

docker run --rm -p 8080:30001 -e MYSQL_HOST=mysql --name sample-app --net=my-net user/sample-app:7
```

Voltamos o ID para UUID (caso tenha mantido a base do exercicio 6, execute um drop table na tabela do seu banco para a aplicação gerar a nova estrutura da tabela). Execute novamente o teste de carga com os parâmetros do exercicio 6.

O que aconteceu? Reveja os parâmetros e descubra a partir de qual momento a aplicação começou a perder escalabilidade.
