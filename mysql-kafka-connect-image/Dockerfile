#FROM confluentinc/cp-kafka-connect
# https://docs.confluent.io/current/connect/managing/extending.html
FROM confluentinc/cp-kafka-connect-base

RUN   confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.0.2 \
   && confluent-hub install --no-prompt confluentinc/connect-transforms:1.4.0 \
   && confluent-hub install --no-prompt debezium/debezium-connector-mysql:1.4.0 
   
RUN curl -sk -SL "http://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.49.tar.gz" \ 
	 | tar -xzf - -C /usr/share/confluent-hub-components/confluentinc-kafka-connect-jdbc/lib/

COPY kconnect-jdbc-outbox-smt-*.jar /usr/share/java/

