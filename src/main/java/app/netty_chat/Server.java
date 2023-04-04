package app.netty_chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

//    private static final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

//    private static final Map<SocketChannel, Connection> connectionMap = new ConcurrentHashMap<>();

    protected static final List<SocketChannel> connection = new ArrayList<>();

//    public static List<SocketChannel> getConnection() {
//        return connection;
//    }

    private final int PORT;

    public int getPORT() {
        return PORT;
    }

    public Server(int port) {
        PORT = port;
    }

    public void run () throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ServerHandler());
                            connection.add(socketChannel);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = b.bind(PORT).sync(); // (7)

            f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }


    }


    public static void main(String[] args)  {

        ConsoleHelper.writeMessage("Введите порт сервера:");

        try {
            Server server = new Server(ConsoleHelper.readInt());
            server.run();
            logger.log(Level.INFO, "Сервер успешно запущен на порту  {0}", server.getPORT());
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
