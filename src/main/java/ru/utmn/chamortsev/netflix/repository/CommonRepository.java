package ru.utmn.chamortsev.netflix.repository;

import java.util.Collection;

public interface CommonRepository<T> {
    T save (T domain);
    Iterable<T> save (Collection<T> domains);
    void delete(String show_id);
    void delete(T show_id);
    T findById(String show_id);
    Iterable<T> findAll();
    boolean exsists(String show_id);
    long count();
}
