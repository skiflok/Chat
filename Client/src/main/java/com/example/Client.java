package com.example;


import com.example.utils.PropertiesLoader;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Client {

  private static final Logger logger = LoggerFactory.getLogger(Client.class);
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

  public String getHOST() {
    return HOST;
  }

  public int getPORT() {
    return PORT;
  }

  public void run() {

    EventLoopGroup workerGroup = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap(); // (1)
      b.group(workerGroup); // (2)
      b.channel(NioSocketChannel.class); // (3)
      b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
      b.handler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) {

          ch.pipeline().addLast(
              new StringDecoder(),
              new ClientAuthHandler(),
              new StringEncoder()
          );
        }
      });

      // Start the client.
      ChannelFuture f = b.connect(HOST, PORT).sync(); // (5)
      setChannel(f.channel());

      logger.info("Клиент успешно запущен. HOST = {}, PORT = {}", this.getHOST(), this.getPORT());

      f.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    } finally {
      workerGroup.shutdownGracefully();
    }


  }

  public static void main(String[] args) {

    Client client = new Client();
    client.run();

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

