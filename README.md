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

### Instalando o ambiente


#### Local com vagrant e virtual box 

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

Se não for instalar o vagrant local, e for subir uma vm na mão, seja local ou em alguma cloud, sugiro subir um ubuntu e veja o aqruivo [bootstrap.sh](vm/bootstrap.sh) os comandos para instalação das dependências.

#### Amazon

Acesse o [console da aws](https://console.aws.amazon.com/ec2), se não tiver um usuário na aws, crie um novo (usuários novos tem 12 meses com 750h de alguns EC2 grátis). 

Ao abrir o console, mude o idioma para português se preferir (lá embaixo na esqueda). Você deve estar vendo [isso](amazon/p1.png), no meio dessa tela, clique em **executar instância**, você deve ver uma [lista de imagens](amazon/p2.png), selecione a imagem **Ubuntu Server 18.04 LTS** (a instalação do ccm usa python e na 20 só tem python3), para achar ela mais fácil, pode selecionar no painel da direita o check de somente nível gratuito.  

Depois de selecionar a imagem, você deve escolher o [tipo da instância]((amazon/p3.png)), escolha **t2.micro** (ou aquela mais alta que ainda esteja no nível gratuito). Pode então clicar em verificar e ativar, verifique [se está tudo certo](amazon/p4.png) e então **executar**. 

Vai aparecer uma [janela](amazon/p5.png) para gerar chaves de acesso a sua instância. Selecionar **criar novo par de chaves** e digite um nome para ela, clique **fazer download do par de chaves**, salve esse arquivo, então será habilitado o botão **executar instâncias**, clique nele (se você for criar novas instâncias depois, pode usar a mesma chave criada agora).

Pronto, sua instância já está [subindo](amazon/p6.png), clique em **exibir instâncias**, você verá as [instâncias](amazon/p7.png) que tem rodando.

Clique nos [detalhes](amazon/p8.png) da sua instância, lá você vai ver o **DNS IPv4 público**, por ele que você vai acessar os serviços que os exercícios pedem para acessar pelo browser (esse endereço substitui é o respectivo do 172.0.2.32 que é o IP que a VM sobe quando usamos vagrant). Clique em conectar (no menu lá no alto a direita), vai ser então apresentado [formas de conectar](amazon/p9.png) a sua instância. 

Uma vez conectado, execute os comandos a seguir:

```
git clone https://github.com/luizroos/hands-on-microservices.git

chmod +x hands-on-microservices/amazon/bootstrap.sh

./hands-on-microservices/amazon/bootstrap.sh

```

Aguarda a instalação de tudo

Depois de usar, **não esqueça** de dar um stop na sua instância, vá nos detalhes da instância, no menu selecione **estado da instância** e então **interromper instância** (no outro dia basta iniciar ela novamente).

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
