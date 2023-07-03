package com.example.server;

import com.example.message.Message;
import com.example.message.MessageType;
import com.example.server.command.MenuCommandExecutor;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationChatMenu {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationChatMenu.class);
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

  private final MenuCommandExecutor commandExecutor;

  private MenuStage menuStage = MenuStage.MENU;

  private final Channel channel;

  public ApplicationChatMenu(Channel channel) {
    this.channel = channel;
    commandExecutor = new MenuCommandExecutor(channel);
  }

  public void menu() throws JsonProcessingException {
    logger.info("");
//    channel.writeAndFlush(jsonUtil.objectToString(new Message(
//        MessageType.TEXT,
//        """
//            Hello from Server!
//            Available commands:
//            1. signIn
//            2. signUp
//            3. exit
//            """)));
    commandExecutor.execute("menu");
  }

  public void messageHandler(String incomeMsg) {
    channel.writeAndFlush(incomeMsg + "\n");
  }

  // todo
  // принимать соообщения изначально в меню
  // сделать енам на местонахождение в меню МЕНЮ КОМНАТЫ ЧАТИК и ТД
  // менять енам и обработчик при выборе пользователя

}
