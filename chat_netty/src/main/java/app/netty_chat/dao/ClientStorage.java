package app.netty_chat.dao;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.html.parser.Entity;


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

    public void addUser (String userName, Channel channel) {
        //TODO
        connectionMap.put(userName, channel);
    }

    public void removeUser (String userName) {
        //TODO
        connectionMap.remove(userName);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (var entry: connectionMap.entrySet()) {
            res.append(entry);
            res.append("\n");
        }
        return res.toString();
    }
}
