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


	private int type;

	private int hp;

	private int sellRate;

	private int buyRate;

	private String shop;

	private Parser shopReference;

	private List<VendorItem> itemsList = new Vector<VendorItem>();

	public Npc(int type) {
		super();
		this.type = type;
		shopReference = new Parser();
		shop = null;
	}

	public void addItem(VendorItem item) {
		itemsList.add(item);
	}

	public int getBuyRate() {
		return buyRate;
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

	public int getType() {
		return type;
	}

	public Iterator<VendorItem> itemsListIterator() {
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
				VendorItem item = new VendorItem(Integer.parseInt(i.getMemberValue("Type")));
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

	@Override
	public void enter(Session session) {
		this.getPosition().getMap().getWorld().getCommand()
		.npcIn(session.getOwner(), this);		
	}

	@Override
	public void exit(Session session) {
		this.getPosition().getMap().getWorld().getCommand()
		.npcOut(session.getOwner(), this);
		
	}
}