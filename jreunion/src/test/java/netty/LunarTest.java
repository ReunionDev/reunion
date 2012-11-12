package netty;

import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.*;

import netty.packets.*;
import netty.parsers.*;

import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.client.*;
import org.reunionemu.jreunion.protocol.packets.server.*;
import org.reunionemu.jreunion.protocol.parsers.client.*;
import org.slf4j.*;

public class LunarTest {
	
	private final static Logger logger = LoggerFactory.getLogger(LunarTest.class);

	String testCharName = "test";
	
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
				
		client.setHandler(new CharListHandler(client));
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
	
	class CharListHandler extends ChannelInboundMessageHandlerAdapter<Packet>{
		final List<CharsExistPacket> chars = new LinkedList<CharsExistPacket>();
		
		final NettyClient client;
		public CharListHandler(NettyClient client){
			this.client = client;
			
		}

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
				logger.info("successful login ("+chars.size()+" chars found)");
				if(chars.size()==0){
					logger.debug("No characters found, checking if '"+testCharName + "' exists");
					ctx.channel().write(new CharExistPacket(testCharName));
					client.setHandler(new ChannelInboundMessageHandlerAdapter<Packet>() {

						@Override
						public void messageReceived(
								ChannelHandlerContext ctx, Packet msg)
								throws Exception {
							if(msg instanceof SuccessPacket){
								logger.debug("No '"+testCharName + "' found, so attempting to create");

								CharNewPacket packet = new CharNewPacket();
								packet.setSlot(0);
								packet.setName(testCharName);
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
											logger.debug("'"+testCharName + "' succesfully created, back to charlist");
											client.setHandler(CharListHandler.this);
										}
										if(msg instanceof FailPacket){
											logger.debug("'"+testCharName + "' failed to be created("+((FailPacket)msg).getMessage()+"), disconnecting");
											ctx.channel().disconnect();
											
										}
										
										
									}
								});
							}
							if(msg instanceof FailPacket){
								logger.debug("Character '"+testCharName + "' already exists so cant be created, disconnected");
								ctx.channel().disconnect();
								
							}
						}
					
					});
				
				}else{
					StartPacket start = new StartPacket();
					start.setSlot(chars.get(0).getSlot());
					ctx.channel().write(start);

					
					// log in
				}
				
			}
			
		}
		
		
		
		
	}

}
