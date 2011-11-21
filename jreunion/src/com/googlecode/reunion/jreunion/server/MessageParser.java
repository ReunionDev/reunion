package com.googlecode.reunion.jreunion.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.ItemType;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.NpcSpawn;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.Player.Status;
import com.googlecode.reunion.jreunion.game.npc.Mob;
import com.googlecode.reunion.jreunion.server.Area.Field;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;
import com.googlecode.reunion.jcommon.ParsedItem;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class MessageParser {

	static int spawnCounter = 0;
	
	public MessageParser() {
		super();
	}

	String parse(Player player, String text) {
		float userlvl = player.getAdminState();
		text = text.trim();
		String words[] = text.split(" ");
		Command com = Server.getInstance().getWorld()
				.getCommand();
		Client client = player.getClient();
		ItemManager itemManager = client.getWorld().getItemManager();

		if (userlvl > -1) {
			
			if (words[0].equals("@levelup")) {
				if (words.length > 1) {
					
					int lvlup = Integer.parseInt(words[1]);
					
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
				else
					player.setLevelUpExp(0);
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
				} else if(words[1].equals("reskill")) {
					if(words.length == 7) {
						int strength = Integer.parseInt(words[2]);
						int wisdom   = Integer.parseInt(words[3]);
						int dex      = Integer.parseInt(words[4]);
						int strain   = Integer.parseInt(words[5]);
						int charisma = Integer.parseInt(words[6]);
						
						int sumuSP = strength+wisdom+dex+strain+charisma;
						
						if(sumuSP <= 80 && sumuSP >= 25) {
							player.setStrength(strength);
							player.setWisdom(wisdom);
							player.setDexterity(dex);
							player.setConstitution(strain);
							player.setLeadership(charisma);
							
							player.resetSkills();
							
							sumuSP = 80-sumuSP;
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
			else if (words[0].equals("@global")) //Global chat is with -[space]Your message
			{
				int lengthofinfo = words.length;
				String data = "";
				for(int i = 1; i < lengthofinfo;i++){
					data+=" "+words[i];					
				}
				client.getWorld().sendPacket(Type.SAY, data,player);
			}
			
			else if (words[0].equals("@shutdown") && player.getAdminState() == 255) {
				final Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
				final World world = Server.getInstance().getWorld();
				
				world.sendPacket(Type.INFO, "Server shutdown immediately! (25 Seconds)");
				Timer t = new Timer();
				t.schedule(new TimerTask(){
					int counter = 0;
					@Override
					public void run() {
						if(counter == 1)
						world.sendPacket(Type.INFO, "Server shutdown immediately! (20 Seconds)");
						if(counter == 2)
						world.sendPacket(Type.INFO, "Server shutdown immediately! (15 Seconds)");
						if(counter == 3)
						world.sendPacket(Type.INFO, "Server shutdown immediately! (10 Seconds)");
						if(counter == 4)
						{
							while(iterPlayer.hasNext())
							{
								Player currplayer = iterPlayer.next();
								Client pclient = currplayer.getClient();
								try{
									currplayer.save();
									pclient.sendPacket(Type.SAY, currplayer.getName()+" saved ...");
								}catch(Exception e){
									pclient.sendPacket(Type.SAY, "Saving of "+currplayer.getName()+" failed ...");
								}
							}
							world.sendPacket(Type.INFO, "Server is going down! (5 Seconds)");
						}
						if(counter == 5)
							System.exit(1);
						counter++;
					}
				}, 0, 5000); //all 5 seconds
			}
			
			else if (words[0].equals("@fp")) {
				String packetData = "";
				for (int i = 1; i <= (words.length-1);i++){
					packetData = packetData+words[i];
					if(i < (words.length-1))
						packetData = packetData+" ";
				}
				client.sendData(packetData);
			}

			else if (words[0].equals("@eid")) {
				if(words.length == 1)
					client.sendPacket(Type.SAY, "Your EntityID is: "+player.getEntityId());
				else if(words.length > 1)
				{
					Player target = Server.getInstance().getWorld().getPlayerManager().getPlayer(words[1]);
					client.sendPacket(Type.SAY,"EntityID of "+words[1]+" is "+target.getEntityId());
				}
			}

			else if (words[0].equals("@testcol")) {
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
			} else if (words[0].equals("@d") || words[0].equals("@drop")) { //Drop Item
				if (words.length >= 2) {
						try {
							/*if(words[0].equals("@d")){
								item = itemManager.create(Integer.parseInt(words[1]));
							} else if(words[0].equals("@drop")){
								ItemType itemType = new ItemType(Integer.parseInt(words[1]));
								item = itemType.create();
								DatabaseUtils.getDinamicInstance().saveItem(item);
							}*/
							
							//System.out.println("Entity list size before: "+player.getPosition().getLocalMap().getEntityListSize());
							Item<?> item = itemManager.create(Integer.parseInt(words[1]));
							
							if (words.length == 6) {							
								int gemNumber = Integer.parseInt(words[2]);
								int extraStats = Integer.parseInt(words[3]);
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
							RoamingItem roamingItem = com.dropItem(player.getPosition(), item);
							Logger.getLogger(MessageParser.class).info("Player "+player+" droped roaming item "+roamingItem);
							
						} catch (Exception e) {
							client.sendPacket(Type.SAY, "@drop failed (ID:"+words[1]+")");
						}
				}
			}
			else if (words[0].equals("@info")) {
				int lengthofinfo = words.length;
				String data = "";
				for(int i = 1; i < lengthofinfo;i++){
					data+=" "+words[i];					
				}
				client.getWorld().sendPacket(Type.INFO, "- "+data+" -");
			}
			else if (words[0].equals("@say")) {
				String data = "";
				for(String word: words){
					data+=" "+word;					
				}
				client.sendPacket(Type.SAY, data.substring(2));	
				return null;
				//client.SendPacket(Type.SAY, words[3],Integer.parseInt(words[2]),Integer.parseInt(words[1]));
			
			/* not sure what is this (SAM)
			} else if (words[0].equals("@p")) { //Adds a NPC
				String data = "";
				for(int i = 1 ;i<words.length;i++){
					if(!data.isEmpty())
						data+=" ";
					data+=words[i];
					
					
				}
				if(data.isEmpty())
					client.sendData(data);
				return null;
			*/
			} else if (words[0].equals("@addmob")) { //Adds a NPC 
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
							spawn.setRespawnTime(10);
							spawn.spawn();
						}
					} catch (Exception NumberFormatException) {
						client.sendPacket(Type.SAY,  "@addmob with "+count+" mob failed");
					}
				
				} else if (words.length == 6) {
					//LocalMap map = player.getPosition().getLocalMap();
					Mob mob = (Mob)Npc.create(Integer.parseInt(words[1]));
					mob.getPosition().setX(player.getPosition().getX() + 10);
					mob.getPosition().setY(player.getPosition().getY() + 10);
					mob.setIsRunning(true);
					mob.setMutant(Integer.parseInt(words[2]));
					mob.setUnknown1(Integer.parseInt(words[3]));
					mob.setNeoProgmare(Integer.parseInt(words[4]));
					mob.setUnknown2(Integer.parseInt(words[5]));
					//Server.getInstance().getWorld().getMobManager().addMob(mob);
				}
			} else if (words[0].equals("@addnpc")) { //adds a NPC
				try {
				if (words.length == 2) {
					
					NpcSpawn spawn = new NpcSpawn();
					
					spawn.setPosition(player.getPosition().clone());
					spawn.setNpcType(Integer.parseInt(words[1]));
					spawn.spawn();
					
				}
				} catch (Exception e) {
					//TODO: Fix the Mob id error server crash
					Logger.getLogger(MessageParser.class).error("Mob id error detected");
				}
			} else if (words[0].equals("@tele")) {
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
		}  else if (words[0].equals("@goto")) {
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
			else
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
		} else if (words[0].equals("@debug")) {
			
			Logger logger = Logger.getRootLogger();
			
			String host = words[1];
			InetAddress address = null;
			try {
				address = InetAddress.getByName(host);
				int port = Integer.parseInt(words[2]);
				SocketAppender socketAppender = new SocketAppender(address, port);			
				socketAppender.setReconnectionDelay(10);			
				logger.addAppender(socketAppender);
				
			} catch (UnknownHostException e) {
				
				Logger.getLogger(Debug.class).warn("host("+host+") not found in @debug",e);
				com.serverTell(client, "Host not found");
			}
			 client.sendPacket(Type.SAY, "Spawnpoint cannot be added");
			com.serverTell(client, "Logger connected");
			
			
			
		} else if (words[0].equals("@spawn")||words[0].equals("@s")) {
				//@spawn mobid mobtypecount radius
				BufferedWriter bw = null;
			      try {	    	  
					bw = new BufferedWriter(new FileWriter("OutSpawns.dta", true));
					int mobid = Integer.parseInt(words[1]);
					ParsedItem mob = Reference.getInstance().getMobReference()
							.getItemById(mobid);
					
					String mobname = mob.getName();
					int typecount = Integer.parseInt(words[2]);
					
					bw.write("["+mobname+" "+typecount+"]");			         
					bw.newLine();
					bw.write("ID = "+ ++spawnCounter);
					bw.newLine();
					bw.write("X = "+player.getPosition().getX());
					bw.newLine();
					bw.write("Y = "+player.getPosition().getY());
					bw.newLine();
					bw.write("Z = "+player.getPosition().getZ());
					bw.newLine();
					bw.write("Radius = "+words[3]);
					bw.newLine();
					bw.write("Rotation = "+player.getPosition().getRotation());
					bw.newLine();
					bw.write("RespawnTime = 10");
					bw.newLine();
					bw.write("Type = "+words[1]);
					bw.newLine();			      
					bw.newLine();
					bw.flush();
			    	 
					NpcSpawn spawn = new NpcSpawn();
					spawn.setPosition(player.getPosition().clone());
					spawn.setNpcType(Integer.parseInt(words[1]));
					spawn.setRadius(Integer.parseInt(words[3]));
					spawn.setRespawnTime(10);
					spawn.spawn();

					client.sendPacket(Type.SAY,  "Spawnpoint succesfully added");
			      }catch(Exception e){
			    	  com.serverTell(player.getClient(),e.getMessage());
			    	  Logger.getLogger(this.getClass()).warn("Exception",e);

			    	  client.sendPacket(Type.SAY, "Spawnpoint cannot be added");
			      }
			} else if (words[0].equals("@spot")) {
				client.sendPacket(Type.SAY, "{ X:" + player.getPosition().getX() + ", Y:"
						+ player.getPosition().getY()+", Z:"+player.getPosition().getZ()+"}");
			}
			// resets all the skill from player: @resetskills [skillID]
			else if (words[0].equals("@resetskills")) {
						
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
			else if (words[0].equals("@mobattack")) {
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
					player.getClient().sendPacket(Type.ATTACK,mob,player);
					if(player.getHp() <= 0){
						break;
					}
				}
			}
			else if (words[0].equals("@quest")) {
				if(words.length == 2){
					try{
						int questId = Integer.parseInt(words[1]);
						Quest quest = player.getQuest();
					
						if(quest != null)
							player.setQuest(null);
					
						//quest = new Quest(questId); //used only to send the quest id packet
						//quest = QuestFactory.loadQuest(questId); //load full quest from database
						quest = DatabaseUtils.getStaticInstance().loadQuest(questId);
						
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
				} else{
					player.getClient().sendPacket(Type.SAY, "Correct usage: @quest [questID]");
				}
				
			}
			else if (words[0].equals("@delete")) {
					if(words[1].equals("item")){
						LocalMap localMap = player.getPosition().getLocalMap();
	
						if(words.length == 2){	//deletes all RoamingItems from LocalMap
							for(RoamingItem roamingItem : localMap.getRoamingItemsList()){
								roamingItem.delete();
								Logger.getLogger(MessageParser.class).info("Player "+player+" deleted roaming item "+roamingItem);
							}
						} else {	//deletes only the given RoamingItem from LocalMap
							int roamingItementityId = Integer.parseInt(words[2]);
							RoamingItem roamingItem = (RoamingItem)localMap.getEntity(roamingItementityId);
							roamingItem.delete();
							Logger.getLogger(MessageParser.class).info("Player "+player+" deleted roaming item "+roamingItem);
						}
					}
				
			}
		}

		return text;
	}

}