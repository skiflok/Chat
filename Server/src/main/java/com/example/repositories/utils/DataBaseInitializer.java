package com.example.repositories.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Statement;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:db.properties")
public class DataBaseInitializer {

  private static final Logger logger
      = LoggerFactory.getLogger(DataBaseInitializer.class);

  private final DataSource ds;

  @Value("${db.init.schema.path}")
  String schemaPath;
  @Value("${db.init.data.path}")
  String dataPath;

  @Autowired
  public DataBaseInitializer(DataSource ds) {
    this.ds = ds;
  }

  public void init() {

    try (Statement statement = ds.getConnection().createStatement()) {
      logger.debug("Директория запуска {}", Paths.get("./").toAbsolutePath().normalize());

      String sql = Files.lines(Paths.get(schemaPath).normalize().toAbsolutePath()).collect(Collectors.joining("\n"));
      logger.debug("sql\n {}", sql);
      statement.executeUpdate(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
