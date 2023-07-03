package com.example.server;

import com.example.message.Message;
import com.example.message.MessageType;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ApplicationChatMenu {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationChatMenu.class);
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

  private final Channel channel;

  public void menu() throws JsonProcessingException {
    logger.info("");
    channel.writeAndFlush(jsonUtil.objectToString(new Message(
        MessageType.TEXT,
        """
            Hello from Server!
            Available commands:
            1. signIn
            2. signUp
            3. exit""")));
  }
}
