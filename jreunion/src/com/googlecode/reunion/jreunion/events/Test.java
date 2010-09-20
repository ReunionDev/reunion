package com.googlecode.reunion.jreunion.events;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.googlecode.reunion.jreunion.events.ClientEvent.ClientFilter;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Test extends EventBroadcaster implements EventListener {

	public Test() {
		this.addEventListener(Event.class,this, new ClientFilter(null));
		
		System.out.println(this.listeners==null);
		
	}
	static int count = 0;

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Test t = new Test();
				
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

}
