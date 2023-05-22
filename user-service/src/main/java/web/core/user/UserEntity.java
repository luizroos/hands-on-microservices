package web.core.user;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = UserEntity.TABLE_NAME)
public class UserEntity {

	public static final String ENTITY_NAME = "User";
	public static final String TABLE_NAME = "user";

	@PrimaryKey
	private String id;

	@Column
	private String email;

	@Column
	private String name;

	@Column
	private Integer age;

	@Column
	private String addressPostalCode;

	public UserEntity() {
	}

	public UserEntity(String email, String name, int age, String addressPostalCode) {
		if (age < 0) {
			throw new IllegalArgumentException();
		}
		this.id = UUID.randomUUID().toString();
		this.name = Objects.requireNonNull(name);
		this.email = Objects.requireNonNull(email);
		this.age = Objects.requireNonNull(age);
		this.addressPostalCode = Objects.requireNonNull(addressPostalCode);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getAddressPostalCode() {
		return addressPostalCode;
	}

	public void setAddressPostalCode(String addressPostalCode) {
		this.addressPostalCode = addressPostalCode;
	}

}
