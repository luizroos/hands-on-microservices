package web.core.event;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity(name = EventEntity.ENTITY_NAME)
@Table(name = EventEntity.TABLE_NAME, //
		indexes = { //
				@Index(name = "event_ix01", columnList = "createdAt") })
@DynamicUpdate
public class EventEntity {

	public static final String ENTITY_NAME = "Event";

	public static final String TABLE_NAME = "event";

	@Id
	@Type(type = "uuid-binary")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@GenericGenerator(name = "EventId", strategy = "uuid2")
	@Column(columnDefinition = "binary(16)")
	private UUID id;

	@Column(nullable = false, updatable = false)
	private ZonedDateTime createdAt;

	@Column(nullable = false, updatable = false)
	private String messagePayload;

	@Column(nullable = false, updatable = false)
	private String messageKey;

	@Column(nullable = false, updatable = false)
	private String messageTopic;

	public EventEntity(String messageTopic, String messageKey, byte[] messagePayload) {
		this.messagePayload = Objects.requireNonNull(Base64.getEncoder().encodeToString(messagePayload));
		this.messageKey = Objects.requireNonNull(messageKey);
		this.createdAt = ZonedDateTime.now();
		this.messageTopic = Objects.requireNonNull(messageTopic);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getMessagePayload() {
		return messagePayload;
	}

	public void setMessagePayload(String messagePayload) {
		this.messagePayload = messagePayload;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageTopic() {
		return messageTopic;
	}

	public void setMessageTopic(String messageTopic) {
		this.messageTopic = messageTopic;
	}

}
