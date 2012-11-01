import java.util.concurrent.*;

import org.reunionemu.jreunion.server.ManualScheduledExecutor;


public class SchedulerTest {

	
	public SchedulerTest() {
		
	}


	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
				
		ManualScheduledExecutor executor = new ManualScheduledExecutor();
		
	    executor.scheduleAtFixedRate(new Runnable() {
				
			@Override
			public void run() {
				System.out.println("run");
				
			}
		}, 0, 1, TimeUnit.SECONDS);
		
	    // This will make the executor accept no new threads
	    // and finish all existing threads in the queue
	    //executor.shutdown();
	    Runnable task = null;
	    while(true){
		    while((task = executor.getQueue().poll())!=null){
		    	task.run();
		    	
		    	
		    }
		    Thread.sleep(100);
	    }
	    
	    //System.out.println("Finished all threads");
	
		
	}

}
