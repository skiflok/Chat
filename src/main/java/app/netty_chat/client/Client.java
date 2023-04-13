package app.netty_chat.client;


import app.netty_chat.Connection;
import app.netty_chat.ConsoleHelper;
import app.netty_chat.Message;
import app.netty_chat.MessageType;
import app.netty_chat.service.PropertiesLoader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private final String HOST;
    private final int PORT;

    private Channel channel;

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    private final PropertiesLoader propertiesLoader = PropertiesLoader.getPropertiesLoader();

    {
        PORT = Integer.parseInt(propertiesLoader.getProperty("server.port"));
        HOST = propertiesLoader.getProperty("server.host");
    }

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    public void run() {

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {

                    ch.pipeline().addLast(
                            new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                            new ClientAuthHandler(),
//                            new ClientHandler(ch),
                            new ObjectEncoder()
                    );
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(HOST, PORT).sync(); // (5)
            setChannel(f.channel());


//            new Thread(() -> {
//                while (true) {
//                    String line = ConsoleHelper.readString();
//                    if (line == null || "/exit".equals(line)) {
//                        break;
//                    }
//                    sendMessage(line);
//                }
//            }).start();

//            f.channel().closeFuture().addListener((ChannelFutureListener)
//                    channelFuture -> workerGroup.shutdownGracefully());


//             Ввод сообщений с консоли и отправка на сервер
//            ClientHandler handler = (ClientHandler) channel.pipeline().get("ClientHandler");
//            handler.readMessageFromConsoleAndSendMessage();

//            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            while (true) {
//                String line = in.readLine();
//                if (line == null || "quit".equalsIgnoreCase(line)) {
//                    break;
//                }
//                f.channel().writeAndFlush(new Message(MessageType.TEXT, line + "\r"));
//            }
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) throws IOException {

        Client client = new Client();
        client.run();

//        while (true) {
//            client.sendMessage(ConsoleHelper.readString());
//        }
    }

//    public void sendMessage(String str) {
//        channel.writeAndFlush(new Message(MessageType.TEXT, str));
//    }

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

