package com.study.NettyTest.Client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;


/**
 * 这个例子的客户端一直报如下错误：
 * Exception in thread "main" java.net.BindException: Address already in use: bind
	at sun.nio.ch.Net.bind0(Native Method)
	at sun.nio.ch.Net.bind(Net.java:463)
	at sun.nio.ch.Net.bind(Net.java:455)
	at sun.nio.ch.SocketChannelImpl.bind(SocketChannelImpl.java:612)
	at sun.nio.ch.SocketAdaptor.bind(SocketAdaptor.java:147)
	at io.netty.channel.socket.nio.NioSocketChannel.doBind(NioSocketChannel.java:165)
	at io.netty.channel.AbstractChannel$AbstractUnsafe.bind(AbstractChannel.java:464)
	at io.netty.channel.DefaultChannelPipeline$HeadHandler.bind(DefaultChannelPipeline.java:1032)
	at io.netty.channel.ChannelHandlerInvokerUtil.invokeBindNow(ChannelHandlerInvokerUtil.java:99)
	at io.netty.channel.DefaultChannelHandlerInvoker.invokeBind(DefaultChannelHandlerInvoker.java:196)
	at io.netty.channel.DefaultChannelHandlerContext.bind(DefaultChannelHandlerContext.java:366)
	at io.netty.channel.DefaultChannelPipeline.bind(DefaultChannelPipeline.java:898)
	at io.netty.channel.AbstractChannel.bind(AbstractChannel.java:189)
	at io.netty.bootstrap.AbstractBootstrap$2.run(AbstractBootstrap.java:309)
	at io.netty.util.concurrent.SingleThreadEventExecutor.runAllTasks(SingleThreadEventExecutor.java:318)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:353)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:794)
	at java.lang.Thread.run(Thread.java:745)
 * @author jian01.lin
 *
 *2017-09-11 23：20 找到原因，原来是客户端写成了：
 *ChannelFuture f = b.bind(host, port).sync();
 *应该是：
 *ChannelFuture f = b.connect(host, port).sync();
 *
 *服务端才是使用bind，客户端使用connect
 *
 *
 */
public class EchoClient {
	public void connect(int port, String host) throws InterruptedException{
		EventLoopGroup group = new NioEventLoopGroup();
		
		try{
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());	
						ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
						
						ch.pipeline().addLast(new StringDecoder());
						ch.pipeline().addLast(new EchoClientHandler());
						
					}
				});
			
			
			//发起异步连接操作
			//ChannelFuture f = b.bind(host, port).sync();
			ChannelFuture f = b.connect(host, port).sync();
			
			//等待客户端链路关闭
			f.channel().closeFuture().sync();
			
		}finally{
			//优雅退出，释放NIO线程组
			group.shutdownGracefully();
		}
		
	}
	
	/*private class ChildChannelHandle extends  ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
			
			ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new EchoClientHandler());
		}
		
	}*/

	public static void main(String[] args) throws InterruptedException {
		int port = 8080;
		if(args != null && args.length > 0){
			try{
				port = Integer.valueOf(args[0]);
			}catch(Exception e){
				//e.printStackTrace();
			}
		}
		
		
		new EchoClient().connect(port, "127.0.0.1");
		
	}

}
