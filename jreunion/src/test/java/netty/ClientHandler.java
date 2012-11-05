package netty;

import io.netty.channel.*;

import org.slf4j.*;

public class ClientHandler extends ChannelInboundMessageHandlerAdapter<String> implements ChannelStateHandler{

	private static Logger logger = LoggerFactory.getLogger(ClientHandler.class);
	
	public ClientHandler() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		
		logger.debug("Sending login request");
		
		ctx.write("100");
		ctx.write("login");
		ctx.write("admin");
		ctx.write("admin");
	
		
	}
	
	

	@Override
	public void messageReceived(ChannelHandlerContext ctx, String msg)
			throws Exception {
		
		logger.debug("Received: "+ msg);
		
	}
	
	
	

}
