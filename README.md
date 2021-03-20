# Exercício 10 - Usando cassandra

Usando [ccm](https://www.datastax.com/blog/ccm-development-tool-creating-local-cassandra-clusters), vamos criar um cluster com 5 nós usando a versão 3.11.10 do [cassandra](https://cassandra.apache.org/):

Obs: Aqui aconselho criar a vm de novo (vagrant destroy, vagrant up), porque com 5 nós, vai usar muita memória, a vm subindo com 4Gb zerada da conta. Mas qualquer coisa, crie com 3 nós

```console
ccm create --version 3.11.10 --nodes 5 --start sample-cassandra-cluster
```

Verifique o status do cluster (todos nós devem estar UP):

```console
ccm status
```

Conecte em um nó do cluster:

```console
ccm node1 cqlsh
```

Assim como em bancos relacionais nós temos os schemas, no cassandra temos [keyspace](https://docs.datastax.com/en/cql-oss/3.x/cql/cql_reference/cqlCreateKeyspace.html), vamos criar uma keyspace de teste com replication factor de 3 (significa que cada registro vai ser armazenado em 3 nós):

```cql
create keyspace sample WITH durable_writes = true and replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

use sample;
```

Vamos criar agora nossa tabela de pessoas da copa:

```cql
create table if not exists pessoa2 ( 
ano_nascimento int, 
nome text, 
telefone int,
nacionalidade text,
primary key (ano_nascimento, nacionalidade, nome)) 
```

Verifique a tabela criada:

```cql
select * from pessoa;
```

### Consistência
---- 

Temos agora 5 nós de cassandra rodando com uma tabela **pessoa** criada na namespace **sample**. O Cassandra permite que a [consistência](https://docs.datastax.com/en/cassandra-oss/3.0/cassandra/dml/dmlConfigConsistency.html) seja alterada, verifique a consistencia da sessão:

```cql
consistency
```

Se a consistência não for ONE, altere para ONE:

```cql
consistency ONE
```

Vamos inserir agora umas pessoas na nossa tabela (tente variar o ano de nascimento entre elas):

```cql
insert into pessoa (nome, nacionalidade, telefone, ano_nascimento) values ('joao', 'br', 1111, 1998);
insert into pessoa (nome, nacionalidade, telefone, ano_nascimento) values ('john', 'eua', 2222, 1993);
...
```

Usando ccm, pare um outro nó do cluster (abra uma nova sessão ssh da vm para facilitar):

```console
ccm node2 stop

ccm status
```

Mude a consistência da sessão para QUORUM e tente incluir novas pessoas (variando o ano de nascimento, ao menos mais 5):

```console
consistency QUORUM

insert into pessoa (nome, nacionalidade, telefone, ano_nascimento) values ('giovanni', 'it', 3333, 1994);
...
...
```

Algum insert deu erro? 

Pare mais um nó do cluster:

```console
ccm node3 stop
```

E execute novamente outros inserts

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) E agora, algum insert deu erro? Por que?

Tente executar novamente um select: 

```cql
select * from pessoa
```

Também deu erro, por que?

Altere a consistência de volta para ONE e tente fazer a inserção e a leitura novamente.


### Filtros
---

Apesar de [CQL](https://cassandra.apache.org/doc/latest/cql/) ter sintaxe parecida com SQL, eles não é a mesma coisa, tente fazer executar essa query:

```cql
select * from pessoa where telefone = 1111;
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Por que não deixou?

Tente essas:

```cql
select * from pessoa where ano_nascimento = 1998; 
select * from pessoa where ano_nascimento = 1998 and nome = 'joao';
select * from pessoa where ano_nascimento = 1998 and nacionalidade = 'br';
select * from pessoa where ano_nascimento = 1998 and nacionalidade = 'br' and nome = 'joao';
select * from pessoa where ano_nascimento = 1998 and nacionalidade = 'br' and nome = 'joao' and telefone = 1111;
```

Quais deram sucesso e quais falharam? por que?


