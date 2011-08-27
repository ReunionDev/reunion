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
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.NpcSpawn;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.skills.GroupedSkill;
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
		int userlvl = player.getAdminState();
		text = text.trim();
		String words[] = text.split(" ");
		Command com = Server.getInstance().getWorld()
				.getCommand();
		Client client = player.getClient();

		if (userlvl > -1) {
			
			if (words[0].equals("@levelup")) {
				if (words.length > 1) {
					int count=1;
					while(count <= Integer.parseInt(words[1]))
					{
						player.setLevelUpExp(0);
						count++;
					}
				}
				else
					player.setLevelUpExp(0);
			}
			else if (words[0].equals("@shutdown")) {
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
								currplayer.save();
								pclient.sendPacket(Type.SAY, currplayer.getName()+" saved ...");
								
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
			else if (words[0].equals("@testcol")) {
				Player p = player;
				
				LocalMap map = p.getPosition().getLocalMap();
				Area area = map.getArea();
				
				String s1 = ""
						+ area.get(p.getPosition().getX() / 10 - 300, p.getPosition().getY() / 10,Field.PLAYER);
				String s2 = ""
						+ area.get(p.getPosition().getX() / 10 - 300, p.getPosition().getY() / 10,Field.MOB);
				String s3 = ""
						+ area.get(p.getPosition().getX() / 10 - 300, p.getPosition().getY() / 10,Field.PVP);

				com.serverSay("(" + p.getPosition().getX() / 10 + "," + p.getPosition().getY() / 10
						+ ")" + "collision test: " + s1 + " " + s2 + " " + s3);
			} else if (words[0].equals("@d")||words[0].equals("@drop")) { //Drop Item
				if (words.length >= 2) {
					try {
						
						int itemType = Integer.parseInt(words[1]);
						Item item = ItemFactory.create(itemType);
						if (words.length >= 4) {							
							int gemNumber = Integer.parseInt(words[2]);
							int extraStats = Integer.parseInt(words[3]);
							
							item.setGemNumber(gemNumber);
							item.setExtraStats(extraStats);
						}else{
							item.setGemNumber(0);
							item.setExtraStats(0);
						}
						com.dropItem(player.getPosition(), item);
						
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
					LocalMap map = player.getPosition().getLocalMap();
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
					Logger.getLogger(MessageParser.class).info("Mob id error detected");
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
			if (words[1].equals("pos")) {
				Position position = player.getPosition().clone();
				position.setX(Integer.parseInt(words[2]));
				position.setY(Integer.parseInt(words[3]));
				com.GoToPos(player, position);
			}
			if (words[1].equals("char")) {
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
				
			} else if (words[0].equals("@com")) {
				String packetData = "";
				for (int i = 1; i < words.length; i++) {
					packetData = packetData + words[i];
					if (i < words.length - 1) {
						packetData = packetData + " ";
					}
				}
				client.sendData( packetData + "\n");
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
					if(player.getHp() <= 0)
						break;
				}
			}
		}

		return text;
	}

}