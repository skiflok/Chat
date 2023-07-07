package com.example.repositories.utils;

import com.example.utils.ApplicationSettings;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Statement;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class DataBaseInitializer {

  private static final Logger logger = LoggerFactory.getLogger(DataBaseInitializer.class);

  private final DataSource ds;
  private final ApplicationSettings as;

  public void init() {

    try (Statement statement = ds.getConnection().createStatement()) {
      logger.debug("Директория запуска {}", Paths.get("./").toAbsolutePath().normalize());

      String sql = Files.lines(Paths.get(as.getSchemaPath()).normalize().toAbsolutePath())
          .collect(Collectors.joining("\n"));
      logger.debug("sql\n {}", sql);
      statement.executeUpdate(sql);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
