package app.netty_chat;

import app.netty_chat.service.PropertiesLoader;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 */
public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());

//    private static final Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private final int PORT;
    private final String HOST;

    private final PropertiesLoader propertiesLoader = PropertiesLoader.getPropertiesLoader();

    {
        PORT = Integer.parseInt(propertiesLoader.getProperty("server.port"));
        HOST = propertiesLoader.getProperty("server.host");
    }

    public int getPORT() {
        return PORT;
    }

    public String getHOST() {
        return HOST;
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
                            socketChannel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ServerMessageHandler());
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

        try {
            Server server = new Server();
            server.run();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Произошла ошибка при запуске или работе сервера {0}", e.getMessage());
            throw new RuntimeException(e);

        }
    }

}
