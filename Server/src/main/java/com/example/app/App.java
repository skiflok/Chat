package com.example.app;

import com.example.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

  // TODO авторизация
  // TODO BD

  private static final Logger logger
      = LoggerFactory.getLogger(App.class);


  public static void main(String[] args) {
    try {
      Server server = new Server();
      server.start();
    } catch (Exception e) {
      logger.warn("Произошла ошибка при запуске или работе сервера {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }

}
