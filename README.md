# Exercício 3 - aplicação mais realista

Aumentamos um pouco a aplicação, incluimos um api de cadastro de usuário usando um [banco de dados](https://www.h2database.com/html/main.html) em memória, muito utilizado em ambientes de teste. 

Vamos gerar o container e subir essa aplicação: 

```
./gradlew build

docker build --build-arg JAR_FILE=build/libs/*.jar -t user/sample-app:3 .

docker run -d -p 8080:30001 --name sample-app user/sample-app:3
```

Notem que subimos a aplicação agora já com mapeamento na porta 8080, assim não precisamos ficar checando toda vez qual a porta aleatória que foi feito o mapeamento.

Discussão: notem o tipo de ID de UserEntity, por que UUID ao invés de um ID númerico? Vamos falar mais detalhes sobre isso no exercício 6.

Acesse http://172.0.2.32:8080/swagger-ui.html e faça o cadastro de um usuário.
