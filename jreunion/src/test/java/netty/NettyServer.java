package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class NettyServer implements ProtocolFactory {

	int count = 0;
	public NettyServer() {
		
   
	}
	OtherProtocol protocol = new OtherProtocol();
	
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
								count ++;
								if(count%4==0){
							        ctx.write("fail no go!");
							        ctx.flush().addListener(ChannelFutureListener.CLOSE);
								}
						        
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

		InetAddress address = ((InetSocketAddress)channel.localAddress()).getAddress();
		protocol.setAddress(address);
		protocol.setMapId(4);
		protocol.setPort(4005);
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
