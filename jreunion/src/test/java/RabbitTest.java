import static org.junit.Assume.assumeTrue;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.QueueingConsumer;



public class RabbitTest {
	private final String QUEUE_NAME = "test";
	private Connection connection;
	private Channel channel;
	@Before
	public void setUp() throws Exception {
		
		try{
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setHost("localhost");
	        connection = factory.newConnection();
		    channel = connection.createChannel();	    
		    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		    assumeTrue(connection.isOpen());
	    }catch(Exception e){
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
	    assumeTrue(connection.isOpen());
		
		long start = System.currentTimeMillis();
		String message = "Hello World!";
	    channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_BASIC, message.getBytes());
	    //System.out.println(" [x] Sent '" + message + "'");
	    
	    
	    QueueingConsumer consumer = new QueueingConsumer(channel);
	    channel.basicConsume(QUEUE_NAME, false, consumer);
	    
	    
	    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	    message = new String(delivery.getBody());
	    
	    System.out.println(System.currentTimeMillis()-start);
	    System.out.println(" [x] Received '" + message + "'");
	    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	    
		    
	}

}
