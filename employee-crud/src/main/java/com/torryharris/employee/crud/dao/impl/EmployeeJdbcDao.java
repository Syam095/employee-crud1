package com.torryharris.employee.crud.dao.impl;

import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.service.JdbcDbService;
import com.torryharris.employee.crud.util.ConfigKeys;
import com.torryharris.employee.crud.util.PropertyFileUtils;
import com.torryharris.employee.crud.util.QueryNames;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class EmployeeJdbcDao implements Dao<Employee> {
  private static final Logger LOGGER = LogManager.getLogger(EmployeeJdbcDao.class);
  private JDBCPool jdbcPool;
  private Employee employee;

  public EmployeeJdbcDao(Vertx vertx) {
    jdbcPool = JdbcDbService.getInstance(vertx, getJdbcConnectionOptions(), getPoolOptions()).getJdbcPool();
  }

  @Override
  public Promise<List<Employee>> get(String id) {
    Promise<List<Employee>> promise = Promise.promise();
    List<Employee> employees = new ArrayList<>();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.GET_BY_ID))
      .execute(Tuple.of(id))
//    employee.getId(),employee.getName(),employee.getDesignation(),employee.getSalary()
      .onSuccess(rows -> {
        for (Row row : rows) {
          Employee employee = new Employee();
          employee.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"));
//            .setUsername(row.getString("username"));

          System.out.println(employee.getId());
          employees.add(employee);
        }
        promise.tryComplete(employees);
      });
    return promise;
  }


  @Override
  public Promise<List<Employee>> getAll() {
    Promise<List<Employee>> promise = Promise.promise();
    List<Employee> employees = new ArrayList<>();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.GET_ALL_EMPLOYEES))
      .execute()
      .onSuccess(rows -> {
        for (Row row : rows) {
          Employee employee = new Employee();
          employee.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"))
            .setUsername(row.getString("username"))
            .setPassword(row.getString("password"));
          employees.add(employee);
        }
        promise.tryComplete(employees);
      });
    return promise;
  }

  @Override
  public void save(Employee employee) {
    Promise<List<Employee>> promise = Promise.promise();
    List<Employee> employees = new ArrayList<>();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.INSERT_EMPLOYEE))
      .execute(Tuple.of(employee.getName(), employee.getDesignation(), employee.getSalary(), employee.getUsername(), employee.getPassword()))
      .onSuccess(rows -> {
        for (Row row : rows) {
          Employee emp = new Employee();
          employee.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"))
            .setUsername(row.getString("username"))
            .setPassword(row.getString("password"));


          employees.add(emp);
        }
        promise.tryComplete(employees);
      });


  }

  @Override
  public Promise login(String username, String password) {
    Promise<List<Employee>> promise = Promise.promise();
    List<Employee> employees = new ArrayList<>();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.LOGIN_EMPLOYEE))
      .execute(Tuple.of(username, password))
      .onSuccess(rows -> {
        for (Row row : rows) {
          Employee emp = new Employee();
          employee.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"))
            .setUsername(row.getString("username"))
            .setPassword(row.getString("password"));


          employees.add(emp);
        }
        promise.tryComplete(employees);
      });
    return promise;

  }

  @Override
  public void update(Employee employee) {
      Promise<List<Employee>> promise= Promise.promise();
      List<Employee> employees=new ArrayList<>();
      jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.UPDATE_EMPLOYEE))
        .execute()
        .onSuccess(rows -> {
          for (Row row : rows) {
            Employee emp = new Employee();
            employee.setId(row.getLong("id"))
              .setName(row.getString("name"))
              .setDesignation(row.getString("designation"))
              .setSalary(row.getLong("salary"))
             .setUsername(row.getString("username"))
              .setPassword(row.getString("password"));

            employees.add(employee);
          }
          promise.tryComplete(employees);
        });




    }

  @Override
  public void delete(String id) {
    Promise<List<Employee>> promise= Promise.promise();
    List<Employee> employees=new ArrayList<>();
    jdbcPool.preparedQuery(PropertyFileUtils.getQuery(QueryNames.DELETE_EMPLOYEE))
      .execute(Tuple.of(id))
      .onSuccess(rows -> {
        for (Row row : rows) {
          Employee emp = new Employee();
          employee.setId(row.getLong("id"))
            .setName(row.getString("name"))
            .setDesignation(row.getString("designation"))
            .setSalary(row.getLong("salary"))
           .setUsername(row.getString("username"))
            .setPassword(row.getString("password"));

          employees.remove(employee);
        }
        promise.tryComplete(employees);
      });



  }

  private JDBCConnectOptions getJdbcConnectionOptions() {
    return new JDBCConnectOptions()
      .setJdbcUrl(PropertyFileUtils.getProperty(ConfigKeys.JDBC_URL))
      .setUser(PropertyFileUtils.getProperty(ConfigKeys.JDBC_USERNAME))
      .setPassword(PropertyFileUtils.getProperty(ConfigKeys.JDBC_PASSWORD))
      .setAutoGeneratedKeys(true);
  }

  private PoolOptions getPoolOptions() {
    return new PoolOptions()
      .setMaxSize(Integer.parseInt(PropertyFileUtils.getProperty(ConfigKeys.POOL_SIZE)));
  }
}
