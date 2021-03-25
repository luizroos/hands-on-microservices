# baixa e compila o connector para outbox
git clone https://github.com/luizroos/kconnect-jdbc-outbox-smt

cd kconnect-jdbc-outbox-smt; ./gradlew clean build; cd ..

cp ./kconnect-jdbc-outbox-smt/build/libs/kconnect-jdbc-outbox-smt*.jar .

# gera a imagem
docker build -t local/kafka-mysql-connect .

# exclui o que baixou
rm -rf kconnect-jdbc-outbox-smt
rm  kconnect-jdbc-outbox-smt*.jar 