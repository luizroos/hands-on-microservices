# Exercicio 3

Aumentamos um pouco a aplicação, incluimos um api de cadastro de usuário usando uma base em memoria, vamos gerar o container, subir a aplicação 

```
./gradlew build

docker build --build-arg JAR_FILE=build/libs/*.jar -t user/sample-app:3 .

docker run -d -p 8080:30001 --name sample-app user/sample-app:3

```

Acesse http://172.0.2.32:8080/swagger-ui.html e faça o cadastro de um usuário