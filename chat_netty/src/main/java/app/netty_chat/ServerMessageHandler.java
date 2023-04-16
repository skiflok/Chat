package app.netty_chat;

import app.netty_chat.dao.UserStorage;
import app.netty_chat.message.Message;
import app.netty_chat.message.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {

    UserStorage userStorage = UserStorage.getInstance();

    private static final Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);


    public void channelActive(ChannelHandlerContext ctx)  {
        logger.info("пользователь подключился {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // Notify clients when someone disconnects.
        logger.info("пользователь {} отключился {}", "[ИМЯ]", ctx.channel().remoteAddress());
        logger.info("Список соединений\n{}", UserStorage.getInstance().toString());
        broadcastMessage("[SERVER] - " + ctx.channel().remoteAddress() + " has left the chat!");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg)  {
        logger.debug("Пользователь {} прислал сообщение = {}", ctx.channel().remoteAddress(), msg.getMessage());
//        broadcastMessage(msg);
        for (Channel channel : userStorage.getConnectionMap().values()) {
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
        for (var channel : userStorage.getConnectionMap().entrySet()) {
            if (ctx.channel().equals(channel.getValue())) {
                userStorage.removeUser(channel.getKey());
            }
        }
        logger.info("ctx {}", ctx);
        ctx.close();
    }

    private void broadcastMessage(String msg) {
        logger.info("[{}]",msg);
        for (var channel : userStorage.getConnectionMap().values()) {
            channel.writeAndFlush(new Message(MessageType.TEXT,msg + "\n"));
            logger.info("channel = {}", channel);
        }
    }
}
