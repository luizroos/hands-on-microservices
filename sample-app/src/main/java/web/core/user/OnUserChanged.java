package web.core.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import web.core.user.pub.MultiWindowEmailDomainChangesCountMessage;
import web.core.user.pub.UserChangedMessage;

@Service
public class OnUserChanged {

	private static final Logger LOGGER = LoggerFactory.getLogger(OnUserChanged.class);

	@KafkaListener(topics = "${topic.user-changed}", groupId = "sampleApp.onUserChanged")
	public void onUserChanged(UserChangedMessage message, Acknowledgment ack) {
		LOGGER.info("user created, id={}, name={}", message.getUserId(), message.getUserName());
		ack.acknowledge();
	}

	@KafkaListener(topics = "${topic.email-domain-count}", groupId = "sampleApp.onUserChanged")
	public void onEmailDomainCount(MultiWindowEmailDomainChangesCountMessage message, Acknowledgment ack) {
		LOGGER.info("window count, message={}", message);
		ack.acknowledge();
	}
}
