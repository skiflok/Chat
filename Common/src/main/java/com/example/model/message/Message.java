package com.example.model.message;

import com.example.model.Room;
import com.example.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class Message {

  @JsonIgnore
  private Long id;
  @JsonIgnore
  private User user;
  @JsonIgnore
  private Room room;
  private MessageType messageType;
  private String message;
  @JsonIgnore
  private LocalDateTime localDateTime;
  @JsonIgnore
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "dd/MM/yy HH:mm");

  public Message(Long id,
      User user,
      Room room,
      String message,
      LocalDateTime localDateTime) {
    this.id = id;
    this.user = user;
    this.room = room;
    this.message = message;
    this.localDateTime = localDateTime;
  }

  public Message(MessageType messageType) {
    this.messageType = messageType;
    message = null;
  }

  public Message(MessageType messageType, String message) {
    this.messageType = messageType;
    this.message = message;
  }
}
