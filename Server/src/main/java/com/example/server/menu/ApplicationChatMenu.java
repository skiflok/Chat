package com.example.server.menu;

import static com.example.model.message.MessageType.*;
import com.example.model.message.MessageType;
import com.example.repositories.messageRepositories.MessageRepository;
import com.example.server.connection.ActiveConnectionStorage;
import com.example.model.message.Message;
import com.example.model.Room;
import com.example.model.User;
import com.example.repositories.roomRepositories.RoomRepository;
import com.example.repositories.userRepositories.UserRepository;
import com.example.server.connection.Connection;
import com.example.server.menu.command.Command;
import com.example.services.UsersService;
import com.example.utils.json.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
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
  ActiveConnectionStorage activeConnectionStorage;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private UsersService usersService;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private JsonUtil<Message> jsonUtil;

  private MenuStage menuStage = MenuStage.MENU;
  private Channel channel;
  private ChannelHandlerContext ctx;
  private User user;
  private int stage;
  private long exitKey;
  private List<Room> rooms;

  private Room room;

  Queue<Command> requestQueue = new LinkedList<>();
  Queue<String> answerQueue = new LinkedList<>();

  public void init(ChannelHandlerContext ctx, User user)
      throws JsonProcessingException {
    this.ctx = ctx;
    this.user = user;
    this.channel = ctx.channel();
    menu();
  }

  public void messageHandler(String incomeMsg) throws JsonProcessingException {

    logger.info(menuStage.toString());

    Message msg = jsonUtil.stringToObject(incomeMsg);

    switch (menuStage) {
      case MENU -> processMenuInput(msg.getMessage());
      case AUTHENTICATION -> authentication(msg.getMessage());
      case REGISTRATION -> registration(msg.getMessage());
      case ROOM_MENU -> processRoomMenuInput(msg.getMessage());
      case CREATE_ROOM -> createRoom(msg.getMessage());
      case CHOOSE_ROOM -> chooseRoom(msg.getMessage());
      case CHAT -> processChat(msg.getMessage());
      default -> logger.error("Некорректный статус меню: {}", menuStage);
    }
  }

  private void processRoomMenuInput(String input) throws JsonProcessingException {
    logger.info("");
    switch (input) {
      case "1" -> createRoom(input);
      case "2" -> chooseRoom(input);
      case "3" -> exit();
      default -> {
        logger.info("Некорректный ввод. Попробуйте снова.");
        roomMenu();
      }
    }
  }

  private void processMenuInput(String input) throws JsonProcessingException {
    logger.info("");
    switch (input) {
      case "1" -> authentication(input);
      case "2" -> registration(input);
      case "3" -> exit();
      default -> {
        logger.info("Некорректный ввод. Попробуйте снова.");
        menu();
      }
    }
  }

  public void registration(String input) throws JsonProcessingException {

    logger.info("stage {}", stage);
    switch (stage) {
      case 0 -> {
        menuStage = MenuStage.REGISTRATION;
        userNameRequest();
        stage++;
      }
      case 1 -> {
        answerQueue.offer(input);
        logger.info(answerQueue.toString());
        passwordRequest();
        stage++;
      }
      case 2 -> {
        stage = 0;
        answerQueue.offer(input);
        logger.info(answerQueue.toString());
        String userName = answerQueue.poll();
        String userPassword = answerQueue.poll();
        if ("".equals(userName) || "".equals(userPassword)) {
          logger.info("null or empty");
          menuStage = MenuStage.MENU;
          menu();
          return;
        }
        Optional<User> optionalUserFromDB = userRepository.findByName(userName);

        if (optionalUserFromDB.isPresent()) {
          userAlreadyRegister();
        } else {
          usersService.signUp(new User(null, userName, userPassword));
          registrationSuccess();
        }
        menuStage = MenuStage.MENU;
        menu();
      }
      default -> {
        stage = 0;
        menuStage = MenuStage.MENU;
      }
    }
  }

  private void authentication(String input) throws JsonProcessingException {

    logger.info("stage {}", stage);
    switch (stage) {
      case 0 -> {
        menuStage = MenuStage.AUTHENTICATION;
        userNameRequest();
        stage++;
      }
      case 1 -> {
        answerQueue.offer(input);
        logger.info(answerQueue.toString());
        passwordRequest();
        stage++;
      }
      case 2 -> {
        answerQueue.offer(input);
        logger.info(answerQueue.toString());
        String userName = answerQueue.poll();
        String userPassword = answerQueue.poll();
        stage = 0;
        if ("".equals(userName) || "".equals(userPassword)) {
          logger.info("null or empty");
          menuStage = MenuStage.MENU;
          menu();
          return;
        }
        Optional<User> optionalUserFromDB = userRepository.findByName(userName);
        if (optionalUserFromDB.isPresent()) {
          this.user = optionalUserFromDB.get();
          menuStage = MenuStage.ROOM_MENU;
          sendMessage("authentication success! Hello " + user.getName());
          roomMenu();
        } else {
          userNotRegister();
          menuStage = MenuStage.MENU;
          menu();
        }
      }
      default -> {
        menuStage = MenuStage.MENU;
        stage = 0;
      }
    }
  }

  private void processChat(String input) throws JsonProcessingException {
    if ("exit".equals(input)) {
      exit();
      return;
    }
    sendBroadcastMessage(input);
  }

  private void userAlreadyRegister() throws JsonProcessingException {
    channel.writeAndFlush(
        jsonUtil.objectToString(new Message(TEXT, "user Already Register")));
  }

  private void registrationSuccess() throws JsonProcessingException {
    sendMessage("Registration success");
  }

  private void createRoom(String input) throws JsonProcessingException {

    switch (stage) {
      case 0 -> {
        roomNameRequest();
        stage++;
        menuStage = MenuStage.CREATE_ROOM;
      }
      case 1 -> {
        if (input.isEmpty()) {
          sendMessage("room created failed: name is empty");
          menuStage = MenuStage.ROOM_MENU;
          roomMenu();
          stage = 0;
          return;
        }
        if (roomRepository.findAll().stream()
            .anyMatch(room -> input.equals(room.getName()))) {
          sendMessage("this room already exist");
          menuStage = MenuStage.ROOM_MENU;
          roomMenu();
          stage = 0;
          return;
        }

        roomRepository.save(new Room(null, input, user));
        sendMessage("room created");
        menuStage = MenuStage.ROOM_MENU;
        roomMenu();
        stage = 0;
      }
    }
  }

  private void chooseRoom(String input) throws JsonProcessingException {

    switch (stage) {

      case 0 -> {
        logger.info("stage {}", stage);
        rooms = roomRepository.findAll();
        final long[] maxId = new long[1];
        String res = roomRepository.findAll().stream()
            .map(room -> {
              maxId[0] = room.getId();
              return room.getId() + ". " + room.getName();
            })
            .collect(Collectors.joining("\n"));
        res += "\n" + (maxId[0] + 1) + ". exit";
        sendMessage(res);
        exitKey = maxId[0];
        stage++;
        menuStage = MenuStage.CHOOSE_ROOM;
      }
      case 1 -> {
        logger.info("stage {}", stage);
        Optional<Room> room = rooms.stream()
            .filter(r -> r.getId().toString().equals(input))
            .findFirst();
        if (room.isPresent()) {
          this.room = room.get();
          sendMessage("welcome to room " + this.room.getName());
          activeConnectionStorage.addUser(user.getName(), channel, this.room.getName());
          sendLast30MsgFromRoom();
          menuStage = MenuStage.CHAT;
        }
        stage = 0;
      }
      default -> {
        roomMenu();
        menuStage = MenuStage.ROOM_MENU;
      }
    }
    // todo проверки
  }

  private void sendLast30MsgFromRoom() {
    logger.info("sendLast30MsgFromRoom | room id = {}", room.getId());
    messageRepository.findLast30(room.getId())
        .forEach(message -> {
          try {
            logger.info(message.toString());
            sendMessage(
                (String.format("[%s]: %s", message.getUser().getName(), message.getMessage())));
          } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
            throw new RuntimeException(e);
          }
        });
  }

  private void exit() throws JsonProcessingException {
    logger.info("EXIT {} ", channel.remoteAddress());
    sendMessage("exit");
    ctx.disconnect();
    ctx.close();
  }


  private void userNotRegister() throws JsonProcessingException {
    channel.writeAndFlush(
        jsonUtil.objectToString(new Message(TEXT, "User not register")));
    logger.info("User not register");
  }

  private void sendBroadcastMessage(String income) throws JsonProcessingException {
    messageRepository.save(new Message(null, user, room, income, LocalDateTime.now()));
    for (Connection connection : activeConnectionStorage.getConnectionList()) {
      if (this.room.getName().equals(connection.getRoomName())) {
        connection.getChannel().writeAndFlush(jsonUtil.objectToString(
            new Message(TEXT, String.format("[%s]: %s", user.getName(), income))));
      }
    }
  }

  private void userNameRequest() throws JsonProcessingException {
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter username")));
  }

  private void passwordRequest() throws JsonProcessingException {
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter password")));
  }

  private void roomNameRequest() throws JsonProcessingException {
    channel.writeAndFlush(
        jsonUtil.objectToString(new Message(MessageType.TEXT, "Enter room name")));
  }

  private void sendMessage(String msgString) throws JsonProcessingException {
    channel.writeAndFlush(
        jsonUtil.objectToString(new Message(TEXT, msgString)));
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


}
