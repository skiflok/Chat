package app.netty_chat;

import app.netty_chat.dao.ClientStorage;
import app.netty_chat.message.Message;
import app.netty_chat.message.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {

    ClientStorage clientStorage = ClientStorage.getInstance();
    List<Channel> channels = clientStorage.getChannels();

    private static final Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);


    public void channelActive(ChannelHandlerContext ctx)  {
        logger.info("пользователь подключился {}", ctx.channel().remoteAddress());
        // Notify clients when someone disconnects.
        channels.add(ctx.channel());
        ctx.writeAndFlush(new Message(MessageType.TEXT,
                "[SERVER] - " + ctx.channel().remoteAddress() + " has join the chat!\n"));
        for (Channel channel : channels) {
            System.out.println(channel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        // Notify clients when someone disconnects.
        logger.info("пользователь {} отключился {}", "[ИМЯ]", ctx.channel().remoteAddress());
        broadcastMessage("[SERVER] - " + ctx.channel().remoteAddress() + " has left the chat!\n");
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg)  {
        logger.debug("Пользователь {} прислал сообщение = {}", ctx.channel().remoteAddress(), msg.getMessage());
//        broadcastMessage(msg);
        for (Channel channel : clientStorage.getConnectionMap().values()) {
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
        channels.remove(ctx.channel());
        ctx.close();
    }

    private void broadcastMessage(String message) {
        for (Channel channel : channels) {
            channel.writeAndFlush(message + "\n");
        }
    }

//    /**
//     *
//     */
//    void serverHandshake() {
//
//    }
}
