//package com.example.server.menu.command;
//
//import static com.example.model.message.MessageType.TEXT;
//import static com.example.server.menu.MenuStage.MENU;
//import static com.example.server.menu.command.Commands.*;
//import static com.example.server.menu.MenuStage.*;
//
//import com.example.model.message.Message;
//import com.example.model.message.MessageType;
//import com.example.server.menu.MenuStage;
//import com.example.utils.json.util.JsonUtil;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import io.netty.channel.Channel;
//import java.util.HashMap;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@RequiredArgsConstructor
//public class MenuCommandExecutor {
//
//  public interface CommandWithParam {
//    void execute(String str) throws JsonProcessingException;
//  }
//
//  private static final Logger logger = LoggerFactory.getLogger(MenuCommandExecutor.class);
//
//  private final Channel channel;
//  private final JsonUtil<Message> jsonUtil;
//  private final HashMap<String, Command> commandMap = new HashMap<>();
//
//  private final HashMap<MenuStage, Command> testCommandMap = new HashMap<>();
//  private final HashMap<Commands, CommandWithParam> commandMapWithParam = new HashMap<>();
//
//  public void registerTest(MenuStage menuStage, Command command) {
//    testCommandMap.put(menuStage, command);
//  }
//
//  public void executeTest(MenuStage menuStage) throws JsonProcessingException {
//    Command command = testCommandMap.get(menuStage);
//    if (command == null) {
//      throw new IllegalStateException("no command registered for " + menuStage.toString());
//    }
//    command.execute();
//  }
//
//  public void registerWithParam(Commands Commands, CommandWithParam commandWithParam) {
//    commandMapWithParam.put(Commands, commandWithParam);
//  }
//
//  public void executeWithParam(Commands Commands, String string) throws JsonProcessingException {
//    CommandWithParam command = commandMapWithParam.get(Commands);
//    if (command == null) {
//      throw new IllegalStateException("no command registered for " + Commands.toString());
//    }
//    command.execute(string);
//  }
//
//  {
//    this.registerWithParam(SEND_MESSAGE, this::sendMessage);
//  }
//
//  {
//    this.registerTest(MENU, this::menu);
//    this.registerTest(ROOM_MENU, this::roomMenu);
//    this.registerTest(ROOM_NAME_REQUEST, this::roomNameRequest);
//    this.registerTest(USER_NANE_REQUEST, this::userNameRequest);
//    this.registerTest(PASSWORD_REQUEST, this::passwordRequest);
//  }
//
//  public void register(String commandName, Command command) {
//    commandMap.put(commandName, command);
//  }
//
//  public void execute(String commandName) throws JsonProcessingException {
//    Command command = commandMap.get(commandName);
//    if (command == null) {
//      throw new IllegalStateException("no command registered for " + commandName);
//    }
//    command.execute();
//  }
//
//  {
//    this.register("menu", this::menu);
//    this.register("roomMenu", this::roomMenu);
//    this.register("roomNameRequest", this::roomNameRequest);
//  }
//
//
//  private void menu() throws JsonProcessingException {
//    logger.info("");
//    channel.writeAndFlush(jsonUtil.objectToString(new Message(
//        MessageType.TEXT,
//        """
//            Main menu commands:
//            1. signIn
//            2. signUp
//            3. exit""")));
//  }
//
//  private void roomMenu() throws JsonProcessingException {
//    logger.info("");
//    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT,
//        """
//            Room menu
//             1. Create room
//             2. Choose room
//             3. Exit""")));
//  }
//
//  public void userNameRequest() throws JsonProcessingException {
//    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter username")));
//  }
//
//  public void passwordRequest() throws JsonProcessingException {
//    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter password")));
//  }
//
//  private void roomNameRequest() throws JsonProcessingException {
//    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter room name")));
//  }
//
//  public void sendMessage(String msgString) throws JsonProcessingException {
//    channel.writeAndFlush(
//        jsonUtil.objectToString(new Message(TEXT, msgString)));
//  }
//
//}
