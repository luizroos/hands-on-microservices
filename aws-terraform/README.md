# hands-on-microservices

Podemos criar o ambiente na AWS usando uma ferramenta chamada terraform

O Terraform é uma ferramenta de código aberto para provisionamento de infraestrutura como código. Com o Terraform, é possível definir e gerenciar a infraestrutura de nuvem de maneira declarativa, o que significa que é possível descrever a infraestrutura que se deseja e o Terraform irá criar e gerenciar essa infraestrutura automaticamente. Com o Terraform, é possível gerenciar recursos em diversas nuvens e provedores de serviços, como AWS, Azure, Google Cloud, entre outros. 

Primeiro passo é, se você não tiver, [criar uma conta](/doctos/aws.md) na AWS

O segundo passo é você [instalar o Terraform](/doctos/terraform.md) no seu computador

## Criando o ambiente de testes

Após criada a conta AWS, de posse das chaves de acesso e com o Terraform instalado, faça o clone desse repositório

```
git clone https://github.com/luizroos/hands-on-microservices
```

Acesse o diretório aws-terraform e execute:

```
terraform apply -var 'aws_access_key=<SUA_ACCESS_KEY>' -var 'aws_secret_key=<SUA_SECRET_KEY>'
```

Aguarde até que a máquina tenha sido gerada e depois conecte via ssh. A chave de acesso está no arquivo aws_aulas.pem.

**Lembre** depois da aula de destruir o ambiente, senão a AWS vai te cobrar pelos recursos. Basta executar:

```
terraform destroy -var 'aws_access_key=<SUA_ACCESS_KEY>' -var 'aws_secret_key=<SUA_SECRET_KEY>'
```
