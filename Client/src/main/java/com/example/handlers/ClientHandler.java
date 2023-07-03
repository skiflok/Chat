package com.example.handlers;


import com.example.message.Message;
import com.example.message.MessageType;
import com.example.utils.ConsoleHelper;
import com.example.utils.json.util.JsonUtil;
import com.example.utils.json.util.JsonUtilJacksonMessageImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientHandler extends SimpleChannelInboundHandler<String> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final Channel channel;

    private final String userName;

    private final JsonUtil<Message> jsonUtil = new JsonUtilJacksonMessageImpl();

    public ClientHandler(Channel channel, String userName) {
        this.channel = channel;
        this.userName = userName;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Соединение установлено");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String incomeMsg)
        throws JsonProcessingException {
        Message msg = jsonUtil.stringToObject(incomeMsg);
        logger.debug(String.format("Сообщение от сервера. ТИП %s ", msg.getMessageType()));
        System.out.print(msg.getMessage());
    }

    public void sendMessage(String text) throws JsonProcessingException {
        logger.debug("sendMessage");
        // Отправка сообщения на сервер
        channel.writeAndFlush(jsonUtil.objectToString(new Message(MessageType.TEXT, text, userName)));
    }

    public void readMessageFromConsoleAndSendMessage() throws IOException {
        logger.debug(this.getClass().toString());
        while (true) {
            String line = ConsoleHelper.readString();
            if ("/exit".equals(line)) {
                break;
            }
            sendMessage(line);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("Ошибка соединения");
        cause.printStackTrace();
        ctx.close();
    }

}
