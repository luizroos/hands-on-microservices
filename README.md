# Exercício 11 - Modelando para consulta

Remova o cluster criado no exercicio 10 e crie um novo, com a mesma keyspace e tabela, mas agora só com 3 nós:

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

java -jar build/libs/sample-app-0.0.11-SNAPSHOT.jar
```

Pode usar curl ou acesse http://172.0.2.32:30001/swagger-ui.html.

Qual erro que deu na inclusão? Como resolver esse problema?

Conecte no banco e crie uma materialized view, onde a chave de partição será o email.

```
create materialized view user_by_email 
as select email, id, name, age, addressPostalCode
from user 
where email is not null
primary key (email, id);
```


