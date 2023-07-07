package com.example.server.connection;

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
  private final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

  public Collection<Channel> getChannelList() {
    return connectionMap.values().stream()
        .map(Connection::getChannel).toList();
  }

  public Collection<Connection> getConnectionList() {
    return connectionMap.values();
  }

  public boolean contains(String userName) {
    return connectionMap.containsKey(userName);
  }

  public void addUser(String userName, Channel channel) {
    //TODO
    connectionMap.put(userName, new Connection(channel));
  }

  public void addUser(String userName, Channel channel, String roomName) {
    //TODO
    connectionMap.put(userName, new Connection(channel, roomName));
  }

  public void removeUser(String userName) {
    //TODO
    connectionMap.remove(userName);
  }

  public void setRoom(String userName, String roomNane) {
    connectionMap.get(userName).setRoomName(roomNane);
  }

}
