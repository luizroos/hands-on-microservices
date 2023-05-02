# Instalando terraform

Você pode seguir as intruções de instação mais detalhadas no [site](https://developer.hashicorp.com/terraform/tutorials/aws-get-started/install-cli) do projeto as instruções.

## Windows

1. Baixe o instalador do Terraform no site oficial: [https://www.terraform.io/downloads.html](https://www.terraform.io/downloads.html)
2. Extraia o arquivo ZIP baixado para uma pasta de sua escolha.
3. Adicione o caminho completo do executável do Terraform (terraform.exe) ao PATH do sistema.

## Linux

Abra o terminal e execute o seguinte comando para baixar o pacote do Terraform:

```
wget https://releases.hashicorp.com/terraform/1.0.9/terraform_1.0.9_linux_amd64.zip
```

Descompacte o arquivo ZIP baixado:

```
unzip terraform_1.0.9_linux_amd64.zip
```

Mova o executável do Terraform para uma pasta no PATH do sistema:

```
sudo mv terraform /usr/local/bin/
```