import static org.junit.Assert.fail;
import io.netty.bootstrap.*;
import io.netty.buffer.MessageBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;
import io.netty.handler.codec.http.*;

import java.net.URI;


public class NettyTest {

	static class Server {
		public static void main(String [] args) throws Exception{
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
								
						        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
						        
						        
						        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
						        
								System.out.println(msg);
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
			
			fail("Not yet implemented");
		}
	}
	static class Client {
		public static void main(String [] args) throws Exception{
		    Bootstrap b = new Bootstrap();
		    URI uri = new URI("tcp://localhost:4005");
		    try{
		    	b.group(new NioEventLoopGroup())
		    	.channel(NioSocketChannel.class)
		    	.remoteAddress(uri.getHost(), uri.getPort())
		    	.handler(new ChannelInitializer<SocketChannel>() {
	                 @Override
	                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline pipeline = ch.pipeline();
	                 }
                 });
		    	Channel ch = b.connect().sync().channel();
	            //handler.handshakeFuture().sync();
		    	ch.write("test");
		        
		    } finally {
		        // Shut down all event loops to terminate all threads.
		        b.shutdown();
		    }
			
			fail("Not yet implemented");
		}
	}

}
