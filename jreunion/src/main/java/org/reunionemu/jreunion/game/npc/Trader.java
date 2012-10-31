package org.reunionemu.jreunion.game.npc;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.reunionemu.jreunion.game.ExchangeItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.NpcType;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.items.equipment.Armor;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.Database;
import org.reunionemu.jreunion.server.ItemManager;
import org.reunionemu.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class Trader extends NpcType {

	public Trader(int id) {
		super(id);
		//loadFromReference(id);
	}

	/****** Exchange 5 "grade n" gems for 1 "grade n-1" gem ******/
	/****** or Gem Gambler ******/
	public void chipExchange(Player player, int gemTraderType, int chipType,
			int playerBet) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		ItemManager itemManager = client.getWorld().getItemManager();
		int serverBetResult = (int) (Math.random() * 6);

		Iterator<ExchangeItem> exchangeIter = player.getExchange().itemListIterator();

		while (exchangeIter.hasNext()) {
			ExchangeItem exchangeItem = exchangeIter.next();
			Item<?> item = exchangeItem.getItem();
			item.delete();
			player.getPosition().getLocalMap().removeEntity(item);
		}

		player.getExchange().clearExchange();

		if (gemTraderType == 0) {
			int newChipType = getNewChipTypeUp(chipType);
			Item<?> item = itemManager.create(newChipType);
			
			if(item.getEntityId() == -1)
				player.getPosition().getLocalMap().createEntityId(item);
			
			ExchangeItem exchangeItem = new ExchangeItem(item, 0, 0, player);
			player.getExchange().addItem(exchangeItem);
			client.sendPacket(Type.CHIP_EXCHANGE, gemTraderType, "", item, "");
		} else {
			if (playerBet == serverBetResult) {
				int newChipType = getNewChipTypeUp(chipType);
				Item<?> item = itemManager.create(newChipType);
				
				if(item.getEntityId() == -1)
					player.getPosition().getLocalMap().createEntityId(item);
				
				ExchangeItem exchangeItem = new ExchangeItem(item, 0, 0, player);
				player.getExchange().addItem(exchangeItem);
				client.sendPacket(Type.CHIP_EXCHANGE, gemTraderType, "win ", item, (Integer.toString(serverBetResult))+" ");
			} else {
				int newChipType = getNewChipTypeDown(chipType);
				Item<?> item = null;
				if (newChipType != -1) {
					 item = itemManager.create(newChipType);
					
					if(item.getEntityId() == -1)
						player.getPosition().getLocalMap().createEntityId(item);
					
					ExchangeItem exchangeItem = new ExchangeItem(item, 0, 0, player);
					player.getExchange().addItem(exchangeItem);
				}
				client.sendPacket(Type.CHIP_EXCHANGE, gemTraderType, "lose ", item, (Integer.toString(serverBetResult))+" ");
			}
		}
				
		
	}

	/******
	 * Exchange a certain race armor part for another race armor part of the
	 * same level
	 ******/
	public void exchangeArmor(Player player, int armorType) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		ItemManager itemManager = client.getWorld().getItemManager();

		if (armorType == 0) {
			client.sendPacket(Type.ICHANGE, null, null);
		} else {
			Iterator<ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
			ExchangeItem oldExchangeItem = exchangeIter.next();
			
			Item<?> newItem = itemManager.create(armorType);
			
			if(newItem.getEntityId() == -1)
				player.getPosition().getLocalMap().createEntityId(newItem);
			
			Item<?> oldItem = oldExchangeItem.getItem();

			if ((newItem.is(Armor.class) == false) ||
				((Armor)newItem.getType()).getLevel() != ((Armor)oldItem.getType()).getLevel()) {
				LoggerFactory.getLogger(Trader.class).warn("Player "+player+" tried to exchange item "
						+oldItem+" for item "+newItem);
				return;
			}

			ExchangeItem newExchangeItem = new ExchangeItem(newItem, 0, 0, player);
			player.getExchange().clearExchange();
			player.getExchange().addItem(newExchangeItem);
			int cost =  (int) (newItem.getType().getPrice() * 0.33328);
			synchronized(player){							
				player.setLime(player.getLime()-cost);

			}
			client.sendPacket(Type.ICHANGE, oldItem, newItem);
			oldItem.delete();
			player.getPosition().getLocalMap().removeEntity(oldItem);
		}
	}

	public int getNewChipTypeDown(int chipType) {

		int newChipType = 0;

		switch (chipType) {
		case 521: {
			newChipType = 528;
			break;
		}
		case 522: {
			newChipType = 529;
			break;
		}
		case 523: {
			newChipType = 530;
			break;
		}
		case 524: {
			newChipType = 531;
			break;
		}
		case 525: {
			newChipType = 532;
			break;
		}
		case 526: {
			newChipType = 533;
			break;
		}
		case 527: {
			newChipType = 534;
			break;
		}
		case 528: {
			newChipType = 535;
			break;
		}
		case 529: {
			newChipType = 536;
			break;
		}
		case 530: {
			newChipType = 537;
			break;
		}
		case 531: {
			newChipType = 538;
			break;
		}
		case 532: {
			newChipType = 539;
			break;
		}
		case 533: {
			newChipType = 540;
			break;
		}
		case 534: {
			newChipType = 541;
			break;
		}
		default:
			newChipType = -1;
		}
		return newChipType;
	}

	public int getNewChipTypeUp(int chipType) {

		int newChipType = 0;

		switch (chipType) {
		case 521: {
			newChipType = 222;
			break;
		}
		case 522: {
			newChipType = 223;
			break;
		}
		case 523: {
			newChipType = 224;
			break;
		}
		case 524: {
			newChipType = 225;
			break;
		}
		case 525: {
			newChipType = 226;
			break;
		}
		case 526: {
			newChipType = 227;
			break;
		}
		case 527: {
			newChipType = 228;
			break;
		}
		case 528: {
			newChipType = 521;
			break;
		}
		case 529: {
			newChipType = 522;
			break;
		}
		case 530: {
			newChipType = 523;
			break;
		}
		case 531: {
			newChipType = 524;
			break;
		}
		case 532: {
			newChipType = 525;
			break;
		}
		case 533: {
			newChipType = 526;
			break;
		}
		case 534: {
			newChipType = 527;
			break;
		}
		case 535: {
			newChipType = 528;
			break;
		}
		case 536: {
			newChipType = 529;
			break;
		}
		case 537: {
			newChipType = 530;
			break;
		}
		case 538: {
			newChipType = 531;
			break;
		}
		case 539: {
			newChipType = 532;
			break;
		}
		case 540: {
			newChipType = 533;
			break;
		}
		case 541: {
			newChipType = 534;
			break;
		}
		default:
			newChipType = -1;
		}
		return newChipType;
	}
}