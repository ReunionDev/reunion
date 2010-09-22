package com.googlecode.reunion.jreunion.server;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Spawn;
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
		String word[] = text.split(" ");
		Command com = Server.getInstance().getWorldModule()
				.getWorldCommand();
		Client client = player.getClient();

		if (userlvl > -1) {
			if (word[0].equals("@stats")) {
				if (word.length >= 2 && word[1].equals("dump")) {
					if (word.length >= 3) {
						PerformanceStats.getInstance().dumpPerformance(
								word[2]);
					} else {
						PerformanceStats.getInstance().dumpPerformance();
					}
				}

			} else if (word[0].equals("@testcol")) {
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
			}

			else if (word[0].equals("@d")||word[0].equals("@drop")) { //Drop Item
				if (word.length >= 2) {
					try {
						
						int itemType = Integer.parseInt(word[1]);
						Item item = ItemFactory.create(itemType);
						if (word.length >= 4) {							
							int gemNumber = Integer.parseInt(word[2]);
							int extraStats = Integer.parseInt(word[3]);
							
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
						client.SendData(packetData);
					}
				}

			} else if (word[0].equals("@addmob")) { //Adds a NPC 
				if (word.length == 2) {
					Spawn spawn = new Spawn();
					spawn.setCenterX(player.getPosition().getX() + 10);
					spawn.setCenterY(player.getPosition().getY() + 10);
					spawn.setMap(player.getPosition().getMap());
					spawn.setMobType(Integer.parseInt(word[1]));
					spawn.setRadius(300);
					spawn.setRespawnTime(10);
					spawn.spawnMob();

			} else if (word.length == 3) {
				try {
					int count = Integer.parseInt(word[2]);
					for (int x = 1; x < count; x++) {
						Spawn spawn = new Spawn();
						spawn.setCenterX(player.getPosition().getX() + 10);
						spawn.setCenterY(player.getPosition().getY() + 10);
						spawn.setMap(player.getPosition().getMap());
						spawn.setMobType(Integer.parseInt(word[1]));
						spawn.setRadius(300);
						spawn.setRespawnTime(10);
						spawn.spawnMob();
					}
				} catch (Exception NumberFormatException) {
					String packetData = "say 1 S_Server (NOTICE) @addmob with more than 1 mob failed";
					client.SendData(packetData);
				}
				} else if (word.length == 6) {
					Mob mob = Server.getInstance().getWorldModule()
							.getMobManager()
							.createMob(Integer.parseInt(word[1]));
					mob.getPosition().setX(player.getPosition().getX() + 10);
					mob.getPosition().setY(player.getPosition().getY() + 10);
					mob.setRunning(true);
					mob.setMutant(Integer.parseInt(word[2]));
					mob.setUnknown1(Integer.parseInt(word[3]));
					mob.setNeoProgmare(Integer.parseInt(word[4]));
					mob.setUnknown2(Integer.parseInt(word[5]));
					Server.getInstance().getWorldModule().getMobManager()
							.addMob(mob);
				}
			} else if (word[0].equals("@addnpc")) {
				try {
				if (word.length == 2) {
					Npc npc = Server.getInstance().getWorldModule()
							.getNpcManager()
							.createNpc(Integer.parseInt(word[1]));
					npc.getPosition().setX(player.getPosition().getX() + 10);
					npc.getPosition().setY(player.getPosition().getY() + 10);
					npc.getPosition().setRotation(0.0);
					com.npcIn(player, npc);
				}
				} catch (Exception e) {
					//TODO: Fix the Mob id error server crash
					System.out.println("Mob id error detected");
				}
			} else if (word[0].equals("@tele")) {
			try {
				String worldname = word[1];	
				ParsedItem mapref = Reference.getInstance().getMapConfigReference().getItem(worldname);
				
				if(mapref!=null){
					int mapId   =Integer.parseInt(mapref.getMemberValue("Id"));
					Map map = Server.getInstance().getWorldModule().getMap(mapId);
					
					if (map != null) {
						com.GoToWorld(player, map, 0);
					}
				}
				else{
					String packetData = "say 1 S_Server (NOTICE) @tele failed -> @tele worldname";
					client.SendData(packetData);					
				}
			} catch (Exception e) {
				
			}
		}  else if (word[0].equals("@goto")) {
			if (word[1].equals("pos")) {
				com.GoToPos(player, Integer.parseInt(word[2]),
						Integer.parseInt(word[3]));
			}
			if (word[1].equals("char")) {
				com.GoToChar(player, word[2]);
			}
		} else if (word[0].equals("@spawn")||word[0].equals("@s")) {
				//@spawn mobid mobtypecount radius
				BufferedWriter bw = null;
			      try {	    	  
					bw = new BufferedWriter(new FileWriter("OutSpawns.dta", true));
					int mobid = Integer.parseInt(word[1]);
					ParsedItem mob = Reference.getInstance().getMobReference()
							.getItemById(mobid);
					
					String mobname = mob.getName();
					int typecount = Integer.parseInt(word[2]);
					
					bw.write("["+mobname+" "+typecount+"]");			         
					bw.newLine();
					bw.write("ID = "+ ++spawnCounter);
					bw.newLine();
					bw.write("X = "+player.getPosition().getX());
					bw.newLine();
					bw.write("Y = "+player.getPosition().getY());
					bw.newLine();
					bw.write("Radius = "+word[3]);
					bw.newLine();
					bw.write("RespawnTime = 10");
					bw.newLine();
					bw.write("Type = "+word[1]);
					bw.newLine();			      
					bw.newLine();
					bw.flush();
			    	 
					Spawn spawn = new Spawn();
					spawn.setCenterX(player.getPosition().getX() + 10);
					spawn.setCenterY(player.getPosition().getY() + 10);
					spawn.setMap(player.getPosition().getMap());
					spawn.setMobType(Integer.parseInt(word[1]));
					spawn.setRadius(Integer.parseInt(word[3]));
					spawn.setRespawnTime(10);
					spawn.spawnMob();
					String packetData = "say 1 S_Server Spawnpoint succesfully added";
					client.SendData(packetData);
			      }catch(Exception e){
			    	  com.serverTell(player,e.getMessage());
			    	  e.printStackTrace();
			    	  String packetData = "say 1 S_Server Spawnpoint cannot be added";
			    	  client.SendData(packetData);
			      }
				
			} else if (word[0].equals("@com")) {
				String packetData = "";
				for (int i = 1; i < word.length; i++) {
					packetData = packetData + word[i];
					if (i < word.length - 1) {
						packetData = packetData + " ";
					}
				}
				client.SendData( packetData + "\n");
			} else if (word[0].equals("@spot")) {
				com.serverSay("X:" + player.getPosition().getX() + "; Y:"
						+ player.getPosition().getY());
			}
		}

		return text;
	}

}
