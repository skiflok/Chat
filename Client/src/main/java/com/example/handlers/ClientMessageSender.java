package com.example.handlers;

import com.example.message.Message;
import com.example.message.MessageType;
import com.example.utils.ConsoleHelper;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import java.io.IOException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ClientMessageSender implements Runnable{

  private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

  @Setter
  @Getter
  private boolean stop;

  private final Channel channel;

  @Override
  public void run() {
      try {
        readMessageFromConsoleAndSendMessage();
      } catch (IOException e) {
        logger.warn(e.getMessage());
        throw new RuntimeException(e);
      }
  }

  public void sendMessage(String text) throws JsonProcessingException {
    logger.debug("sendMessage");
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, text)));
  }

  public void readMessageFromConsoleAndSendMessage() throws IOException {
    logger.debug(this.getClass().toString());
    while (!stop) {
      String line = ConsoleHelper.readString();
      if ("/exit".equals(line)) {
        break;
      }
      sendMessage(line);
    }
  }

}
