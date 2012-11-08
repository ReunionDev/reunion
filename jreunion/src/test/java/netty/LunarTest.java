package netty;

import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.*;

import netty.packets.*;
import netty.parsers.*;

import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.slf4j.*;

public class LunarTest {
	
	private final static Logger logger = LoggerFactory.getLogger(LunarTest.class);

	@Test
	public void test() throws InterruptedException {
		logger.debug("starting test");
		final int version = 2052;
		//final InetSocketAddress address = new InetSocketAddress("202.183.192.22", 4105);
		final InetSocketAddress address = new InetSocketAddress("127.0.0.1", 4005);
		final ClientsideParser parser = new ClientsideParser();
		parser.add(new FailParser());
		parser.add(new CharListEndParser());
		parser.add(new SuccessParser());
		parser.add(new CharsExistParser());
		
		final NettyClient client = new NettyClient(address, version, new ParserFactory() {
			
			@Override
			public Parser getParser(Channel channel) {
				return parser;
			}
		});		
		
		final List<CharsExistPacket> chars = new LinkedList<CharsExistPacket>();
		
		client.setHandler(new ChannelInboundMessageHandlerAdapter<Packet>() {

			@Override
			public void messageReceived(ChannelHandlerContext ctx, Packet msg)
					throws Exception {
				
					if(msg instanceof FailPacket){
						logger.info("Unable to log in: "+ ((FailPacket)msg).getMessage());
						
					}
					if(msg instanceof CharsExistPacket){
						chars.add((CharsExistPacket)msg);
						
					}
					if(msg instanceof CharListEndPacket){
						logger.info("successful login ("+chars.size()+" chars)");
						if(chars.size()==0){
						ctx.channel().write(new CharExistPacket("testchar"));
						client.setHandler(new ChannelInboundMessageHandlerAdapter<Packet>() {

							@Override
							public void messageReceived(
									ChannelHandlerContext ctx, Packet msg)
									throws Exception {
								if(msg instanceof SuccessPacket){
									System.out.println("success");
									CharNewPacket packet = new CharNewPacket();
									packet.setSlot(0);
									packet.setName("testchar");
									packet.setSex(Sex.MALE);
									packet.setRace(Race.BULKAN);
									packet.setHair(0);
									packet.setStrength(30);
									packet.setIntellect(30);
									packet.setDexterity(30);
									packet.setConstitution(30);
									packet.setLeadership(30);									
									ctx.channel().write(packet);
									client.setHandler(new ChannelInboundMessageHandlerAdapter<Packet>() {
										
										@Override
										public void messageReceived(ChannelHandlerContext ctx, Packet msg)
												throws Exception {
											
											if(msg instanceof SuccessPacket){
												
												
											}
											if(msg instanceof FailPacket){
												
												
											}
											ctx.channel().disconnect();
											
										}
									});
								}
								if(msg instanceof FailPacket){
									ctx.channel().disconnect();
	
									
								}
							}
						
						});
						
						}else{
							
							// log in
						}
						
					}
				
			}
		});
		ChannelFuture connect = client.connect();
		connect.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(future.isSuccess()){
					chars.clear();
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
