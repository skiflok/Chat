package com.example.server.handlers;

import com.example.model.message.Message;
import com.example.model.User;
import com.example.server.connection.ActiveConnectionStorage;
import com.example.server.menu.ApplicationChatMenu;
import com.example.utils.json.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ServerHandler extends SimpleChannelInboundHandler<String> {

  @Autowired
  private ApplicationContext applicationContext;

  @Autowired
  private JsonUtil<Message> jsonUtil;

  @Autowired
  private ActiveConnectionStorage activeConnectionStorage;
  private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
  private ApplicationChatMenu applicationChatMenu;
  private Channel channel;

  private User user;

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    logger.debug(msg);
    applicationChatMenu.messageHandler(msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws JsonProcessingException {
    logger.info("Попытка подключения {}, запрос на авторизацию", ctx.channel().remoteAddress());
    this.channel = ctx.channel();
    applicationChatMenu = applicationContext.getBean("applicationChatMenu", ApplicationChatMenu.class);
    applicationChatMenu.init(ctx, user);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.info("Пользователь {} отключился", ctx.channel().remoteAddress());
    if (user != null) {
      activeConnectionStorage.removeUser(user.getName());
    }
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }
}
