# Exercício 8 - impacto das dependências externas

[Setup do ambiente](https://github.com/luizroos/hands-on-microservices)

[Vídeo](https://drive.google.com/file/d/1JaB-n3g5FYTLGzNmu3NNBIVo8aFx_cpr/view?usp=sharing)

---

Compile e execute a aplicação (não se esqueça do mockserver e do banco de dados):

```console
cd ~/hands-on-microservices/user-service/

git checkout e8

./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*SNAPSHOT.jar -t user-service:8 .

docker run --rm -p 8080:30001 -e MYSQL_HOST=mysql -e POSTALCODE_HOST=mockserver:1080 --name user-service --net=my-net user-service:8
```

Execute novamente o teste de carga com os valores do exercício 7 e compare os resultados.

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) O que mudou nessa versão da aplicação?
