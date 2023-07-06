package com.example.server.menu.command;

import com.example.model.message.Message;
import com.example.model.message.MessageType;
import com.example.utils.json.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class MenuCommandExecutor {

  private static final Logger logger = LoggerFactory.getLogger(MenuCommandExecutor.class);

  private final Channel channel;
  private final JsonUtil<Message> jsonUtil;

  private final HashMap<String, Command> commandMap = new HashMap<>();

  public void register(String commandName, Command command) {
    commandMap.put(commandName, command);
  }

  public void execute(String commandName) throws JsonProcessingException {
    Command command = commandMap.get(commandName);
    if (command == null) {
      throw new IllegalStateException("no command registered for " + commandName);
    }
    command.execute();
  }

  {
    this.register("menu", this::menu);
    this.register("roomMenu", this::roomMenu);
  }


  private void menu() throws JsonProcessingException {
    logger.info("");
    channel.writeAndFlush(jsonUtil.objectToString(new Message(
        MessageType.TEXT,
        """
            Main menu commands:
            1. signIn
            2. signUp
            3. exit""")));
  }

  private void roomMenu() throws JsonProcessingException {
    logger.info("");
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT,
        """
            Room menu
             1. Create room
             2. Choose room
             3. Exit""")));
  }

  public void userNameRequest() throws JsonProcessingException {
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter username")));
  }

  public void passwordRequest() throws JsonProcessingException {
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter password")));
  }

  public void roomNameRequest() throws JsonProcessingException {
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter room name")));
  }


}
