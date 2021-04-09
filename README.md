# Exercício 13 - Kafka

Voltamos ao banco H2, para simplificar subir a aplicação. 


O Kafka utiliza o [Zookeeper](https://zookeeper.apache.org/) para sincronizar as configurações entre os nós do cluster, então antes de subir o servidor do Kafka, temos que subir o serviço do Zookeeper.

A empresa que mantém o Kafka possui uma ferramenta web para gerenciar e monitorar o Kafka chamada [control center](https://docs.confluent.io/platform/current/control-center/index.html). Mantém também outra ferramenta chamada [schema registry](https://docs.confluent.io/platform/current/control-center/index.html) que serve para registrar schemas das mensagens que trafegam no Kafka, garantindo que as mensagens não quebrem alguma compatibilidade.

Por conta disso, ao invés de subir cada container separado, vamos usar o [docker-compose](https://docs.docker.com/compose/) para subir todos os serviços (apesar que para usar o Kafka, só é necessário mesmo o Zookeeper e o Kafka Broker).

Veja o arquivo [docker-compose.yam](docker-compose.yml), a estrutura é simples, e uma nova forma para vocês subirem containers. Vamos subir nosso Kafka então:

```console
cd ~/hands-on-microservices/sample-app/

git checkout e13

docker-compose up -d
```

Para parar todos serviços e remover todos containers, execute:

```console
docker-compose down
```

Se quiser ver o log de todo containers que estão subindo:

```console
docker-compose logs -f
```

Após todos os serviços subirem, acesse o control center a partir de http://172.0.2.32:9021/.

### Gerando e consumindo algumas mensagens

Vamos criar um novo tópico chamado **sample.topic**, para isso acesse o menu **Topics** e em seguida selecione **Add a topic**. Pode deixar o número de partições como **1**, selecione **Customize settings**, veja que o control center já nos da algumas configurações pré definidas (faz mais sentido em um ambiente com mais nós no cluster). Selecione **Custom availability settings** e deixe **replication_factor** e **min_insync_replicas** com valor 1. Em **Storage** você configura como será a retenção das mensagens, o Kafka permite rentação por tempo e/ou tamanho. Pode deixar retention time como 1 hora e retention size como 1 Mb. Por fim, pode mandar crir o tópico.

Vamos gerar agora algumas mensagens nesse tópico, para isso vamos usar a ferramenta [kafka-console-producer](https://docs.cloudera.com/documentation/kafka/latest/topics/kafka_command_line.html) do próprio Kafka que permite gerar mensagens texto em um tópico: 

```console
docker exec broker bash -c "seq 5 | kafka-console-producer --request-required-acks 1 --broker-list localhost:29092 --topic sample.topic"
```

Enviamos 5 mensagens para o tópico que criamos. Vamos usar outra ferramenta chamada [kafka-console-consumer](https://docs.cloudera.com/documentation/kafka/latest/topics/kafka_command_line.html) para consumir essas mensagens:

```console
docker exec broker kafka-console-consumer --bootstrap-server localhost:29092 --topic sample.topic --from-beginning --group consumer --timeout-ms 5000
```

Marcamos para consumir desde o inicio do tópico, nomeamos o consumidor de "consumer" e setamos um timeout de 5 segundos (significa que se não chegar mensagens em 5 segundos, o consumidor para de consumir, isso é útil para os demais testes). 

Veja no control center os detalhes do tópico e do consumer (menu Consumers). Se você só gerou as 5 mensagens, o end offset deve estar em 5, o current offset do consumer está em 5 e o lag em 0.

Podemos usar a ferramenta [kafka-consumer-groups](https://docs.cloudera.com/documentation/kafka/latest/topics/kafka_command_line.html) para ver os detalhes do consumer também via linha de comando:

```console
docker exec broker kafka-consumer-groups --bootstrap-server localhost:29092 --group consumer --describe
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Execute novamente o consumer, algo foi exibido? Por que não?

Vamos reprocessar as mensagens alterando o current offset do consumer:

```console
docker exec broker kafka-consumer-groups --bootstrap-server localhost:29092 --group consumer --topic sample.topic --reset-offsets --to-earliest --execute
```

Agora sim, execute novamente o consumer.
 
Via linha de comando, podemos visutalizar também os detalhes do tópico: 

```console
docker exec broker kafka-topics --bootstrap-server localhost:29092 --topic sample.topic --describe
```

### Integrando com a aplicação

Veja as alterações na aplicação, principalmente os arquivos [KafkaConfig.java](sample-app/src/main/java/web/KafkaConfig.java), [OnUserChanged.java](sample-app/src/main/java/web/core/user/OnUserChanged.java) e (UserCreateService.java)[sample-app/src/main/java/web/core/user/UserCreateService.java].

O Kafka que subimos está habilitado com **auto create topic**, isso significa que se a aplicação enviar uma mensagem para um tópico, ele sera criado pelo servidor. Se quiser, pode criar o tópico **user.changed**, ou então suba a aplicação que o tópico sera criado por ela.

Vamos subir a aplicação na própria vm:

```console
cd ~/hands-on-microservices/sample-app/

./gradlew clean build

java -jar build/libs/sample-app-0.0.13-SNAPSHOT.jar
```

A aplicação vai conectar no kafka via localhost (quando subimos o kafka, fizemos mapeamento da porta do container para uma porta da vm).

Acesse http://172.0.2.32:30001/swagger-ui.html e crie uns usuários (pode usar o mesmo endpoint que usamos para o teste de carga), fique vendo o log da aplicação enquanto cria usuários. Pode criar usando curl também:

```console
curl localhost:30001/users/random
```

Veja no log o consumo do evento do usuário gerado. 

```console
2021-02-25 19:51:15.630  INFO 19256 --- [ntainer#0-0-C-1] web.core.user.OnUserChanged : user created, id=bcbb92f1-fd07-4e22-8c9d-c40635de369c, name=vYWHzQrI
```

Enviamos a mensagem para o Kafka na criação do usuário e consumimos esse evento na mesma aplicação.

Veja no control center o tópico e consumidor criado. Se você deixou a aplicação criar o tópico, verifique nos detalhes do dele que você pode alterar algumas configurações, altere o retenton time para 1 hora por exemplo.

Agora crie um usuário com dominio de email **hotmail** (na classe [OnUserChanged](sample-app/src/main/java/web/core/user/OnUserChanged.java) tem um if para que usuários com esse dominio, lance uma exceção no consumidor). 

```console
curl localhost:30001/users/random?emailDomain=hotmail
```
Crie novos usuários e veja que paramos de consumir mensagens, mesmo desses novos, por que?

Vamos criar mais partições para nosso tópico. Usando a [kafka-topics](https://docs.cloudera.com/documentation/kafka/latest/topics/kafka_command_line.html) execute o seguinte comando (fique de olho no log da aplicação enquanto executa isso):

```console
docker exec broker kafka-topics --bootstrap-server localhost:29092 --alter --topic user.changed --partitions 6
```

A aplicação comeceu a consumir de outras partições. Se tivermos mais de uma aplicação conectada, cada partição é consumida por apenas uma aplicação (não adianta ter 10 aplicações consumindo um tópico de 6 partições), pois lembre-se que o consumo do kafka é ordenado. Mas uma mesma aplicação pode consumir mais de uma partição (é o caso aqui). 

Veja no control center como ficou o tópico e o consumidor.

Crie novos usuários, o consumo "voltou", mas olhe no control center, somente nas partições que não estão com erro de consumo. Como resolver isso? 

Em últimos casos:

```console
docker exec broker kafka-consumer-groups --bootstrap-server localhost:29092 --group sampleApp.onUserChanged --describe

docker exec broker kafka-consumer-groups --bootstrap-server localhost:29092 --group sampleApp.onUserChanged --topic user.changed:0 --reset-offsets --shift-by 1
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Por que não alterou? Pare a aplicação, execute novamente o comando e suba a aplicação.

Por que esse deve ser a última coisa que você deve fazer para resolver um problema desse tipo?
