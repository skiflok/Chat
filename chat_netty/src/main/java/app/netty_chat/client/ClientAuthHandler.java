package app.netty_chat.client;

import app.netty_chat.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientAuthHandler extends SimpleChannelInboundHandler<Message> {

    private final Logger logger = Logger.getLogger(ClientAuthHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        logger.log(Level.INFO, String.format("Тип сообщения %s ", msg.getMessageType()));


        if (msg.getMessageType() == MessageType.NAME_REQUEST) {
            logger.log(Level.INFO, "Запрос авторизации от сервера");
            ConsoleHelper.writeMessage("[Сервер] : " + msg.getMessageType().getMsg());
            ctx.channel().writeAndFlush(new Message(MessageType.USER_NAME,
                    ConsoleHelper.readString()));
        } else if (msg.getMessageType() == MessageType.NAME_ACCEPTED) {
            ConsoleHelper.writeMessage("[Сервер] : " +msg.getMessageType().getMsg());
            logger.log(Level.INFO, ctx.pipeline().toString());
//            ctx.pipeline().replace()
            ctx.pipeline().remove(this);
            ctx.pipeline().addAfter("ObjectDecoder#0", "ClientHandler#0", new ClientHandler(ctx.channel()));
            logger.log(Level.INFO, ctx.pipeline().toString());
            new Thread(() -> {
                try {
                    new ClientHandler(ctx.channel()).readMessageFromConsoleAndSendMessage();
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

