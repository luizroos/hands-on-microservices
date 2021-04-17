# Exercício 15 - Outbox com kafka connect

Para que a mensagem kafka seja transacional com as operações que fazemos no nosso banco de dados, vamos usar um pattern chamado [transactional outbox](https://microservices.io/patterns/data/transactional-outbox.html). Imagine que ao invés de enviar para o kafka, a gente grave nosso evento em uma tabela do nosso banco de dados, de forma transacional com as demais operações, e depois crie um processo que busque de tempos em tempos os novos registros dessa tabela e envie para o kafka.

Para implementar esse processo de envio, podemos fazer na mão (implementando um schedule que de tempos em tempos busca os eventos e envia), mas podemos usar também uma ferramenta chamada [kafka connect](https://docs.confluent.io/platform/current/connect/index.html) (ele não serve só para isso, veja mais na documentação dele).

Nosso objetivo será criar configurar kafka connect que conecte no nosso MySQL, leia o evento da nossa tabela de outbox e envie para o tópico que usamos do kafka. Para isso, vamos criar uma imagem com um kafka connect com connectors para conectar a um banco MySQL (você pode pesquisar diferentes tipos connectors em https://www.confluent.io/hub/):

```console
cd ~/hands-on-microservices

git checkout e15

cd ~/hands-on-microservices/mysql-kafka-connect-image

chmod +x build-image.sh

./build-image.sh
```

Veja no [Dockerfile](mysql-kafka-connect-image/Dockerfile) que [herdamos](https://docs.confluent.io/home/connect/extending.html) de uma imagem **confluentinc/cp-kafka-connect-base** e adicionamos suporte a dois conectors: [JDBCConnector](https://www.confluent.io/hub/confluentinc/kafka-connect-jdbc) (o que iremos usar) e [Debezium MySQL Connector](https://www.confluent.io/hub/debezium/debezium-connector-mysql) ([Debezium](https://debezium.io/) é uma ferramenta que pode ser usada também para fazer isso, aqui vamos usar alguns extensões criadas por eles). 

Além disso, para poder fazer outbox com JDBCConnector de um jeito que é mais desacoplado com o banco de dados, vamos usar um [transform customizado](https://docs.confluent.io/platform/current/connect/transforms/custom.html), por isso que usamos um script para fazer o build da imagem, esse script vai baixar o código desse transform, compilar e instalar na imagem. Veja mais desse transform em https://github.com/luizroos/kconnect-jdbc-outbox-smt, lá tem uma explicação sobre a motivação de se usar.

Com nossa imagem do kafka connect gerada, via docker compose, vamos subir todos serviços necessários (exceto o connect): kafka, control center, schema registry, MySQL:

```console
cd ~/hands-on-microservices

docker-compose up -d 
```

Agora veja as alterações que fizemos na aplicação. Temos uma tabela de [Evento](sample-app/src/main/java/web/core/event/EventEntity.java), e ao invés de enviar para o kafka via kafka template, no nosso serviço [UserCreateService](sample-app/src/main/java/web/core/user/UserCreateService.java) nós vamos persistir a mensagem na nossa tabela de eventos. É uma tabela como outra qualquer, porém com uma coluna onde vamos gravar os bytes do evento em base 64, outra coluna que vamos gravar o que seria a chave do evento e outra com a coluna.

Vamos rodar então a aplicação 

```console
cd ~/hands-on-microservices/sample-app/

./gradlew clean build

java -jar build/libs/sample-app-0.0.15-SNAPSHOT.jar
```

Faça a inclusão de alguns usuários, pode usar aquele endpoint de criação de usuarios randomicos:

```console
curl http://localhost:30001/users/random
```

E então veja os usuários e eventos criados:

```console
docker exec -it mysql mysql -u db_user -p sample-db -e "select * from user";

docker exec -it mysql mysql -u db_user -p sample-db -e "select * from event";
```

Porém perceba que nossa aplicação não está consumindo esses eventos (veja no log, gerado pela classe [OnUserChanged](sample-app/src/main/java/web/core/user/OnUserChanged.java), afinal até agora a unica coisa que fizemos foi gravar o evento na tabela.

### Subindo o Kafka Connect
----

Aquela imagem que criamos a pouco, vamos subir ela, é bastante parâmetro mesmo, poderiamos colocar ela para subir junto no docker-compose mas como o foco aqui é o kafka connect, vamos subir ela separado. Perceba que são muitos parâmetros, execute então o connect:

```console
docker run --rm -d -p 8083:8083 --name connect --net hands-on-microservices_default \
 -e CONNECT_BOOTSTRAP_SERVERS=broker:29092 \
 -e CONNECT_REST_ADVERTISED_HOST_NAME=connect \
 -e CONNECT_REST_PORT=8083 \
 -e CONNECT_GROUP_ID=compose-connect-group \
 -e CONNECT_CONFIG_STORAGE_TOPIC=compose-connect-configs \
 -e CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR=1 \
 -e CONNECT_OFFSET_FLUSH_INTERVAL_MS=10000 \
 -e CONNECT_OFFSET_STORAGE_TOPIC=compose-connect-offsets \
 -e CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR=1 \
 -e CONNECT_STATUS_STORAGE_TOPIC=compose-connect-status \
 -e CONNECT_STATUS_STORAGE_REPLICATION_FACTOR=1 \
 -e CONNECT_KEY_CONVERTER=io.confluent.connect.avro.AvroConverter \
 -e CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL=http://schema-registry:8081 \
 -e CONNECT_VALUE_CONVERTER=io.confluent.connect.avro.AvroConverter \
 -e CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL=http://schema-registry:8081 \
 -e CONNECT_INTERNAL_VALUE_CONVERTER=org.apache.kafka.connect.json.JsonConverter \
 -e CLASSPATH=/usr/share/java/monitoring-interceptors/monitoring-interceptors-6.1.0.jar \
 -e CONNECT_PRODUCER_INTERCEPTOR_CLASSES="io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor" \
 -e CONNECT_CONSUMER_INTERCEPTOR_CLASSES="io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor" \
 -e CONNECT_PLUGIN_PATH="/usr/share/java,/usr/share/confluent-hub-components" \
 -e CONNECT_LOG4J_LOGGERS=io=DEBUG,org.apache.zookeeper=ERROR,org.I0Itec.zkclient=ERROR,org.reflections=ERROR \
 local/kafka-mysql-connect
```

Espere subir, e veja então no control center que agora temos um connect vinculado: http://172.168.0.32:9021/clusters (quando subimos o control center, dizemos que um connect rodaria no host connect, por isso que o control center enxerga).

### Criando o connector
----

Até agora só temos o connect executando, porém não temos nada criado para conectar na nossa base e enviar nossos eventos. Para isso vamos criar um connector que consulta nossa tabela de eventos (a outbox table) e envia para nosso tópico no kafka, execute esse post (lembre-se, o kafka connect está rodando via docker, e mapeamos a porta 8083 para o host):

```console
cd ~/hands-on-microservices

curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @mysql-kafka-connect/outbox-connect.json
```

Veja os [parâmetros](mysql-kafka-connect/outbox-connect.json) desse post, basicamente especificamos os dados do nosso banco, a query da nossa tabela de eventos e configuramos aquele nosso transform customizado para ler as colunas corretas da tabela e enviar para o tópico que queremos.

No control center o connector foi criado, acesse http://172.0.2.32:8083/connectors/outbox-connect/status para ver o status.   

Crie novos usuários e perceba que agora as mensagens vão chegar, nos logs da aplicação:

```console
web.core.user.OnUserChanged : user created
```

Tente criar o usuário com email do **hotmail** (aquele que deu erro no [exercicio 14](https://github.com/luizroos/hands-on-microservices/tree/e14)) e perceba que dessa vez a mensagem não será notificada.

```console
curl localhost:30001/users/random?emailDomain=hotmail
```
 
 
### Notificando tabelas do base de dados
----

Com kafka connect você pode conectar em várias fonte de dados (source connector) e escreve também para outras variedades de locais (sink connector). 

É muito comum situações onde usa kafka connect para notificar todas as tabelas do seu banco de dados, por exemplo, execute:

```console
cd ~/hands-on-microservices

curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @mysql-kafka-connect/user-cdc-connect.json
```

Veja nas configurações, estamos notificando em um tópico chamado **user-table** tudo que ocorre nessa tabela, sem ter que na aplicação gerar o evento. Apesar que isso gera um acoplamento grande do modelo da aplicação com os eventos, o que pode ser um problema grande a médio prazo, mas é muito comum isso ser usado para replicação de dados, se quiser usar, veja os melhores conectores para cada fonte (alguns são mais performaticos, se plugando nos logs do banco de dados).

