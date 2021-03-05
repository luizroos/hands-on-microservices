# Exercício 8 - impacto das dependências externas

Compile e execute a aplicação (não se esqueça do mockserver e do banco de dados):

```console
./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*.jar -t sample-app:8 .

docker run --rm -p 8080:30001 -e MYSQL_HOST=mysql -e POSTALCODE_HOST=mockserver:1080 --name sample-app --net=my-net sample-app:8
```

Execute novamente o teste de carga com os valores do exercÍcio 7 e compare os resultados.

![#686bd4](https://via.placeholder.com/10/686bd4?text=+)  O que mudou nessa versão da aplicação?
