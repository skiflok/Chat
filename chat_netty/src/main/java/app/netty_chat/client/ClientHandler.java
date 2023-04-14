package app.netty_chat.client;

import app.netty_chat.ConsoleHelper;
import app.netty_chat.Message;
import app.netty_chat.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ClientHandler extends SimpleChannelInboundHandler<Message> {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    private final Channel channel;

    public ClientHandler(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Соединение установлено");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg)  {
        logger.debug(String.format("Сообщение от сервера. ТИП %s ", msg.getMessageType()));
        System.out.print(msg.getMessage());
    }

    public void sendMessage(String text) {
        logger.debug("sendMessage");
        // Отправка сообщения на сервер
        channel.writeAndFlush(new Message(MessageType.TEXT, text));
    }

    public void readMessageFromConsoleAndSendMessage() throws IOException {
        logger.debug(this.getClass().toString());
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
        logger.warn("Ошибка соединения");
        cause.printStackTrace();
        ctx.close();
    }

}
