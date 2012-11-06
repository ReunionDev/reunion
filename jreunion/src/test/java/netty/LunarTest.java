package netty;

import static org.junit.Assert.fail;

import java.net.InetSocketAddress;

import org.junit.Test;

public class LunarTest {

	@Test
	public void test() throws InterruptedException {
		int version = 2052;
		InetSocketAddress address = new InetSocketAddress("202.183.192.22", 4105);	
		NettyClient client = new NettyClient(address, version);
		Thread thread = new Thread(client);
		thread.start();
		thread.join();
		
	}

}
