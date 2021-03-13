# Exercício 11 - desnormalização de dados

Ainda sobre a aplicação com cassandra, remova o cluster criado no exercicio 10 e crie um novo, com a mesma keyspace e tabela, mas agora só com 3 nós:

```console
ccm remove

ccm create --version 3.11.10 --nodes 3 --start sample-cassandra-cluster

ccm node1 cqlsh
```

```cql
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

```console
cd ~/hands-on-microservices/sample-app/

git checkout e11

./gradlew clean build

java -jar build/libs/sample-app-0.0.11-SNAPSHOT.jar
```

Pode usar curl ou acesse http://172.0.2.32:30001/swagger-ui.html.

Qual erro que deu na inclusão? Como resolver esse problema?

Conecte no banco e crie uma materialized view, onde a chave de partição será o email.

```console
ccm node1 cqlsh
```

```cql
use sample

create materialized view user_by_email 
as select email, id, name, age, addressPostalCode
from user 
where email is not null
primary key (email, id);
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Para debater, a materialized view é uma tabela normal, vai estar particionada então cuidado com hot spots e existe um [custo](https://www.datastax.com/blog/materialized-view-performance-cassandra-3x) de escrita. Vale sempre uma avaliação entre materialized views e desnormalização manual.
