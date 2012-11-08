package netty;

import io.netty.channel.*;

import java.net.InetSocketAddress;

import netty.packets.*;
import netty.parsers.*;

import org.junit.Test;
import org.slf4j.*;

public class LunarTest {
	
	private final static Logger logger = LoggerFactory.getLogger(LunarTest.class);

	@Test
	public void test() throws InterruptedException {
		final int version = 2052;
		//final InetSocketAddress address = new InetSocketAddress("202.183.192.22", 4105);
		final InetSocketAddress address = new InetSocketAddress("127.0.0.1", 4005);
		final ClientsideParser parser = new ClientsideParser();
		parser.add(new FailParser());
		parser.add(new CharListEndParser());
		parser.add(new SuccessParser());
		
		NettyClient client = new NettyClient(address, version, new ParserFactory() {
			
			@Override
			public Parser getParser(Channel channel) {
				return parser;
			}
		},new ChannelInboundMessageHandlerAdapter<Packet>() {
			
			@Override
			public void messageReceived(ChannelHandlerContext ctx, Packet msg)
					throws Exception {
					if(msg instanceof CharListEndPacket){
						ctx.channel().write(new CharExistPacket("testname"));
						
					}
					if(msg instanceof SuccessPacket){
						System.out.println("success");
					}
				
				
			}
		});
		ChannelFuture connect = client.connect();
		connect.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					LoginPacket packet = new LoginPacket();
					packet.setVersion(version);
					packet.setUsername("admin");
					packet.setPassword("admin");				
					future.channel().write(packet);
				}else{
					logger.error("Unable to connect to "+address);
				}
			}
		});		
		
		connect.channel().closeFuture().sync();
		
		
		
	}

}
