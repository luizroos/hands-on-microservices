# Exercício 11 - desnormalização de dados
[Setup do ambiente](https://github.com/luizroos/hands-on-microservices)

---

### Criando o banco de dados

Ainda sobre cassandra, vamos criar um novo cluster para testar com a aplicação, mas dessa vez com suporte a views materializadas. Para isso, temos que habilitar o cassandra para suportar, altere a propriedade **enable_materialized_views** de false para true no arquivo:

```console
vim ~/.ccm/repository/4.0.2/conf/cassandra.yaml
```

Depois, remova cluster de cassandra criado no exercício 10 e crie um cluster novo, mas agora com somente 3 nós:

```console
ccm remove

ccm create --version 4.0.2 --nodes 3 --start sample-cassandra-cluster

ccm node1 cqlsh
```

Vamos criar a nossa tabela de usuários da aplicação:

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

### Subindo a aplicação

Vamos agora compilar, subir a aplicação e inserir um usuário (podemos rodar direto na máquina virtual):

```console
cd ~/hands-on-microservices/sample-app/

git checkout e11

./gradlew clean build

java -jar build/libs/sample-app-0.0.11-SNAPSHOT.jar
```

Pode usar curl ou acesse http://172.0.2.32:30001/swagger-ui.html.

Qual erro que deu na inclusão? Como resolver esse problema?

### Criando materialized view

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
and id is not null
primary key (email, id);
```

Caso você tenha tomado o erro erro na criação da materialized view: "InvalidRequest: Error from server: code=2200 [Invalid query] message="Materialized views are disabled. Enable in cassandra.yaml to use.". Então você não fez o primeiro passo desse exercício, nesse caso, pare todos os nós:

```console
ccm node1 stop
ccm node2 stop
ccm node3 stop
```

Edite os arquivos trocando de false para true a propriedade **enable_materialized_views**:

```console
vim ~/.ccm/sample-cassandra-cluster/node1/conf/cassandra.yaml
vim ~/.ccm/sample-cassandra-cluster/node2/conf/cassandra.yaml
vim ~/.ccm/sample-cassandra-cluster/node3/conf/cassandra.yaml
```

E então inicialize todos os nós novamente.

Altere a query para buscar na materialized view ao invés de na tabela, então compile a aplicação e teste novamente:

```console
vim src/main/java/web/core/user/CassandraUserRepository.java

./gradlew clean build

java -jar build/libs/sample-app-0.0.11-SNAPSHOT.jar
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) A materialized view é uma tabela normal, vai estar particionada então cuidado com hot spots e existe um [custo](https://www.datastax.com/blog/materialized-view-performance-cassandra-3x) de escrita, ainda não é recomendado usa-las em produção. Mas, sem materialized view, vamos discutir como se resolveria esse problema?
