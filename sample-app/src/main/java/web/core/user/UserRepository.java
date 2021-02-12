package web.core.user;

import java.util.Optional;

public interface UserRepository {
	
	Optional<UserEntity> findUserByEmail(String email);

	UserEntity createUser(String email, String name, int age, String addressPostalCode);

}
