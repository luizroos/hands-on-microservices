# Exercicio 6 - teste de carga

Executa o container da aplicação (não se esqueça do banco de dados)


Desde o inicio:

```
docker network create my-net

docker run -p 3306:3306 --name mysql --net=my-net -v ~/temp/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51

./gradlew build

docker build --build-arg JAR_FILE=build/libs/*.jar -t user/sample-app:6 .

docker run -p 8080:30001 -e MYSQL_HOST=mysql --name sample-app --net=my-net user/sample-app:6
```

Conecte seu cliente mysql no banco e verifique se os containers estão rodando.

Apague todos os usuários da tabela

```
delete from user
```

Executa um teste de carga (n = numero de requests, c = paralelismo, db --help)

```
ab -n 1000 -c 50 http://localhost:8080/users/random
```

Verifique a quantidade de usuários inseridos:

```
select count(1) from user
```

Deixe o log da aplicação aberto, ache o limite de escalabilidade:

```
docker logs -f sample-app
```

Ache o limite da sua aplicação. Anote os parâmetros usados e o resultado.

Notaram alguma coisa relacionado ao aumento do paralelismo (c) com o tempo médio de resposta?

Na classe UserEntity, vamos alterar o tipo da chave primária para um valor númerico (além de alterar a aplicação, você tera que recriar a tabela do banco, então faça: drop table user). Gere novamente a aplicação (gradle e docker build) e inicie (docker run).

Execute novamente o teste de carga.

Os resultados foram os mesmos? o que aconteceu?



