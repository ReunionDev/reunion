package org.reunionemu.jreunion.events;

import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.Position;
/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class Test extends EventDispatcher implements EventListener,Runnable {

	public Test() {
		this.addEventListener(Event.class,this, null);
		
		
	}
	

	static int count = 0;
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws Exception {
		Test t = new Test();
		try{
			throw new Exception("lalalala");
		}catch(Exception e){
			LoggerFactory.getLogger(Test.class).warn(e.getMessage(), e);
		}
		int a = 0;
		System.out.println(++a);
		
		
		
		Position position1 = new Position();
		Position position2 = new Position();
		position2 = position2.setX(10);
		position2 = position2.setY(10);
		
		LoggerFactory.getLogger(Test.class).info(""+position1.distance(position2));
		LoggerFactory.getLogger(Test.class).info(""+position1.distance(position2));
		LoggerFactory.getLogger(Test.class).info(""+position1.within(position2,15));
		
		/*
		int port = 4009;
		ServerSocket socket = new ServerSocket();
		socket.bind(new InetSocketAddress(port));	
		Thread thread = new Thread(t);
		thread.start();
		Socket cl = socket.accept();
		LoggerFactory.getLogger(Parser.class).info(cl.getLocalSocketAddress());		
		*/
		
		{
			long start = System.currentTimeMillis();
			for(int i =0;i<10000;i++){
				t.fireEvent(t.createEvent(TestEvent.class));
			}
			LoggerFactory.getLogger(Test.class).info(""+(System.currentTimeMillis()-start));
		}
		LoggerFactory.getLogger(Test.class).info(""+count);
		
		EventDispatcher.shutdown();
		LoggerFactory.getLogger(Test.class).info(""+count);
	}

	@Override
	public void handleEvent(Event event) {
		//LoggerFactory.getLogger(Test.class).info("ohai!"+event.getClass());
		synchronized(this){
			count++;
		}
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket("home.aidamina.org",4009);
			LoggerFactory.getLogger(Test.class).info(socket.toString());
			
		} catch (Exception e) {

			LoggerFactory.getLogger(Test.class).warn(e.toString());
		}
	}
}
