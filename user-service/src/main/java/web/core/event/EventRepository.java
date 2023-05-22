package web.core.event;

import org.apache.avro.specific.SpecificRecord;

public interface EventRepository {

	EventEntity createEvent(String messageTopic, String messageKey, byte[] messagePayload);

	<T extends SpecificRecord> EventEntity createEvent(String messageTopic, String messageKey, T message);

}
