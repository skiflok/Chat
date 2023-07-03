package com.example.server.handlers;

//TODO запрос пароля

import com.example.dao.ActiveConnectionStorage;
import com.example.dao.User;
import com.example.dao.UserStorage;
import com.example.exception.AuthorisationErrorException;
import com.example.exception.NameAlreadyUseException;
import com.example.message.Message;
import com.example.message.MessageType;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAuthHandler extends SimpleChannelInboundHandler<String> {

  private Channel channel;
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

  private static final Logger logger
      = LoggerFactory.getLogger(ServerAuthHandler.class);

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws JsonProcessingException {
    logger.info("Попытка подключения {}, запрос на авторизацию", ctx.channel().remoteAddress());
    this.channel = ctx.channel();
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.NAME_REQUEST)));
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String inputMsg)
      throws NameAlreadyUseException, AuthorisationErrorException, JsonProcessingException {
    logger.info("Ответ на запрос авторизации от {} ", ctx.channel().remoteAddress());

    Message msg = jsonUtil.stringToObject(inputMsg);

    if (msg.getMessageType() != MessageType.USER_NAME) {
      logger.info("Ошибка авторизации. Тип сообщения не соответствует протоколу {}",
          ctx.channel().remoteAddress());
      channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.NAME_REQUEST)));
      throw new AuthorisationErrorException();
    }

    logger.debug("Тип соответствует протоколу\n");
    String userName = msg.getMessage();

    if (userName.isEmpty()) {
      logger.debug("Имя пустое");
      logger.info("Ошибка авторизации. Попытка подключения к серверу с пустым именем от {}",
          ctx.channel().remoteAddress());
      channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.NAME_REQUEST)));
      throw new AuthorisationErrorException();
    }

    if (ActiveConnectionStorage.getInstance().getConnectionMap().containsKey(userName)) {
      logger.info(
          "Ошибка авторизации. Попытка подключения к серверу с уже используемым именем {} от {}",
          userName, ctx.channel().remoteAddress());
      channel.writeAndFlush(jsonUtil.objectToString(
          new Message(MessageType.NAME_REQUEST, userName + " уже используется")));
      throw new NameAlreadyUseException();
    }

    ActiveConnectionStorage.getInstance().addUser(userName, channel);

    //TODO перевести все на Юзеров
    UserStorage.getInstance().addUser(new User(userName));
    channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.NAME_ACCEPTED)));

    ctx.pipeline().remove(this);
    ctx.pipeline().addLast(new ServerMessageHandler());

    for (Channel ch : ActiveConnectionStorage.getInstance().getConnectionMap().values()) {
      ch.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.USER_ADDED,
          "[Сервер] : Пользователь " + userName + " подключился к чату\n")));
    }

    logger.info("Список соединений\n{}", ActiveConnectionStorage.getInstance().toString());
    logger.debug("Список хендлеров {}", ctx.pipeline().toString());
    logger.info("Авторизация {} завершена, пользователь {}", ctx.channel().remoteAddress(),
        userName);

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }
}


