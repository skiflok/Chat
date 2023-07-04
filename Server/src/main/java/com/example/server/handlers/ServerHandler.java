package com.example.server.handlers;

import com.example.message.Message;
import com.example.server.menu.ApplicationChatMenu;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
  private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();
  private ApplicationChatMenu applicationChatMenu;
  private Channel channel;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    logger.debug(msg);
    applicationChatMenu.messageHandler(msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws JsonProcessingException {
    logger.info("Попытка подключения {}, запрос на авторизацию", ctx.channel().remoteAddress());
    this.channel = ctx.channel();
    applicationChatMenu = new ApplicationChatMenu(channel, jsonUtil);
    applicationChatMenu.menu();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }
}
