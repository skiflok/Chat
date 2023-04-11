package app.netty_chat.client;

import app.netty_chat.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientAuthHandler extends SimpleChannelInboundHandler<Message> {

    private final Logger logger = Logger.getLogger(ClientAuthHandler.class.getName());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        logger.log(Level.INFO, "Запрос авторизации от сервера");
        if (msg.getMessageType()== MessageType.NAME_REQUEST) {
            ConsoleHelper.writeMessage(msg.getMessageType().getMsg());
            ctx.channel().writeAndFlush(new Message(MessageType.USER_NAME,
                    ConsoleHelper.readString()));
        } else if (msg.getMessageType()== MessageType.NAME_ACCEPTED) {
            ConsoleHelper.writeMessage(msg.getMessageType().getMsg());
            ctx.pipeline().remove(this);
            ctx.pipeline().addLast(new ClientHandler(ctx.channel()));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}

