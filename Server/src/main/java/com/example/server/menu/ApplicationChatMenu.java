package com.example.server.menu;

import com.example.message.Message;
import com.example.message.MessageType;
import com.example.model.User;
import com.example.server.menu.command.Command;
import com.example.server.menu.command.MenuCommandExecutor;
import com.example.utils.json.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationChatMenu {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationChatMenu.class);
  private final JsonUtil<Message> jsonUtil;
  private final MenuCommandExecutor commandExecutor;

  private MenuStage menuStage = MenuStage.MENU;

  private final Channel channel;

  private User user;

  Queue<Command> requestQueue = new LinkedList<>();
  Queue<String> answerQueue = new LinkedList<>();

  public ApplicationChatMenu(Channel channel, JsonUtil<Message> jsonUtil) {
    this.jsonUtil = jsonUtil;
    this.channel = channel;
    commandExecutor = new MenuCommandExecutor(channel);
  }

  public void menu() throws JsonProcessingException {
    logger.info("");
    commandExecutor.execute("menu");
  }

  public void messageHandler(String incomeMsg) throws JsonProcessingException {
//    channel.writeAndFlush(incomeMsg + "\n");

    logger.info(menuStage.toString());

    Message msg = jsonUtil.stringToObject(incomeMsg);

    switch (menuStage) {
      case MENU -> processMenuInput(msg.getMessage());
      case AUTHENTICATION -> authentication(msg.getMessage());
//      case REGISTRATION -> processRegistrationInput(input);
      case ROOM_MENU -> roomMenu();
//      case CHAT -> processChatInput(input);
      default -> {
        logger.error("Некорректный статус меню: {}", menuStage);
      }
    }
  }

  private void processMenuInput(String input) throws JsonProcessingException {
    logger.info("");
    switch (input) {
      case "1":
        menuStage = MenuStage.AUTHENTICATION;
        if (requestQueue.isEmpty()) {
          logger.info("requestQueue offer");
          requestQueue.offer(commandExecutor::userNameRequest);
          requestQueue.offer(commandExecutor::passwordRequest);
        }
        authentication(input);
        break;
      case "2":
        menuStage = MenuStage.REGISTRATION;
        channel.writeAndFlush(
            jsonUtil.objectToString(new Message(MessageType.TEXT, "REGISTRATION")));
        break;
      case "3":
        menuStage = MenuStage.EXIT;
        channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "EXIT")));
      case "0":
        return;
      default:
        logger.info("Некорректный ввод. Попробуйте снова.");
    }
  }

  private void roomMenu() throws JsonProcessingException {
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "ROOM MENU")));
  }


  private void userRequest() {

  }

  private void authentication(String input) throws JsonProcessingException {
    logger.info("");
    if (requestQueue.size() < 2) {
      answerQueue.offer(input);
    }
    if (!requestQueue.isEmpty()) {
      requestQueue.poll().execute();
    }
    if (answerQueue.size() == 2) {
      String userName = answerQueue.poll();
      String userPassword = answerQueue.poll();
      if ("".equals(userName) || "".equals(userPassword)) {
        logger.info("null or empty");
        menuStage = MenuStage.MENU;
      } else {
//        authenticationUser(new User(null, userName, userPassword));
        menuStage = MenuStage.ROOM_MENU;
      }
    }
    logger.info(answerQueue.toString());

  }

//  private void authenticationUser (User user) {
//    Optional<User> optionalUserFromDB = usersRepository.findByName(userName);
//  }

  // todo
  // принимать соообщения изначально в меню
  // сделать енам на местонахождение в меню МЕНЮ КОМНАТЫ ЧАТИК и ТД
  // менять енам и обработчик при выборе пользователя

}
