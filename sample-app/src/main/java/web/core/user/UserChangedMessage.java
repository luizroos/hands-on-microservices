package web.core.user;

import java.util.UUID;

public class UserChangedMessage {

	public static final String TOPIC_NAME = "user.changed";

	private UUID userId;

	private String userName;

	public UserChangedMessage() {
	}

	public UserChangedMessage(UserEntity user) {
		this.userId = user.getId();
		this.userName = user.getName();
	}

	public String getUserName() {
		return userName;
	}

	public UUID getUserId() {
		return userId;
	}

}
