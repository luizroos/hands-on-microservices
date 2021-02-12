# Exercício 6 - teste de carga

Remova todas os containers que vocês tem, vamos começar do zero (banco e aplicação)

Desde o inicio:

```
docker network create my-net

docker run --rm -p 3306:3306 --name mysql --net=my-net -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51

./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*.jar -t sample-app:6 .

docker run --rm -d -p 8080:30001 -e MYSQL_HOST=mysql --name sample-app --net=my-net sample-app:6
```

Conecte seu cliente mysql no banco e verifique se os containers estão rodando. Se tiver dados na sua tabela, apague todos:

```
delete from user
```

Execute um teste de carga usando [apache bench](https://httpd.apache.org/docs/2.4/programs/ab.html) (n = numero de requests, c = paralelismo, db --help), já instalado na vm:

```
ab -n 1000 -c 3 http://localhost:8080/users/random
```

Criamos um endpoint na aplicação (users/random), só para podermos criar usuários aleatórios e usar nesse teste de carga.

Verifique a quantidade de usuários inseridos:

```
select count(1) from user
```

Deixe exibindo, em uma aba separado, os logs da aplicação 

```
docker logs -f sample-app
```

E em outra aba as estatisticas do docker (a coluna PIDS indica a quantidade de threads que a aplicação está usando):

```
docker stats
```

Variando **n** e principalmente **c** do apache bench, em determinado momento, a aplicação vai começar a degradar a **performance**, mas vai continuar respondendo ok para todos requests, porém vai chegar num limite que ela vai falhar a **disponibilidade**, começando a responder com erros. Ache esse limite.

Veja no log e descubra o que fez a aplicação começar a falhar, o que poderiamos mudar nos parâmetros da aplicação para aumentar sua escalabilidade?

Sem compilar, rode de novo a aplicação para que ela consiga executar 1000 requests com 200 de concorrência e que tenha um resultado sem falhas:

```
ab -n 1000 -c 200 http://localhost:8080/users/random
```

Notem no docker stats, perceberam algo diferente nos containers?

#### O problema de sequences

Na classe [UserEntity](sample-app/src/main/java/web/core/user/UserEntity.java), vamos alterar o tipo da chave primária para um valor númerico:

```
vim src/main/java/web/core/user/UserEntity.java
```

Comente (insira /* e */) o código com ID do tipo UUID:
```
@Id
@Type(type = "uuid-binary")
@GeneratedValue(strategy = GenerationType.AUTO)
@GenericGenerator(name = "UserId", strategy = "uuid2")
private UUID id;
...
public UUID getId() {
  return id;
}

public void setId(UUID id) {
  this.id = id;
}
```

Descomente o ID com Long

```
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
...
public Long getId() {
  return id;
}

public void setId(Long id) {
  this.id = id;
}
```

Compile a aplicação e gere a nova imagem:

```
./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*.jar -t sample-app:6 .
```

Como a chave primária da tabela mudou, caso você esteja rodando o mesmo MySQL, execute um drop table user para que a aplicação possa recriar a tabela com novo tipo da PK. Ou então remova o container do MySQL e inicie um novo junto com a aplicação:

```
docker run --rm -p 3306:3306 --name mysql --net=my-net -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51

docker run --rm -p 8080:30001 -e MYSQL_HOST=mysql --name sample-app --net=my-net sample-app:6
```

Execute novamente o teste de carga (aquele com os valores que você encontrou). 

Os resultados foram os mesmos? o que aconteceu? Ache o limite de escalabilidade dessa aplicação com essa alteração.
