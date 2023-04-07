package app.netty_chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

//    private static final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private final int PORT;
    private final String HOST;

    public int getPORT() {
        return PORT;
    }

    public String getHOST() {
        return HOST;
    }

    public Server(int port, String host) {
        PORT = port;
        HOST = host;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = b.bind(HOST, PORT).sync(); // (7)
            logger.log(Level.INFO, "Сервер успешно запущен на хосте {0}", this.getHOST());
            logger.log(Level.INFO, "Сервер успешно запущен на порту {0}", this.getPORT());
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }


    public static void main(String[] args) {

//        ConsoleHelper.writeMessage("Введите порт сервера:");

        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("application.properties")) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int port = Integer.parseInt(properties.getProperty("server.port"));
        String host = properties.getProperty("server.host");

        try {
            Server server = new Server(port, host);
            server.run();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка при запуске или работе сервера {0}", e.getMessage());
            throw new RuntimeException(e);

        }

//        try (ServerSocket serverSocket = new ServerSocket(port)) {
//            ConsoleHelper.writeMessage("Чат сервер запущен.");
//            while (true) {
//                Socket socket = serverSocket.accept();
////                new Handler(socket).start();
//            }
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Произошла ошибка при запуске или работе сервера {0}", e.getMessage());
//            ConsoleHelper.writeMessage("Произошла ошибка при запуске или работе сервера.");
//        }


    }

//    /**
//     * Рассылает сообщение всем участникам чата
//     *
//     * @param message
//     */
//    public static void sendBroadcastMessage(Message message) {
//        for (Connection connection : connectionMap.values()) {
//            try {
//                connection.send(message);
//            } catch (IOException e) {
//                logger.log(Level.SEVERE, "Не смогли отправить сообщение {0}", e.getMessage());
//                logger.log(Level.SEVERE, "RemoteSocketAddress {0}", connection.getRemoteSocketAddress());
//                ConsoleHelper.writeMessage("Не смогли отправить сообщение " + connection.getRemoteSocketAddress());
//            }
//        }
//    }


}
