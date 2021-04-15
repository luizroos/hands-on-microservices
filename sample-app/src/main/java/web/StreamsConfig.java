package web;

import java.time.Duration;
import java.util.function.Function;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;

import web.core.user.pub.EmailDomainChangesCountMessage;
import web.core.user.pub.UserChangedMessage;

public class StreamsConfig {

	@Bean
	public Function<KStream<String, UserChangedMessage>, KStream<String, EmailDomainChangesCountMessage>> emailDomainCount() {
		return input -> input//
				// troca chave para o dominio do email
				.map((key, value) -> KeyValue.pair(getEmailDomain(value), value))//
				// agrupa pelo dominio
				.groupByKey(Grouped.as("group_by_email_domain")) //
				// cria uma janela de 30 segundas
				.windowedBy(TimeWindows.of(Duration.ofSeconds(30))) //
				// conta os eventos dessa janela
				.count(Materialized.as("window_count")) //
				.toStream() //
				// gera um objeto novo com as informações da janela
				.map((key, value) -> {

					final EmailDomainChangesCountMessage emailDomain = EmailDomainChangesCountMessage.newBuilder() //
							.setEmailDomain(key.key())//
							.setStartTime(key.window().startTime()) //
							.setEndTime(key.window().endTime()) //
							.setCount(value) //
							.build();

					return KeyValue.pair(key.key(), emailDomain);

				});
	}

	private static String getEmailDomain(UserChangedMessage value) {
		final String email = value.getUserEmail();
		return email.substring(email.indexOf("@") + 1);
	}

}
