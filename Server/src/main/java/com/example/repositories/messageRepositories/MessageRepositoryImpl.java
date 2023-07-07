package com.example.repositories.messageRepositories;

import com.example.model.User;
import com.example.model.message.Message;
import com.example.repositories.userRepositories.UserRepository;
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
public class MessageRepositoryImpl implements MessageRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final UserRepository usersRepository;

  @Autowired
  public MessageRepositoryImpl(
      DataSource ds,
      UserRepository usersRepository) {
    this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(ds);
    this.usersRepository = usersRepository;
  }


  @Override
  public Optional<Message> findById(Long id) {
    String sql = "select * from chat.message where id = :id;";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", id);

    return namedParameterJdbcTemplate.query(sql, params, (rs) -> {
      if (rs.next()) {
        return Optional.of(new Message(
            rs.getLong("id"),
            usersRepository.findById(rs.getLong("id")).orElseThrow(IllegalArgumentException::new),
            null,
            rs.getString("text"),
            rs.getTimestamp("date_time").toLocalDateTime()
        ));
      }
      return Optional.empty();
    });
  }

  @Override
  public List<Message> findAll() {
//    String sql = "select * from chat.message";
//    return namedParameterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Message.class));
    return null;
  }

  @Override
  public void save(Message entity) {
    String sql = "insert into chat.message (author, room, text, date_time) values (:author, :room,:text, :date_time)";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params
        .addValue("author", entity.getUser().getId())
        .addValue("room", entity.getRoom().getId())
        .addValue("text", entity.getMessage())
        .addValue("date_time", entity.getLocalDateTime());

    KeyHolder keyHolder = new GeneratedKeyHolder();
    namedParameterJdbcTemplate.update(sql, params, keyHolder);
    entity.setId((Long) Objects.requireNonNull(keyHolder.getKeys()).get("id"));
  }

  @Override
  public void update(Message entity) {

  }

  @Override
  public void delete(Long id) {

  }

  @Override
  public List<Message> findLast30(Long chatId) {
    String sql = """
        SELECT
        m.id id,
        u.name name,
        m.text text
        FROM chat.message as m
        join chat.users as u on u.id = m.author
        join chat.chat_rooms as r on r.id = m.room
        where m.room = :id
        ORDER BY date_time ASC
        limit 30""";
    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("id", chatId);
    return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> new Message(
        rs.getLong("id"),
        new User(null, rs.getString("name"), null),
        null,
        rs.getString("text"),
        null));
  }
}
