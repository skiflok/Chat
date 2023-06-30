package com.example.utils.json.util;

import com.example.message.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtilJacksonImpl implements JsonUtil<Message>{

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String objectToString(Message object) throws JsonProcessingException {
    return objectMapper.writeValueAsString(object);
  }

  @Override
  public Message StringToObject(String string) throws JsonProcessingException {
    return objectMapper.readValue(string, Message.class);
  }

}
