package com.example.server;

import com.example.dao.UserStorage;
import com.example.utils.ApplicationSettings;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.io.IOException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


@Component
@Getter
public class Server {

  @Autowired
  private ApplicationSettings appSet;
  @Autowired
  private ApplicationContext applicationContext;
  private static final Logger logger
      = LoggerFactory.getLogger(Server.class);
  private final UserStorage userStorage = UserStorage.getInstance();

  public void start() {

    // добавляем хук для сохранения пользователей при остановке сервера
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        logger.info("Сохранение юзеров в {}", appSet.getFilePathUsers());
        userStorage.saveToFile(appSet.getFilePathUsers());
      } catch (IOException e) {
        logger.error("Failed to save user storage");
      }
    }));

    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {

      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(applicationContext.getBean("myChannelInitializer", MyChannelInitializer.class))
          .option(ChannelOption.SO_BACKLOG, 128)          // (5)
          .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

      ChannelFuture f = b.bind(appSet.getHOST(), appSet.getPORT()).sync(); // (7)
      logger.info("Сервер успешно запущен. HOST = {}, PORT = {}", appSet.getHOST(), appSet.getPORT());
      f.channel().closeFuture().sync();

    } catch (Exception e) {
      logger.error("Server was interrupted", e);
      e.printStackTrace();
    } finally {
      logger.info("finally");

      // сохранение пользователей при остановке сервера
      try {
        logger.info("Сохранение юзеров в {}", appSet.getFilePathUsers());
        userStorage.saveToFile(appSet.getFilePathUsers());
      } catch (IOException e) {
        logger.error("Failed to save user storage");
      }

      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

}
