package com.torryharris.employee.crud.controller;

import com.torryharris.employee.crud.dao.Dao;
import com.torryharris.employee.crud.dao.impl.EmployeeJdbcDao;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.Utils;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static io.vertx.core.Future.future;

public class EmployeeController {
  private static final Logger LOGGER = LogManager.getLogger(EmployeeController.class);
  private final Dao<Employee> employeeDao;

  public EmployeeController(Vertx vertx) {
    employeeDao = new EmployeeJdbcDao(vertx);
  }

  public Promise<Response> getEmployees() {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
    employeeDao.getAll()
      .future()
      .onSuccess(employees -> {
        response.setStatusCode(200)
          .setResponseBody(Json.encode(employees));
        responsePromise.tryComplete(response);
      })
      .onFailure(throwable -> {
        response.setStatusCode(200)
          .setResponseBody(Utils.getErrorResponse(throwable.getMessage()).toString());
        responsePromise.tryComplete(response);
        LOGGER.catching(throwable);
      });
    return responsePromise;
  }

  public Promise<Response> getEmployeebyId(String id) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
//    String id = new String();
    LOGGER.info("recorded");
    employeeDao.get(id)
      .future()
      .onSuccess(employees -> {
        response.setStatusCode(200)
          .setResponseBody(Json.encode(employees));
        responsePromise.tryComplete(response);
        LOGGER.info("recorded again");
      })

      .onFailure(throwable -> {
        response.setStatusCode(500)
          .setResponseBody(Utils.getErrorResponse(throwable.getMessage()).toString());
        responsePromise.tryComplete(response);
        LOGGER.catching(throwable);
      });
    return responsePromise;
  }

  public Promise<Response> postEmployee(Employee employee) {
    Promise<Response> responsePromise = Promise.promise();
    List<Employee> employeeList=new ArrayList<>();
    Response response = new Response();
    employeeDao.save(employee);
//    .future<object>()
//        .onSuccess(employees -> {
//        responsePromise.setStatusCode(200)
//            .setResponseBody(Json.encode(employee);
//          responsePromise.tryComplete(responsePromise);
//        });
//       .onFailure(throwable -> {
//        responsePromise.setStatusCode(500)
//          .setResponseBody(Utils.getErrorResponse(throwable.getMessage());
//        responsePromise.tryComplete(responsePromise);
//        LOGGER.catching(throwable);
    return responsePromise;
  }
  public Promise<Response> deleteEmployeebyId(String id) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
//    String id=new String();
    employeeDao.delete(id);

    return responsePromise;
  }

  public Promise<Response> updateEmployee(Employee employee) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
    employeeDao.update(employee);
    return responsePromise;
  }

  public Promise<Response> deleteEmployee(Employee employee) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
    employeeDao.save(employee);
    return responsePromise;
  }

  public Promise<Response> login(String username, String password) {
    Promise<Response> responsePromise = Promise.promise();
    Response response = new Response();
    employeeDao.login(username,password);
    return responsePromise;
  }

}










