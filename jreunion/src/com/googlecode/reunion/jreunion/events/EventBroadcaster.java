package com.googlecode.reunion.jreunion.events;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class EventBroadcaster{
	private static LinkedBlockingQueue<EventWorker> workers;
	private static Thread [] threads = new Thread[100];
	static {
		workers = new LinkedBlockingQueue<EventWorker>(threads.length);
		for(int i=0; i<threads.length;i++) {
			EventWorker worker = new EventWorker();
			worker.thread = threads[i] = new Thread(worker);
			worker.thread.setName("EventThread="+i);
			worker.thread.setDaemon(true);
			worker.thread.start();
		}
	}
	
	public <T extends Event> T createEvent(Class<T> eventClass, Object... args){
		return Event.<T>Create(eventClass, this, args);
	}
	
	public <T extends Event> int fireEvent(Class<T> eventClass,Object... args){
		return this.fireEvent(this.createEvent(eventClass, args));
	}
	
	private  Map<Class,Map<EventListener,Filter>> listeners;
	public Map<Class, Map<EventListener,Filter>> getListeners() {
		synchronized (this){
			if(listeners==null)
				listeners = new HashMap<Class,Map<EventListener,Filter>>();			
		}
		return listeners;
	}

	public void addEventListener(Class c, EventListener listener){
		addEventListener(c, listener, null);
	}
	
	public void addEventListener(Class c, EventListener listener, Filter filter){
		Map<Class,Map<EventListener,Filter>> listeners = this.getListeners();
		synchronized(listeners){
			Map<EventListener, Filter> list = null;
			if(listeners.containsKey(c)){
				list = listeners.get(c);
			}
			else{
				list = new HashMap<EventListener,Filter>();
				listeners.put(c, list);		
			}
			if(!list.containsKey(listener))
				list.put(listener,filter);
		}
	}
	
	public void removeEventListener(Class c, EventListener listener){
		Map<Class,Map<EventListener,Filter>> listeners = this.getListeners();
		synchronized(listeners){
			if(c==null){
				Iterator<Entry<Class, Map<EventListener,Filter>>> iter = listeners.entrySet().iterator();
				while(iter.hasNext()){
					Entry<Class, Map<EventListener,Filter>> entry = iter.next();
					Map<EventListener,Filter> list = entry.getValue();
					if(list.containsKey(listener)){
						list.remove(listener);
						if(list.isEmpty())
							iter.remove();							
					}
				}
			} else {				
				if(listeners.containsKey(c)){
					Map<EventListener,Filter> list = listeners.get(c);
					if(list.containsKey(listener)){
						list.remove(listener);
						if(list.isEmpty())
							listeners.remove(c);
					}
				}
			}
			if(listeners.isEmpty())
			{
				this.listeners = null;
			}
		}		
	}	
	
	protected int fireEvent(Event event){
		Map<Class,Map<EventListener,Filter>> listeners = this.getListeners();
		int counter =0;		
		List<Entry<EventListener,Filter>> entries = new LinkedList<Entry<EventListener,Filter>>();		
		synchronized(listeners){
			for(Class c :listeners.keySet()){
				if(c.isInstance(event)){
					for(Entry<EventListener,Filter> entry: listeners.get(c).entrySet()){
						
						entries.add(entry);
			
					}
				}
			}		
		}
		for(Entry<EventListener,Filter> entry: entries){
			counter++;
			try{					
				EventWorker worker = null;
					while(worker==null) {
						worker = workers.poll();
						if(worker==null){
							synchronized(workers){
								workers.wait();
							}							
						}
					}
					synchronized(worker){
						worker.event = event;
						worker.listener = entry.getKey();
						worker.filter = entry.getValue();
						worker.notify();
					}
			}catch(Exception e){
				Logger.getLogger(this.getClass()).warn("Exception",e);
				throw new RuntimeException(e);
			}
		}
		return counter;
	}
	public static class EventWorker implements Runnable{
		
		EventListener listener;
		Event event;
		Filter filter;
		boolean waiting = false;
		Thread thread;
		
		@Override
		public void run() {			
			try {				
				while(true){
					while((listener==null||event==null)){
						synchronized(this){							
							workers.add(this);
							synchronized(workers){
								workers.notifyAll();
							}
							this.waiting = true;
							this.wait();
							this.waiting = false;
						}
					}
					try{
						if(filter==null||filter.filter(event))
							listener.handleEvent(event);						
						
					}catch(Exception e){
						Logger.getLogger(this.getClass()).warn("Exception",e);
						
					}finally{
						listener = null;
						event = null;
						filter = null;
					}
					
				}
			} catch (Exception e) {
				if (!(e instanceof InterruptedException)){
					Logger.getLogger(this.getClass()).warn("Exception",e);
					throw new RuntimeException(e);
				}
			}			
		}		
	}
	public static void shutdown(){
		int counter = threads.length;
		
		while(counter>0){
			EventWorker worker = workers.poll();
			if(worker!=null){
				synchronized(worker){
					if(worker.waiting){
						worker.thread.interrupt();
						counter--;
					}
				}
			}
		}	
	}	
}
