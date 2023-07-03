package com.example.handlers;

import com.example.message.Message;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler_new extends SimpleChannelInboundHandler<String> {

  private static final Logger logger = LoggerFactory.getLogger(ClientHandler_new.class);
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String incomeMsg) throws Exception {

      Message msg = jsonUtil.stringToObject(incomeMsg);
      logger.debug(String.format("Сообщение от сервера. ТИП %s ", msg.getMessageType()));
      System.out.print(msg.getMessage());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.warn("Ошибка соединения");
    cause.printStackTrace();
    ctx.close();
  }
}
