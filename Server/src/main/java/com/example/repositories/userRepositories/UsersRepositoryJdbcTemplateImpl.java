package com.example.repositories.userRepositories;

import com.example.model.User;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class UsersRepositoryJdbcTemplateImpl implements UserRepository{

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public UsersRepositoryJdbcTemplateImpl(DataSource ds) {
    namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(ds);
  }

  @Override
  public Optional<User> findById(Long id) {
    String sql = "select * from chat.users where id = :id;";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", id);

    return namedParameterJdbcTemplate.query(sql, params, (rs) -> {
      if (rs.next()) {
        return Optional.of(new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("password")
        ));
      }
      return Optional.empty();
    });
  }

  @Override
  public List<User> findAll() {
    String sql = "select * from chat.users";
    return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
  }

  @Override
  public void save(User entity) {
    String sql = "insert into chat.users (name, password) values (:name, :password)";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params
        .addValue("name", entity.getName())
        .addValue("password", entity.getPassword());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate.update(sql, params, keyHolder);
    entity.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));

  }

  @Override
  public void update(User entity) {
    String sql = "update chat.users set email = :email, password = :password where id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params
        .addValue("id", entity.getId())
        .addValue("name", entity.getName())
        .addValue("password", entity.getPassword());
    if (namedParameterJdbcTemplate.update(sql, params) == 0) {
      throw new IllegalArgumentException("Failed to update entity");
    }
  }

  @Override
  public void delete(Long id) {
    String sql = "delete from chat.users where id = :id;";

    if (namedParameterJdbcTemplate.update(sql,
        new MapSqlParameterSource().addValue("id", id)) == 0) {
      throw new IllegalArgumentException("Failed to delete entity");
    }
  }

  @Override
  public Optional<User> findByName(String name) {
    String sql = "select * from chat.users where name = :name";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("name", name);
    List<User> users = namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) ->
        new User(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("password")
        ));

    if (users.isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(users.get(0));
    }

  }
}