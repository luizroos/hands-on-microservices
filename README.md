# Exercicio 4 - conectando containers

A maioria das aplicações tem dependências externas, podemos criar o container da aplicação aproveitando para subir todas suas dependências junto no mesmo container, ou podemos subir containers separados para cada dependência (o que é mais comum).

### Criando um container para o banco de dados

Vamos configurar a aplicação para se conectar a um banco de dados MySQL (ao invés do nossos banco em memória). Existem milhares de imagens dockers para as mais diversas aplicações, visite https://hub.docker.com para buscar por imagens.

Verifique as opções para subir um container com MySQL: https://hub.docker.com/_/mysql

```
docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51
```

Aqui subimos um container com nome **mysql**, setando usuário do banco como **db_user** e senha **db_pass**, criando um schema chamado **sample-db** e fazendo bind da porta default do mysql: 3306.

Configure algum client SQL para conectar no banco de dados. Se não tiver nenhum, pode usar https://dbeaver.io/. Veja que subimos o banco em um container docker dentro da vm, que tem ip 172.0.2.32, então como o client vai rodar na sua máquina, [o host da conexão](dbeaver/conn_conf.png) é o ip da vm (já que mapeamos também uma porta do container para a vm).

Mas agora como fazemos a aplicação conectar no banco? Se tivessemos uma aplicação rodando no nosso computador, usariamos o ip da vm (da mesma forma que o client SQL), se a aplicação tivesse rodando dentro da vm (sem docker), usáriamos localhost (já que mapeamos uma porta do container para a vm) ou o ip do do container. Mas queremos na verdade que o container da nossa aplicação conecte no container do MySQL, e para isso vamos usar as funcionálidades do docker networking.

### Docker network

Vamos esquecer por enquanto nosso container MySQL e fazer o seguinte:

```
docker run --rm -p 9001:80 -d --name c1 httpd

docker run --rm -p 9002:80 -d --name c2 httpd
```

Subimos dois containers da imagem do [Apache HTTP Server](https://hub.docker.com/_/httpd), o serviço em si não nos interessa, mas verifique que eles estão up e respondendo:

```
docker ps

curl http://localhost:9001

curl http://localhost:9002
```

O que estamos interessados é que os dois containers (c1 e c2) possam se comunicar entre si, portanto teste o seguinte comando:

```
docker exec -it c1 ping c2
```

Crie uma rede

```
docker network create my-net
```

Veja a sua rede (não tem nenhum container conectado)

```
docker network inspect my-net
```

Conecte os dois containers na rede

```
docker network connect my-net c1
docker network connect my-net c2 
```

Veja que agora existe containers conectados a rede

```
docker network inspect my-net
```

Confirme que agora eles estão se comunicando

```
docker exec -it c1 ping c2
```

Iniciando outro container já direto na rede:

```
docker run --rm -d --name c3 --net=my-net httpd
```

Logue em um container e verifique que um pode chamar o servico do outro:

```
docker exec -it c2 /bin/bash

ping c3
```

Rodando um container na rede do host:

```
docker run --rm -d --net=host httpd

curl http://localhost
```

Veja o erro se tentar subir um novo:

```
docker run --rm --net=host httpd
```

### Conectando a app no banco

Compile a aplicação

```
./gradlew build
```

Gere a imagem

```
docker build --build-arg JAR_FILE=build/libs/*.jar -t user/sample-app:4 .
```

Suba a aplicação

```
docker run -p 8080:30001 --name sample-app user/sample-app:4
```

Veja o erro de conexão com o banco. Veja em application.properties, é possivel informar o host via variavel de ambiente MYSQL_HOST

```
docker run -p 8080:30001 -e MYSQL_HOST=mysql --name sample-app user/sample-app:4
```

Ainda deu erro, o que podemos fazer?

```
docker start sample-app

docker logs -f sample-app
```
