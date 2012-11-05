package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;
import io.netty.handler.logging.LoggingHandler;

import java.net.*;

import org.slf4j.*;

public class NettyServer implements ProtocolFactory, Runnable {

	
	Logger logger = LoggerFactory.getLogger(NettyServer.class);
	
	int count = 0;

	private InetSocketAddress address;
	public NettyServer(InetSocketAddress address) {
		this.address = address;
   
	}
	OtherProtocol protocol = new OtherProtocol();
	
	public void run(){
		
		 ServerBootstrap b = new ServerBootstrap();
			
		    try{
		    	logger.debug("Server starting"); 
			    b.group(new NioEventLoopGroup(), new NioEventLoopGroup())
			  //  .channel(new NioServerSocketChannel())
			    .channel(NioServerSocketChannel.class)
		        .option(ChannelOption.SO_BACKLOG, 100)
			    .localAddress(address)
			    .childHandler(new ChannelInitializer<SocketChannel>() {
		             @Override
		             public void initChannel(SocketChannel ch) throws Exception {
		            	 ch.pipeline().addLast("logger", new LoggingHandler());
		            	 ch.pipeline().addLast("codec", new ProtocolCodec(NettyServer.this));
		            	 ch.pipeline().addLast("handler", new ChannelInboundMessageHandlerAdapter<String>(){
							@Override
							public void messageReceived(
									ChannelHandlerContext ctx, String msg)
									throws Exception {
								System.out.println(msg);
								count ++;
								if(count%4==0){
							        ctx.write("fail no go!");
							        ctx.flush().addListener(ChannelFutureListener.CLOSE);
								}
							}
							@Override
							public void channelActive(ChannelHandlerContext ctx)
									throws Exception {
								System.out.println("connectionn established");
								super.channelActive(ctx);
							}
		            	 });
		             }
		        });
			    
			    ChannelFuture f = b.bind().sync();
		
		        // Wait until the server socket is closed.
		        f.channel().closeFuture().sync();
		        
		    } catch (InterruptedException e) {
				
			} finally {
		        // Shut down all event loops to terminate all threads.
		        b.shutdown();
		    	logger.debug("Server stopped"); 
		    }
		
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new NettyServer(new InetSocketAddress("127.0.0.1", 4005)).run();

	}

	@Override
	public Protocol getProtocol(Channel channel) {

		protocol.setAddress(address.getAddress());
		protocol.setMapId(4);
		protocol.setPort(address.getPort());
		protocol.setVersion(100);
		
		return new Protocol() {
			
			@Override
			public byte encode(char c) {
				return protocol.encryptServer(c);
			}
			
			@Override
			public char decode(byte b) {
				return protocol.decryptServer(b);
			}
		};
	}

	
}
