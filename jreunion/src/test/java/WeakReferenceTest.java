import java.lang.ref.*;
import java.util.WeakHashMap;

import org.junit.Test;


public class WeakReferenceTest {

	@Test
	public void test() throws InterruptedException {
		
		ReferenceQueue<String> queue = new ReferenceQueue<String>();
		
		WeakHashMap list;
		String input = "i";
		WeakReference<String> ref = null;
		{
			ref = new WeakReference<String>("Test"+ input, queue);
			
		}
		System.out.println(ref.get());
		
		//while(queue.poll()==null)
		{
			
			//System.out.println("gc");
			System.gc();
			Thread.sleep(0);
			
		}
		
	}

}
