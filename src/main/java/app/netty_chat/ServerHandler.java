package app.netty_chat;


import app.netty_chat.dao.ChatChannels;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerHandler extends SimpleChannelInboundHandler<String> {

    ChatChannels chatChannels= ChatChannels.getInstance();
    List<Channel> channels = chatChannels.getChannels();

    private final Logger logger = Logger.getLogger(ServerHandler.class.getName());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.log(Level.INFO, "пользователь подключился {0}", ctx.channel().remoteAddress());
        // Notify clients when someone disconnects.
        channels.add(ctx.channel());
        ctx.writeAndFlush("[SERVER] - " + ctx.channel().remoteAddress() + " has join the chat!\n");
        for (Channel channel : channels) {
            System.out.println(channel);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // Notify clients when someone disconnects.
        logger.log(Level.INFO, "пользователь отключился {0}", ctx.channel().remoteAddress());
//        ctx.writeAndFlush("[SERVER] - " + ctx.channel().remoteAddress() + " has left the chat!\n");
        broadcastMessage("[SERVER] - " + ctx.channel().remoteAddress() + " has left the chat!\n");
        channels.remove(ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.log(Level.INFO, "Пользователь {0} прислал сообщение", ctx.channel().remoteAddress());
//        ctx.channel().writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg);
        System.out.println(msg);
//        broadcastMessage(msg);
        for (Channel channel : channels) {
            if (channel != ctx.channel()) {
                channel.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + "\n");
            } else {
                channel.writeAndFlush("[you] " + msg + "\n");
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

    //    /**
//     * Обработчик сообщений
//     */
//    private static class Handler extends Thread {
//        private final Socket socket;
//
//        public Handler(Socket socket) {
//            this.socket = socket;
//        }
//
//        /**
//         * Авторизация пользователя по имени, отправляет запросы,
//         * вносит в базу участников чата для рассылки
//         * авторизация по уникальному имени пользователя
//         *
//         * @param connection соединение
//         * @return имя пользователя
//         * @throws IOException
//         * @throws ClassNotFoundException
//         */
//        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
//            while (true) {
//
//                connection.send(new Message(MessageType.NAME_REQUEST));
//
//                Message message = connection.receive();
//
//                if (message.getMessageType() != MessageType.USER_NAME) {
//                    logger.log(Level.SEVERE, "Получено сообщение от " +
//                            socket.getRemoteSocketAddress() +
//                            ". Тип сообщения не соответствует протоколу.");
//                    ConsoleHelper.writeMessage("Получено сообщение от " +
//                            socket.getRemoteSocketAddress() +
//                            ". Тип сообщения не соответствует протоколу.");
//                    continue;
//                }
//
//                String userName = message.getMessage();
//
//                if (userName.isEmpty()) {
//                    logger.log(Level.SEVERE, "Попытка подключения к серверу с пустым именем от " + socket.getRemoteSocketAddress());
//                    ConsoleHelper.writeMessage("Попытка подключения к серверу с пустым именем от " + socket.getRemoteSocketAddress());
//                    continue;
//                }
//
//                if (connectionMap.containsKey(userName)) {
//                    logger.log(Level.SEVERE, "Попытка подключения к серверу с уже используемым именем от " + socket.getRemoteSocketAddress());
//                    ConsoleHelper.writeMessage("Попытка подключения к серверу с уже используемым именем от " + socket.getRemoteSocketAddress());
//                    continue;
//                }
//
//                connectionMap.put(message.getMessage(), connection);
//
//                connection.send(new Message(MessageType.NAME_ACCEPTED));
//
//                return userName;
//            }
//
//        }
//
//
//        private void notifyUsers(Connection connection, String userName) throws IOException {
//
//            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
//                if (!entry.getKey().equalsIgnoreCase(userName)) {
//                    connection.send(new Message(MessageType.USER_ADDED, userName));
//                }
//            }
//
//
//        }
//
//        /**
//         * главный цикл обработки сообщений сервером,
//         * принимает сообщения от клиента и рассылает всем клиентам
//         *
//         * @param connection Соединение
//         * @param userName   имя отправителя
//         * @throws IOException
//         * @throws ClassNotFoundException
//         */
//        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
//
//            while (true) {
//
//                Message message = connection.receive();
//
//                if (message.getMessageType() == MessageType.TEXT) {
//                    String textMessage = String.format("%s: %s", userName, message.getMessage());
//                    sendBroadcastMessage(new Message(MessageType.TEXT, textMessage));
//                } else {
//                    logger.log(Level.SEVERE, "Получено сообщение от " +
//                            socket.getRemoteSocketAddress() +
//                            ". Тип сообщения не соответствует протоколу.");
//                    ConsoleHelper.writeMessage("Получено сообщение от " +
//                            socket.getRemoteSocketAddress() +
//                            ". Тип сообщения не соответствует протоколу.");
//                }
//            }
//        }
//
//
//        @Override
//        public void run() {
//
//            logger.log(Level.INFO, "run() socket {0}", socket.getRemoteSocketAddress());
//            ConsoleHelper.writeMessage("Установлено новое соединение с " + socket.getRemoteSocketAddress());
//
//            String userName = null;
//
//            try (Connection connection = new Connection(socket)) {
//
//                userName = serverHandshake(connection);
//
//                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
//
//                notifyUsers(connection, userName);
//
//                serverMainLoop(connection, userName);
//
//            } catch (IOException | ClassNotFoundException e) {
//                logger.log(Level.SEVERE, "Ошибка при обмене данными с " + socket.getRemoteSocketAddress());
//                ConsoleHelper.writeMessage("Ошибка при обмене данными с " + socket.getRemoteSocketAddress());
//            }
//
//            if (userName != null) {
//                connectionMap.remove(userName);
//                logger.log(Level.INFO, "Пользователь {0} удален", userName);
//                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
//            }
//
//            logger.log(Level.INFO, "Соединение с " + socket.getRemoteSocketAddress() + " закрыто.");
//            ConsoleHelper.writeMessage("Соединение с " + socket.getRemoteSocketAddress() + " закрыто.");
//        }
//    }


}
