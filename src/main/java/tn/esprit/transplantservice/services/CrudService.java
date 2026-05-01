package tn.esprit.transplantservice.services;

import java.util.List;

public interface CrudService<T, ID> {
    T save(T entity);
    List<T> findAll();
    T findById(ID id);
    void delete(ID id);
}
