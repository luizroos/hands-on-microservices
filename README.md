# Exercicio 6 - teste de carga

Rodando mock server

```
docker run -d --rm -p 1080:1080 --net=my-net --name mockserver mockserver/mockserver
```

* O parametro rm indica que o container pode ser excluido quando for desligado


Vamos configurar o mock server para retornar ter uma resposta com delay de 1 segundo:

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

Vamos fazer um teste de carga agora

ab -n 10 -c 3 http://localhost:30001/users/random