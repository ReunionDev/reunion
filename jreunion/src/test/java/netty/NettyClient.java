package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.*;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import netty.packets.FailPacket;


public class NettyClient implements ProtocolFactory, PacketFactory {

	private static Logger logger = LoggerFactory.getLogger(NettyClient.class);
	Bootstrap bootstrap;
	private ChannelFuture channel;
	int version;
	private InetSocketAddress address;
	public NettyClient(InetSocketAddress address,final int version, final ParserFactory parserFactory) {
		this.address = address;
		this.version = version;
		bootstrap = new Bootstrap();
		
	   
		bootstrap.group(new NioEventLoopGroup())
		  //  .channel(new NioServerSocketChannel())
		    .channel(NioSocketChannel.class)
		    .remoteAddress(address)			    
		    .handler(new ChannelInitializer<SocketChannel>() {
	             @Override
	             public void initChannel(SocketChannel ch) throws Exception {
	            	 ch.pipeline().addLast("logger", new LoggingHandler());
	            	 ch.pipeline().addLast("codec", new ProtocolCodec(NettyClient.this));
	            	 ch.pipeline().addLast("parser", new PacketParserCodec(parserFactory, NettyClient.this));
	            	 ch.pipeline().addLast("handler", new ClientHandler(version));
	             }
	        });
   
	}
	OtherProtocol protocol = new OtherProtocol();
	
	public ChannelFuture connect(){
		
			channel = bootstrap.connect(); 
		 return channel;
	}
	
	
	public ChannelFuture close(){
		ChannelFuture future = channel.channel().close();
		
		return future;
	}

	@Override
	public Protocol getProtocol(Channel channel) {

		protocol.setAddress(address.getAddress());
		protocol.setMapId(4);
		protocol.setPort(address.getPort());
		protocol.setVersion(version);
		
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
	@Override
	public String build(Packet msg) {
		String packet = msg.toString();
		return packet;
	}	

}
