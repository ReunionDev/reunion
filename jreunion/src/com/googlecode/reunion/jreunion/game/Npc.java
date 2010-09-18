package com.googlecode.reunion.jreunion.game;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jcommon.ParsedItem;
import com.googlecode.reunion.jcommon.Parser;
import com.googlecode.reunion.jreunion.server.Reference;
import com.googlecode.reunion.jreunion.server.Server;
import com.googlecode.reunion.jreunion.server.Session;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Npc extends LivingObject {

	private int uniqueId;

	private int type;

	private int hp;

	private int sellRate;

	private int buyRate;

	private int spawnId;

	private String shop;

	private Parser shopReference;

	private List<Item> itemsList = new Vector<Item>();

	public Npc(int type) {
		super();
		this.type = type;
		shopReference = new Parser();
		shop = null;
	}

	public void addItem(Item item) {
		itemsList.add(item);
	}

	public int getBuyRate() {
		return buyRate;
	}

	/*** Return the distance between the npc and the living object ***/
	public int getDistance(LivingObject livingObject) {
		double xcomp = Math.pow(livingObject.getPosition().getX() - getPosition().getX(), 2);
		double ycomp = Math.pow(livingObject.getPosition().getY() - getPosition().getY(), 2);
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

	public Iterator<Item> itemsListIterator() {
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
		ParsedItem npc = Reference.getInstance().getNpcReference()
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
	
			Iterator<ParsedItem> iter = shopReference.getItemListIterator();
	
			while (iter.hasNext()) {
	
				ParsedItem i = iter.next();
	
				if (!i.checkMembers(new String[] { "Type" })) {
					System.out.println("Error loading a Npc Shop Item on map: "
							+ getPosition().getMap());
					continue;
				}
				Item item = new Item(Integer.parseInt(i.getMemberValue("Type")));
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

	@Override
	public void enter(Session session) {
		Server.getInstance().getWorldModule().getWorldCommand()
		.npcIn(session.getOwner(), this);
		
	}

	@Override
	public void exit(Session session) {
		Server.getInstance().getWorldModule().getWorldCommand()
		.npcOut(session.getOwner(), this);
		
	}
}