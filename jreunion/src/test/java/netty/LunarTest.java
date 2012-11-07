package netty;

import io.netty.channel.*;

import java.net.InetSocketAddress;

import netty.packets.LoginPacket;

import org.junit.Test;

public class LunarTest {

	@Test
	public void test() throws InterruptedException {
		final int version = 2052;
		InetSocketAddress address = new InetSocketAddress("202.183.192.22", 4105);
		
		final ClientsideParser parser = new ClientsideParser();
		
		NettyClient client = new NettyClient(address, version, new ParserFactory() {
			
			@Override
			public Parser getParser(Channel channel) {
				return parser;
			}
		});
		ChannelFuture connect = client.connect();
		connect.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				LoginPacket packet = new LoginPacket();
				packet.setVersion(version);
				packet.setUsername("admin");
				packet.setPassword("admin");				
				future.channel().write(packet);
			}
		});		
		
		connect.channel().closeFuture().sync();
		
		
		
	}

}
