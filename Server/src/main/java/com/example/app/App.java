package com.example.app;

import com.example.server.Server;
import com.example.config.ChatServerApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {

  // TODO авторизация
  // TODO BD

  private static final Logger logger = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    try {
      ApplicationContext ctx = new AnnotationConfigApplicationContext(
          ChatServerApplicationConfig.class);
//      ctx.getBean("dataBaseInitializer", DataBaseInitializer.class).init();
      Server server = ctx.getBean("server", Server.class);
      server.start();
    } catch (Exception e) {
      logger.warn("Произошла ошибка при запуске или работе сервера {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

}
