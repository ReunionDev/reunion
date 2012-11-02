import java.util.concurrent.*;

import org.reunionemu.jreunion.server.ManualScheduledExecutor;


public class SchedulerTest {

	
	public SchedulerTest() {
		
	}


	
	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws ExecutionException 
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
				
		final ManualScheduledExecutor executor = new ManualScheduledExecutor();
		
	    executor.scheduleAtFixedRate(new Runnable() {
				
			@Override
			public void run() {
				System.out.println("run");
				
			}
		}, 0, 1, TimeUnit.SECONDS);
	    
	    Future<Integer> solution =  executor.submit(new Callable<Integer>() {
	    	
	    	@Override
	    	public Integer call() throws Exception {
	    		return 1337;
	    	}
		});
	   
	    
	    solution = executor.submit(new Callable<Integer>() {
	    	
	    	@Override
	    	public Integer call() throws Exception {
	    		
	    		Future<Integer> solution = executor.submit(new Callable<Integer>() {
	    	    	
	    	    	@Override
	    	    	public Integer call() throws Exception {
	    	    		return 1337;
	    	    	}
	    		});
	    		return solution.get();
	    	}
		});
	    
	    
	    System.out.println(solution.isDone());
	    
	    
	    // This will make the executor accept no new threads
	    // and finish all existing threads in the queue
	    //executor.shutdown();
	    Runnable task = null;
	    while(true){
	    	System.out.println(solution.isDone());
	    	if(solution.isDone()){
		    	break;
		    }
		    while((task = executor.runNext())!=null){
		    	System.out.println("run task: "+task);
		    	//task.run();
		    	
		    }
		    
		    
		    Thread.sleep(100);
	    }
	    
	    //System.out.println("Finished all threads");
	
		
	}

}
