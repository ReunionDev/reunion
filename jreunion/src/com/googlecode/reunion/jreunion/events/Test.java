package com.googlecode.reunion.jreunion.events;

import java.util.LinkedList;
import java.util.List;

public class Test extends EventBroadcaster implements EventListener {

	public Test() {
		List<EventListener> list = new LinkedList<EventListener>();
		list.add(this);
		this.addEventListener(Event.class, this);
		
		//this.removeEventListener(Event.class, this);
		System.out.println(this.listeners==null);
		
	}
	static int count = 0;

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		Test t = new Test();
		Event event = new Event();
		SubEvent subEvent = new SubEvent();
		//t.fire(event);
		//t.fire(subEvent);
		//t.fire(networkEvent);
		{
			long start = System.currentTimeMillis();
			for(int i =0;i<10000;i++){
				t.fireEvent(subEvent);
			}
			

			System.out.println(System.currentTimeMillis()-start);
			
			
			
		}
		//Thread.sleep(150);
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
		//throw new RuntimeException("!");
	}

}
