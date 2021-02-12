package web.core.user;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class JPAUserRepository implements UserRepository {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public UserEntity createUser(String email, String name, int age, String addressPostalCode) {
		UserEntity entity = new UserEntity(email, name, age, addressPostalCode);
		entityManager.persist(entity);
		return entity;
	}

	@Override
	public Optional<UserEntity> findUserByEmail(String email) {
		StringBuilder hql = new StringBuilder();
		hql.append("select u from ");
		hql.append(UserEntity.ENTITY_NAME);
		hql.append(" u where u.email = :email");

		final List<UserEntity> users = entityManager.createQuery(hql.toString(), UserEntity.class)
				.setParameter("email", email).getResultList();

		if (users.isEmpty()) {
			return Optional.empty();
		}
		if (users.size() != 1) {
			throw new IllegalStateException("Mais de um usuario com mesmo email");
		}
		return Optional.of(users.get(0));
	}

}
