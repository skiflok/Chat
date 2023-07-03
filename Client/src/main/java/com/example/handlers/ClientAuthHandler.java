package com.example.handlers;

import com.example.message.Message;
import com.example.message.MessageType;
import com.example.utils.ConsoleHelper;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientAuthHandler extends SimpleChannelInboundHandler<String> {

  private String userName;
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

  private static final Logger logger = LoggerFactory.getLogger(ClientAuthHandler.class);

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String incomeMsg)
      throws JsonProcessingException {

    logger.debug(incomeMsg);

    Message msg = jsonUtil.stringToObject(incomeMsg);

    logger.debug("msg {}", msg.toString());

    logger.debug(String.format("Тип сообщения %s ", msg.getMessageType()));

    switch (msg.getMessageType()) {
      case NAME_REQUEST -> {
        logger.debug(
            String.format("%s. %s", msg.getMessageType().getMsg(), msg.getMessage()));
        ConsoleHelper.writeMessage(
            String.format("[Сервер] : %s %s", msg.getMessageType().getMsg(),
                msg.getMessage()));
        userName = ConsoleHelper.readString();
        ctx.channel()
            .writeAndFlush(jsonUtil.objectToString(new Message(MessageType.USER_NAME, userName)));
      }
      case NAME_ACCEPTED -> {
        ConsoleHelper.writeMessage(
            String.format("[Сервер] : %s %s", msg.getMessageType().getMsg(),
                msg.getMessage()));
        logger.debug(ctx.pipeline().toString());
        ctx.pipeline().remove(this);
        ctx.pipeline().addLast(new ClientHandler(ctx.channel(), userName));
        logger.debug(ctx.pipeline().toString());
        new Thread(() -> {
          try {
            new ClientHandler(ctx.channel(),
                userName).readMessageFromConsoleAndSendMessage();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }).start();
      }
      default -> {
      }
    }

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }
}

