package org.reunionemu.jreunion.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.net.SocketAppender;
import org.reunionemu.jcommon.ParsedItem;
import org.reunionemu.jreunion.dao.QuestDao;
import org.reunionemu.jreunion.game.HandPosition;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.NpcSpawn;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Position;
import org.reunionemu.jreunion.game.RoamingItem;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.model.Quest;
import org.reunionemu.jreunion.server.Area.Field;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
@Configurable
public class MessageParser {

	static int spawnCounter = 0;
	
	public MessageParser() {
		super();
	}
	
	@Autowired
	QuestDao questDao;

	String parse(Player player, String text) {
		text = text.trim();
		String words[] = text.split(" ");
		Client client = player.getClient();
		World world = client.getWorld();
		Command com = world.getCommand();
		
		if (words[0].equals("@levelup") && player.getAdminState() >= 210) {
			if (words.length > 1) {
				
				int maxLevel = 400;
				
				boolean hasMaxLevel = ((maxLevel != 0) ? true : false);
				
				int lvlup = Integer.parseInt(words[1]);
				
				if(hasMaxLevel && maxLevel < (player.getLevel()+lvlup))
				{
					lvlup = maxLevel-player.getLevel();
				}
				int pCurrLvl = player.getLevel();
				int pSPup = 0;
				
				if(pCurrLvl < 250) {
					if(pCurrLvl+lvlup > 250) {
						int pLVLto250 = 250-pCurrLvl;
						int pLVLupRest = lvlup-pLVLto250;
						pSPup = pLVLto250*3+pLVLupRest*10;
					}
					else {
						pSPup = lvlup*3;
					}
				}
				else {
					pSPup = lvlup*10;
				}
				
				player.setLevel(player.getLevel()+lvlup);
				player.setStatusPoints(player.getStatusPoints()+pSPup);
			}
		}
		else if (words[0].equals("@walkable") && player.getAdminState() >= 260)
		{
			Area area = player.getPosition().getLocalMap().getArea(); 
			
			int x = 0;
			int y = 0;
			
			if(words.length == 3)
			{
				x = Integer.parseInt(words[1]);
				y = Integer.parseInt(words[2]);
				client.sendPacket(Type.SAY, "using arguments");
			}
			else
			{
				x = player.getPosition().getX();
				y = player.getPosition().getY();
			}
			
			if(area.get(x / 10, y / 10,Field.MOB))
			{
				client.sendPacket(Type.SAY, "Mob can walk here");
			}
			else
			{
				client.sendPacket(Type.SAY, "Mob can't walk here");
			}
				
			if(area.get(x / 10, y / 10,Field.PLAYER))
			{
				client.sendPacket(Type.SAY, "Player can walk here");
			}
			else
			{
				client.sendPacket(Type.SAY, "Player can't walk here");
			}
		}
		else if (words[0].equals("@points"))
		{
			if(words.length == 3 && (words[1].equals("strength") || words[1].equals("wisdom") || words[1].equals("dex") || words[1].equals("strain") || words[1].equals("charisma"))) {
				long pointsToUpgrade = Integer.parseInt(words[2]);
				
				if(pointsToUpgrade > 0) {
					if(player.getStatusPoints() < pointsToUpgrade)
						pointsToUpgrade = player.getStatusPoints();
					
					if(words[1].equals("strength"))
						player.setStrength(player.getStrength() + pointsToUpgrade);
					else if (words[1].equals("wisdom"))
						player.setWisdom(player.getWisdom()+pointsToUpgrade);
					else if (words[1].equals("dex"))
						player.setDexterity(player.getDexterity()+pointsToUpgrade);
					else if (words[1].equals("strain"))
						player.setConstitution(player.getConstitution()+pointsToUpgrade);
					else if (words[1].equals("charisma"))
						player.setLeadership(player.getLeadership()+pointsToUpgrade);
					
					player.setStatusPoints(player.getStatusPoints()+pointsToUpgrade*-1);
				}
				else
					client.sendPacket(Type.SAY, "You can't enter values < 0");
			} else if(words[1].equals("reskill") && player.getAdminState() >= 120) {
				if(words.length == 7) {
					int strength = Integer.parseInt(words[2]);
					int wisdom   = Integer.parseInt(words[3]);
					int dex      = Integer.parseInt(words[4]);
					int strain   = Integer.parseInt(words[5]);
					int charisma = Integer.parseInt(words[6]);
					
					boolean allowed = (((player.getRace() == Race.HYBRIDER && strength >= 5 && strength <= 30 && wisdom >=5 && wisdom <= 10 && dex >=5 && dex <=10 && strain >= 5 && strain <= 25 && charisma >=5 && charisma <= 5)) ? true : (((player.getRace() == Race.AIDIA && strength >= 5 && strength <= 15 && wisdom >=5 && wisdom <= 30 && dex >=5 && dex <=20 && strain >= 5 && strain <= 15 && charisma >=5 && charisma <= 20) ) ? true : ((player.getRace() == Race.HUMAN  && strength >= 5 && strength <= 15 && wisdom >=5 && wisdom <= 5 && dex >=5 && dex <=30 && strain >= 5 && strain <= 20 && charisma >=5 && charisma <= 10) ? true : ((player.getRace() == Race.KAILIPTON && strength >= 5 && strength <= 15 && wisdom >=5 && wisdom <= 30 && dex >=5 && dex <=5 && strain >= 5 && strain <= 15 && charisma >=5 && charisma <= 15) ? true : ((player.getRace() == Race.BULKAN  && strength >= 5 && strength <= 30 && wisdom >=5 && wisdom <= 5 && dex >=5 && dex <=5 && strain >= 5 && strain <= 30 && charisma >=5 && charisma <= 10) ? true : false)))));
					
					int sumuSP = strength+wisdom+dex+strain+charisma;
					
					if(allowed) {
						player.setStrength(strength);
						player.setWisdom(wisdom);
						player.setDexterity(dex);
						player.setConstitution(strain);
						player.setLeadership(charisma);
						
						player.resetSkills();
						
						sumuSP = 80-sumuSP-3;
						player.setStatusPoints(player.getMaxStatusPoints()+sumuSP);
					}
					else
						client.sendPacket(Type.SAY, "Sum of Strength, Wisdom, Dex, Stain and charisma must be between 25 and 80");
				}
				else
				{
					player.setStrength(5);
					player.setWisdom(5);
					player.setDexterity(5);
					player.setConstitution(5);
					player.setLeadership(5);
					
					player.resetSkills();
					
					player.setStatusPoints(player.getMaxStatusPoints()+55);
				}
			}
		}
		else if ((words[0].equals("@getlime") || words[0].equals("@gl")) && player.getAdminState() >= 260)
		{
			if(words.length == 2)
			{
				try {
					long playerAddLime = Long.parseLong(words[1]);
					long newLime = player.getLime()+playerAddLime;
					
					if(newLime > Long.MAX_VALUE)
					{
						newLime = Long.MAX_VALUE;
					}
					
					if(newLime > 0)
					{
						player.setLime(newLime);
						client.sendPacket(Type.SAY, "New Lime :"+newLime);
					}
					else
					{
						client.sendPacket(Type.SAY, "Your new Lime value cant be negative!");
					}
				}
				catch (Exception e)
				{
					client.sendPacket(Type.SAY, "Wrong Lime Value");
				}
			}
		}
		else if (words[0].equals("@guild"))
		{
			if(words[1].equals("create") && player.getAdminState() >= 120) {
				if(words.length == 3)
				{
					String name = (String)words[2];
					
					int guildId = DatabaseUtils.getDinamicInstance().addGuild(name);
					if(guildId != 0)
					{
						player.setGuildId(guildId);
						player.setGuildLevel(10);
						player.setGuildName(name);
						
						player.getClient().sendPacket(Type.GUILD_NAME, player);
						player.getClient().sendPacket(Type.GUILD_GRADE, player);
						player.getClient().sendPacket(Type.GUILD_LEVEL, player);
						
						player.getInterested().sendPacket(Type.GUILD_NAME, player);
						player.getInterested().sendPacket(Type.GUILD_GRADE, player);
						player.getInterested().sendPacket(Type.GUILD_LEVEL, player);
					}
				}
				else if(words.length == 4)
				{
					String name = (String)words[2];
					
					int guildId = DatabaseUtils.getDinamicInstance().addGuild(name);
					if(guildId != 0)
					{
						Player targetPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[3]);
						
						targetPlayer.setGuildId(guildId);
						targetPlayer.setGuildLevel(10);
						targetPlayer.setGuildName(name);
						
						targetPlayer.getClient().sendPacket(Type.GUILD_NAME, targetPlayer);
						targetPlayer.getClient().sendPacket(Type.GUILD_GRADE, targetPlayer);
						targetPlayer.getClient().sendPacket(Type.GUILD_LEVEL, targetPlayer);
						
						targetPlayer.getInterested().sendPacket(Type.GUILD_NAME, targetPlayer);
						targetPlayer.getInterested().sendPacket(Type.GUILD_GRADE, targetPlayer);
						targetPlayer.getInterested().sendPacket(Type.GUILD_LEVEL, targetPlayer);
					}
				}
			}
			else if(words[1].equals("add")) {
				if(player.getGuildId() != 0 && player.getGuildLvl() > 1)
				{
					Player targetPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[2]);
					
					//client.sendPacket(Type.SAY, "Your Guild: "+player.getGuildName()+" req:"+targetPlayer.getGuildRequestName());
					
					if(targetPlayer.getGuildRequestName().equals(player.getGuildName()))
					{
						targetPlayer.setGuildId(player.getGuildId());
						targetPlayer.setGuildName(player.getGuildName());
						
						if(words.length == 4)
							targetPlayer.setGuildLevel(Integer.parseInt(words[3]));
						else
							targetPlayer.setGuildLevel(1);
						
						targetPlayer.getClient().sendPacket(Type.GUILD_NAME, targetPlayer);
						targetPlayer.getClient().sendPacket(Type.GUILD_GRADE, targetPlayer);
						targetPlayer.getClient().sendPacket(Type.GUILD_LEVEL, targetPlayer);
						
						targetPlayer.getInterested().sendPacket(Type.GUILD_NAME, targetPlayer);
						targetPlayer.getInterested().sendPacket(Type.GUILD_GRADE, targetPlayer);
						targetPlayer.getInterested().sendPacket(Type.GUILD_LEVEL, targetPlayer);
						
						targetPlayer.getClient().sendPacket(Type.SAY, "You've been accepted to join guild");
						
						client.sendPacket(Type.SAY, "Added Player "+targetPlayer.getName()+" to guild ");
					}
					else
					{
						client.sendPacket(Type.SAY, "Player "+targetPlayer.getName()+" didnt requested membership");
					}
				}
				else {
					client.sendPacket(Type.SAY, "You are not in a guild or you dont have permission to add member");
				}
			}
			else if(words[1].equals("changeMember")) {
				if(words.length == 4){
					try {
						Player targetPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[2]);
						
						if(targetPlayer.getGuildId() == player.getGuildId() && (player.getGuildLvl() > targetPlayer.getGuildLvl() || player.getGuildLvl() == 10 ))
						{
							targetPlayer.setGuildLevel(Integer.parseInt(words[3]));
							
							targetPlayer.getClient().sendPacket(Type.GUILD_GRADE, targetPlayer);
							targetPlayer.getInterested().sendPacket(Type.GUILD_GRADE, targetPlayer);
							targetPlayer.getClient().sendPacket(Type.GUILD_LEVEL, targetPlayer);
							targetPlayer.getInterested().sendPacket(Type.GUILD_LEVEL, targetPlayer);
							
							client.sendPacket(Type.SAY, "Guildlevel of Player "+words[2]+" changed to "+words[3]);
							targetPlayer.getClient().sendPacket(Type.SAY, "Your Guildlevel changed by "+player.getName()+" to "+words[3]);
						}
					} catch (Exception e)
					{
						client.sendPacket(Type.SAY, "Player "+words[2]+" is not online!");
					}
				}
				else
					client.sendPacket(Type.SAY, "Wrong Parameters, @guild changeUser Name Level");
			}
			else if(words[1].equals("close")) {
				if(player.getGuildId() != 0 && player.getGuildLvl() == 10)
				{
					DatabaseUtils.getDinamicInstance().deleteGuild((int)player.getGuildId());
					
					Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
					
					long guildId = player.getGuildId();
					
					while(iterPlayer.hasNext())
					{
						Player currplayer = iterPlayer.next();
						
						if(currplayer.getGuildId() == guildId)
						{
							currplayer.setGuildId(0);
							currplayer.setGuildName("");
							currplayer.setGuildLevel(0);
							
							currplayer.getClient().sendPacket(Type.GUILD_NAME, currplayer);
							
							currplayer.getInterested().sendPacket(Type.GUILD_NAME, currplayer);
						}
					}
				}
				else
				{
					client.sendPacket(Type.SAY, "You dont have a guild");
				}
			}
			else if (words[1].equals("req"))
			{
				if(words.length == 3)
				{
					if(player.getGuildId() != 0)
					{
						client.sendPacket(Type.SAY, "You are allready in guild "+player.getGuildName()+" leave that first");
					}
					else
					{
						player.setGuildRequestName(words[2]);
						client.sendPacket(Type.SAY, "You applyed for guild membership on "+words[2]);
					}
				}
			}
			else if(words[1].equals("leave"))
			{
				if(words.length == 2 && player.getGuildId() != -1)
				{
					player.setGuildId(0);
					player.setGuildLevel(0);
					player.setGuildName("");
					
					client.sendPacket(Type.GUILD_NAME, player);
					player.getInterested().sendPacket(Type.GUILD_NAME, player);
					
					client.sendPacket(Type.SAY,"You left your guild");
				}
				else
				{
					client.sendPacket(Type.SAY,"You are in no guild");
				}
			}
			else if(words[1].equals("kick"))
			{
				if(words.length == 3)
				{
					if(player.getGuildLvl() > 1)
					{
						Player targetPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[2]);
						
						if(targetPlayer.getGuildId() == player.getGuildId())
						{
							if(player.getGuildLvl() > 8)
							{
								targetPlayer.setGuildId(0);
								targetPlayer.setGuildLevel(0);
								targetPlayer.setGuildName("");
	
								targetPlayer.getInterested().sendPacket(Type.GUILD_NAME, targetPlayer);
								targetPlayer.getClient().sendPacket(Type.GUILD_NAME, targetPlayer);
								targetPlayer.getClient().sendPacket(Type.SAY, "You've got kicked out of guild");
								client.sendPacket(Type.SAY, "You've kicked "+targetPlayer.getName()+" out of guild");
							}
						}
					}
					else
					{
						client.sendPacket(Type.SAY, "You don't have the right to kick players out of guild!");
					}
				}
			}
			else if(words[1].equals("info"))
			{
				Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
				
				long guildId = player.getGuildId();
				
				String gPlayer = "";
				
				boolean online = false;
				
				if(guildId != 0)
				{
					client.sendPacket(Type.SAY, "Who is online in your guild ("+player.getGuildName()+"):");
					while(iterPlayer.hasNext())
					{
						Player currplayer = iterPlayer.next();
						
						if(currplayer.getGuildId() == guildId)
						{
							online = true;
							gPlayer = currplayer.getName()+" (Lv."+currplayer.getLevel()+" Map:"+currplayer.getPosition().getMap().getName()+")";
							
							client.sendPacket(Type.SAY, gPlayer);
						}
					}
					if(!online)
						client.sendPacket(Type.SAY, "No guild member online!");
				}
			}
			
			//client.getWorld().sendPacket(Type.GUILD_SAY, data,player);
		}
		else if (words[0].equals("@online"))
		{
			if(player.getAdminState() >= 200) {
				Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
				
				if(player.getAdminState() >= 120) {
					client.sendPacket(Type.G_POS_START);
				}
				while(iterPlayer.hasNext())
				{
					Player currplayer = iterPlayer.next();
				
					if(currplayer.getPosition().getMap() == player.getPosition().getMap() && currplayer != player)
						client.sendPacket(Type.G_POS_BODY, currplayer);
				}
				if(player.getAdminState() >= 200) {
					client.sendPacket(Type.G_POS_END);
				}
			}
			client.sendPacket(Type.SAY, "Online Players: "+Server.getInstance().getWorld().getPlayerManager().getNumberOfPlayers());
		}
		else if (words[0].equals("@event") && player.getAdminState() >= 120)
		{
			if(words.length == 3)
			{
				if(words[1].equals("exp"))
				{
					if(Server.getInstance().getWorld().getServerSetings().getXp() < Long.parseLong(words[2]))
						Server.getInstance().getWorld().sendPacket(Type.INFO, "EXP Event (x"+words[2]+") started!");
					else if(Server.getInstance().getWorld().getServerSetings().getXp() > Long.parseLong(words[2]))
						Server.getInstance().getWorld().sendPacket(Type.INFO, "EXP Event has ended!");
					Server.getInstance().getWorld().getServerSetings().setXp(Long.parseLong(words[2]));
				}
				else if(words[1].equals("lime"))
				{
					if(Server.getInstance().getWorld().getServerSetings().getLime() < Long.parseLong(words[2]))
						Server.getInstance().getWorld().sendPacket(Type.INFO, "Lime Event (x"+words[2]+") started!");
					else if(Server.getInstance().getWorld().getServerSetings().getLime() > Long.parseLong(words[2]))
						Server.getInstance().getWorld().sendPacket(Type.INFO, "Lime Event has ended!");
					Server.getInstance().getWorld().getServerSetings().setLime(Long.parseLong(words[2]));
					
				}
			}
		}
		else if (words[0].equals("@kick") && player.getAdminState() >= 200)
		{
			if(words.length == 2)
			{
				Player targetPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[1]);
				
				client.sendPacket(Type.SAY, "Player "+targetPlayer.getName()+" kicked");
				
				targetPlayer.getClient().disconnect();
			}
		}
		else if (words[0].equals("@global") && player.getAdminState() >= 120) //Global chat is with -[space]Your message
		{
			int lengthofinfo = words.length;
			String data = "";
			for(int i = 1; i < lengthofinfo;i++){
				data+=" "+words[i];					
			}
			client.getWorld().sendPacket(Type.SAY, data,player);
		}
		
		else if (words[0].equals("@shutdown") && player.getAdminState() >= 260) {
			final Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
			final World worldShutdown = world;
			
			final Client clientU = client;
			Timer t = new Timer();
			t.schedule(new TimerTask(){
				int counter = 30;
				@Override
				public void run() {
					if(counter > 0) {
						worldShutdown.sendPacket(Type.INFO, "Server shutdown immediately! ("+counter+" Seconds)");
					}
					if(counter == 5) {
						while(iterPlayer.hasNext())	{
							Player currplayer = iterPlayer.next();
							Client pClient = currplayer.getClient();
							try {
								currplayer.save();
								pClient.sendPacket(Type.SAY, currplayer.getName()+" saved ...");	
							} catch (Exception e)
							{
								clientU.sendPacket(Type.SAY, "Player saving of "+ currplayer.getName()+" failed..");
							}
						}
					}
					else if(counter <= 0) {
						System.exit(1);
					}
					counter -= 5;
				}
			}, 0, 5000); //all 5 seconds
		}
		
		else if (words[0].equals("@fp") && player.getAdminState() >= 260) {
			String packetData = "";
			for (int i = 1; i <= (words.length-1);i++){
				packetData = packetData+words[i];
				if(i < (words.length-1))
					packetData = packetData+" ";
			}
			
			client.sendData(packetData);
		}
	
		else if (words[0].equals("@eid") && player.getAdminState() >= 260) {
			if(words.length == 1)
				client.sendPacket(Type.SAY, "Your EntityID is: "+player.getEntityId());
			else if(words.length > 1)
			{
				Player target = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[1]);
				client.sendPacket(Type.SAY,"EntityID of "+words[1]+" is "+target.getEntityId());
			}
		}
	
		else if (words[0].equals("@testcol") && player.getAdminState() >= 260) {
			Player p = player;
			
			LocalMap map = p.getPosition().getLocalMap();
			Area area = map.getArea();
			
			String s1 = ""
					+ area.get(p.getPosition().getX() / 10, p.getPosition().getY() / 10,Field.PLAYER);
			String s2 = ""
					+ area.get(p.getPosition().getX() / 10, p.getPosition().getY() / 10,Field.MOB);
			String s3 = ""
					+ area.get(p.getPosition().getX() / 10, p.getPosition().getY() / 10,Field.PVP);
	
			com.serverSay("(" + p.getPosition().getX() / 10 + "," + p.getPosition().getY() / 10
					+ ")" + "collision test: " + s1 + " " + s2 + " " + s3);
		} else if ((words[0].equals("@d") || words[0].equals("@drop"))  && player.getAdminState() >= 259) { //Drop Item
			if (words.length >= 2) {
				ItemManager itemManager = world.getItemManager();
					try {
		
						Item<?> item = itemManager.create(Integer.parseInt(words[1]));
						player.getPosition().getLocalMap().createEntityId(item);
						
						if (words.length == 6) {							
							int gemNumber = 0;
							if(item.getType().isUpgradable()){
								gemNumber = Integer.parseInt(words[2]);
								gemNumber = gemNumber > 15 ? 15 : gemNumber;
							} else {
								client.sendPacket(Type.SAY,item.getType().getName() + " it's not upgradable.");
							}
							int extraStats = ((words[3].length() <= 8) ? Integer.parseInt(words[3]) : 0);
							int unknown1 = Integer.parseInt(words[4]);
							int unknown2 = Integer.parseInt(words[5]);
							
							item.setGemNumber(gemNumber);
							item.setExtraStats(extraStats);
							item.setDurability(item.getType().getMaxDurability());
							item.setUnknown1(unknown1);
							item.setUnknown2(unknown2);
						}else{
							item.setGemNumber(0);
							item.setExtraStats(item.getType().getMaxExtraStats());
							item.setDurability(item.getType().getMaxDurability());
							item.setUnknown1(0);
							item.setUnknown2(0);
						}
						RoamingItem roamingItem = com.dropItem(player.getPosition(), item, player);
						LoggerFactory.getLogger(MessageParser.class).info("Player "+player+" droped roaming item "+roamingItem);
						
					} catch (Exception e) {
						client.sendPacket(Type.SAY, "@drop failed (ID:"+words[1]+")");
					}
			}
		}
		else if ((words[0].equals("@gi") || words[0].equals("@getitem") )  && player.getAdminState() >= 258)
		{
			ItemManager itemManager = world.getItemManager();
			try {
				int amount = ((words.length == 2) ? 1 : Integer.parseInt(words[2]));
				
				for(int i = 1; i <= amount; i++)
				{
					Item<?> item = itemManager.create(Integer.parseInt(words[1]));
					player.getPosition().getLocalMap().createEntityId(item);
					if (words.length == 7) {					
						int gemNumber = 0;
						if(item.getType().isUpgradable()){
							gemNumber = Integer.parseInt(words[3]);
							gemNumber = gemNumber > 15 ? 15 : gemNumber;
						} else {
							client.sendPacket(Type.SAY,item.getType().getName() + " it's not upgradable.");
						}
						int extraStats = ((words[4].length() <= 8) ? Integer.parseInt(words[4]) : 0);
						int unknown1 = Integer.parseInt(words[5]);
						int unknown2 = Integer.parseInt(words[6]);
						
						item.setGemNumber(gemNumber);
						item.setExtraStats(extraStats);
						item.setDurability(item.getType().getMaxDurability());
						item.setUnknown1(unknown1);
						item.setUnknown2(unknown2);
					} else {
						item.setGemNumber(0);
						item.setExtraStats(item.getType().getMaxExtraStats());
						item.setDurability(item.getType().getMaxDurability());
						item.setUnknown1(0);
						item.setUnknown2(0);
					}
					int[] tabPosition = player.getInventory().getFreeSlots(item,-1);
					if(tabPosition == null) {
					   if(player.getInventory().getHoldingItem() == null){
					      player.getInventory().setHoldingItem(new HandPosition(item));
					      player.getClient().sendPacket(Type.EXTRA,item);
					   } else {
					       player.getClient().sendPacket(Type.SAY, "Inventory full. Please get some space available.");
					       player.getPosition().getLocalMap().removeEntity(item);
					       DatabaseUtils.getDinamicInstance().deleteItem(item.getItemId());
					       return null;
					   }
					} else {
					      player.pickItem(item, tabPosition[0]);
					}
				}
			} catch (Exception e) {
				client.sendPacket(Type.SAY, "@drop failed (ID:"+words[1]+")");
			}
		}
		else if (words[0].equals("@info") && player.getAdminState() >= 120) {
			int lengthofinfo = words.length;
			String data = "";
			for(int i = 1; i < lengthofinfo;i++){
				data+=" "+words[i];					
			}
			client.getWorld().sendPacket(Type.INFO, "- "+data+" -");
		}
		else if (words[0].equals("@addmob") && player.getAdminState() >= 200) { //Adds a mob type NPC 
			if (words.length == 2||words.length == 3) {
				int count = 0;
				try {
					count = words.length == 3?Integer.parseInt(words[2]):1;
					
					if(count > 5)
						count = 5;
					
					for (int x = 0; x < count; x++) {
						NpcSpawn spawn = new NpcSpawn();
						spawn.setPosition(player.getPosition().clone());
						spawn.setNpcType(Integer.parseInt(words[1]));
						spawn.setRadius(300);
						spawn.setRespawnTime(-1);
						spawn.spawn();
					}
				} catch (Exception NumberFormatException) {
					client.sendPacket(Type.SAY,  "@addmob with "+count+" mob failed");
				}
			} else if (words.length == 6) {
				//LocalMap map = player.getPosition().getLocalMap();
				//Mob mobType = (Mob)Npc.create(Integer.parseInt(words[1]));
				Npc<?> mob = client.getWorld().getNpcManager().create(Integer.parseInt(words[1]));
				
				mob.getPosition().setX(player.getPosition().getX() + 10);
				mob.getPosition().setY(player.getPosition().getY() + 10);
				mob.setIsRunning(true);
				mob.setMutantType(Integer.parseInt(words[2]));
				mob.setUnknown1(Integer.parseInt(words[3]));
				mob.getType().setNeoProgmare(Integer.parseInt(words[4]));
				mob.setUnknown2(Integer.parseInt(words[5]));
				//Server.getInstance().getWorld().getMobManager().addMob(mob);
			}
		} else if (words[0].equals("@addnpc") && player.getAdminState() >= 200) { //adds a NPC
			try {
				if (words.length == 2) {
					
					NpcSpawn spawn = new NpcSpawn();
					
					spawn.setPosition(player.getPosition().clone());
					spawn.setNpcType(Integer.parseInt(words[1]));
					spawn.spawn();
					
				}
			} catch (Exception e) {
				//TODO: Fix the Mob id error server crash
				LoggerFactory.getLogger(this.getClass()).error("Npc id error detected");
			}
		} else if (words[0].equals("@tele") && player.getAdminState() >= 40) {
			try {
				String worldname = words[1];	
				ParsedItem mapref = Reference.getInstance().getMapConfigReference().getItem(worldname);
				
				if(mapref!=null){
					int mapId   =Integer.parseInt(mapref.getMemberValue("Id"));
					Map map = Server.getInstance().getWorld().getMap(mapId);
					
					if (map != null) {
						com.GoToWorld(player, map, 0);
					}
				}
				else{
					client.sendPacket(Type.SAY,  "@tele failed -> @tele worldname");				
				}
			} catch (Exception e) {
				
			}
		}  else if (words[0].equals("@goto") && player.getAdminState() >= 150) {
			if (words[1].equals("pos")) { //@goto pos [X] [Y]
				Position position = player.getPosition().clone();
				position.setX(Integer.parseInt(words[2]));
				position.setY(Integer.parseInt(words[3]));
				com.GoToPos(player, position);
			}
			if (words[1].equals("char")) { //@goto char
				com.GoToChar(player, words[2]);
			}
		
		} else if (words[0].equals("@save")) {
			if(words.length == 1)
			{
				player.save();
				client.sendPacket(Type.SAY, player.getName()+" saved ...");
			}
			else if(words.length == 2 && words[1].equals("all") && player.getAdminState() >= 200)
			{
				Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
				
				while(iterPlayer.hasNext())
				{
					Player currplayer = iterPlayer.next();
					Client pclient = currplayer.getClient();
					currplayer.save();
					pclient.sendPacket(Type.SAY, currplayer.getName()+" saved ...");
				}
			}
			else if(words.length == 2  && player.getAdminState() >= 200)
			{
				try {
					Player target = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[1]);
					target.save();
					client.sendPacket(Type.SAY, words[1]+" saved ...");
				}
				catch(Exception e)
				{
					client.sendPacket(Type.SAY, "Player "+words[1]+" is not online or saving failed");
				}
			}
		} else if (words[0].equals("@debug") && player.getAdminState() >= 260) {
			
			org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
			
			String host = words[1];
			InetAddress address = null;
			try {
				address = InetAddress.getByName(host);
				int port = Integer.parseInt(words[2]);
				SocketAppender socketAppender = new SocketAppender(address, port);			
				socketAppender.setReconnectionDelay(10);			
				root.addAppender(socketAppender);
				
			} catch (UnknownHostException e) {
				
				LoggerFactory.getLogger(MessageParser.class).warn("host("+host+") not found in @debug",e);
				com.serverTell(client, "Host not found");
			}
			 client.sendPacket(Type.SAY, "Spawnpoint cannot be added");
			com.serverTell(client, "Logger connected");	
		} else if (words[0].equals("@spot")) {
				client.sendPacket(Type.SAY, "{ X:" + player.getPosition().getX() + ", Y:"
						+ player.getPosition().getY()+", Z:"+player.getPosition().getZ()+"}");
			}
		
		else if (words[0].equals("@resetskills")  && player.getAdminState() >= 260) {
			java.util.Map<Skill,Integer> affectedSkills = new HashMap<Skill,Integer> ();
			
			if (words.length == 2) { //@resetskills [skillID]
				int skillId = Integer.parseInt(words[1]);
				Skill skill = player.getSkill(skillId);
				
				if(skill == null){
					client.sendPacket(Type.SAY, "SkillID "+skillId+" don't bellong to "+player.getRace());
					return null;
				}
				
				affectedSkills.put(skill, player.getSkillLevel(skill));
				
			} else //@resetskills
				affectedSkills = player.getSkills(); 
				
				// reset player skills to its minimum level
				for(Skill skill: affectedSkills.keySet()){
					skill.reset(player);
			}
		}
			// order a mob to attack player several times: @mobattack [mobUniqueID] [numberOfattacks]
		else if (words[0].equals("@mobattack") && player.getAdminState() >= 260) {
			if(words.length < 2){ // command to short
				client.sendPacket(Type.SAY, "USAGE: @mobattack [mobUniqueID] / @mobattack [mobUniqueID] [numberOfAttacks]");
				return "";
			}
			int numberOfAttacks = 1;
			// get mob by entity
			Mob mob = (Mob)player.getPosition().getLocalMap().getEntity(Integer.parseInt(words[1]));
			if (words.length == 3) { // get number of attacks
				numberOfAttacks = Integer.parseInt(words[2]);
			}
			// sends the several mob attack packets
			for(int attacksCounter = numberOfAttacks; attacksCounter > 0; attacksCounter-- ){
				mob.attack(player);
				player.getClient().sendPacket(Type.ATTACK,mob,player,0);
				if(player.getHp() <= 0){
					break;
				}
			}
		} else if (words[0].equals("@quest") && player.getAdminState() >= 260) {
			if(words.length == 2){
				try{
					int questId = Integer.parseInt(words[1]);
					Quest quest = player.getQuest();
				
					if(quest != null)
						player.setQuest(null);
				
					//quest = new Quest(questId); //used only to send the quest id packet
					//quest = QuestFactory.loadQuest(questId); //load full quest from database
					quest = questDao.findById(questId);
					
					player.setQuest(quest);	
					
					player.getClient().sendPacket(Type.QT, "get "+quest.getId());
					
					/*
					player.getClient().sendPacket(Type.SAY, "Quest: "+quest.getID()+" "+quest.getDescrition()+" ("+quest.getType().byValue()+")");		
					for(Objective objective: quest.getObjectives()){
						player.getClient().sendPacket(Type.SAY, "Objective: [ID] "+objective.getId()+" [QT] "+objective.getAmmount()+" [TYPE] "+objective.getType().byValue());
					}
					for(Reward reward: quest.getRewards()){
						player.getClient().sendPacket(Type.SAY, "Reward: [ID] "+reward.getId()+" [QT] "+reward.getAmmount()+" [TYPE] "+reward.getType().byValue());
					}
					*/
				
				} catch (Exception e) {
					client.sendPacket(Type.SAY, "@quest failed (ID:"+words[1]+")");
				}
			}
			else {
				player.getClient().sendPacket(Type.SAY, "Correct usage: @quest [questID]");
			}
		} else if (words[0].equals("@mobs_movement") && player.getAdminState() >= 250) {
			if(words.length == 2){
				if(words[1].equals("enable") || words[1].equals("1")){
					Server.getInstance().getWorld().getServerSetings().setMobsMovement(1);
				} else if(words[1].equals("disable") || words[1].equals("0")){
					Server.getInstance().getWorld().getServerSetings().setMobsMovement(0);
				} else if(words[1].equals("restart") || words[1].equals("2")){
					player.getPosition().getLocalMap().stopMobsAI();
					player.getPosition().getLocalMap().startMobsAI(1000);
				} else {
					player.getClient().sendPacket(Type.SAY, 
							"USAGE: @mobs_movement [enable/disable] / @mobs_movement [0/1]");
				}
			} else {
				player.getClient().sendPacket(Type.SAY, 
						"USAGE: @mobs_movement [enable/disable] / @mobs_movement [0/1]");
			}
		} else if (words[0].equals("@delete") && player.getAdminState() >= 260) {
			if(words[1].equals("item")){
				LocalMap localMap = player.getPosition().getLocalMap();

				if(words.length == 2){	//deletes all RoamingItems from LocalMap
					for(RoamingItem roamingItem : localMap.getRoamingItemList()){
						roamingItem.delete();
						LoggerFactory.getLogger(MessageParser.class).info("Player "+player+" deleted roaming item "+roamingItem);
					}
				} else {	//deletes only the given RoamingItem from LocalMap
					int roamingItementityId = Integer.parseInt(words[2]);
					RoamingItem roamingItem = (RoamingItem)localMap.getEntity(roamingItementityId);
					roamingItem.delete();
					LoggerFactory.getLogger(MessageParser.class).info("Player "+player+" deleted roaming item "+roamingItem);
				}
			}
		
		} else if (words[0].equals("@special") && player.getAdminState() >= 260) {
			int isActivated = 1;
			int typeId = 0;
			int[] availableTypeId = {10003,10011,10012,10013,10014,10015,10016,10017,10018,10019,
					10020,10021,10022,10023,10024,10025,10026,10027,10028};
			
			if(words.length == 3){
				typeId = Integer.parseInt(words[1]);
				isActivated = Integer.parseInt(words[2]);
			} else if(words.length == 2){
				if(words[1].equals("alladd")) isActivated = 1;
				if(words[1].equals("allremove")) isActivated = 0;
					for(int id : availableTypeId){
						player.getClient().sendPacket(Type.K, isActivated, player, id);
						player.getInterested().sendPacket(Type.K, isActivated, player, id);
					}
			} else if(words.length == 1){
				int typeIdPos = 100;
				while(typeIdPos > 18)
					typeIdPos = (int)(Math.random()*100);
				typeId = availableTypeId[typeIdPos];
			}
			
			player.getClient().sendPacket(Type.K, isActivated, player, typeId);
			player.getInterested().sendPacket(Type.K, isActivated, player, typeId);
			
			// (10003: fairy)**
			// (10011: gold pig)*
			// (10012: pink pig)*
			// (10013: black pig)
			// (10014: yellow pig)
			// (10015: red ghost)**
			// (10016: blue ghost)
			// (10017: yellow ghost)
			// (10018: red bat)**
			// (10019: red reindeer)**
			// (10020: ring of white light)**
			// (10021: ring of purple light)
			// (10022: ring of red light)
			// (10023: ring of blue light)
			// (10024: ring of green light)
			// (10025: black reindeer)
			// (10026: blue reindeer)
			// (10027: green reindeer)
			// (10028: yellow reindeer)
		}

		return text;
	}

}