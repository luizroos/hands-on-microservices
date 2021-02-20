# hands-on-microservices

Os exercícios estão cada um branch separado e que devem ser feitos na **ordem**, o ideal é fazer em aula, junto com a explicação:

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

### Subindo o ambiente

Instale [vagrant](https://www.vagrantup.com/) para subir uma vm com setup feito ([instalação de vagrant no windows](https://nandovieira.com.br/usando-o-vagrant-como-ambiente-de-desenvolvimento-no-windows)): docker, java, git, apache bench, etc já instalados, será mais facil executar cada execício.

Uma vez o vagrant instalado, entre no diretório **vm** e execute:

```
vagrant up
```

Aguarde a instalação e configuração da vm e então logue nela:

```
vagrant ssh
```

Para sair da vm, execute

```
exit
```

Para remover a vm (dentro do host):

```
vagrant destroy
```

### Rodando um container

Dentro da VM, execute:

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
