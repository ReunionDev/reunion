package netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

import netty.packets.LoginPacket;
import netty.parsers.FailParser;

import org.junit.Test;

public class LunarTest {

	@Test
	public void test() throws InterruptedException {
		final int version = 2052;
		InetSocketAddress address = new InetSocketAddress("202.183.192.22", 4105);
		
		final ClientsideParser parser = new ClientsideParser();
		parser.add(new FailParser());
		
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
