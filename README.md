# Exercicio 4

### configurando um baco

Queremos conectar agora a um banco de dados

Vamos subir um mysql: https://hub.docker.com/_/mysql

```
docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51
```

Se não tiver, baixe um client sql: https://dbeaver.io/ e conecte no banco

### docker network

Como subir o container conectando no outro container?

Inicie dois containers:

```
docker run --rm -d --name c1 httpd

docker run --rm -d --name c2 httpd
```

Veja que eles não se comunicam:

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
