package app.netty_chat.client;

import app.netty_chat.ConsoleHelper;
import app.netty_chat.Message;
import app.netty_chat.MessageType;
import app.netty_chat.ServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private Channel channel;

    public ClientHandler(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.log(Level.INFO, "Соединение установлено");
        super.channelActive(ctx);
//        readMessageFromConsoleAndSendMessage();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        logger.log(Level.INFO, String.format("Сообщение от сервера. ТИП %s ", msg.getMessageType()));
        System.out.print(msg.getMessage());
    }

    public void sendMessage(String text) {
        logger.log(Level.INFO, this.getClass().toString());
        // Отправка сообщения на сервер
        channel.writeAndFlush(new Message(MessageType.TEXT, text));
    }

    public void readMessageFromConsoleAndSendMessage() throws IOException {
        logger.log(Level.INFO, this.getClass().toString());
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
        logger.log(Level.SEVERE, "Ошибка соединения");
        cause.printStackTrace();
        ctx.close();
    }

}
