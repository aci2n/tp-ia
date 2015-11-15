package bean;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;

import com.google.gson.Gson;

import entity.ExceptionLog;
import entity.PersistentObject;
import exception.NoExisteException;
import exception.PersistException;

@SuppressWarnings("unchecked")
public abstract class GenericBean<T> {

	@PersistenceContext(unitName = "CRM")
	public EntityManager em;
	private Class<T> type;
	public Gson gson;

	protected GenericBean(Class<T> type) {
		this.type = type;
		gson = new Gson();
	}

	public T get(Integer id) throws NoExisteException {
		T t = em.find(type, id);
		if (t == null) {
			throw new NoExisteException(
					String.format("No existe el objeto del tipo %s con el ID %s.", type.getName(), id));
		}
		return t;
	}

	public Integer save(PersistentObject obj) throws PersistException {
		try {
			em.persist((T) obj);
			em.flush();
		} catch (Exception e) {
			throw new PersistenceException("No se pudo persistir la entidad. Excepcion: " + e.getMessage());
		}

		Integer id = obj.getId();
		if (id == null) {
			throw new PersistException("No se pudo persistir la entidad");
		}
		return id;
	}

	public List<T> getAll() {
		return (List<T>) em.createQuery("from " + type.getName()).getResultList();
	}

	public List<T> executeQuery(String query) {
		return (List<T>) em.createQuery(query).getResultList();
	}

	public List<Object[]> executeTypelessQuery(String query) { // que se yo
		return (List<Object[]>) em.createQuery(query).getResultList();
	}

	public void logException(Exception e) {
		e.printStackTrace();
		em.persist(new ExceptionLog(e));	
		em.flush();
	}

	public String toJson(Object obj) {
		return gson.toJson(obj);
	}

	public T fromJson(String json) {
		return gson.fromJson(json, type);
	}

}
