# Exercicio 5 - compartilhando diretórios

Remova o container do banco

```
docker stop mysql

docker rm mysql
```

Inicie novamente:

```
docker run -p 3306:3306 --name mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51
```

No client do banco, verifica os dados previamente inseridos. Perdemos os dados? por que? Como manter?

```
docker run --name apache -d -v ~/:/host_dir  httpd

docker exec -it apache /bin/bash

ls /host_dir/
```

Crie um arquivo no host e veja ele dentro do container (vice versa)

```
docker stop mysql

docker rm mysql

docker run -p 3306:3306 --name mysql --net=my-net -v ~/temp/mysql-data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=rootpass -e MYSQL_USER=db_user -e MYSQL_PASSWORD=db_pass -e MYSQL_DATABASE=sample-db -d mysql:5.6.51
```

Reiniciamos a aplicação, pois ela quem cria as tabelas

```
docker restart sample-app
```

Remova o banco de novo, recrie com o mesmo volume e veja seus dados mantidos

Veja mais:

https://docs.docker.com/storage/volumes/