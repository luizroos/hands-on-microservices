# hands-on-microservices

O Vagrant é uma ferramenta de linha de comando que permite criar e gerenciar ambientes de desenvolvimento virtualizados. Ele automatiza o processo de criação, configuração e provisionamento de máquinas virtuais, facilitando o trabalho de desenvolvedores e equipes de desenvolvimento que precisam criar ambientes de desenvolvimento padronizados e replicáveis. Com o Vagrant, é possível criar máquinas virtuais em diferentes provedores de virtualização, como VirtualBox, VMware e AWS. O Vagrant é especialmente útil para projetos que exigem ambientes de desenvolvimento complexos, como aplicações web e bancos de dados, pois permite criar, compartilhar e gerenciar esses ambientes de forma fácil e eficiente.

O primeiro passo é você instalar [vagrant](https://www.vagrantup.com/) ([instalação de vagrant no windows](https://nandovieira.com.br/usando-o-vagrant-como-ambiente-de-desenvolvimento-no-windows)) e [virtualbox](https://www.virtualbox.org/).

## Criando o ambiente de testes

Uma vez o vagrant instalado, faça o clone desse projeto:

```
git clone https://github.com/luizroos/hands-on-microservices
```

Acesse o diretório local-vagrant e execute:

```
vagrant up
```

Aguarde a instalação e configuração da máquina virtual e então logue nela com:

```
vagrant ssh
```

Para sair da máquina virtual, execute

```
exit
```

Para remover a máquina virtual (dentro do host):

```
vagrant destroy
```

Se não for instalar o vagrant e quiser subir um ubuntu direto no virtualbox, veja o aqruivo [bootstrap.sh](local-vagrant/bootstrap.sh) com os comandos que você precisa executar para instalar todas dependências.