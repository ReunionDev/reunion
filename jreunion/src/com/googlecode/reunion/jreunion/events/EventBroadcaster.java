package com.googlecode.reunion.jreunion.events;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
public class EventBroadcaster{
	static LinkedBlockingQueue<EventWorker> workers;
	static Thread [] threads = new Thread[25];
	static {
		workers = new LinkedBlockingQueue<EventWorker>(threads.length);
		for(int i=0; i<threads.length;i++) {
			EventWorker worker = new EventWorker();
			worker.thread = threads[i] = new Thread(worker);
			worker.thread.start();
		}
	}
	public  Map<Class,List<EventListener>> listeners;
	public Map<Class, List<EventListener>> getListeners() {
		synchronized (this){
			if(listeners==null)
				listeners = new HashMap<Class,List<EventListener>>();			
		}
		return listeners;
	}

	public void addEventListener(Class c, EventListener listener){
		Map<Class,List<EventListener>> listeners = this.getListeners();
		synchronized(listeners){
			List<EventListener> list = null;
			if(listeners.containsKey(c)){
				list = listeners.get(c);
			}
			else{
				list = new LinkedList<EventListener>();
				listeners.put(c, list);		
			}
			if(!list.contains(listener))
				list.add(listener);
		}
	}
	
	public void removeEventListener(Class c, EventListener listener){
		Map<Class,List<EventListener>> listeners = this.getListeners();
		synchronized(listeners){
			if(c==null){
				Iterator<Entry<Class, List<EventListener>>> iter = listeners.entrySet().iterator();
				while(iter.hasNext()){
					Entry<Class, List<EventListener>> entry = iter.next();
					List<EventListener> list = entry.getValue();
					if(list.contains(listener)){
						list.remove(listener);
						if(list.isEmpty())
							iter.remove();							
					}
				}
			} else {				
				if(listeners.containsKey(c)){
					List<EventListener> list = listeners.get(c);
					if(list.contains(listener)){
						list.remove(listener);
						list.remove(c);
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
	
	protected void fireEvent(Event event){
		Map<Class,List<EventListener>> listeners = this.getListeners();
		synchronized(listeners){
			for(Class c :listeners.keySet()){
				if(c.isInstance(event)){
					for(EventListener listener:listeners.get(c)){						
						try{					
							EventWorker worker = workers.take();
								synchronized(worker){
									worker.event = event;
									worker.listener = listener;
									worker.notify();
								}
						}catch(Exception e){
							e.printStackTrace();
							throw new RuntimeException(e);
						}
					}
				}
			}		
		}
	}
	public static class EventWorker implements Runnable{
		
		EventListener listener;
		Event event;
		boolean waiting = false;
		Thread thread;
		
		@Override
		public void run() {			
			try {				
				while(true){
					while((listener==null||event==null)){
						synchronized(this){							
							workers.add(this);
							this.waiting = true;
							this.wait();
							this.waiting = false;
						}
					}
					try{				
						listener.handleEvent(event);						
						
					}catch(Exception e){
						e.printStackTrace();
						
					}finally{
						listener = null;
						event = null;
					}
					
				}
			} catch (Exception e) {
				if (!(e instanceof InterruptedException)){
					e.printStackTrace();
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
