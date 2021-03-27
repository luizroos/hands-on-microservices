# Exercício 14 - Kafka Schema Registry

Alteramos a aplicação para ao invés de envir um objeto json, vamos enviar um objeto serializado com [avro](https://avro.apache.org/), integramos também com o schema registry. Veja as alterações no código, principalmente o arquivo [UserChangedMessage.avsc](/sample-app/src/main/avro/UserChangedMessage.avsc) (descreve o schema da mensagem).

Suba a aplicação novamente:

```console
./gradlew clean build

java -jar build/libs/sample-app-0.0.14-SNAPSHOT.jar
```

Crie alguns usuários via http://172.0.2.32:30001/swagger-ui.html, e verifique a aba schema do tópico no control center, mude a compatibilidade para [FORWARD](https://docs.confluent.io/platform/current/schema-registry/avro.html) (ou seja, o antigo schema deve poder ler o que for escrito no novo schema).

Altere o schema (UserChangedMessage) da mensagem deixando o nome opcional: 

```console
...
{
  "name": "userName",
  "type": [
    "null",
    "string"
  ]
} 
```

Recompile, reinicie a aplicação e crie um novo usuário. Você devera ver o seguine erro no log e o usuário não vai ser criado (a API vai retornar erro 500).

```console
org.apache.kafka.common.errors.SerializationException: Error registering Avro schema: {"type":"record","name":"UserChangedMessage","namespace":"web.core.user.pub","fields":[{"name":"userId","type":{"type":"string","avro.java.string":"String"}},{"name":"userName","type":["null",{"type":"string","avro.java.string":"String"}]}]}
Caused by: io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException: Schema being registered is incompatible with an earlier schema for subject
```

Porque ocorreu o erro? Como podemos corrigir? 

Altere a compatibilidade do schema no control center para NONE, tente gerar um novo usuário agora. Agora funcionou certo? mas por que essa deve ser a última opção para resolver esse problema?

Agora crie um usuário com nome **create_name_err**, veja no log da aplicação o que ocorreu. Esse é um problema de consistência, como resolver?
