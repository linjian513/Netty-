package com.study.NettyTest.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class TimeClient {

	public void connect(String host , int port) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						/**
						 * 客户端的添加LineBasedFrameDecoder和StringDecoder解码器
						 */
						ch.pipeline().addLast(new LineBasedFrameDecoder(1024)); 
						ch.pipeline().addLast(new StringDecoder());
						
						
						
						ch.pipeline().addLast(new TimeClientHandler());
						
					}
				});
			
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
			
		} finally{
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		int port = 8080;
		if(args != null && args.length > 0){
			try {
				port = Integer.valueOf(args[0]);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		new TimeClient().connect("127.0.0.1", port);
		
	}

}
