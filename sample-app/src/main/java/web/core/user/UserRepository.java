package web.core.user;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository // extends CrudRepository<UserEntity, String> 
{
	
	Optional<UserEntity> findUserByEmail(String email);

	UserEntity createUser(String email, String name, int age, String addressPostalCode);

}
