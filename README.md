# Exercício 1 - criando uma imagem + comandos básicos

### Clonando o repositório

Dentro da vm, faça clone do repositório e checkout desse branch:

```console
git clone https://github.com/luizroos/hands-on-microservices.git

cd hands-on-microservices

git checkout e1
```

Caso esteja rodando dentro de uma instância na aws, você já deve ter feito o git clone antes.

### Aplicação sample-app

Veja o código da aplicação (se quiser, configure alguma IDE, as mais comuns para java são [eclipse](https://www.eclipse.org/) ou [intellj](https://www.jetbrains.com/pt-br/idea/)). 
Vamos chamar de **sample-app**, é uma aplicação java usando [spring boot](https://spring.io/projects/spring-boot) com [gradle](https://gradle.org/). Está é uma aplicação muito simples e ela será usada para demonstrar alguns conceitos de micro serviços. 

Caso se interesse e queira criar suas próprias aplicações com java e spring, use https://start.spring.io/ para criar o esqueleto do projeto.

#### Compilando e executando a aplicação

Para compilar a aplicação, dentro do diretório sample-app execute o comando:

```console
./gradlew clean build
```

Isso vai baixar o gradle, compilar e gerar o executável (jar) da aplicação. Para ver mais tasks existentes no gradle, execute:

```console
./gradlew tasks
```

Agora que temos a aplicação compilada, poemos podemos executar a aplicação dentro da vm:

```console
java -jar build/libs/sample-app-0.0.1-SNAPSHOT.jar
```

Acesse http://172.0.2.32:30001/hello no seu browser

Quando subimos a vm, demos o ip 172.0.2.32, a aplicação está subindo na porta 30001. 

Para interromper a aplicação, precione CTRL + C

#### Executando a aplicação com docker

O docker é muito bem documentado, veja https://docs.docker.com/, mesmo usando sua ferramenta client, para qualquer comando você pode ver quais opções exitem, por exemplo veja opções para o build

```console
docker build --help
```

Nossa aplicação já está compilada (é o arquivo jar gerado). Quando queremos gerar uma imagem docker, temos um arquivo [Dockerfile](sample-app/Dockerfile), de uma olhada nesse arquivo. 

Vamos gerar uma imagem da nossa aplicação, tagueando-a como **sample-app:1**

```console
docker build --build-arg JAR_FILE=build/libs/\*.jar -t sample-app:1 .
```

Quando criamos imagens, essas imagens herdam outras imagens, e assim vai se criando uma hierarquia de imagens. Quando executamos o comando acima, o docker baixou várias imagens intermediárias para poder criar a nossa, já que nossa imagem herda de uma imagem chamada **openjdk:11**

Agora que geramos nossa imagem, podemos rodar um container dela:

```console
docker run sample-app:1
```

O container iniciou, exibindo os logs da aplicação, se pressionarmos  CTRL + C, ele para de executar. Em um ambiente real não é assim, a aplicação deve rodar em segundo plano, para isso execute dessa vez da seguinte forma:

```console
docker run -d sample-app:1
```

Agora nossa aplicação está rodando em segundo plano. Vamos listar os containers que estão executando:

```console
docker ps
```

Após, execute novamente a lista de containers opção -a (significa **all**, veja mais opções com **docker ps --help**)

```console
docker ps -a
```

Existem dois containers, um parado e um rodando. Por que?

Remova com o comando **rm** o container que não está rodando:

```console
docker rm {container_id}
```

Ou

```console
docker rm $(docker ps -q --filter "status=exited")
```

#### Acessando a aplicação

Tente acessar no seu browser http://172.0.2.32:30001/hello, por que não funciona como antes?

Verifique detalhes do container que está executando:

```console
docker inspect {container_id}
```
Procure pelo **IPAddress** e execute:

```console
curl -i {container_ip}:30001/hello
```

Mas como eu acesso de fora da vm?

Vamos rodar um novo container, expondo a porta definida em **EXPOSE** do Dockerfile

```console
docker run -P -d sample-app:1
```

Isso vai mapear uma porta do host (a vm) e o container. Verifique a porta mapeada no host com:

```console
docker ps
```

E agora sim, acesse no seu browser:

Veja a porta que foi exposta e acesse http://172.0.2.32:{PORTA_EXPOSTA}/hello

#### Customizado o container

Muitas imagens ou aplicações podem ter um comportamento customizado a partir de variáveis de ambiente. Podemos informar de váriaveis de ambiente para o container passando o parâmetro **-e** (ou --env, veja mais opções com **docker run --help**)

```console
docker run -P -d -e HELLO_MESSAGE=ola sample-app:1
```

Veja a porta exposta desse novo container e acesse novamente http://172.0.2.32:{PORTA_EXPOSTA}/hello

#### Parando e reexecutando um container

Para parar um container, utilize o comando **stop**:

```console
docker stop {container_id}
```

Você pode executar varias ações em vários containers, usando docker ps -aq, por exemplo, parando todos containers:

```console
docker stop $(docker ps -aq)
```

Veja a lista de todos containers criados:

```console
docker ps -a
```

Para iniciar o container novamente, utilize o comando **start**:

```console
docker start {container_id}
```

E ao invés de fazer stop/start, você pode executar também o comando de **restart**:

```console
docker restart {container_id}
```

#### Nomeando containers

Ficar executando operações com o id do container é chato. Toda vez que você cria um container, ele ganha um id e nome aleatórios. Todas operações que são executadas passando o id do container, podem ser executadas passando o nome.

Para iniciar um container dando um nome, use o parâmetro  **--name** quando for rodar seu container:

```console
docker run -P -d -e HELLO_MESSAGE=ola --name sample-app sample-app:1

docker ps

docker stop sample-app

docker start sample-app

docker inspect sample-app
```

#### Logs

Visualize qualquer texto enviado para STDOUT ou STDERR através do comando **log**:

```console
docker logs sample-app
```

Use a opção -f vai para manter aberto o streaming do log.

#### Executando comandos no container

É muito comum no desenvolvimento ou na investigação de algum problema, quereremos executar comandos dentro do container rodando. Para isso você pode usar o comando exec:

```console
docker exec sample-app ls
```

Você também pode abrir um shell na imagem e executar vários comandos:

```console
docker exec -it sample-app /bin/bash
```
