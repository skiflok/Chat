package com.example.chat.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class serverApp {


    public static void main(String[] args) {

        // поток для подключающихся клиентов
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // потоки для работы
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // настройка сервера
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup) // задаем потоки для работы
                    .channel(NioServerSocketChannel.class) // подключение клиентов
                    .childHandler(new ChannelInitializer<SocketChannel>() { // обработчик подключения
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {

                        }
                    });


            // запуск сервера

            ChannelFuture future = b.bind(8189).sync(); // старт сервера на порту
            future.channel().closeFuture().sync(); // ожидание остановки канала

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

    }

}
