package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;
import io.netty.handler.logging.LoggingHandler;

import java.net.*;
import java.util.*;

import org.slf4j.*;

public class NettyServer implements ProtocolFactory, Runnable, PacketFactory, ParserFactory {

	
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
		            	 ch.pipeline().addLast("parser", new PacketParserCodec(NettyServer.this, NettyServer.this));
		            	 ch.pipeline().addLast("handler", new ChannelInboundMessageHandlerAdapter<Packet>(){
							@Override
							public void messageReceived(
									ChannelHandlerContext ctx, Packet msg)
									throws Exception {
								System.out.println(msg);
						        ctx.write(new FailPacket("No go!"));
						        ctx.flush().addListener(ChannelFutureListener.CLOSE);
							
							}
							@Override
							public void channelActive(ChannelHandlerContext ctx)
									throws Exception {
								System.out.println("connectionn established");
								super.channelActive(ctx);
							}
							
							@Override
							public void channelInactive(
									ChannelHandlerContext ctx) throws Exception {
								
								super.channelInactive(ctx);
								parsers.remove(ctx.channel());
								System.out.println("connectionn closed serverside");
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
	
	@Override
	public String build(Packet msg) {
		if(msg instanceof FailPacket){
			FailPacket packet = (FailPacket)msg;
			return "fail "+packet.getMessage();
		}
		return null;
	}
	
	Map<Channel,Parser> parsers = new HashMap<Channel,Parser>();
	@Override
	public Parser getParser(final Channel channel) {
		if(!parsers.containsKey(channel)){
		
			parsers.put(channel, new Parser(){
				
				Integer version;				

				String login;
				
				String username;
				
				String password;				
				
				@Override
				public Packet parse(String input) {
					
					Packet packet = null;
					if(version==null){
						version = Integer.parseInt(input);
					}else if(login==null){
						login = input;
					}else if(username==null){
						username = input;
					}else if(password==null) {
						password = input;
						if(login.equals("login")){
							LoginPacket loginPacket = new LoginPacket();
							loginPacket.setVersion(version);
							loginPacket.setUsername(username);
							loginPacket.setPassword(password);
							packet = loginPacket;
							
						}
					}else{
						channel.close();
					}
					return packet;
				}			
				
			});
			
		}
		return parsers.get(channel);
	}

	
}
