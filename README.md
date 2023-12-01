# Exercício 10 - Usando cassandra
[Setup do ambiente](https://github.com/luizroos/hands-on-microservices)

---

Usando [ccm](https://www.datastax.com/blog/ccm-development-tool-creating-local-cassandra-clusters), vamos criar um cluster com 5 nós usando a versão 4.0.2 do [cassandra](https://cassandra.apache.org/):

Obs: Aqui aconselho criar a máquina virtual de novo (vagrant destroy, vagrant up), porque com 5 nós, vai usar muita memória, a vm subindo com 4Gb zerada da conta. Mas qualquer coisa, crie com 3 nós

```console
ccm create --version 4.0.2 --nodes 5 --start sample-cassandra-cluster
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
create keyspace user_ks WITH durable_writes = true and replication = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

use user_ks;
```

Vamos criar agora uma tabela para representar campeonatos realizados, os campeonatos acontecem de forma anual, então não podemos ter dois campeonatos da mesma modalidade no mesmo ano.

```cql
create table if not exists campeonato ( 
  ano int, 
  nome text,
  campeao text,
  esporte text,
primary key (ano, nome, esporte));
```

Verifique a tabela criada:

```cql
select * from campeonato;
```

### Consistência
---- 

Temos agora 5 nós de cassandra rodando com uma tabela **campeonato** criada na keyspace **user_ks**. O Cassandra permite que a [consistência](https://docs.datastax.com/en/cassandra-oss/3.0/cassandra/dml/dmlConfigConsistency.html) seja alterada, verifique a consistencia da sessão:

```cql
consistency
```

Se a consistência não for ONE, altere para ONE:

```cql
consistency ONE
```

Vamos inserir agora alguns campeonatos na nossa tabela (tente variar campeonatos de varios anos):

```cql
insert into campeonato (nome, campeao, esporte, ano) values ('copa_mundo', 'brasil', 'futebol_masculino', 1994);
insert into campeonato (nome, campeao, esporte, ano) values ('copa_mundo', 'franca', 'futebol_masculino', 1998);
insert into campeonato (nome, campeao, esporte, ano) values ('copa_mundo', 'brasil', 'futebol_masculino', 2002);
insert into campeonato (nome, campeao, esporte, ano) values ('copa_mundo', 'eua', 'basquete_masculino', 2014);
insert into campeonato (nome, campeao, esporte, ano) values ('copa_mundo', 'espanha', 'basquete_masculino', 2019);
insert into campeonato (nome, campeao, esporte, ano) values ('copa_mundo', 'alemanha', 'basquete_masculino', 2023);
insert into campeonato (nome, campeao, esporte, ano) values ('olimpiadas', 'brasil', 'volei_feminino', 2012);
insert into campeonato (nome, campeao, esporte, ano) values ('olimpiadas', 'china', 'volei_feminino', 2016);
insert into campeonato (nome, campeao, esporte, ano) values ('olimpiadas', 'eua', 'volei_feminino', 2020);
insert into campeonato (nome, campeao, esporte, ano) values ('olimpiadas', 'russia', 'volei_masculino', 2012);
insert into campeonato (nome, campeao, esporte, ano) values ('olimpiadas', 'brasil', 'volei_masculino', 2016);
insert into campeonato (nome, campeao, esporte, ano) values ('olimpiadas', 'franca', 'volei_masculino', 2020);
...
```

Usando ccm, pare um outro nó do cluster (abra uma nova sessão ssh da vm para facilitar):

```console
ccm node2 stop

ccm status
```

Mude a consistência da sessão para QUORUM e tente incluir novas campeonatos (variando sempre o ano, ao menos mais 5, pode inventar):

```console
consistency QUORUM

insert into campeonato (nome, campeao, esporte, ano) values ('copa_mundo', 'alemanha', 'futebol_masculino', 1990);
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
select * from campeonato
```

Também deu erro, por que?

Altere a consistência de volta para ONE e tente fazer a inserção e a leitura novamente.


### Filtros
---

Apesar de [CQL](https://cassandra.apache.org/doc/latest/cql/) ter sintaxe parecida com SQL, eles não é a mesma coisa, tente fazer executar essa query:

```cql
select * from campeonato where campeao = 'brasil';
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Por que não deixou?

Tente essas:

```cql
select * from campeonato where ano = 1998; 
select * from campeonato where ano = 1998 and esporte = 'futebol_masculino';
select * from campeonato where ano = 1998 and nome = 'copa_mundo';
select * from campeonato where ano = 1998 and nome = 'copa_mundo' and esporte = 'futebol_masculino';
select * from campeonato where ano = 1998 and nome = 'copa_mundo' and esporte = 'futebol_masculino' and campeao = 'franca';
```

Quais deram sucesso e quais falharam? por que?
