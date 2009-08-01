package com.googlecode.reunion.jreunion.game;

import java.util.Iterator;

import com.googlecode.reunion.jreunion.server.*;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class G_Trader extends G_Npc {
	
	public G_Trader(int id) {
		super(id);
	}
		
	/******		Exchange 5 "grade n" gems for 1 "grade n-1" gem		******/
	/******						or Gem Gambler						******/
	public void chipExchange(G_Player player, int gemTraderType, int chipType, int playerBet){
		
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
		
		if(client == null)
			return;
				
		int serverBetResult = (int)(Math.random()*6);
				
		Iterator<G_ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
		
		while(exchangeIter.hasNext()){
			G_ExchangeItem exchangeItem = exchangeIter.next();
			G_Item item = exchangeItem.getItem();
			S_DatabaseUtils.getInstance().deleteItem(item);
		}
		
		player.getExchange().clearExchange();		
								
		String packetData = new String();
		
		if(gemTraderType == 0){
			int newChipType = getNewChipTypeUp(chipType);
			G_Item item = S_ItemFactory.createItem(newChipType);
			G_ExchangeItem exchangeItem = new G_ExchangeItem(item,0,0);
			player.getExchange().addItem(exchangeItem);
			packetData = "chip_exchange 0 ok "+item.getType()+" "+item.getEntityId()+"\n";
		}
		else{
			if(playerBet == serverBetResult){
				int newChipType = getNewChipTypeUp(chipType);
				G_Item item = S_ItemFactory.createItem(newChipType);
				G_ExchangeItem exchangeItem = new G_ExchangeItem(item,0,0);
				player.getExchange().addItem(exchangeItem);
				packetData = "chip_exchange 1 ok win "+item.getType()+" "+playerBet+" "+item.getEntityId()+"\n";
			}
			else{
				int newChipType = getNewChipTypeDown(chipType);
				if(newChipType != -1){
					G_Item item = S_ItemFactory.createItem(newChipType);
					G_ExchangeItem exchangeItem = new G_ExchangeItem(item,0,0);
					player.getExchange().addItem(exchangeItem);
				}
				packetData = "chip_exchange 1 ok lose "+newChipType+" "+serverBetResult+"\n";
			}
		}
		
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
	}
	
	/******		Exchange a certain race armor part for another race armor part of the same level	******/
	public void exchangeArmor(G_Player player, int armorType){
		
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
		
		if(client == null)
			return;
		
		String packetData = new String();
		
		if(armorType == 0){
			packetData = "ichange 0 0 0 0 0";
		}
		else {
			Iterator<G_ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
			G_ExchangeItem oldExchangeItem = (G_ExchangeItem)exchangeIter.next();
			G_Armor newItem = (G_Armor)S_ItemFactory.createItem(armorType);
			G_Armor oldItem = (G_Armor)S_ItemFactory.loadItem(oldExchangeItem.getItem().getEntityId());
			
			if(newItem instanceof G_Armor == false || newItem.getLevel() != oldItem.getLevel())
				return;
			
			S_DatabaseUtils.getInstance().deleteItem(oldItem);
			G_ExchangeItem newExchangeItem = new G_ExchangeItem(newItem,0,0);
			
			player.getExchange().clearExchange();
			player.getExchange().addItem(newExchangeItem);
			player.updateStatus(10,(int)(newItem.getPrice()*0.333328)*(-1),0);
			
			packetData = "ichange "+oldExchangeItem.getItem().getEntityId()+
						 " "+newItem.getEntityId()+
						 " "+newItem.getType()+
						 " "+newItem.getGemNumber()+
						 " "+newItem.getExtraStats()+"\n";
		}
		
		S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData);
		
	}
		
	public int getNewChipTypeUp(int chipType){
		
		int newChipType=0;
		
		switch(chipType){
			case 521: {newChipType=222; break;}
			case 522: {newChipType=223; break;}
			case 523: {newChipType=224; break;}
			case 524: {newChipType=225; break;}
			case 525: {newChipType=226; break;}
			case 526: {newChipType=227; break;}
			case 527: {newChipType=228; break;}
			case 528: {newChipType=521; break;}
			case 529: {newChipType=522; break;}
			case 530: {newChipType=523; break;}
			case 531: {newChipType=524; break;}
			case 532: {newChipType=525; break;}
			case 533: {newChipType=526; break;}
			case 534: {newChipType=527; break;}
			case 535: {newChipType=528; break;}
			case 536: {newChipType=529; break;}
			case 537: {newChipType=530; break;}
			case 538: {newChipType=531; break;}
			case 539: {newChipType=532; break;}
			case 540: {newChipType=533; break;}
			case 541: {newChipType=534; break;}
			default: newChipType = -1;
		}
		return newChipType;
	}
	
	public int getNewChipTypeDown(int chipType){
		
		int newChipType=0;
		
		switch(chipType){
			case 521: {newChipType=528; break;}
			case 522: {newChipType=529; break;}
			case 523: {newChipType=530; break;}
			case 524: {newChipType=531; break;}
			case 525: {newChipType=532; break;}
			case 526: {newChipType=533; break;}
			case 527: {newChipType=534; break;}
			case 528: {newChipType=535; break;}
			case 529: {newChipType=536; break;}
			case 530: {newChipType=537; break;}
			case 531: {newChipType=538; break;}
			case 532: {newChipType=539; break;}
			case 533: {newChipType=540; break;}
			case 534: {newChipType=541; break;}
			default: newChipType = -1;
		}
		return newChipType;
	}
}