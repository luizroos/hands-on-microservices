# Exercicio 8 - lidando com dependencias externa

Execute a aplicação:

```
docker network create my-net

docker run --rm -p 3306:3306 --name mysql --net=my-net -v ~/temp/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51

./gradlew build

docker build --build-arg JAR_FILE=build/libs/*.jar -t user/sample-app:8 .

docker run --rm -p 8080:30001 -e MYSQL_HOST=mysql --name sample-app --net=my-net user/sample-app:8
```

Rode novamente o teste de carga com os valores, compare os resultados com o teste de carga do exercicio 7.

O que mudou?
