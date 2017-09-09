package com.study.NettyTest.Server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;

public class TimeServerHandler extends ChannelHandlerAdapter{
	
	private int counter;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/*
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req,"UTF-8").substring(0, req.length - System.getProperty("line.separator").length());
		*/
		
		String body = (String)msg;
		
		System.out.println("时间服务器获取到命令：" + body + "；counter＝" + ++counter);
		String currentTime  = "Query Time Order".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
		
		
		/**
		 * 注意：一开始把System.getProperty("line.separator")中的line.separator写错成line.sepatator，导致没有
		 * 获取到操作系统的换行符，结果客户端的channelRead方法不执行。
		 * 
		 * 原因是：收到的消息没有指定的结束标记。 比如指定了lineBasedFrameDecoder，没有换行标志，是不会调用channelRead方法的，其他的类似
		 */
		currentTime = currentTime + System.getProperty("line.separator");
		
		
		ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
		ctx.write(resp);
	} 
	
	@Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}
