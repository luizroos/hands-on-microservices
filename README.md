# Exercício 10 - Usando cassandra

Usando [ccm](https://www.datastax.com/blog/ccm-development-tool-creating-local-cassandra-clusters), vamos criar um cluster com 5 nós usando a versão 3.11.10 do [cassandra](https://cassandra.apache.org/):

```
ccm create --version 3.11.10 --nodes 5 --start sample-cassandra-cluster
```

Verifique o status do cluster (todos nós devem estar UP):

```
ccm status
```

Conecte em um nó do cluster:

```
ccm node1 cqlsh
```

Assim como em bancos relacionais nós temos os schemas, no cassandra temos [keyspace](https://docs.datastax.com/en/cql-oss/3.x/cql/cql_reference/cqlCreateKeyspace.html), vamos criar uma keyspace de teste com replication factor de 3 (significa que cada registro vai ser armazenado em 3 nós):

```
create keyspace sample WITH durable_writes = true and replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

use sample;
```

Vamos criar agora nossa tabela de usuários:

```
create table if not exists user  (
 id varchar primary key,
 name varchar,
 email varchar,
 age int,
 addressPostalCode varchar);
```

Verifique a tabela criada:

```
select * from user;
```

Temos agora 5 nós de cassandra rodando com uma tabela **user** criada na namespace **sample**. O Cassandra permite que a [consistência](https://docs.datastax.com/en/cassandra-oss/3.0/cassandra/dml/dmlConfigConsistency.html) seja alterada, verifique a consistencia da sessão:

```
consistency
```

Se a consistência não for ONE, altere para ONE:

```
consistency ONE
```

Vamos inserir agora um usuário na nossa tabela:

```
insert into user (addresspostalcode, age, email, name, id) values ('01122080', 30, 'Joao', 'joao@teste.com', '1');
```

Usando ccm, pare um outro nó do cluster (abra uma nova sessão ssh da vm para facilitar):

```
ccm node2 stop

ccm status
```

Mude a consistência da sessão para QUORUM e tente incluir novos usuários (ao menos mais 5 usuários):

```
consistency QUORUM

insert into user (addresspostalcode, age, email, name, id) values ('01122080', 30, 'Joao', 'joao@teste.com', '2');
...
```

Algum insert deu erro? 

Execute novamente outros inserts, parando outro nó:

```
ccm node3 stop
```

E agora, algum insert deu erro? Por que?

Tente executar novamente um select: 

```
select * from user
```

Também deu erro, por que?

Altere a consistência de volta para ONE e tente fazer a inserção e a leitura novamente.

Apesar de [CQL](https://cassandra.apache.org/doc/latest/cql/) ter sintaxe parecida com SQL, eles não é a mesma coisa, tente fazer executar essa query:

```
select * from user where name = 'joao';
```

Por que não deixou?

Removao cluster e crie um novo, com a mesma keyspace e tabela, mas agora só com 3 nós:

```
ccm remove

ccm create --version 3.11.10 --nodes 3 --start sample-cassandra-cluster

ccm node1 cqlsh

create keyspace sample WITH durable_writes = true and replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 2 };

use sample;

create table if not exists user  (
 id varchar primary key,
 name varchar,
 email varchar,
 age int,
 addressPostalCode varchar);
```

Tente então executar a aplicação e inserir um usuário (podemos rodar direto na vm):

```
./gradlew build

java -jar build/libs/sample-app-0.0.10-SNAPSHOT.jar
```