package web.restapi;

import java.util.Objects;

import web.core.user.UserEntity;

public class UserCreateResponse {

	private final UserEntity createdUser;

	public UserCreateResponse(UserEntity createdUser) {
		this.createdUser = Objects.requireNonNull(createdUser);
	}

	public Object getId() {
		return createdUser.getId();
	}

}
