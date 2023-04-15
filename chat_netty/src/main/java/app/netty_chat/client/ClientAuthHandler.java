package app.netty_chat.client;

import app.netty_chat.*;
import app.netty_chat.message.Message;
import app.netty_chat.message.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ClientAuthHandler extends SimpleChannelInboundHandler<Message> {

    private String userName;

    private static final Logger logger = LoggerFactory.getLogger(ClientAuthHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info(String.format("Тип сообщения %s ", msg.getMessageType()));

        if (msg.getMessageType() == MessageType.NAME_REQUEST) {
            logger.info("Запрос авторизации от сервера");
            ConsoleHelper.writeMessage("[Сервер] : " + msg.getMessageType().getMsg());
            userName = ConsoleHelper.readString();
            ctx.channel().writeAndFlush(new Message(MessageType.USER_NAME, userName));
        } else if (msg.getMessageType() == MessageType.NAME_ACCEPTED) {
            ConsoleHelper.writeMessage("[Сервер] : " + msg.getMessageType().getMsg());
            logger.debug(ctx.pipeline().toString());
            ctx.pipeline().remove(this);
            ctx.pipeline().addAfter("ObjectDecoder#0", "ClientHandler#0", new ClientHandler(ctx.channel(), userName));
            logger.debug(ctx.pipeline().toString());
            new Thread(() -> {
                try {
                    new ClientHandler(ctx.channel(), userName).readMessageFromConsoleAndSendMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

