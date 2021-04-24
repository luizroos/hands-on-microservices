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
-----

Nos exercícios, vamos usar [java](https://www.java.com/pt-BR/), [docker](https://www.docker.com/), [git](https://git-scm.com/), [apache bench](https://httpd.apache.org/docs/2.4/programs/ab.html), [ccm](https://www.datastax.com/blog/ccm-development-tool-creating-local-cassandra-clusters), [kubernetes](https://kubernetes.io/pt/), etc. Para isso vamos subir um [ubuntu](https://ubuntu.com) e realizar nossos testes nele.

### Local com vagrant e virtual box 
-----

Sugiro usar esse modelo, é um modelo que você pode usar até para seu ambiente local de desenvolvimento.

Instale [vagrant](https://www.vagrantup.com/) ([instalação de vagrant no windows](https://nandovieira.com.br/usando-o-vagrant-como-ambiente-de-desenvolvimento-no-windows)) e [virtualbox](https://www.virtualbox.org/).

Uma vez o vagrant instalado, entre no diretório **vm** (ou copie os arquivos do diretório vm desse repositório) e execute:

```
vagrant up
```

Aguarde a instalação, configuração da vm e então logue nela com:

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

Se não for instalar o vagrant e quiser subir um ubuntu direto no virtualbox, veja o aqruivo [bootstrap.sh](vm/bootstrap.sh) com os comandos que você precisa executar para instalar todas dependências.

### Através de uma instância EC2 na Amazon
-----

Acesse o [console da aws](https://console.aws.amazon.com/ec2), se não tiver um usuário na aws, crie um novo (usuários novos tem 12 meses com 750h de alguns tipos de instância EC2 grátis). 

Ao abrir o console, mude o idioma para português se preferir (lá embaixo na esqueda). Você deve estar vendo [o painel EC2](amazon/p1.png), no meio dessa tela, clique em **executar instância**. 

Sera direcionado para a primeira etapa onde devem [selecionar a imagem](amazon/p2.png) que será usada na sua instância EC2, selecione **Ubuntu Server 18.04 LTS** (a instalação do ccm, que vamos usar para um exercício, usa python e na imagem do ubuntu 20 só tem python3). Para achar a imagem mais fácil, pode selecionar no painel da direita o check de somente nível gratuito.  

Depois de selecionar a imagem, você deve escolher o [tipo da instância]((amazon/p3.png)), escolha **t2.micro** (ou aquela mais alta que ainda esteja no nível gratuito). Pode então clicar em verificar e ativar, verifique [se está tudo certo](amazon/p4.png) e então **executar**.  Um detalhe, teremos exercicios que vamos subir bastante dependências, que podem consumir bastante memoria, t2.micro pode não conseguir rodar tudo que precisa, então se puder, crie também uma instância **t2.medium** para esses exercícios (apesar dessa instância não fazer parte do nível gratuito, qualquer coisa, crie no google cloud, usando os créditos de $ 90 que o google da para novos usuários).

Vai aparecer uma [janela](amazon/p5.png) para gerar chaves de acesso à sua instância. Selecione **criar novo par de chaves** e digite um nome, clique **fazer download do par de chaves**, salve esse arquivo, então será habilitado o botão **executar instâncias**, clique nele (se você for criar novas instâncias depois, pode usar a mesma chave criada agora).

Pronto, sua instância já está [subindo](amazon/p6.png), clique em **exibir instâncias**, você verá as [instâncias](amazon/p7.png) que tem rodando.

Clique nos [detalhes](amazon/p8.png) da instância, veja o **DNS IPv4 público**, por ele que você vai acessar os serviços que os exercícios pedem para acessar pelo browser (use **http**, esse endereço substitui o 172.0.2.32 que usamos quando usamos vagrant). 

Ainda nos detalhes, clique em **segurança** (segunda aba do menu abaixo do resumo), e então **grupos de segurança**. Você deve ver uma [tela com os detalhes das regras](amazon/p10.png) de entrada da sua instância. Provavelmente vai estar habilitada apenas a porta 22 (ssh). Nós vamos executar vários serviços que rodam em portas distintas, para não precisar ficar abrindo uma a uma, clique em **editar regras de entrada** , em sequida **adicionar regra**, escolha tipo **TCP personalizado**, com intervalo de portas **1000-65000** e origem **qualquer lugar**, [dessa forma](amazon/p11.png) (nunca faça isso em um ambiente produtivo, acabamos de deixar aberto praticamente todas as portas da instância). Clique em salvar regras e então volte aos detalhes da instância.

De volta aos detalhes da instância, agora clique em conectar (no menu lá no alto a direita), será apresentado [formas de conectar](amazon/p9.png) a sua instância, tente conectar (use putty ou outro client ssh). 

Uma vez conectado via ssh na instância, execute os comandos a seguir na home do usuário:

```
git clone https://github.com/luizroos/hands-on-microservices.git

chmod +x hands-on-microservices/amazon/bootstrap.sh

./hands-on-microservices/amazon/bootstrap.sh

```

Aguarde a instalação de tudo e pronto, pode seguir para executar os containers.

**IMPORTANTE**: Depois de usar, **não esqueça** de dar um stop na sua instância, vá nos detalhes da instância, no menu selecione **estado da instância** e então **interromper instância** (no outro dia basta iniciar ela novamente).

### Através de uma instância de VM do Google Cloud
-----

Acesse o [console do google cloud](https://console.cloud.google.com/cloud-resource-manager). Crie uma conta do google ou, se já tiver, adicione o produto google cloud a sua conta. Vocẽ vai precisar ativar o faturamento na conta, o google cloud promete $ 90 em créditos para usar em 3 meses. 
 
Na tela de [gestão de recursos](gcloud/p1.png), clique em **criar um projeto**. Preencha o nome e [salve](gcloud/p2.png).

Com o projeto criado selecionado, selecione no menu: [Rede VPC > Firewall](gcloud/p3.png). Aqui vamos criar uma regra que permite acesso a todas as portas. Na [listagem de regras](gcloud/p4.png), selecione **criar regra de firewall**. Na crição das regras, preencha o [nome](gcloud/p5.png) com valor **allow-all**, tags de destino com valor **allow-all**, intervalos de IP de origem com valor **0.0.0.0/0** e em [protocolos e portas](gcloud/p6.png), deixe **permitir todos**. Crie a regra. 

Com a regra criada, volta ao menu e selecione [Compute Engine > Instâncias de VM](gcloud/p7.png) para acessar a tela de [instâncias de VM](gcloud/p8.png). Selecione criar. [Preencha](gcloud/p9.png) o nome da instância, o tipo de máquina **e2-medium** e a imagem [Ubuntu 18.04 TS](gcloud/p10.png). Mais abaixo na [tela](gcloud/p11.png) preencha tag de rede com valor **allow-all**  e mande criar a instância.

Espere a instância ficar pronta, na [tabela](gcloud/p12.png), clique em **SSH** e selecione **abrir na janela do navegador**. Caso queira acessar a partir de um client SSH, selecione **utilizar outro cliente ssh** e siga as instruções (você vai precisar gerar uma chave, usando **ssh-keygen** por exemplo, onde vai gerar uma chave publica e uma privada, pegue a publica, acesse os detalhes da instância, busque por **chaves SSH** e preencha a chave pública, então use seu cliente com a chave privada para fazer o acesso através do IP externo da instância).

Nos detalhes da instância, procure pela tabela de **interfaces de rede**, copie o IP externo, esse IP você vai usar para acessar os serviços pelo browser (no lugar do 172.0.2.32 que é usado com vagrant)

Uma vez conectado via ssh na instância, execute os comandos a seguir na home do usuário:

```
git clone https://github.com/luizroos/hands-on-microservices.git

chmod +x hands-on-microservices/gcloud/bootstrap.sh

./hands-on-microservices/gcloud/bootstrap.sh

```

**IMPORTANTE**: Depois de usar, **não esqueça** de dar um stop na sua instância, selecione **interromper** no menu '...' da tabela de instâncias.

## Rodando o primeiro container
-----

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

### Clonando o repositório da aplicação

Dentro da máquina virtual, faça clone do repositório e checkout desse branch:

```console
git clone https://github.com/luizroos/hands-on-microservices.git

cd hands-on-microservices

git checkout e1
```

Caso esteja rodando dentro de uma instância na aws, você já deve ter feito o git clone antes.
