package web.core.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.relation.Relation;
import com.datastax.oss.driver.api.querybuilder.select.Select;

@Repository
public class CassandraUserRepository implements UserRepository {

	@Autowired
	private CassandraTemplate cassandraTemplate;

	@Override
	public UserEntity createUser(String email, String name, int age, String addressPostalCode) {
		UserEntity entity = new UserEntity(email, name, age, addressPostalCode);
		cassandraTemplate.insert(entity);
		return entity;
	}

	@Override
	public Optional<UserEntity> findUserByEmail(String email) {
		Select select = QueryBuilder.selectFrom(UserEntity.TABLE_NAME).all()//
				.where(Relation.column("email").isEqualTo(QueryBuilder.literal(email)));
		UserEntity user = cassandraTemplate.selectOne(select.asCql(), UserEntity.class);
		return Optional.ofNullable(user);
	}

}
