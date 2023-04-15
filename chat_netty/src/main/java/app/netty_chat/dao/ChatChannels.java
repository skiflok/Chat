package app.netty_chat.dao;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChatChannels {

    private static ChatChannels instance = null;

    private static final Logger logger = LoggerFactory.getLogger(ChatChannels.class);

    private final List<Channel> channels = new ArrayList<>();


    private final Map<String, Channel> connectionMap = new ConcurrentHashMap<>();

    public Map<String, Channel> getConnectionMap() {
        return connectionMap;
    }
    private ChatChannels() {}

    public List<Channel> getChannels() {
        return channels;
    }

    public static ChatChannels getInstance () {
        if (instance == null) {
            instance = new ChatChannels();
        }
        return instance;
    }


}
