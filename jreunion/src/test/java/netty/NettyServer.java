package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;

public class NettyServer implements ProtocolFactory, Protocol {

	public NettyServer() {
   
	}

	
	public void start() throws Exception{
		
		 ServerBootstrap b = new ServerBootstrap();
			
		    try{
			    b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
			  //  .channel(new NioServerSocketChannel())
			    .channel(NioServerSocketChannel.class)
		        .option(ChannelOption.SO_BACKLOG, 100)
			    .localAddress(4005)
			    .childHandler(new ChannelInitializer<SocketChannel>() {
		             @Override
		             public void initChannel(SocketChannel ch) throws Exception {
		            	 ch.pipeline().addLast("codec", new ProtocolCodec(NettyServer.this));
		            	 ch.pipeline().addLast("handler", new ChannelInboundMessageHandlerAdapter<String>(){

							@Override
							public void messageReceived(
									ChannelHandlerContext ctx, String msg)
									throws Exception {
								
								System.out.println(msg);
						        ctx.write("test");
						        ctx.flush();
						        
						        //.addListener(ChannelFutureListener.CLOSE);
						        
								//System.out.println(msg);
								//ctx.channel().write("test".getBytes());
							}		            		 
		            	 });
		            	 
		             }
		         });
				
			    
			    ChannelFuture f = b.bind().sync();
		
		        // Wait until the server socket is closed.
		        f.channel().closeFuture().sync();
		        
		    } finally {
		        // Shut down all event loops to terminate all threads.
		        b.shutdown();
		    }
		
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new NettyServer().start();

	}

	@Override
	public Protocol getProtocol(Channel channel) {
		return this;
	}


	@Override
	public byte encode(char c) {
		return (byte)c;
	}


	@Override
	public char decode(byte b) {
		return (char)b;
	}

}
