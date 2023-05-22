package web.core.exc;

import java.util.Objects;

public class EntityAlreadyExistsException extends Exception {

	private static final long serialVersionUID = 1L;

	private final Class<?> entityClass;

	private final Object entityId;

	private final Object searchValue;

	private final String searchField;

	public EntityAlreadyExistsException(Class<?> entityClass, Object entityId, String searchField, String searchValue) {
		this.entityClass = Objects.requireNonNull(entityClass);
		this.entityId = Objects.requireNonNull(entityId);
		this.searchValue = Objects.requireNonNull(searchValue);
		this.searchField = Objects.requireNonNull(searchField);
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public Object getEntityId() {
		return entityId;
	}

	public String getSearchField() {
		return searchField;
	}

	public Object getSearchValue() {
		return searchValue;
	}

}
