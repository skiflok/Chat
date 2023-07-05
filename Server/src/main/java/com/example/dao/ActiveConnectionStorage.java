package com.example.dao;

import io.netty.channel.Channel;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ActiveConnectionStorage {

  private static final Logger logger = LoggerFactory.getLogger(ActiveConnectionStorage.class);
  private final Map<String, Channel> connectionMap = new ConcurrentHashMap<>();

  public Map<String, Channel> getConnectionMap() {
    return connectionMap;
  }

  public Collection<Channel> getConnectionList() {
    return connectionMap.values();
  }

  public boolean contains(String userName) {
    return connectionMap.containsKey(userName);
  }


  public void addUser(String userName, Channel channel) {
    //TODO
    connectionMap.put(userName, channel);
  }

  public void removeUser(String userName) {
    //TODO
    connectionMap.remove(userName);
  }

}
