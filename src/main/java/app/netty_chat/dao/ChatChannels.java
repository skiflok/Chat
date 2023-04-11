package app.netty_chat.dao;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ChatChannels {

    private static ChatChannels instance = null;

    private final Logger logger = Logger.getLogger(ChatChannels.class.getName());

    private final List<Channel> channels = new ArrayList<>();

    public Map<String, Channel> getConnectionMap() {
        return connectionMap;
    }

    private final Map<String, Channel> connectionMap = new ConcurrentHashMap<>();

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
