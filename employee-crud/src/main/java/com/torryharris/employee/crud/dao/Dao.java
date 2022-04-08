package com.torryharris.employee.crud.dao;

import com.torryharris.employee.crud.model.Employee;
import io.vertx.core.Promise;

import java.util.List;
import java.util.Optional;

public interface Dao<T> {

  Promise<List<T>> get(String id);

  Promise<List<T>> getAll();

  void save(T t);

  Promise login(String username ,String password);


  void update(Employee employee);

  void delete (String id);
}
