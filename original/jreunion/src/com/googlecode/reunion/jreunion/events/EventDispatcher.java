package com.googlecode.reunion.jreunion.events;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.log4j.Logger;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class EventDispatcher{
	
	private Object sync = new Object();

	private static ExecutorService tpe = Executors.newCachedThreadPool(new ThreadFactory() {
		
		@Override
		public Thread newThread(Runnable r) {
			Thread thread = Executors.defaultThreadFactory().newThread(r);
			System.out.println("Thread created: "+thread);
			return thread;
		}
	});
	static{
		
	}
	public <T extends Event> T createEvent(Class<T> eventClass, Object... args){
		return Event.<T>Create(eventClass, this, args);
	}
	
	public <T extends Event> int fireEvent(Class<T> eventClass, Object... args){
		return this.fireEvent(this.createEvent(eventClass, args));
	}
	
	private  Map<Class<? extends Event>,Map<EventListener,Filter>> listeners;
	
	public Map<Class<? extends Event>, Map<EventListener,Filter>> getListeners() {
		synchronized (sync){
			if(listeners==null)
				listeners = new HashMap<Class<? extends Event>,Map<EventListener,Filter>>();			
		}
		return listeners;
	}

	public void addEventListener(Class<? extends Event> c, EventListener listener){
		addEventListener(c, listener, null);
	}
	
	public void addEventListener(Class<? extends Event> c, EventListener listener, Filter filter){
		Map<Class<? extends Event>,Map<EventListener,Filter>> listeners = this.getListeners();
		synchronized(sync){
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
	
	public void  removeEventListener(Class<? extends Event> c, EventListener listener){
		Map<Class<? extends Event>,Map<EventListener,Filter>> listeners = this.getListeners();
		synchronized(sync){
			if(c==null){
				Iterator<Entry<Class<? extends Event>, Map<EventListener,Filter>>> iter = listeners.entrySet().iterator();
				while(iter.hasNext()){
					Entry<Class<? extends Event>, Map<EventListener,Filter>> entry = iter.next();
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
	
	
	
	private List<Entry<EventListener,Filter>> findEntries(Event event){
		Map<Class<? extends Event>,Map<EventListener,Filter>> listeners = this.getListeners();
			
		List<Entry<EventListener,Filter>> entries = new LinkedList<Entry<EventListener,Filter>>();		
		synchronized(sync){
			for(Class<? extends Event> c :listeners.keySet()){
				if(c.isInstance(event)){
					for(Entry<EventListener,Filter> entry: listeners.get(c).entrySet()){
						entries.add(entry);
					}
				}
			}		
		}
		return entries;
	}
	
	protected int fireEvent(Event event) {
		List<Entry<EventListener,Filter>> entries = findEntries(event);
		int counter =0;	
		for(Entry<EventListener,Filter> entry: entries){
			counter++;
			try{
				EventListener listener = entry.getKey();
				Filter filter = entry.getValue();
				if(filter==null || filter.filter(event))
					listener.handleEvent(event);
			}catch(Exception e){
				Logger.getLogger(this.getClass()).error("Exception",e);
			}
		}
		return counter;
	}
	protected int fireEventAsync(final Event event) {
		List<Entry<EventListener,Filter>> entries = findEntries(event);
		int counter =0;	
		for(final Entry<EventListener,Filter> entry: entries){
			counter++;
			tpe.submit(
			new Callable<Object>(){
				@Override
				public Object call() throws Exception {
					try{
						EventListener listener = entry.getKey();
						Filter filter = entry.getValue();
						if(filter==null||filter.filter(event))
							listener.handleEvent(event);
						}catch(Exception e){
							Logger.getLogger(this.getClass()).warn("Exception",e);
							throw new RuntimeException(e);
						}
					return null;
				}
			});
		}
		return counter;
	}

	public static void shutdown(){
		tpe.shutdown();
	}	
}
