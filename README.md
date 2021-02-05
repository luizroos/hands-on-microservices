# Exercicio 1 - criando uma imagem + comandos básicos

### Clonando a aplicação

Dentro da vm, faça clone do repositório e checkout desse branch:

```
git clone https://github.com/luizroos/sample-app.git
cd sample-app
git checkout e1
```
### Aplicação

Veja o código da aplicação, uma aplicação java usando [spring boot](https://spring.io/projects/spring-boot) com [gradle](https://gradle.org/). Está é uma aplicação muito simples e ela será usada para demonstrar alguns conceitos de micro serviços. 

Caso se interesse e queira criar suas próprias aplicações com java e spring, use https://start.spring.io/ para criar o esqueleto do projeto.

#### Compilando e executando a aplicação

Para compilar a aplicação, execute o seguinte comando:

```
./gradlew build
```

Para ver mais tasks existentes no gradle, execute:

```
./gradlew tasks
```

Nesse momento, temos a aplicação compilada, veja o jar (como se fosse um executavel) em:

```
ls build/libs
```
Podemos executar a aplicação dentro da vm:

```
java -jar build/libs/sample-app-0.0.1-SNAPSHOT.jar
```

Acesse http://172.0.2.32:30001/hello no seu browser

Quando subimos a vm, dissemos que seu ip é 172.0.2.32 e a aplicação está subindo na porta 30001.

Para interromper a aplicação, precione control C

#### executando a aplicação com docker

O docker é muito bem documentado, veja https://docs.docker.com/. Mesmo usando sua ferramenta de client, para qualquer comando você pode ver as opções, por exemplo veja opções para o build

```
docker build --help
```

Nossa aplicação já está compilada (é o arquivo jar gerado). Quando queremos gerar uma imagem docker, temos um arquivo Dockerfile, veja o arquivo DOckerfile dessa aplicação:

```
vim Dockerfile
```

Vamos gerar uma imagem da nossa aplicação, tagueando-a como **user/sample-app:1**

```
docker build --build-arg JAR_FILE=build/libs/\*.jar -t user/sample-app:1 .
```

Veja que o docker vai baixar varias imagens intermediárias, já que nossa imagem deriva de uma imagem tagueada como **openjdk:11**

Agora que geramos a imagem, podemos iniciar um container dessa imagem:

```
docker run user/sample-app:1
```

O container iniciou, exibindo os logs da aplicação, se pressionarmos controle C, ele para de executar, mas fazemos para executar o serviço em segundo plano:

```
docker run -d user/sample-app:1
```

Agora nossa aplicação está rodando e temos liberdade no console para executar outros comandos. Liste os containers que estão executando

```
docker ps
```

Após, execute novamente a listagem passando a opção -a (docker ps --help para mais opções)

```
docker ps -a
```

Existem dois containers, um parado e um rodando. Por que?

Removendo um container

```
docker rm {container_id}
```

#### Acessando sua aplicação

Tente acessar no seu browser http://172.0.2.32:30001/hello, por que não funciona como antes?

Verifique detalhes do container que está executando:

```
docker inspect {container_id}
```
Procure pelo **IPAddress** e execute:

```
curl http://{container_ip}:30001/hello
```

Mas como eu acesso de fora da vm?

Executando a aplicação expondo a porta definida em EXPOSE do Dockerfile

```
docker run -P -d user/sample-app:1
```

Isso vai mapear uma porta do host (a vm) e o container. Verifique a porta mapeada no host com:

```
docker ps
```

E agora sim, acesse no seu browser:

Veja a porta que foi exposta e acesse http://172.0.2.32:{PORT}/hello

#### Customizado o container

Muitas imagens ou aplicações podem ter um comportamento customizado a partir de uma variável de ambiente. Podemos customizar váriaveis de ambiente do nosso container passando o parâmetro -e (ou --env, veja mais opções com docker run --help)

```
docker run -P -d -e HELLO_MESSAGE=ola user/sample-app:1
```

#### Parando e reexecutando um container

Para parar um container, utilize o comando **stop**:

```
docker stop {container_id}
```

Você pode executar varias ações em vários containers, usando docker ps -aq, por exemplo, parando todos containers:

```
docker stop $(docker ps -aq)
```

Veja a lista de todos containers criados:

```
docker ps -a
```
Para iniciar o container novamente, utilize o comando **start**:

```
docker start {container_id}
```

Ao invés de fazer stop/start, você pode executar também o comando de **restart**:

```
docker restart {container_id}
```

#### Nomeando containers

Ficar executando operações com o id do container, pode ser dificil. Toda vez que você inicia um container, ele ganha um id e um nome aleatórios. Todas operações que são executadas passando o id do container, podem ser executadas passando o nome.

Para iniciar um container dando um nome, use o parâmetro  **--name** quando for rodar seu container:

```
docker run -P -d -e HELLO_MESSAGE=ola --name sample-app user/sample-app:1

docker ps

docker stop sample-app

docker start sample-app

docker inspect sample-app
```

#### Logs

Qualquer texto enviado para STDOUT ou STDERR através do comando **log**:

```
docker logs sample-app
```

Use a opção -f vai para manter um streaming do log.

#### Logando no container

Caso queira ver detalhes de algo dentro do container, você pode "se logar" dentro dele, execute:

```
docker exec -it sample-app /bin/bash
```

O comando **exec** permite que você execute comandos dentro do container, pode ser qualquer coisa, veja mais opções com docker exec --help

