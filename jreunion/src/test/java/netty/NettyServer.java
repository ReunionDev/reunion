package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.http.*;

public class NettyServer {

	public NettyServer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
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
	            	 ch.pipeline().addLast("decoder", new HttpRequestDecoder());
	            	 ch.pipeline().addLast("encoder", new HttpResponseEncoder());
	            	 ch.pipeline().addLast("handler", new ChannelInboundMessageHandlerAdapter<HttpRequest>(){

						@Override
						public void messageReceived(
								ChannelHandlerContext ctx, HttpRequest msg)
								throws Exception {
							
					        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CREATED);
					        
					        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
					        
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

}
