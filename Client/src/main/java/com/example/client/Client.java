package com.example.client;


import com.example.client.settings.Settings;
import com.example.client.handlers.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class Client {
  private static final Logger logger = LoggerFactory.getLogger(Client.class);

  @Autowired
  private Settings settings;

  private Channel channel;

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  public void run() {

    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap(); // (1)
      b.group(workerGroup); // (2)
      b.channel(NioSocketChannel.class); // (3)
      b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
      b.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) {

          ch.pipeline().addLast(
              new StringDecoder(),
              new ClientHandler(),
              new StringEncoder()
          );
        }
      });

      // Start the client.
      ChannelFuture f = b.connect(settings.getHOST(), settings.getPORT()).sync(); // (5)
      setChannel(f.channel());

      logger.info("Клиент успешно запущен. HOST = {}, PORT = {}", settings.getHOST(), settings.getPORT());

      f.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      workerGroup.shutdownGracefully();
    }
  }
}
