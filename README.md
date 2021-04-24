# Exercício 4 - comunicação entre containers
[Setup do ambiente](https://github.com/luizroos/hands-on-microservices)

---

A maioria das aplicações tem dependências externas, podemos criar o container da aplicação aproveitando para subir todas suas dependências junto no mesmo container, ou podemos subir containers separados para cada dependência (o que é mais comum).

### Criando um container para o banco de dados

Vamos configurar a aplicação para se conectar a um banco de dados MySQL (ao invés do nossos banco em memória h2). Existem milhares de imagens dockers para as mais diversas aplicações, visite https://hub.docker.com para buscar por imagens.

Aqui vamos usar para banco de dados um container do MySQL, veja em https://hub.docker.com/_/mysql as opções para subir o container e execute:

```console
docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51
```

Subimos um container com nome **mysql**, setando usuário do banco como **db_user** e senha **db_pass**, criando um schema chamado **sample-db** e mapeando a porta default do mysql: **3306**.

Depois, você pode instalar um client SQL para conectar no banco de dados. Se não tiver nenhum, pode usar https://dbeaver.io/. Veja que subimos o banco em um container docker dentro da vm, que tem ip 172.0.2.32, então como o client vai rodar fora da vm, [o host da conexão](dbeaver/conn_conf.png) é o ip da vm (já que mapeamos também uma porta do container para a vm).

Mas agora como faremos para a aplicação conectar no banco? Se tivessemos uma aplicação rodando no nosso computador, usaríamos o ip da vm (da mesma forma que o client SQL rodando na sua máquina), se a aplicação tivesse rodando dentro da vm (sem docker), usaríamos localhost (já que mapeamos uma porta do container para uma porta da vm) ou o ip do do container (pego via docker inspect). Mas queremos que o container da nossa aplicação conecte no container do MySQL, e para isso vamos usar as funcionalidades do docker network.

### Docker network

Vamos esquecer por enquanto nosso container MySQL e fazer o seguinte:

```console
docker run --rm -d --name c1 busybox sleep infinity

docker run --rm -d --name c2 busybox sleep infinity
```

Subimos dois containers da imagem do [busybox](https://hub.docker.com/_/busybox/), é uma pequena imagem de um linux, com algumas comandos já instalados.

O que estamos interessados é que os dois containers (c1 e c2) possam se comunicar entre si, portanto teste o seguinte comando:

```console
docker exec c1 ping c2
```

Qual foi o resultado? O host c2 não é visto pelo container c1.

E se tentarmos dar um ping no ip? 

```console
docker inspect c2

docker exec c1 ping {c2_ip_address}
```

Quando criamos containers, a rede default que o container é inserido se chamada bridge, essa rede faz bridge com a interface docker0 do docker host, é uma rede de escopo local (apenas os containers rodando no mesmo docker daemon conseguem ver). 

Verifique detalhes das redes

```console
docker network ls

docker network inspect bridge
```

Você deve ver, conectado a rede bridge, 3 containers: mysql, c1 e c2. Então os containers se comumicam entre si, mas porque não pelo nome? 
Por default, a rede bridge herda as configurações de DNS do host, então um container acaba não enxergando o hostname do outro. Porém redes criadas pelo usuário são diferentes, elas usam um servidor DNS interno do docker que associa todos os nomes de containers dessa rede para seus respectivos IPs da rede IP.

Vamos criar uma rede chamada **my-net**:

```console
docker network create my-net
```

Veja a sua rede (não tem nenhum container conectado):

```console
docker network inspect my-net
```

Agora vamos conectar nossos dois containers na nossa rede:

```console
docker network connect my-net c1

docker network connect my-net c2 

docker network inspect my-net
```

Veja que agora um container consegue se comunicar com o outro via hostname:

```console
docker exec c1 ping c2
```

Podemos iniciar containers conectados direto já na nossa rede através do parametro **--net**:

```console
docker run --rm -d --net my-net --name c3 busybox sleep infinity 
```

Valide a conectividade entre eles:

```console
docker exec c3 ping c2

docker exec c1 ping c3
...
```

E se quisermos executar o container na rede do host? (ao invés de bridge ou uma do usuário)

```console
docker run --rm -d --net=host httpd

curl http://localhost
```

Veja que não mapeamos nenhuma porta, porque o container está rodando usando a rede do host, então o bind é feito direto na rede do host (tente acessar http://172.0.2.32/), e portando, se tentarmos rodar um novo serviço que faz bind na mesma porta, tomaremos erro, por ex:

```console
docker run --rm --net=host httpd
```

### Conectando a aplicação no banco MySQL

Deixe apenas o container do MySQL rodando

Alteramos a aplicação para se conectar em um banco de dados MySQL. Veja no arquivo [application.properties](sample-app/src/main/resources/application.properties) que a conexão já está configurada para os parâmetros que criamos nosso banco MySQL. O host que a aplicação conecta, por default está localhost, mas pode ser sobrescrito via uma variável de ambiente chamada **MYSQL_HOST**. 

Vamos compilar, gerar a imagem da aplicação e rodar um container dessa versão da aplicação:

```console
cd ~/hands-on-microservices/sample-app/

git checkout e4

./gradlew clean build

docker build --build-arg JAR_FILE=build/libs/*.jar -t sample-app:4 .

docker run --rm -p 8080:30001 --name sample-app sample-app:4
```

Tivemos um problema para conectar no banco. Tente executar:

```console
java -jar build/libs/sample-app-0.0.4-SNAPSHOT.jar
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Ocorreu erro dessa vez? Por que não?

Queremos rodar a aplicação via container, como podemos executar a imagem **sample-app:4** para que ela consiga se conectar no banco de dados MySQL? (existem ao menos 3 formas, sem precisar alterar nada da imagem).

Depois de subir, acesse http://172.0.2.32:8080/swagger-ui.html e inclua alguns usuários, a aplicação vai criar automaticamente as tabelas necessárias (em uma aplicação real, **nunca** de para aplicação um usuário com permissão de DDL, isso é **muito** perigoso, estamos usando aqui só para facilitar), verifique no seu client SQL os usuários inseridos:

```console
docker exec -it mysql mysql -u db_user -p sample-db -e "select * from user";
```
