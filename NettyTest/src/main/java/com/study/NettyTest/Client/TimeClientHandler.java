package com.study.NettyTest.Client;

import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TimeClientHandler extends ChannelHandlerAdapter{
	private static final Logger logger = Logger.getLogger(TimeClientHandler.class.getName());
	//private final ByteBuf firstMessage;
	
	private int counter;
	
	private byte[] req;
	
	public TimeClientHandler(){
//		byte[] req = "Query Time order".getBytes();
//		firstMessage = Unpooled.buffer(req.length);
//		firstMessage.writeBytes(req);
		
		
		
		//req = ("Query time order" + System.getProperty("line.separator")).getBytes(); 
	}

	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		ctx.writeAndFlush(firstMessage);
		
		
		ByteBuf message = null;
		for(int i = 0; i < 100; i++){
			
			/**
			 * Windows系统中的换行是\r\n，而linux系统中的换行是\n。为了能够适应不同的操作系统，java可以获取系统换行。格式如下:
　　			 * System.getPRoperty(“line.sepatator”);
			 */
			req = ("Query time order i=" + i + System.getProperty("line.separator")).getBytes(); 
			message = Unpooled.buffer(req.length);
			message.writeBytes(req);
			ctx.writeAndFlush(message);
		}
	}
	
	
	/**
	 * 
	 * 
	 * 注意：一开始在服务端的ChannelHandlerContext进行write的时候把System.getProperty("line.separator")中的line.separator写错成line.sepatator，导致没有
	 * 获取到操作系统的换行符，结果该客户端的channelRead方法不执行。
	 * 收到的消息没有指定的结束标记。 比如指定了lineBasedFrameDecoder，没有换行标志，是不会调用channelRead方法的，其他的类似
	 */
	@Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		/*
		ByteBuf buf = (ByteBuf) msg;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		
		String body = new String(req, "UTF-8");
		*/
		
		
		String body = (String)msg;
		
		
		System.out.println("Now is : " + body + " ; the counter is : "+ ++counter);
	}
	
	@Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.warning("Unexpected exception from downstream : " + cause.getMessage());
		ctx.close();
	}
}
