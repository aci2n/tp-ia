package dao;

import java.util.List;

@SuppressWarnings("unchecked")
public abstract class GenericDAO<T> extends CheckedSessionOperations {
	private final Class<T> type;

	public GenericDAO(Class<T> type) {
		this.type = type;
	}

	public T get(Integer id) {
		return (T) executeCheckedRead(s -> s.get(type, id));
	}

	public Integer insert(T obj) {
		return (Integer) executeCheckedWrite((s, o) -> s.save(o), obj);
	}

	public void update(T obj) {
		executeCheckedWrite((s, o) -> {
			s.update(o);
			return null;
		} , obj);
	};

	public void delete(T obj) {
		executeCheckedWrite((s, o) -> {
			s.delete(o);
			return null;
		} , obj);
	}

	public List<T> executeQuery(String q) {
		return (List<T>) executeCheckedRead(s -> s.createQuery(q).list());
	}

	public T executeQueryUniqueResult(String q) {
		return (T) executeCheckedRead(s -> s.createQuery(q).uniqueResult());
	}

	public List<T> getAll() {
		return (List<T>) executeQuery(String.format("from %s", type.getName()));
	}
}