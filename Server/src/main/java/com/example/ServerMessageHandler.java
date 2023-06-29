package com.example;

import com.example.dao.ActiveConnectionStorage;
import com.example.message.Message;
import com.example.message.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {

    ActiveConnectionStorage activeConnectionStorage = ActiveConnectionStorage.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);


    public void channelActive(ChannelHandlerContext ctx)  {
        logger.info("пользователь подключился {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // Notify clients when someone disconnects.
        logger.info("пользователь {} отключился {}", "[ИМЯ]", ctx.channel().remoteAddress());
        logger.info("Список соединений\n{}", ActiveConnectionStorage.getInstance().toString());
        broadcastMessage(new Message(MessageType.USER_REMOVED,
                "[SERVER] - " + ctx.channel().remoteAddress() + " покинул чат!"));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg)  {
        logger.debug("Пользователь {} прислал сообщение = {}", ctx.channel().remoteAddress(), msg.getMessage());
//        broadcastMessage(msg);
        for (Channel channel : activeConnectionStorage.getConnectionMap().values()) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush(new Message(MessageType.TEXT,
                        "[" + ctx.channel().remoteAddress() + "] " + msg.getMessage() + "\n"));
            } else {
                channel.writeAndFlush(new Message(MessageType.TEXT,
                        "[you] " + msg.getMessage() + "\n"));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)  {
        logger.info("Ошибка  {}", cause.getMessage());
        for (var channel : activeConnectionStorage.getConnectionMap().entrySet()) {
            if (ctx.channel().equals(channel.getValue())) {
                activeConnectionStorage.removeUser(channel.getKey());
            }
        }
        logger.info("ctx {}", ctx);
        ctx.close();
    }

    private void broadcastMessage(Message msg) {
        logger.info("[{}]",msg);
        for (var channel : activeConnectionStorage.getConnectionMap().values()) {
            channel.writeAndFlush(msg);
            logger.info("channel = {}", channel);
        }
    }
}