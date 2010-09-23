package com.googlecode.reunion.jreunion.events;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		
		Position pos1 = new Position(1,2,3, null ,Math.PI/2);
		Position pos2 = pos1.clone();
		pos1.setZ(5);
		
		
		System.out.println(pos2);
		
		/*
		int port = 4009;
		ServerSocket socket = new ServerSocket();
		socket.bind(new InetSocketAddress(port));	
		Thread thread = new Thread(t);
		thread.start();
		Socket cl = socket.accept();
		System.out.println(cl.getLocalSocketAddress());		
		*/
		
		{
			long start = System.currentTimeMillis();
			for(int i =0;i<10000;i++){
				t.fireEvent(t.createEvent(TestEvent.class));
			}
			System.out.println(System.currentTimeMillis()-start);
		}
		System.out.println(count);
		
		EventBroadcaster.shutdown();
		System.out.println(count);
	}

	@Override
	public void handleEvent(Event event) {
		//System.out.println("ohai!"+event.getClass());
		synchronized(this){
			count++;
		}
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket("home.aidamina.org",4009);
			System.out.println(socket);
			
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
