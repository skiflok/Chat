package com.example.server;

import com.example.server.handlers.ServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

  @Autowired
  private ObjectProvider<ServerHandler> serverHandlerProvider;

  @Override
  protected void initChannel(SocketChannel socketChannel) {
    ServerHandler serverHandler = serverHandlerProvider.getObject();
    socketChannel.pipeline().addLast(
        new StringDecoder(),
        serverHandler,
        new StringEncoder()
    );
  }
}
