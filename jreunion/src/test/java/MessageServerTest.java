import static org.junit.Assume.assumeTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
//import org.reunionemu.jreunion.messages.MessageServer;



public class MessageServerTest {
	private final String QUEUE_NAME = "test";
	private Connection connection;
	private Channel channel;
	@Before
	public void setUp() throws Exception {
		try {
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost("localhost");
		    connection = factory.newConnection();
		    channel = connection.createChannel();	    
		    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		    assumeTrue(connection.isOpen());
	    } catch(Exception e) {
		    Assume.assumeNoException(e);
	    	
	    }
	}

	@After
	public void tearDown() throws Exception {
		channel.close();
	    connection.close();
	}

	@Test
	public void test() throws Exception {
		
		long start = System.currentTimeMillis();
		String message = "Hello World!";
		
		//System.out.println(" [x] Sent '" + message + "'");
	    
	    //MessageServer server = new MessageServer();
	    
	    Thread t = new Thread();
	    t.start();
	    channel.exchangeDeclare("all", "fanout");
	    
	    
	    
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	    
	    channel.basicPublish("all", "", null, baos.toByteArray());
	    
	    t.join(5*1000);
	    t.interrupt();
	    
		    
	}

}
