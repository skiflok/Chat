package com.example.repositories.roomRepositories;

import com.example.model.Room;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

@Component
public class RoomRepositoryImpl implements RoomRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Autowired
  public RoomRepositoryImpl(DataSource ds) {
    namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(ds);
  }

  @Override
  public Optional<Room> findById(Long id) {
    String sql = "select * from chat.chat_rooms where id = :id;";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", id);

    return namedParameterJdbcTemplate.query(sql, params, (rs) -> {
      if (rs.next()) {
        return Optional.of(new Room(
            rs.getLong("id"),
            rs.getString("name"),
            null
        ));
      }
      return Optional.empty();
    });
  }

  @Override
  public List<Room> findAll() {
    return namedParameterJdbcTemplate.query("select * from chat.chat_rooms order by id",
        (rs, rowNum) -> {
          return new Room(rs.getLong("id"), rs.getString("name"), null);
        });
  }

  @Override
  public void save(Room entity) {
    String sql = "insert into chat.chat_rooms (name, creator) values (:name, :creator)";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params
        .addValue("name", entity.getName())
        .addValue("creator", entity.getCreator().getId());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate.update(sql, params, keyHolder);
    entity.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
  }

  @Override
  public void update(Room entity) {
    String sql = "update chat.chat_rooms set name = :name, creator = :creator where id = :id";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params
        .addValue("id", entity.getId())
        .addValue("name", entity.getName())
        .addValue("creator", entity.getCreator().getId());
    if (namedParameterJdbcTemplate.update(sql, params) == 0) {
      throw new IllegalArgumentException("Failed to update entity");
    }
  }

  @Override
  public void delete(Long id) {
    String sql = "delete from chat.chat_rooms where id = :id;";

    if (namedParameterJdbcTemplate.update(sql,
        new MapSqlParameterSource().addValue("id", id)) == 0) {
      throw new IllegalArgumentException("Failed to delete entity");
    }
  }
}