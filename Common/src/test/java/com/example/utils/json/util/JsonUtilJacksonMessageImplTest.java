package com.example.utils.json.util;

import com.example.message.Message;
import com.example.message.MessageType;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilJacksonMessageImplTest {

  private final Message message =  new Message(MessageType.TEXT, "Hello", "User");
  private final String testString = "{\"messageType\":\"TEXT\",\"message\":\"Hello\",\"userName\":\"User\"}";
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

  JsonUtilJacksonMessageImplTest() {
  }


  @Test
  void objectToString() throws JsonProcessingException {
    String result = jsonUtil.objectToString(message);
    System.out.println(jsonUtil.objectToString(message));
    assertEquals(result, testString);
  }

  @Test
  void stringToObject() throws JsonProcessingException {
    Message messageResult = jsonUtil.stringToObject(testString);
    System.out.println(messageResult);
    assertEquals(MessageType.TEXT, messageResult.getMessageType());
    assertEquals("Hello", messageResult.getMessage());
    assertEquals("User", messageResult.getUserName());
  }
}