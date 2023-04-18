package app.netty_chat;

//TODO запрос пароля

import app.netty_chat.dao.ActiveConnectionStorage;
import app.netty_chat.dao.User;
import app.netty_chat.dao.UserStorage;
import app.netty_chat.exception.AuthorisationErrorException;
import app.netty_chat.exception.NameAlreadyUseException;
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
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("Попытка подключения {}, запрос на авторизацию", ctx.channel().remoteAddress());
        this.channel = ctx.channel();
        channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws NameAlreadyUseException, AuthorisationErrorException {
        logger.info("Ответ на запрос авторизации от {} ", ctx.channel().remoteAddress());

        if (msg.getMessageType() != MessageType.USER_NAME) {
            logger.info("Ошибка авторизации. Тип сообщения не соответствует протоколу {}", ctx.channel().remoteAddress());
            channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
            throw new AuthorisationErrorException();
        }

        logger.debug("Тип соответствует протоколу\n");
        String userName = msg.getMessage();

        if (userName.isEmpty()) {
            logger.debug("Имя пустое");
            logger.info("Ошибка авторизации. Попытка подключения к серверу с пустым именем от {}", ctx.channel().remoteAddress());
            channel.writeAndFlush(new Message(MessageType.NAME_REQUEST));
            throw new AuthorisationErrorException();
        }

        if (ActiveConnectionStorage.getInstance().getConnectionMap().containsKey(userName)) {
            logger.info("Ошибка авторизации. Попытка подключения к серверу с уже используемым именем {} от {}", userName ,ctx.channel().remoteAddress());
            channel.writeAndFlush(new Message(MessageType.NAME_REQUEST, userName + " уже используется"));
            throw new NameAlreadyUseException();
        }

        ActiveConnectionStorage.getInstance().addUser(userName, channel);

        //TODO перевести все на Юзеров
        UserStorage.getInstance().addUser(new User(userName));
        channel.writeAndFlush(new Message(MessageType.NAME_ACCEPTED));

        ctx.pipeline().remove(this);
        ctx.pipeline().addLast(new ServerMessageHandler());

        for (Channel ch : ActiveConnectionStorage.getInstance().getConnectionMap().values()) {
            ch.writeAndFlush(new Message(MessageType.USER_ADDED,
                    "[Сервер] : Пользователь " + userName + " подключился к чату\n"));
        }

        logger.info("Список соединений\n{}", ActiveConnectionStorage.getInstance().toString());
        logger.debug("Список хендлеров {}", ctx.pipeline().toString());
        logger.info("Авторизация {} завершена, пользователь {}", ctx.channel().remoteAddress(), userName);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}


