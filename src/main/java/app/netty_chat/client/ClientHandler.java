package app.netty_chat.client;

import app.netty_chat.Message;
import app.netty_chat.ServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private final Logger logger = Logger.getLogger(ClientHandler.class.getName());


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        logger.log(Level.INFO, "channelRead0");
        System.out.print(msg.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.log(Level.SEVERE, "Ошибка соединения");
        cause.printStackTrace();
        ctx.close();
    }

}
