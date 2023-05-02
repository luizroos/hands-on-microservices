# hands-on-microservices

Os exercícios estão cada um em um branch separado e que devem ser feitos na **ordem**, o ideal é fazer em aula, junto com a explicação:

[exercício 1](https://github.com/luizroos/hands-on-microservices/tree/e1)

[exercício 2](https://github.com/luizroos/hands-on-microservices/tree/e2)

[exercício 3](https://github.com/luizroos/hands-on-microservices/tree/e3)

[exercício 4](https://github.com/luizroos/hands-on-microservices/tree/e4)

[exercício 5](https://github.com/luizroos/hands-on-microservices/tree/e5)

[exercício 6](https://github.com/luizroos/hands-on-microservices/tree/e6)

[exercício 7](https://github.com/luizroos/hands-on-microservices/tree/e7)

[exercício 8](https://github.com/luizroos/hands-on-microservices/tree/e8)

[exercício 9](https://github.com/luizroos/hands-on-microservices/tree/e9)

[exercício 10](https://github.com/luizroos/hands-on-microservices/tree/e10)

[exercício 11](https://github.com/luizroos/hands-on-microservices/tree/e11)

[exercício 12](https://github.com/luizroos/hands-on-microservices/tree/e12)

[exercício 13](https://github.com/luizroos/hands-on-microservices/tree/e13)

[exercício 14](https://github.com/luizroos/hands-on-microservices/tree/e14)

[exercício 15](https://github.com/luizroos/hands-on-microservices/tree/e15)

[exercício 16](https://github.com/luizroos/hands-on-microservices/tree/e16)

## Instalando o ambiente
---

Nos exercícios, vamos usar [java](https://www.java.com/pt-BR/), [docker](https://www.docker.com/), [git](https://git-scm.com/), [apache bench](https://httpd.apache.org/docs/2.4/programs/ab.html), [ccm](https://www.datastax.com/blog/ccm-development-tool-creating-local-cassandra-clusters), [kubernetes](https://kubernetes.io/pt/), etc. Para isso vamos subir um [ubuntu](https://ubuntu.com) e realizar nossos testes nele.

### Local com vagrant e virtual box 

Esse é o jeito mais barato e que eu sugiro para desenvolvedores. Vamos subir uma máquina virtual no seu computador usando uma ferramenta chamada vagrant. Verique os passos em [local-vagrant](local-vagrant/README.md) para mais detalhes. 

### Local importando a imagem direto na máquina virtual

Essa opção não precisa instalar o vagrant, já vai importar para virtual box a imagem pronta para uso.

Instale a [virtualbox](https://www.virtualbox.org/), acesse o diretório https://drive.google.com/drive/u/0/folders/1DCq3_ufUVXUHXYSd3MZhCqnZi2NmTCYT e siga as instruções de https://docs.google.com/document/d/1gVOlSwZKuPcMl5v2zVbvsRZ5dfyi4sv0C970EfW6VKQ/edit?usp=sharing

### Usando terraform, criando uma instância EC2 na AWS

O jeito mais fácil de criar uma instância EC2 na AWS é usando alguma ferramenta para automatizar esse processo. Verique os passos em [aws-terraform](aws-terraform/README.md) para mais detalhes. 

## Rodando o primeiro container

Dentro da vm, execute:

```
docker pull alpine
```

Isso vai baixar uma imagem chamada [alpine](https://hub.docker.com/_/alpine) do Docker Registry e salva-la no nosso sistema. Você pode ver as imagens salvas com o comando:

```
docker images
```

Agora vamos executar um container baseado nessa imagem:

```
docker run alpine ls -l
```

Esse comando criou um container a partir da imagem alpine, e executou o comando 'ls -l' dentro do container, mostrando o output do comando para o client docker. Agora tente:

```
docker run -it alpine /bin/sh
```

## Clonando o repositório da aplicação
---

Dentro da máquina virtual, faça clone do repositório e checkout desse branch:

```console
git clone https://github.com/luizroos/hands-on-microservices.git
```

Caso esteja rodando dentro de uma instância na aws, você já deve ter feito o git clone antes.
