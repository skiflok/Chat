package com.example.client.app;

import com.example.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.client")
public class ClientApp {

  private static final Logger logger = LoggerFactory.getLogger(ClientApp.class);

  public static void main(String[] args) {
    try {
      ApplicationContext ctx = new AnnotationConfigApplicationContext(
          ClientApp.class);
      Client client = ctx.getBean("client", Client.class);
      client.run();
    } catch (Exception e) {
      logger.warn("Произошла ошибка при запуске или работе клиента {}", e.getMessage());
      e.printStackTrace();
    }
  }
}


