package netty;
import static org.junit.Assert.assertNotNull;
import io.netty.channel.*;
import io.netty.logging.*;

import java.net.InetSocketAddress;

import netty.packets.LoginPacket;
import netty.parsers.FailParser;

import org.junit.Test;
import org.slf4j.*;

public class NettyTest {
	
	private static final Logger logger = LoggerFactory.getLogger(NettyTest.class);


	
	@Test
	public void test() throws InterruptedException{
		InternalLoggerFactory.setDefaultFactory(new Slf4JLoggerFactory());
		
		assertNotNull(logger);

		logger.debug("Starting netty tests");
		
		int port = 4005;
		final int version = 101;
		
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
		
		NettyServer server = new NettyServer(address);
		

		final ClientsideParser parser = new ClientsideParser();
		parser.add(new FailParser());
		
		
		NettyClient client = new NettyClient(address, version, new ParserFactory() {
			
			@Override
			public Parser getParser(Channel channel) {
				return parser;
			}
		});		
		client.setHandler(new ChannelInboundMessageHandlerAdapter<Packet>() {
			
			@Override
			public void messageReceived(ChannelHandlerContext ctx, Packet msg)
					throws Exception {
				
				
			}
		});
		
		Thread serverThread = new Thread(server);
		serverThread.start();
		client.connect().addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				LoginPacket packet = new LoginPacket();
				packet.setVersion(version);
				packet.setUsername("admin");
				packet.setPassword("admin");				
				future.channel().write(packet);
			}
		}).channel().closeFuture().sync();
		
		serverThread.interrupt();	
		
	}

}
