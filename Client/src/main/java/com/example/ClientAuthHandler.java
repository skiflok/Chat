package com.example;

import com.example.message.Message;
import com.example.message.MessageType;
import com.example.utils.ConsoleHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientAuthHandler extends SimpleChannelInboundHandler<Message> {

    private String userName;

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info(String.format("Тип сообщения %s ", msg.getMessageType()));

        switch (msg.getMessageType()) {
            case NAME_REQUEST :

                logger.info(String.format("%s. %s", msg.getMessageType().getMsg(), msg.getMessage()));
                ConsoleHelper.writeMessage(String.format("[Сервер] : %s %s", msg.getMessageType().getMsg(), msg.getMessage()));
                userName = ConsoleHelper.readString();
                ctx.channel().writeAndFlush(new Message(MessageType.USER_NAME, userName));
                break;
            case NAME_ACCEPTED:

                ConsoleHelper.writeMessage(String.format("[Сервер] : %s %s", msg.getMessageType().getMsg(), msg.getMessage()));
                logger.debug(ctx.pipeline().toString());
                ctx.pipeline().remove(this);
                ctx.pipeline().addLast(new ClientHandler(ctx.channel(), userName));
                logger.debug(ctx.pipeline().toString());
                new Thread(() -> {
                    try {
                        new ClientHandler(ctx.channel(), userName).readMessageFromConsoleAndSendMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();

                break;
            default:

        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

