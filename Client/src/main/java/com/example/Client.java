package com.example;

import com.example.handlers.ClientHandler;
import com.example.utils.PropertiesLoader;
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

public class Client {

  private static final Logger logger = LoggerFactory.getLogger(Client.class);
  private final String HOST;
  private final int PORT;

  private Channel channel;

  public void setChannel(Channel channel) {
    this.channel = channel;
  }

  private final PropertiesLoader propertiesLoader = PropertiesLoader.getPropertiesLoader();

  {
    PORT = Integer.parseInt(propertiesLoader.getProperty("server.port"));
    HOST = propertiesLoader.getProperty("server.host");
  }

  public String getHOST() {
    return HOST;
  }

  public int getPORT() {
    return PORT;
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
      ChannelFuture f = b.connect(HOST, PORT).sync(); // (5)
      setChannel(f.channel());

      logger.info("Клиент успешно запущен. HOST = {}, PORT = {}", this.getHOST(), this.getPORT());

      f.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      workerGroup.shutdownGracefully();
    }


  }

  public static void main(String[] args) {

    Client client = new Client();
    client.run();

  }

}
