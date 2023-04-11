package app.netty_chat;


// TODO запросить авторизацию
//        ctx.channel().writeAndFlush(new Message(MessageType.NAME_REQUEST, ""));


// TODO проверить валидность на авторизацию
// TODO проверка пустого имени
// TODO проверка повторного подключения с данным именем
// TODO если все успешно добавить пользователя в мапу конектов

import app.netty_chat.dao.ChatChannels;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerAuthHandler extends SimpleChannelInboundHandler<Message> {

    private Channel channel;

    private final Logger logger = Logger.getLogger(ServerAuthHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.log(Level.INFO, "Попытка подключения {0}, запрос на авторизацию", ctx.channel().remoteAddress());
        this.channel = ctx.channel();
        channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        if (msg.getMessageType() == MessageType.USER_NAME) {

            String userName = msg.getMessage();

            if (!userName.isEmpty()) {

                ChatChannels.getInstance().getConnectionMap().put(userName, channel);

                ctx.pipeline().remove(this);
                ctx.pipeline().addLast(new ServerMessageHandler());
                channel.writeAndFlush(new Message(MessageType.NAME_ACCEPTED));

                for (Channel ch : ChatChannels.getInstance().getConnectionMap().values()) {
                    ch.writeAndFlush(new Message(MessageType.USER_ADDED,
                            "Пользователь " + userName + " подключился к чату"));
                }

            } else {
                logger.log(Level.SEVERE, "Ошибка авторизации. Попытка подключения к серверу с пустым именем от {0}", ctx.channel().remoteAddress());
                channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
            }


        } else {
            logger.log(Level.SEVERE, "Ошибка авторизации. Тип сообщения не соответствует протоколу {0}", ctx.channel().remoteAddress());
            channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}


