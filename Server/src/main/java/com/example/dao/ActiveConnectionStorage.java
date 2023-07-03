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

  private static ActiveConnectionStorage instance = null;

  private static final Logger logger = LoggerFactory.getLogger(ActiveConnectionStorage.class);

  private final Map<String, Channel> connectionMap = new ConcurrentHashMap<>();

  public Map<String, Channel> getConnectionMap() {
    return connectionMap;
  }

    public Collection<Channel> getConnectionList () {
        return connectionMap.values();
    }

  private ActiveConnectionStorage() {
  }

    public boolean contains(String userName) {
        return connectionMap.containsKey(userName);
    }

  public static ActiveConnectionStorage getInstance() {
    if (instance == null) {
      instance = new ActiveConnectionStorage();
    }
    return instance;
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
