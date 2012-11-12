package netty;

import io.netty.channel.*;

import netty.packets.*;

import org.reunionemu.jreunion.protocol.packets.server.FailPacket;
import org.slf4j.*;

public class ClientHandler extends ChannelInboundMessageHandlerAdapter<Packet> implements ChannelStateHandler{

	private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	int version;
	public ClientHandler(int version) {
		this.version = version;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		
		logger.debug("Sending login request");
		/*
		LoginPacket packet = new LoginPacket();
		packet.setVersion(version);
		packet.setUsername("admin");
		packet.setPassword("admin");
		
		ctx.write(packet);
		*/
	}
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);
		logger.debug("connection closed");
	}
	

	@Override
	public void messageReceived(ChannelHandlerContext ctx, Packet msg)
			throws Exception {
		
		logger.debug("Received: "+ msg);
		if(msg instanceof FailPacket){
			FailPacket packet = (FailPacket)msg;
			System.out.println("fail: "+ packet.getMessage());			
		}
		
	}
	
	
	

}
