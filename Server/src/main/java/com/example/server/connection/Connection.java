package com.example.server.connection;

import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Connection {

  private final Channel channel;
  @Setter
  private String roomName;
}
