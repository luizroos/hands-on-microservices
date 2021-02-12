package web.core.user;

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

@Entity(name = UserEntity.ENTITY_NAME)
@Table(name = UserEntity.TABLE_NAME, //
		indexes = { //
				@Index(name = "user_uk01", columnList = "email", unique = true) })
@DynamicUpdate
public class UserEntity {

	public static final String ENTITY_NAME = "User";
	public static final String TABLE_NAME = "user";

	@Id
	@Type(type = "uuid-binary")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@GenericGenerator(name = "UserId", strategy = "uuid2")
	private UUID id;

	@Column(updatable = false, nullable = false)
	private String email;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Integer age;

	@Column(nullable = false)
	private String addressPostalCode;

	public UserEntity() {
	}

	public UserEntity(String email, String name, int age, String addressPostalCode) {
		if (age < 0) {
			throw new IllegalArgumentException();
		}
		this.name = Objects.requireNonNull(name);
		this.email = Objects.requireNonNull(email);
		this.age = Objects.requireNonNull(age);
		this.addressPostalCode = Objects.requireNonNull(addressPostalCode);
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
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
