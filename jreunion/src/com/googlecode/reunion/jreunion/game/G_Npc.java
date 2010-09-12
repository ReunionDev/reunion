package com.googlecode.reunion.jreunion.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.server.S_ParsedItem;
import com.googlecode.reunion.jreunion.server.S_Parser;
import com.googlecode.reunion.jreunion.server.S_Reference;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Npc extends G_LivingObject {

	private int uniqueId;

	private int type;

	private int hp;

	private int sellRate;

	private int buyRate;

	private int spawnId;

	private String shop;

	private S_Parser shopReference;

	private List<G_Item> itemsList = new Vector<G_Item>();

	public G_Npc(int type) {
		super();
		this.type = type;
		shopReference = new S_Parser();
		shop = null;
	}

	public void addItem(G_Item item) {
		itemsList.add(item);
	}

	public int getBuyRate() {
		return buyRate;
	}

	/*** Return the distance between the npc and the living object ***/
	public int getDistance(G_LivingObject livingObject) {
		double xcomp = Math.pow(livingObject.getPosX() - getPosX(), 2);
		double ycomp = Math.pow(livingObject.getPosY() - getPosY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);

		return (int) distance;
	}

	public int getHp() {
		return hp;
	}

	public int getSellRate() {
		return sellRate;
	}

	public String getShop() {
		return shop;
	}

	public int getSpawnId() {
		return spawnId;
	}

	public int getType() {
		return type;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public Iterator<G_Item> itemsListIterator() {
		return itemsList.iterator();
	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
		
		try {
			shopReference.Parse("data/"+getShop());
		} catch (IOException e) {
			e.printStackTrace();
		}
		S_ParsedItem npc = S_Reference.getInstance().getNpcReference()
				.getItemById(id);

		if (npc == null) {
			// cant find Item in the reference continue to load defaults:
			setHp(100);
		} else {

			if (npc.checkMembers(new String[] { "Hp" })) {
				// use member from file
				setHp(Integer.parseInt(npc.getMemberValue("Hp")));
			}
		}
	}

	public void loadItemList() {

		// System.out.println("Loading list...");

		if (shopReference != null) {
	
			itemsList.clear();
	
			Iterator<S_ParsedItem> iter = shopReference.getItemListIterator();
	
			while (iter.hasNext()) {
	
				S_ParsedItem i = iter.next();
	
				if (!i.checkMembers(new String[] { "Type" })) {
					System.out.println("Error loading a Npc Shop Item on map: "
							+ getMap());
					continue;
				}
				G_Item item = new G_Item(Integer.parseInt(i.getMemberValue("Type")));
				addItem(item);
			}
		}
	}

	public void loadNpc() {
		loadFromReference(type);
		loadItemList();
	}

	public void setBuyRate(int buyRate) {
		this.buyRate = buyRate;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setSellRate(int sellRate) {
		this.sellRate = sellRate;
	}

	public void setShop(String shop) {
		this.shop = shop;
	}

	public void setSpawnId(int spawnId) {
		this.spawnId = spawnId;
	}
}