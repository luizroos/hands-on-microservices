# Exercício 7 - impacto das dependências externas

[Setup do ambiente](https://github.com/luizroos/hands-on-microservices)

[Vídeo](https://drive.google.com/file/d/1rhJAGqxzeOrioVmZYoFxJyUWQ4dlwaGq/view?usp=sharing)

---

Vamos subir um [wiremock](https://github.com/wiremock/wiremock) para simular uma dependência com outro sistema

```console
docker run -d --rm -p 1080:8080 --net=my-net --name wiremock wiremock/wiremock
```

No wiremock, vamos configurar um endpoint **/postalcodes**, para responder um endereço mockado e demorar 200 ms para dar essa resposta:

```console
curl -v -X POST "http://localhost:1080/__admin/mappings" \
  -H "Content-Type: application/json" \
  -d '{
    "request": {
      "method": "GET",
      "urlPath": "/postalcodes"
    },
    "response": {
      "status": 200,
      "headers": {
        "Content-Type": "application/json; charset=utf-8"
      },
      "body": "{\"address\": \"rua mockada\", \"city\": \"Sao Paulo\", \"uf\": \"SP\"}",
      "fixedDelayMilliseconds": 200
    }
  }'
```

Teste a resposta do wiremock (subimos ele fazendo bind na porta 1080, então pode acessar tanto na vm com localhost quando no seu browser via 172.0.2.32):

```console
curl http://localhost:1080/postalcodes
```

Veja agora as alterações em [UserCreateService](src/main/java/web/core/user/UserCreateService.java), incluimos uma regra de negócio que valida se o endereço informado do estado de São Paulo, para isso usamos uma chamada a um serviço de postalcode.

```console
cd ~/hands-on-microservices/user-service/

git checkout e7

docker network create my-net

docker run --rm -p 3306:3306 --name mysql --net=my-net -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=user-db -d mysql:8.0

./gradlew clean build

docker build --build-arg JAR_FILE="build/libs/*SNAPSHOT.jar" -t user-service:7 .

docker run --rm -p 8080:30001 -e MYSQL_HOST=mysql -e POSTALCODE_HOST=wiremock:8080 --name user-service --net=my-net user-service:7
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Execute novamente o teste de carga com os parâmetros encontrados do exercício 6.

```console
ab -n 1000 -c {concorrência} http://localhost:8080/users/random
```


O que aconteceu? Por que a aplicação perdeu tanta escalabilidade?
