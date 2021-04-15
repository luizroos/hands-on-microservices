package web;

import java.time.Duration;
import java.util.Arrays;
import java.util.function.Function;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;

import web.core.user.pub.EmailDomainChangesCountMessage;
import web.core.user.pub.MultiWindowEmailDomainChangesCountMessage;
import web.core.user.pub.UserChangedMessage;

public class StreamsConfig {

	@Bean
	public Function<KStream<String, UserChangedMessage>, KStream<String, MultiWindowEmailDomainChangesCountMessage>> emailDomainCount() {
		return input -> {
			return emailDomainCount(input);
		};
	}

	private static KStream<String, MultiWindowEmailDomainChangesCountMessage> emailDomainCount(
			KStream<String, UserChangedMessage> input) {
		// troca chave para o dominio do email
		final KGroupedStream<String, Long> groupByEmail = input
				.map((key, value) -> KeyValue.pair(getEmailDomain(value), 1L)) //
				// agrupa pelo dominio do email
				.groupByKey(Grouped.with(Serdes.String(), Serdes.Long()));

		// cria um stream agrupando por 5 e 30 segundos
		final KStream<String, EmailDomainChangesCountMessage> window30 = createWindow(groupByEmail,
				Duration.ofSeconds(30), "30");
		final KStream<String, EmailDomainChangesCountMessage> window5 = createWindow(groupByEmail,
				Duration.ofSeconds(5), "5");

		// faz joins das duas janelas
		final JoinWindows joinWindow = JoinWindows.of(Duration.ofSeconds(5));
		KStream<String, MultiWindowEmailDomainChangesCountMessage> joined = window5.join(window30, (w5, w30) -> {
			return MultiWindowEmailDomainChangesCountMessage.newBuilder()//
					.setWindows(Arrays.asList(w5, w30)) //
					.build();
		}, joinWindow, StreamJoined.as("joined"));

		// tenta limpar as duplicidades geradas pelo join
		return joined.groupByKey().reduce((v1, v2) -> {
			return v2;
		}, Materialized.as("join_reduced")).toStream();
	}

	private static KStream<String, EmailDomainChangesCountMessage> createWindow(
			KGroupedStream<String, Long> groupByEmail, Duration windowDuration, String windowId) {
		// cria uma janela de 30 segundos
		return groupByEmail.windowedBy(TimeWindows.of(windowDuration)) //

				// soma a quantidade de eventos na janela
				.reduce((c1, c2) -> c1 + c2) //

				// converte o ktable gerado pela operação de reduce em um stream
				.toStream() //

				// gera um objeto novo com as informações da janela
				.map((key, value) -> {

					final EmailDomainChangesCountMessage emailDomain = EmailDomainChangesCountMessage.newBuilder() //
							.setEmailDomain(key.key())//
							.setWindowId(windowId) //
							.setStartTime(key.window().startTime()) //
							.setEndTime(key.window().endTime()) //
							.setCount(value) //
							.build();

					return KeyValue.pair(key.key(), emailDomain);

				}); //
	}

	private static String getEmailDomain(UserChangedMessage value) {
		final String email = value.getUserEmail();
		return email.substring(email.indexOf("@") + 1);
	}

}
