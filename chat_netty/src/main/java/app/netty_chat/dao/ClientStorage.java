package app.netty_chat.dao;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientStorage {

    private static ClientStorage instance = null;

    private static final Logger logger = LoggerFactory.getLogger(ClientStorage.class);

    private final List<Channel> channels = new ArrayList<>();

    private final Map<String, Channel> connectionMap = new ConcurrentHashMap<>();

    public Map<String, Channel> getConnectionMap() {
        return connectionMap;
    }

    private ClientStorage() {
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public static ClientStorage getInstance() {
        if (instance == null) {
            instance = new ClientStorage();
        }
        return instance;
    }

    public void addUser () {

    }

    public void removeUser () {

    }

}
