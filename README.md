# Exercício 16 - Kafka streams

Para simplificar, não vamos usar [kafka connect](https://github.com/luizroos/hands-on-microservices/tree/e15) para enviar as mensagens, já que o foco aqui são streams.

Suba novamente o kafka com docker-compose:

```console

cd ~/hands-on-microservices/

git checkout e16

docker-compose up -d
```

Temos um tópico com todos eventos de usuário, aquele usado para enviar os eventos dos exercícios anteriores. Imagine que agora queremos ter um novo tópico mostrando a quantidade de alterações que ocorreram por domínio do email em janelas de 30 segundos, seria algo como consumir todos os eventos de usuários durante 30 segundos, agrupar pelo domínio do evento, e emitir um novo evento com a quantidade de alterações feitas para cada domínio.

Isso e muito mais é possível ser feito de forma relativamente fácil com [Kafka Streams](https://kafka.apache.org/documentation/streams/). Mantemos nossas mensagens em avro, porém dessa vez usamos outro [formato](https://avro.apache.org/docs/current/idl.html) para declarar a [mensagem](sample-app/src/main/avro/EmailDomainChangesCountMessage.avdl) que será enviada para o tópico da janela de eventos por domínio do evento.

Criamos um [consumidor](sample-app/src/main/java/web/core/user/OnUserChanged.java) também para logar as mensagens do novo tópico. 

Por fim, [criamos um stream](sample-app/src/main/java/web/StreamsConfig.java) que consome as mensagens de alteração de usuário e gera o novo tópico. Veja que primeiro mudamos a chave do evento (que era ID do usuário e passou a ser domínio do email), depois agrupamos pela chave (que nesse momento é o domínio), criamos uma janela de 30 segundos, fizemos o count de eventos nessa janela e por fim criamos o objeto avro do evento.

Compile e execute a aplicação:

```console
cd ~/hands-on-microservices/sample-app/

git checkout e12

./gradlew clean build

java -jar build/libs/sample-app-0.0.16-SNAPSHOT.jar
```

Agora gere novos usuários, repitindo o domínio deles:

```console
curl localhost:30001/users/random?emailDomain=gmail
```

Veja nos logs da própria aplicação os eventos:

```console
web.core.user.OnUserChanged : window count, message={"emailDomain": "gmail.com.com", "startTime": 2021-04-15T05:13:30Z, "endTime": 2021-04-15T05:14:00Z, "count": 2}
```