import java.net.Socket;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.items.potion.StaminaPotion;
import com.googlecode.reunion.jreunion.server.Client;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Tools;
import com.googlecode.reunion.jreunion.server.World;


public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	
	static long factorial( long n )
	   {
	   assert 0 <= n && n <= 20 : "factorial only handles numbers 0 to 20";
	   if ( n == 0 )
	      {
	      return 1;
	      }
	   else return n * factorial( n-1 );
	   }
	
	public static int getHp(int n){
		
		int coef = (n/50);		
		int c = 0;
		for(int i= 0; i<coef;i++){
			c +=i+1;
		}
		return(int) (c*50)+ (coef + 1) * (n % 50);
		//return coef*50*coef;
	}
	
	public static int inc(int n, int count){		
		int coef = (n/count);	
		return(int) ((0.5 * coef * (1 + coef)) * count) + (coef + 1) * (n % count);		
	}
	
	public static int getHpB(int n){
		if(n<1 )
			return 0;
		int coef = (n/50);
		return (int) ((int) factorial(n)- factorial(n-1));
	
	}
	
	public static void main(String[] args) throws Exception {
		
		for(int i = 0; i<300;i++){
			
			System.out.println(i+": "+inc(i,20)+" "+Tools.statCalc(i, 20));
			//System.out.println(i+": "+test(i));		
		}
		int strength = 45;
		
		//int constitution;
				
		
		Reference.getInstance().Load();
		StaminaPotion pot = (StaminaPotion) ItemFactory.create(142);
		//pot.use(null);
	}
}
