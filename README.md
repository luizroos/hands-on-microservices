# Exercício 3 - aplicação mais realista
[Setup do ambiente](https://github.com/luizroos/hands-on-microservices)

---

Voltamos ao modelo standalone e aumentamos um pouco a aplicação, incluimos uma api simples de cadastro de usuário usando um [banco de dados](https://www.h2database.com/html/main.html) em memória, muito utilizado em ambientes de teste. 

Vamos gerar o container e subir essa aplicação: 

```console
cd ~/hands-on-microservices/sample-app/

git checkout e3 

./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*.jar -t sample-app:3 .

docker run --rm -d -p 8080:30001 --name sample-app sample-app:3
```

Notem que subimos a aplicação agora já com mapeamento na porta 8080, assim não precisamos ficar checando toda vez qual a porta aleatória que foi feito o mapeamento.

Acesse http://172.0.2.32:8080/swagger-ui.html e faça o cadastro de um usuário.

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Para discutir: notem o tipo de ID de [UserEntity](/src/main/java/web/core/user/UserEntity.java), por que UUID ao invés de um ID númerico?
