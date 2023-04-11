package app.netty_chat;




// TODO запросить авторизацию
//        ctx.channel().writeAndFlush(new Message(MessageType.NAME_REQUEST, ""));


// TODO проверить валидность на авторизацию
// TODO проверка пустого имени
// TODO проверка повторного подключения с данным именем
// TODO если все успешно добавить пользователя в мапу конектов

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AuthHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message message) throws Exception {

    }
}


