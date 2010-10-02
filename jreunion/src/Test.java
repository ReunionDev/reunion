import com.googlecode.reunion.jreunion.game.items.potion.StaminaPotion;
import com.googlecode.reunion.jreunion.server.ItemFactory;
import com.googlecode.reunion.jreunion.server.Reference;


public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Reference.getInstance().Load();
		StaminaPotion pot = (StaminaPotion) ItemFactory.create(142);
		pot.use(null);
	}

}
