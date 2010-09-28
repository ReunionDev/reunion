package com.googlecode.reunion.jreunion.events;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.events.client.ClientEvent.ClientFilter;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Sex;
import com.googlecode.reunion.jreunion.game.Position;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Test extends EventBroadcaster implements EventListener,Runnable {

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
			Logger.getLogger(Test.class).warn(e);
		}
		int a = 0;
		System.out.println(++a);
		
		
		
		Position position1 = new Position();
		Position position2 = new Position();
		position2.setX(10);
		position2.setY(10);
		
		Logger.getLogger(Test.class).info(position1.distance(position2));
		Logger.getLogger(Test.class).info(position1.distance(position2));
		Logger.getLogger(Test.class).info(position1.within(position2,15));
		
		/*
		int port = 4009;
		ServerSocket socket = new ServerSocket();
		socket.bind(new InetSocketAddress(port));	
		Thread thread = new Thread(t);
		thread.start();
		Socket cl = socket.accept();
		Logger.getLogger(Parser.class).info(cl.getLocalSocketAddress());		
		*/
		
		{
			long start = System.currentTimeMillis();
			for(int i =0;i<10000;i++){
				t.fireEvent(t.createEvent(TestEvent.class));
			}
			Logger.getLogger(Test.class).info(System.currentTimeMillis()-start);
		}
		Logger.getLogger(Test.class).info(count);
		
		EventBroadcaster.shutdown();
		Logger.getLogger(Test.class).info(count);
	}

	@Override
	public void handleEvent(Event event) {
		//Logger.getLogger(Test.class).info("ohai!"+event.getClass());
		synchronized(this){
			count++;
		}
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket("home.aidamina.org",4009);
			Logger.getLogger(Test.class).info(socket);
			
		} catch (Exception e) {

			Logger.getLogger(Test.class).warn(e);
		}
	}
}
