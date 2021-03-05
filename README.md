# Exercício 5 - Stateful containers

Faça o seguinte teste com o container MySQL criado no exercicio anterior:

```console
docker exec -it mysql mysql -u db_user -p sample-db -e "select count(1) from user";

docker stop mysql

docker start mysql

docker exec -it mysql mysql -u db_user -p sample-db -e "select count(1) from user";
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) O resultado do select mudou? Por que?

E se fizermos isso:

```console
docker stop mysql

docker rm mysql

docker run -d --rm -p 3306:3306 --net my-net --name mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db mysql:5.6.51 

docker exec -it mysql mysql -u db_user -p sample-db -e "select count(1) from user";
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) O resultado do select mudou? Por que?

### Stateful vs stateless

O conceito de uma aplicação stateful é que ela lembra de ao menos uma coisa da sua última execução, e para lembrar de algo, algum tipo de persistência é necessária, portanto uma aplicação só pode ser stateful se ela tiver algum lugar para armazenar suas informações para poder ler depois. Esse requisito faz com que aplicações stateful escalem menos que aplicações stateless.

E onde os dados são armazenados de forma persistente? Em um disco. O container tem acesso a um disco e pode persistir e ler dados dele, porém esses dados se perdem quando o container é destruido, e o que aconteceria se nós precisássemos executar vários containers da mesma aplicação acessando os mesmos dados? Como por exemplo um servidor web servindo páginas html?

Uma forma simples de tornar a aplicação stateless é delegar a persistência de seu estado para uma outra aplicação: o banco de dados. Porém isso resolve o problema da nossa aplicação e não do sistema como um todo pois o banco ainda assim vai precisar armazenar seus dados em algum lugar

#### Subindo um site simples

Vamos rodar um servidor [HTTP Apache](https://hub.docker.com/_/httpd) (antigo e muito comum na comunidade):

```console
docker run --rm -d --name apache -p 80:80 httpd
```

Acesse no seu browser http://172.0.2.32/

Como mudarmos o html que é exibido? Crie no diretório (pode ser na home da vm), chamado **meu-blog** e junto com um arquivo **index.html** com o conteúdo que deseja. Por exemplo:

```console
mkdir meu-site && echo "<html><body><h1>Meu site</h1></body></html>" >> meu-site/index.html
```

Agora crie um Dockerfile dessa forma:

```dockerfile
FROM httpd:2.4
COPY ./meu-site/ /usr/local/apache2/htdocs/
```

Faça o duild e suba sua imagem 

```console
docker build --tag meu-site .

docker run -p 80:80 --rm --name meu-site meu-site
```

Acesse novamente http://172.0.2.32/

E agora se quisermos alterar o conteúdo do nosso html? Como faríamos? (imagine que podem existir centenas desses containers rodando e um load balance na frente).

Apesar de servir conteúdo, essa aplicação é stateless, ela não persiste nem altera nada, somos nós que alteramos, porém cada alteração do conteúdo que ela serve requer recriar e reiniciar todos containers. Isso normalmente não chega a ser um problema, mas claro que limita a flexibilidade de alteração (imagina a cada novo texto no seu site você ter que reiniciar todos os containers. 

#### Compartilhando diretórios entre containers

Podemos compartilhar diretórios entre o host e o container, execute novamente o servidor apache da seguinte forma (assumindo que você criou o diretório meu-blog na home):

```console
docker run -p 80:80 --rm --name apache -d -v ~/meu-site:/usr/local/apache2/htdocs/  httpd
```

Acesse novamente http://172.0.2.32/

Faça algumas modificações no html, e, sem parar o container, de um reload em http://172.0.2.32/

#### Mantendo os dados salvos do banco

Para não perdemos os dados do nosso banco, temos que salvar ele fora do container, normalmente na documentação da imagem existe a explicação de como fazer isso.

Vamos executar novamente nosso MySQL:

```console
docker run -p 3306:3306 --name mysql --net=my-net -v ~/temp/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51
```

Reiniciamos a aplicação (pois ela quem cria as tabelas):

```console
docker restart sample-app
```

Se tiver removido o container da aplicação:

```console
docker run -d -p 8080:30001 -e MYSQL_HOST=mysql --net my-net --name sample-app sample-app:4
```

Adicione alguns usuários em http://172.0.2.32:8080/swagger-ui.html, verique no banco que eles estão incluidos

```console
docker exec -it mysql mysql -u db_user -p sample-db -e "select * from user";
```

Então remova o container do MySQL novamente, recrie-o com o mesmo volume mapeado, veja que os dados agora serão mantidos.

Como curiosidade, experimente rodar um outro container MySQL apontando para o mesmo dados

```console
docker run --rm -p 3307:3306 --name mysql2 --net=my-net -v ~/temp/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db mysql:5.6.51
```

![#686bd4](https://via.placeholder.com/10/686bd4?text=+) Qual o problema? Isso mostra por que é dificil escalar bancos.
