package com.example.server.menu;

import com.example.message.Message;
import com.example.message.MessageType;
import com.example.model.User;
import com.example.repositories.userRepositories.UserRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ApplicationChatMenu {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationChatMenu.class);
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private JsonUtil<Message> jsonUtil;

  private MenuCommandExecutor commandExecutor;

  private MenuStage menuStage = MenuStage.MENU;
  private Channel channel;

  private User user;

  Queue<Command> requestQueue = new LinkedList<>();
  Queue<String> answerQueue = new LinkedList<>();

  public void init(Channel channel) throws JsonProcessingException {
    this.channel = channel;
    commandExecutor = new MenuCommandExecutor(channel, jsonUtil);
    commandExecutor.execute("menu");
  }

  public void messageHandler(String incomeMsg) throws JsonProcessingException {
//    channel.writeAndFlush(incomeMsg + "\n");

    logger.info(menuStage.toString());

    Message msg = jsonUtil.stringToObject(incomeMsg);

    switch (menuStage) {
      case MENU -> processMenuInput(msg.getMessage());
      case AUTHENTICATION -> authentication(msg.getMessage());
      case REGISTRATION -> registration(msg.getMessage());
      case ROOM_MENU -> commandExecutor.execute("roomMenu");
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
        if (requestQueue.isEmpty()) {
          logger.info("requestQueue offer");
          requestQueue.offer(commandExecutor::userNameRequest);
          requestQueue.offer(commandExecutor::passwordRequest);
        }
        registration(input);
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

  private void processRoomMenuInput(String input) throws JsonProcessingException {
    logger.info("");
    switch (input) {
      case "1":
        createRoom();
        break;
      case "2":
        chooseRoom();
        break;
      case "3":
        menuStage = MenuStage.EXIT;
        exit();
      case "0":
        return;
      default:
        logger.info("Некорректный ввод. Попробуйте снова.");
    }
  }

  public void registration(String input) throws JsonProcessingException {
    logger.info("");
    if (requestQueue.size() < 2) {
      answerQueue.offer(input);
    }
    if (!requestQueue.isEmpty()) {
      requestQueue.poll().execute();
    }
    if (answerQueue.size() == 2) {
      logger.info(answerQueue.toString());
      String userName = answerQueue.poll();
      String userPassword = answerQueue.poll();
      if ("".equals(userName) || "".equals(userPassword)) {
        logger.info("null or empty");
        menuStage = MenuStage.MENU;
        commandExecutor.execute("menu");
        return;
      }
      Optional<User> optionalUserFromDB = userRepository.findByName(userName);

      if (optionalUserFromDB.isPresent()) {
        userAlreadyRegister();
      } else {
        userRepository.save(new User(null, userName, userPassword));
      }
      menuStage = MenuStage.MENU;
      commandExecutor.execute("menu");
    }
  }

  public void userAlreadyRegister() throws JsonProcessingException {
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "user Already Register")));
  }

  public void registrationSuccess() {

  }

  public void createRoom() {

  }

  public void chooseRoom() {

  }

  public void exit() {

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
      logger.info(answerQueue.toString());
      String userName = answerQueue.poll();
      String userPassword = answerQueue.poll();
      if ("".equals(userName) || "".equals(userPassword)) {
        logger.info("null or empty");
        menuStage = MenuStage.MENU;
        commandExecutor.execute("menu");
        return;
      }

      Optional<User> optionalUserFromDB = userRepository.findByName(userName);
      if (optionalUserFromDB.isPresent()) {
        this.user = optionalUserFromDB.get();
        menuStage = MenuStage.ROOM_MENU;
        commandExecutor.execute("roomMenu");
      } else {
        userNotRegister();
        menuStage = MenuStage.MENU;
        commandExecutor.execute("menu");
      }
    }
  }

  public void userNotRegister() throws JsonProcessingException {
    channel.writeAndFlush(
        jsonUtil.objectToString(new Message(MessageType.TEXT, "User not register")));
    logger.info("User not register");
  }

  // todo
  // принимать соообщения изначально в меню
  // сделать енам на местонахождение в меню МЕНЮ КОМНАТЫ ЧАТИК и ТД
  // менять енам и обработчик при выборе пользователя

}
