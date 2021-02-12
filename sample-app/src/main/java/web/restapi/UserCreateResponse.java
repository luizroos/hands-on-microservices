package web.restapi;

import java.util.Objects;
import java.util.UUID;

import web.core.user.UserEntity;

public class UserCreateResponse {

	private final UserEntity createdUser;

	public UserCreateResponse(UserEntity createdUser) {
		this.createdUser = Objects.requireNonNull(createdUser);
	}

	public UUID getId() {
		return createdUser.getId();
	}

}
