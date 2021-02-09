## instalação do docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker vagrant

## instalação docker compose
sudo curl -L "https://github.com/docker/compose/releases/download/1.28.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

## instala apache bench
sudo apt-get install -y apache2-utils

## instalação do java
curl -O https://download.java.net/java/GA/jdk11/9/GPL/openjdk-11.0.2_linux-x64_bin.tar.gz
tar zxvf openjdk-11.0.2_linux-x64_bin.tar.gz
sudo mv jdk-11* /usr/local/
sudo echo "JAVA_HOME=/usr/local/jdk-11.0.2" >> /etc/profile.d/jdk.sh
sudo echo "PATH=\$PATH:\$JAVA_HOME/bin" >> /etc/profile.d/jdk.sh

# modifica .bashrc
cat /home/vagrant/vm_conf_files/bashrc >> /home/vagrant/.bashrc

# instala minikube
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube_latest_amd64.deb
sudo dpkg -i minikube_latest_amd64.deb

# instala o kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# instala bash completion
sudo apt-get install bash-completion

# kubectl autocompletion
echo 'source <(kubectl completion bash)' >>~/.bashrc
kubectl completion bash >/etc/bash_completion.d/kubectl
