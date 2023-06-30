package com.example;

import com.example.dao.UserStorage;
import com.example.utils.PropertiesLoader;
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
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO Добавить jackson или protobuf
// TODO авторизация
// TODO BD


/**
 *
 *
 */
public class Server {

    private static final Logger logger
            = LoggerFactory.getLogger(Server.class);

    private final UserStorage userStorage = UserStorage.getInstance();

    private final int PORT;
    private final String HOST;

    private final String filePathUsers;

    private final PropertiesLoader propertiesLoader = PropertiesLoader.getPropertiesLoader();

    {
        PORT = Integer.parseInt(propertiesLoader.getProperty("server.port"));
        HOST = propertiesLoader.getProperty("server.host");
        filePathUsers = propertiesLoader.getProperty("server.users");

    }

    public int getPORT() {
        return PORT;
    }

    public String getHOST() {
        return HOST;
    }




    public void run()  {

        // добавляем хук для сохранения пользователей при остановке сервера
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                logger.info("Сохранение юзеров в {}", filePathUsers);
                userStorage.saveToFile(filePathUsers);
            } catch (IOException e) {
                logger.error("Failed to save user storage");
            }
        }));


        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel)  {
                            socketChannel.pipeline().addLast(
                                new StringDecoder(),
                                new ServerAuthHandler(),
                                new StringEncoder()
                            );
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            ChannelFuture f = b.bind(HOST, PORT).sync(); // (7)
            logger.info("Сервер успешно запущен. HOST = {}, PORT = {}", this.getHOST(), this.getPORT());
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            logger.error("Server was interrupted", e);
            e.printStackTrace();
        } finally {
            logger.info("finally");

            // сохранение пользователей при остановке сервера
            try {
                logger.info("Сохранение юзеров в {}", filePathUsers);
                userStorage.saveToFile(filePathUsers);
            } catch (IOException e) {
                logger.error("Failed to save user storage");
            }


            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {

        try {
            Server server = new Server();
            server.run();
        } catch (Exception e) {
            logger.warn("Произошла ошибка при запуске или работе сервера {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
