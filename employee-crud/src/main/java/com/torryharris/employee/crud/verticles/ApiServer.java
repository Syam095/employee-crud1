package com.torryharris.employee.crud.verticles;

import com.torryharris.employee.crud.controller.EmployeeController;
import com.torryharris.employee.crud.model.Employee;
import com.torryharris.employee.crud.model.Response;
import com.torryharris.employee.crud.util.ConfigKeys;
import com.torryharris.employee.crud.util.PropertyFileUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Base64;

public class ApiServer extends AbstractVerticle {
  private static final Logger logger = LogManager.getLogger(ApiServer.class);
  private static Router router;
  private EmployeeController employeeController;
  private Employee employees;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    employeeController = new EmployeeController(vertx);
    router = Router.router(vertx);

    // Attach a BodyHandler to parse request body and set upload to false
    router.route().handler(BodyHandler.create(false));

  router.get("/employees")
      .handler(routingContext -> employeeController.getEmployees().future()
      .onSuccess(response -> sendResponse(routingContext, response))
     );

//  String id=new String();

    router.get("/employees/:id")
      .handler(routingContext -> {
        String id = routingContext.request().getParam("id");

        employeeController.getEmployeebyId(id).future()
            .onSuccess(response -> sendResponse(routingContext, response));
        }
      );

    router.post("/employees").consumes("*/json")
      .handler(routingContext -> {
        Employee employee = Json.decodeValue(routingContext.getBody(), Employee.class);
        HttpServerResponse serverResponse=routingContext.response();
        employeeController.postEmployee(employee).future()
          .onSuccess(response -> sendResponse(routingContext, response));
        System.out.println(routingContext.getBodyAsJson());


//        employees.add(employee);
        serverResponse.end("employee added");
        });
//    router.put("/employees")
//      .handler(routingContext -> employeeController.getEmployeebyId().future()
//        .onSuccess(response -> sendResponse(routingContext, response))
//      );
//    router.delete("/employees")
//      .handler(routingContext -> employeeController.getEmployeebyId().future()
//        .onSuccess(response -> sendResponse(routingContext, response))
//      );
    router.delete("/employees/:id")
      .handler(routingContext ->{
          String id= routingContext.request().getParam("id");
          employeeController.deleteEmployeebyId(id).future()
            .onSuccess(response -> sendResponse(routingContext,response));
        }
      );

    router.put("/employees/:id")
      .handler(routingContext ->{
          String id= routingContext.request().getParam("id");
          employeeController.deleteEmployeebyId(id).future()
            .onSuccess(response -> sendResponse(routingContext,response));
        }
      );
    router.post("/login")
//      .handler(routingContext -> {
//        String username=routingContext.request().getParam("username");
//          String password=routingContext.request().getParam("password");
//          HttpHeaders.set(username,password);
      .handler(routingContext -> {
        String authuser= routingContext.request().getHeader(HttpHeaders.AUTHORIZATION);
//        String password= routingContext.request().getParam("password");



        authuser= authuser.substring(6);
        String s= new String(Base64.getDecoder().decode(authuser));
        String[] arr = s.split(":");
        String username = arr[0];
        String password = arr[1];
          HttpServerResponse serverResponse = routingContext.response();
      Employee employee = new Employee();
          employeeController.login(username,password).future()
            .onSuccess(response ->sendResponse(routingContext, response));
          serverResponse.end("login success");
        });




    HttpServerOptions options = new HttpServerOptions().setTcpKeepAlive(true);
    vertx.createHttpServer(options)
      .exceptionHandler(logger::catching)
      .requestHandler(router)
      .listen(Integer.parseInt(PropertyFileUtils.getProperty(ConfigKeys.HTTP_SERVER_PORT)))
      .onSuccess(httpServer -> {
        logger.info("Server started on port {}", httpServer.actualPort());
        startPromise.tryComplete();
      })
      .onFailure(startPromise::tryFail);
  }

  private void sendResponse(RoutingContext routingContext, Response response) {
    response.getHeaders().stream()
      .forEach(entry -> routingContext.response().putHeader(entry.getKey(), entry.getValue().toString()));
    routingContext.response().setStatusCode(response.getStatusCode())
      .end(response.getResponseBody());
  }
}
