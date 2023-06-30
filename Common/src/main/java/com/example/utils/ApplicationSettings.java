package com.example.utils;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
@Getter
public class ApplicationSettings {

  @Value("${server.port}")
  private int PORT;
  @Value("${server.host}")
  private String HOST;
  @Value("${server.users}")
  private String filePathUsers;

}
