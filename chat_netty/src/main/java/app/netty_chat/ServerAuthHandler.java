package app.netty_chat;


// TODO запросить авторизацию
//        ctx.channel().writeAndFlush(new Message(MessageType.NAME_REQUEST, ""));


// TODO проверить валидность на авторизацию
// TODO проверка пустого имени
// TODO проверка повторного подключения с данным именем
// TODO если все успешно добавить пользователя в мапу конектов

import app.netty_chat.dao.ClientStorage;
import app.netty_chat.message.Message;
import app.netty_chat.message.MessageType;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAuthHandler extends SimpleChannelInboundHandler<Message> {

    private Channel channel;

    private static final Logger logger
            = LoggerFactory.getLogger(ServerAuthHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx)  {
        logger.info("Попытка подключения {}, запрос на авторизацию", ctx.channel().remoteAddress());
        this.channel = ctx.channel();
        channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        logger.info("Ответ на запрос авторизации от {} ", ctx.channel().remoteAddress());
        if (msg.getMessageType() == MessageType.USER_NAME) {
            logger.debug("Тип соответствует протоколу\n");
            String userName = msg.getMessage();

            if (!userName.isEmpty()) {
                logger.debug("Имя не пустое");


                //TODO проверка на повторное подключение
                ClientStorage.getInstance().getConnectionMap().put(userName, channel);

                channel.writeAndFlush(new Message(MessageType.NAME_ACCEPTED));
                ctx.pipeline().remove(this);
                ctx.pipeline().addLast(new ServerMessageHandler());

                for (Channel ch : ClientStorage.getInstance().getConnectionMap().values()) {
                    ch.writeAndFlush(new Message(MessageType.USER_ADDED,
                            "[Сервер] : Пользователь " + userName + " подключился к чату\n"));
                }

                logger.info("Список соединений\n {}", ClientStorage.getInstance().toString());
                logger.debug("Список хендлеров {}", ctx.pipeline().toString());

                logger.info("Авторизация {} завершена, пользователь {}", ctx.channel().remoteAddress(), userName);

            } else {
                logger.info("Ошибка авторизации. Попытка подключения к серверу с пустым именем от {}", ctx.channel().remoteAddress());
                channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
            }


        } else {
            logger.info("Ошибка авторизации. Тип сообщения не соответствует протоколу {}", ctx.channel().remoteAddress());
            channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}


