package app.netty_chat;

import app.netty_chat.dao.ChatChannels;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMessageHandler extends SimpleChannelInboundHandler<Message> {

    ChatChannels chatChannels = ChatChannels.getInstance();
    List<Channel> channels = chatChannels.getChannels();

    private final Logger logger = Logger.getLogger(ServerMessageHandler.class.getName());


    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.log(Level.INFO, "пользователь подключился {0}", ctx.channel().remoteAddress());
        // Notify clients when someone disconnects.
        channels.add(ctx.channel());
        ctx.writeAndFlush(new Message(MessageType.TEXT,
                "[SERVER] - " + ctx.channel().remoteAddress() + " has join the chat!\n"));
        for (Channel channel : channels) {
            System.out.println(channel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // Notify clients when someone disconnects.
        logger.log(Level.INFO, "пользователь отключился {0}", ctx.channel().remoteAddress());
        broadcastMessage("[SERVER] - " + ctx.channel().remoteAddress() + " has left the chat!\n");
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        logger.log(Level.INFO, "Пользователь {0} прислал сообщение", ctx.channel().remoteAddress());
        System.out.println(msg.getMessage());
//        broadcastMessage(msg);
        for (Channel channel : chatChannels.getConnectionMap().values()) {
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.SEVERE, "Ошибка  {0}", cause.getMessage());
        channels.remove(ctx.channel());
        ctx.close();
    }

    private void broadcastMessage(String message) {
        for (Channel channel : channels) {
            channel.writeAndFlush(message + "\n");
        }
    }

    /**
     *
     */
    void serverHandshake() {

    }
}
