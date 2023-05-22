package web.core.event;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.avro.specific.SpecificRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@Repository
@Transactional
public class JPAEventRepository implements EventRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private KafkaAvroSerializer kafkaAvroSerializer;

	@Override
	public <T extends SpecificRecord> EventEntity createEvent(String messageTopic, String messageKey, T message) {
		final byte[] messageBytes = kafkaAvroSerializer.serialize(messageTopic, message);
		return createEvent(messageTopic, messageKey, messageBytes);
	}

	@Override
	public EventEntity createEvent(String messageTopic, String messageKey, byte[] messagePayload) {
		final EventEntity entity = new EventEntity(messageTopic, messageKey, messagePayload);
		entityManager.persist(entity);
		return entity;
	}

}
