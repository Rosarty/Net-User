package org.example.user;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public final class NettyUser {

    static final String HOST = "127.0.0.1";
    static final int PORT = 8001;

    public static void main(String[] args) throws Exception {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new StringDecoder());
                            p.addLast(new StringEncoder());
                            p.addLast(new UserHandler());
                        }
                    });

            ChannelFuture f = b.connect(HOST, PORT).sync();
            Channel channel = f.sync().channel();


            String[] users = {"Bill", "John", "Alice"};
            for (String user : users) {
                channel.writeAndFlush(user);
                channel.flush();
                Thread.sleep(1000);
            }

            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
