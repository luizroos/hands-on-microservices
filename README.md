# Exercício 9 - orquestrando containers

Voltamos ao banco H2, para simplificar subir a aplicação. 


O Kafka utiliza o [Zookeeper](https://zookeeper.apache.org/) para sincronizar as configurações entre os nós do cluster, então antes de subir o servidor do Kafka, temos que subir o serviço do Zookeeper.

A empresa que mantém o Kafka possui uma ferramenta web para gerenciar e monitorar o Kafka chamada [control center](https://docs.confluent.io/platform/current/control-center/index.html). Mantém também outra ferramenta chamada [schema registry](https://docs.confluent.io/platform/current/control-center/index.html) que serve para registrar schemas das mensagens que trafegam no Kafka, garantindo que producers não quebrem a compatibilidade.

Por conta disso, ao invés de subir cada container separado, vamos usar o [docker-compose](https://docs.docker.com/compose/) para subir todos os serviços (apesar que para usar o Kafka, só é necessário mesmo o Zookeeper e o Kafka Broker).

Veja o arquivo [docker-compose.yam](docker-compose.yml), a estrutura é simples, e uma nova forma para vocês subirem containers. Vamos subir nosso Kafka então:

```
docker-compose up -d
```

Para parar todos serviços e remover todos containers, execute:

```
docker-compose down
```

Após todos os serviços subir, acesse o control center a partir de http://localhost:9021/ (TODO ip da VM).

### Gerando e consumindo algumas mensagens

Vamos criar um novo tópico chamado **sample.topic**, para isso acesse o menu **Topics** e em seguida selecione **Add a topic**. Pode deixar o número de partições como **1**, selecione **Customize settings**, veja que o control center já nos da algumas configurações pré definidas (faz mais sentido em um ambiente com mais nós no cluster). Selecione **Custom availability settings** com e deixe replication_factor e min_insync_replicas com valor 1. Em **Storage** você configura como será a retenção das mensagens, o Kafka permite rentação por tempo e/ou tamanho. Pode deixar retention time como 1 hora e retention size como 1 Mb. Por fim, pode mandar crir o tópico.

Vamos gerar agora algumas mensagens nesse topico, para isso vamos usar a ferramenta [kafka-console-producer](https://docs.cloudera.com/runtime/7.2.7/kafka-managing/topics/kafka-manage-cli-producer.html) ferramenta do próprio Kafka que permite gerar mensagens texto em um tópico: 

```
docker exec broker bash -c "seq 5 | kafka-console-producer --request-required-acks 1 --broker-list localhost:29092 --topic sample.topic"
```

Enviamos 5 mensagens para o tópico que criamos. Vamos usar outra ferramenta chamada [kafka-console-consumer](https://docs.cloudera.com/runtime/7.2.7/kafka-managing/topics/kafka-manage-cli-consumer.html) para consumir essas mensagens:

```
docker exec broker kafka-console-consumer --bootstrap-server localhost:29092 --topic sample.topic --from-beginning --group consumer --timeout-ms 5000
```

Marcamos para consumir desde o inicio do tópico, nomeamos o consumidor de "consumer" e setamos um timeout de 5 segundos (significa que se não chegar mensagens em 5 segundos, o consumidor para de consumir, isso é útil para os demais testes). 



Veja no control center os detalhes do tópico e do consumer (menu Consumers). Se você só gerou as 5 mensagens, o end offset deve estar em 5, o current offset do consumer está em 5 e o lag em 0.

Podemos usar a ferramenta [kafka-consumer-groups](https://docs.cloudera.com/runtime/7.2.7/kafka-managing/topics/kafka-manage-cli-cgroups.html) para ver os detalhes do consumer também via linha de comando:

```
docker exec broker kafka-consumer-groups --bootstrap-server localhost:29092 --group consumer --describe
```

Execute novamente o consumer, algo foi exibido? Por que não?

Vamos reprocessar as mensagens alterando o current offset do consumer:

```
docker exec broker kafka-consumer-groups --bootstrap-server localhost:29092 --group consumer --topic sample.topic --reset-offsets --to-earliest --execute
```

Agora sim, execute novamente o consumer.
 

Via linha de comando, podemos visutalizar também os detalhes do tópico: 

```
docker exec broker kafka-topics --bootstrap-server localhost:29092 --topic sample.topic --describe
```

### Integrando com a aplicação

Veja as alterações na aplicação, principalmente os arquivos [KafkaConfig.java](sample-app/src/main/java/web/KafkaConfig.java), [OnUserChanged.java](sample-app/src/main/java/web/core/user/OnUserChanged.java) e (UserCreateService.java)[sample-app/src/main/java/web/core/user/UserCreateService.java].

O Kafka que subimos está habilitado com **auto create topic**, isso significa que se a aplicação enviar uma mensagem para um tópico, ele sera criado pelo servidor. Se quiser, pode criar o tópico **user.changed**, ou então suba a aplicação que o tópico sera criado por ela.

Crie alguns usuários para a aplicação (usando o mesmo endpoint que usamos para gerar o teste de carga).

Veja no log o consumo do evento do usuário gerado. 

Agora crie um usuário com nome **consumer_name_err** (na classe OnUserChanged tem um if para que usuários com esse nome, lance uma exceção no consumidor).



