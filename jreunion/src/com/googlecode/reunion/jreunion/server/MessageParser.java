package com.googlecode.reunion.jreunion.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.NpcSpawn;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.Spawn;
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
		

			if (words[0].equals("@testcol")) {
				Player p = player;
				
				LocalMap map = p.getPosition().getMap();
				String s1 = ""
						+ map.getPlayerArea()
								.get(p.getPosition().getX() / 10 - 300, p.getPosition().getY() / 10);
				String s2 = ""
						+ map.getMobArea()
								.get(p.getPosition().getX() / 10 - 300, p.getPosition().getY() / 10);
				String s3 = ""
						+ map.getPvpArea()
								.get(p.getPosition().getX() / 10 - 300, p.getPosition().getY() / 10);

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
						DatabaseUtils.getInstance().saveItem(item);
						com.dropItem(player.getPosition(), item);
						
					} catch (Exception e) {
						String packetData = "say 1 S_Server (NOTICE) @drop failed";
						client.sendData(packetData);
					}
				}
				
			} else if (words[0].equals("@say")) {
				String data = "";
				for(String word: words){
					data+=" "+word;					
				}
				client.sendData(data.substring(2));				
				return null;
				//client.SendPacket(Type.SAY, words[3],Integer.parseInt(words[2]),Integer.parseInt(words[1]));
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
			
			} else if (words[0].equals("@addmob")) { //Adds a NPC 
				if (words.length == 2) {
					NpcSpawn spawn = new NpcSpawn();
					spawn.setPosition(player.getPosition().clone());
					spawn.setNpcType(Integer.parseInt(words[1]));
					spawn.setType(Spawn.Type.MOB);
					spawn.setRadius(300);
					spawn.setRespawnTime(10);
					spawn.spawn();

				} else if (words.length == 3) {
					try {
						int count = Integer.parseInt(words[2]);
						for (int x = 1; x < count; x++) {
							NpcSpawn spawn = new NpcSpawn();
							spawn.setNpcType(Integer.parseInt(words[1]));
							spawn.setType(Spawn.Type.MOB);
							spawn.setPosition(player.getPosition().clone());
							spawn.setRadius(300);
							spawn.setRespawnTime(10);
							spawn.spawn();
						}
					} catch (Exception NumberFormatException) {
						String packetData = "say 1 S_Server (NOTICE) @addmob with more than 1 mob failed";
						client.sendData(packetData);
					}
				} else if (words.length == 6) {
					Mob mob = Server.getInstance().getWorld()
							.getMobManager()
							.createMob(Integer.parseInt(words[1]));
					mob.getPosition().setX(player.getPosition().getX() + 10);
					mob.getPosition().setY(player.getPosition().getY() + 10);
					mob.setIsRunning(true);
					mob.setMutant(Integer.parseInt(words[2]));
					mob.setUnknown1(Integer.parseInt(words[3]));
					mob.setNeoProgmare(Integer.parseInt(words[4]));
					mob.setUnknown2(Integer.parseInt(words[5]));
					Server.getInstance().getWorld().getMobManager()
							.addMob(mob);
				}
			} else if (words[0].equals("@addnpc")) {
				try {
				if (words.length == 2) {
					
					NpcSpawn spawn = new NpcSpawn();
					spawn.setType(Spawn.Type.NPC);
					
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
					String packetData = "say 1 S_Server (NOTICE) @tele failed -> @tele worldname";
					client.sendData(packetData);					
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
					spawn.setType(Spawn.Type.MOB);
					spawn.setPosition(player.getPosition().clone());
					spawn.setNpcType(Integer.parseInt(words[1]));
					spawn.setRadius(Integer.parseInt(words[3]));
					spawn.setRespawnTime(10);
					spawn.spawn();
					String packetData = "say 1 S_Server Spawnpoint succesfully added";
					client.sendData(packetData);
			      }catch(Exception e){
			    	  com.serverTell(player.getClient(),e.getMessage());
			    	  Logger.getLogger(this.getClass()).warn("Exception",e);
			    	  String packetData = "say 1 S_Server Spawnpoint cannot be added";
			    	  client.sendData(packetData);
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
				com.serverSay("{ X:" + player.getPosition().getX() + ", Y:"
						+ player.getPosition().getY()+", Z:"+player.getPosition().getZ()+"}");
			}
		}

		return text;
	}

}
