package com.study.NettyTest.Server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoServerHandler extends ChannelHandlerAdapter{
	
	int counter = 0;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String)msg;
		
		System.out.println("这是第" + ++counter + "次接收到客户端消息：[" + body + "]");
		
		body += "$_";
		
		ByteBuf echoByteBuf = Unpooled.copiedBuffer(body.getBytes());
		
		/**
		 * 必须使用writeAndFlush方法，如果使用write方法将无法将消息发送到客户端。
		 * 
		 * writeAndFlush和write方法有什么不同？？？？？？？？？？？？？？？？？？
		 */
		ctx.writeAndFlush(echoByteBuf);
		
	}

	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();  //发生异常，关闭链路
	}
}
