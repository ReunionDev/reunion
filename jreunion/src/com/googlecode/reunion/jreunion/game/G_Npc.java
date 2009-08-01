package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.server.*;


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
	
	private S_Parser itemsReference ;
	
	private List<G_Item> itemsList = new Vector<G_Item>();

	public G_Npc(int type) {
		super();
		this.type = type;
		itemsReference = new S_Parser();
		shop = null;
	}
		
	public void loadNpc(){
		loadFromReference(type);
		loadItemList();
	}
	
	public int getUniqueId() {
		return this.uniqueId;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setHp(int hp){
		this.hp = hp;
	}
	public int getHp(){
		return this.hp;
	}
	
	public void setSellRate(int sellRate){
		this.sellRate = sellRate;
	}
	public int getSellRate(){
		return this.sellRate;
	}
	
	public void setBuyRate(int buyRate){
		this.buyRate = buyRate;
	}
	public int getBuyRate(){
		return this.buyRate;
	}
	
	public void setShop(String shop){
		this.shop = shop;
	}
	public String getShop(){
		return this.shop;
	}
	
	public void setSpawnId(int spawnId){
		this.spawnId = spawnId;
	}
	public int getSpawnId(){
		return this.spawnId;
	}
	
	/*** 	Return the distance between the npc and the living object	***/
	public int getDistance(G_LivingObject livingObject){
		double xcomp = Math.pow(livingObject.getPosX() - this.getPosX(), 2);
		double ycomp = Math.pow(livingObject.getPosY() - this.getPosY(), 2);
		double distance = Math.sqrt(xcomp + ycomp);
		
		return (int)distance;
	}
	
	public void loadItemList(){
		
		//System.out.println("Loading list...");		
		
		if (itemsReference==null)
			return;	
					
		itemsList.clear();
		
		Iterator<S_ParsedItem> iter = itemsReference.getItemListIterator();
		
		while (iter.hasNext())
		{
			
			S_ParsedItem i = iter.next();
			
			if(!i.checkMembers(new String[]{"Type"}))
			{
				System.out.println("Error loading a Npc Shop Item on map: "+this.getMap());
				continue;
			}
			G_Item item = new G_Item(Integer.parseInt(i.getMemberValue("Type")));
			addItem(item);
		}
	}
	
	public void addItem(G_Item item){
		itemsList.add(item);
	}
	
	public Iterator<G_Item> itemsListIterator(){
		return itemsList.iterator();
	}
	
	public void loadFromReference(int id)
	{	
		super.loadFromReference(id);
	
		
		itemsReference.Parse(getShop());
	    S_ParsedItem npc = S_Reference.getInstance().getNpcReference().getItemById(id);
		
	    if (npc==null)
	    {
		  // cant find Item in the reference continue to load defaults:
	    	setHp(100);  
	    }
	    else {
		
		if(npc.checkMembers(new String[]{"Hp"}))
		{
			// use member from file
			setHp(Integer.parseInt(npc.getMemberValue("Hp")));
		}
	  }
	}
}