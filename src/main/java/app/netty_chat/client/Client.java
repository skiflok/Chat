package app.netty_chat.client;


import app.netty_chat.Connection;
import app.netty_chat.ConsoleHelper;
import app.netty_chat.Message;
import app.netty_chat.MessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private final String HOST;
    private final int PORT;


    public Client(String HOST, int PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public void run () {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }



    }

//    protected Connection connection;
//
//    private volatile boolean clientConnected;
//
//
//    protected String getServerAddress() {
//        ConsoleHelper.writeMessage("Введите адрес сервера:");
//        return ConsoleHelper.readString();
//    }
//
//    protected int getServerPort() {
//        ConsoleHelper.writeMessage("Введите порт сервера:");
//        return ConsoleHelper.readInt();
//    }
//
//    protected String getUserName() {
//        ConsoleHelper.writeMessage("Введите ваше имя:");
//        return ConsoleHelper.readString();
//    }
//
//    protected boolean shouldSendTextFromConsole() {
//        return true;
//    }
//
//    SocketThread getSocketThread() {
//        return new SocketThread();
//    }
//
//    void sendTextMessage(String text) {
//        try {
//            connection.send(new Message(MessageType.TEXT, text));
//        } catch (IOException e) {
//            logger.log(Level.SEVERE, "Не удалось отправить сообщение");
//            clientConnected = false;
//            ConsoleHelper.writeMessage("Не удалось отправить сообщение");
//        }
//    }
//
//
//    public void run() {
//        SocketThread socketThread = getSocketThread();
//        socketThread.setDaemon(true);
//        socketThread.start();
//
//        try {
//            synchronized (this) {
//                wait();
//            }
//        } catch (InterruptedException e) {
//            logger.log(Level.SEVERE, "Произошла ошибка во время работы клиента.");
//            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
//            return;
//        }
//
//        if (clientConnected)
//            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
//        else
//            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
//
//        while (clientConnected) {
//            String text = ConsoleHelper.readString();
//            if (text.equalsIgnoreCase("exit"))
//                break;
//
//            if (shouldSendTextFromConsole())
//                sendTextMessage(text);
//        }
//    }
//
//
//    public class SocketThread extends Thread {
//
//
//        @Override
//        public void run() {
//            try {
//                // Создаем соединение с сервером
//                connection = new Connection(new Socket(getServerAddress(), getServerPort()));
//
//                clientHandshake();
//                clientMainLoop();
//
//            } catch (IOException | ClassNotFoundException e) {
//                notifyConnectionStatusChanged(false);
//            }
//        }
//
//        void processIncomingMessage(String message){
//            ConsoleHelper.writeMessage(message);
//        }
//
//        void informAboutAddingNewUser(String userName){
//            ConsoleHelper.writeMessage("Участник '" + userName + "' присоединился к чату.");
//        }
//
//        void informAboutDeletingNewUser(String userName){
//            ConsoleHelper.writeMessage("Участник '" + userName + "' покинул чат.");
//        }
//
//        void notifyConnectionStatusChanged(boolean clientConnected){
//            Client.this.clientConnected = clientConnected;
//            synchronized (Client.this) {
//                Client.this.notify();
//            }
//        }
//
//
//        protected void clientHandshake() throws IOException, ClassNotFoundException {
//            while (true) {
//                Message message = connection.receive();
//
//                if (message.getMessageType() == MessageType.NAME_REQUEST) { // Сервер запросил имя пользователя
//                    // Запрашиваем ввод имени с консоли
//                    String name = getUserName();
//                    // Отправляем имя на сервер
//                    connection.send(new Message(MessageType.USER_NAME, name));
//
//                } else if (message.getMessageType() == MessageType.NAME_ACCEPTED) { // Сервер принял имя пользователя
//                    // Сообщаем главному потоку, что он может продолжить работу
//                    notifyConnectionStatusChanged(true);
//                    return;
//
//                } else {
//                    logger.log(Level.SEVERE, "Unexpected MessageType.");
//                    throw new IOException("Unexpected MessageType");
//                }
//            }
//        }
//
//        protected void clientMainLoop() throws IOException, ClassNotFoundException {
//
//            // Цикл обработки сообщений сервера
//            while (true) {
//                Message message = connection.receive();
//
//                if (message.getMessageType() == MessageType.TEXT) { // Сервер прислал сообщение с текстом
//                    processIncomingMessage(message.getMessage());
//                } else if (MessageType.USER_ADDED == message.getMessageType()) {
//                    informAboutAddingNewUser(message.getMessage());
//                } else if (MessageType.USER_REMOVED == message.getMessageType()) {
//                    informAboutDeletingNewUser(message.getMessage());
//                } else {
//                    logger.log(Level.SEVERE, "Unexpected MessageType.");
//                    throw new IOException("Unexpected MessageType");
//                }
//            }
//        }
//
//
//    }

    public static void main(String[] args) throws IOException {

        ConsoleHelper.writeMessage("Введите адрес сервера");

        String host = ConsoleHelper.readString();

        ConsoleHelper.writeMessage("Введите порт");

        int port = ConsoleHelper.readInt();

        Client client = new Client(host, port);
        client.run();


//        Client client = new Client();
//        client.run();

    }

}