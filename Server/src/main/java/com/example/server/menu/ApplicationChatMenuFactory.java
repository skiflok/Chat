package com.example.server.menu;

import com.example.message.Message;
import com.example.utils.json.util.JsonUtil;
import io.netty.channel.Channel;

public class ApplicationChatMenuFactory {
  private final Channel channel;
  private final JsonUtil<Message> jsonUtil;

  public ApplicationChatMenuFactory(Channel channel, JsonUtil<Message> jsonUtil) {
    this.channel = channel;
    this.jsonUtil = jsonUtil;
  }

  public ApplicationChatMenu createMenu() {
    return new ApplicationChatMenu(channel, jsonUtil);
  }
}
