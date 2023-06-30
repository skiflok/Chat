package com.example;


import com.example.message.Message;
import com.example.message.MessageType;
import com.example.utils.ConsoleHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final Channel channel;

    private final String userName;

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
    protected void channelRead0(ChannelHandlerContext ctx, Message msg)  {
        logger.debug(String.format("Сообщение от сервера. ТИП %s ", msg.getMessageType()));
        System.out.print(msg.getMessage());
    }

    public void sendMessage(String text) {
        logger.debug("sendMessage");
        // Отправка сообщения на сервер
        channel.writeAndFlush(new Message(MessageType.TEXT, text, userName));
    }

    public void readMessageFromConsoleAndSendMessage() throws IOException {
        logger.debug(this.getClass().toString());
        while (true) {
            String line = ConsoleHelper.readString();
            if (line == null || "/exit".equals(line)) {
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
