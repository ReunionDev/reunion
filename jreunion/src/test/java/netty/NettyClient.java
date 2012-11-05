package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;
import io.netty.handler.logging.LoggingHandler;

import java.net.*;

public class NettyClient implements ProtocolFactory, Runnable {

	int count = 0;
	private InetSocketAddress address;
	public NettyClient(InetSocketAddress address) {
		this.address = address;
   
	}
	OtherProtocol protocol = new OtherProtocol();
	
	public void run() {
		
		 Bootstrap b = new Bootstrap();
			
		    try{
			    b.group(new NioEventLoopGroup())
			  //  .channel(new NioServerSocketChannel())
			    .channel(NioSocketChannel.class)
			    .remoteAddress(address)			    
			    .handler(new ChannelInitializer<SocketChannel>() {
		             @Override
		             public void initChannel(SocketChannel ch) throws Exception {
		            	 ch.pipeline().addLast("logger", new LoggingHandler());
		            	 ch.pipeline().addLast("codec", new ProtocolCodec(NettyClient.this));
		            	 ch.pipeline().addLast("handler", new ClientHandler());
		             }
		             
		        });
			    
			    ChannelFuture f = b.connect().sync();			    
		
		        // Wait until the server socket is closed.
		        f.channel().closeFuture().sync();
		        
		    } catch (InterruptedException e) 
		    {
		    	
			} finally {
		        // Shut down all event loops to terminate all threads.
		        b.shutdown();
		    }
		
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		new NettyClient(new InetSocketAddress("127.0.0.1", 4005)).run();

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
				return protocol.encryptClient(c);
			}
			
			@Override
			public char decode(byte b) {
				return protocol.decryptClient(b);
			}
		};
	}

	
}
