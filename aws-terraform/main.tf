variable "aws_keypair" {
  description = "Informe o nome da sua chave key-pair que você deseja usar para conectar nessa instância"
}

variable "aws_access_key" {
  description = "aws access key"
}

variable "aws_secret_key" {
  description = "aws secret key"
}

# Configura as credenciais do AWS
provider "aws" {
  region     = "us-east-1"
  access_key = var.aws_access_key
  secret_key = var.aws_secret_key
}

resource "aws_security_group" "sg_aula" {
  name_prefix = "sg_aula"

  # ssh
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # app (docker)
  ingress {
    from_port = 8001
    to_port = 8001
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # app (docker)
  ingress {
    from_port = 8080
    to_port = 8080
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # app (direto)
  ingress {
    from_port = 30001
    to_port = 30001
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # kafka control center
  ingress {
    from_port = 9021
    to_port = 9021
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }  

  # rabbit
  ingress {
    from_port = 15672
    to_port = 15672
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }  

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Tags
  tags = {
    Name = "aula"
  }

}

# Cria uma instância EC2
resource "aws_instance" "ec_aula" {
  ami           = "ami-007855ac798b5175e" # ubuntu 22
  instance_type = "t2.medium"
  key_name      = var.aws_keypair
  vpc_security_group_ids = [aws_security_group.sg_aula.id]
  
  # Tags
  tags = {
    Name = "aula"
  }

  # Configura o script de inicialização
  user_data = <<-EOF
              #!/bin/bash
              # Instalando pacotes
              sudo apt-get update
              sudo apt-get install -y docker.io openjdk-11-jdk docker-compose apache2-utils
              
              # Instalando Minikube
              curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 \
                && chmod +x minikube \
                && sudo mv minikube /usr/local/bin/
              
              # Instalando kubectl
              sudo apt-get update && sudo apt-get install -y apt-transport-https gnupg2
              curl -s https://packages.cloud.google.com/apt/doc/apt-key.gpg | sudo apt-key add -
              echo "deb https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee -a /etc/apt/sources.list.d/kubernetes.list
              sudo apt-get update
              sudo apt-get install -y kubectl
              
              # Instalando Bash Completion
              sudo apt-get install -y bash-completion
              
              # Instaçando CCM
              export LC_ALL=C
              sudo apt install -y python2-minimal
              curl https://bootstrap.pypa.io/pip/2.7/get-pip.py --output get-pip.py
              python2 get-pip.py
              sudo pip install cql PyYAML psutil ccm

              # Inclui o usuário ubuntu ao grupo do docker
              sudo usermod -aG docker ubuntu

              # Reinicia o serviço Docker
              sudo systemctl restart docker              
              EOF

}

output "public_dns" {
  value = aws_instance.ec_aula.public_dns
}

output "instance_id" {
  value = aws_instance.ec_aula.id
}

output "ssh_user" {
  value = "ubuntu"
}

output "ssh_command" {
  value = "ssh -i <arquivo_da_sua_chave> ubuntu@${aws_instance.ec_aula.public_dns}"
}