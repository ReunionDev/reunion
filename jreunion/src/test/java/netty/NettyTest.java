package netty;
import static org.junit.Assert.assertNotNull;
import io.netty.logging.*;

import java.net.InetSocketAddress;

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
		
		InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
		
		NettyServer server = new NettyServer(address);
		
		NettyClient client = new NettyClient(address);
		
		Thread serverThread = new Thread(server);
		serverThread.start();
		Thread clientThread = new Thread(client);
		clientThread.start();
		
		clientThread.join();
		
		serverThread.interrupt();	
		
	}
	
	public static void main(String []args) throws InterruptedException{

		new NettyTest().test();
		
	}

}
