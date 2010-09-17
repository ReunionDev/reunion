package com.googlecode.reunion.jreunion.server;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.game.G_Spawn;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_MessageParser {

	static int spawnCounter = 0;
	
	public S_MessageParser() {
		super();

	}

	String parse(G_Player player, String text) {
		int userlvl = player.getAdminState();
		text = text.trim();
		String word[] = text.split(" ");
		S_Command com = S_Server.getInstance().getWorldModule()
				.getWorldCommand();
		S_Client client = S_Server.getInstance().getNetworkModule()
				.getClient(player);

		if (userlvl > -1) {
			if (word[0].equals("@stats")) {
				if (word.length >= 2 && word[1].equals("dump")) {
					if (word.length >= 3) {
						S_PerformanceStats.getInstance().dumpPerformance(
								word[2]);
					} else {
						S_PerformanceStats.getInstance().dumpPerformance();
					}
				}

			} else if (word[0].equals("@testcol")) {
				G_Player p = player;
				
				S_Map map = p.getPosition().getMap();
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
				if (word.length == 2) {
					try {
						com.dropItem(player, Integer.parseInt(word[1]),
								player.getPosition().getX(), player.getPosition().getY(), 0, 0, 0, 0);
					} catch (Exception e) {
						String packetData = "say 1 S_Server (NOTICE) @drop failed";
						client.SendData(packetData);
					}
					
				}

				if (word.length == 4) {
					try {
					com.dropItem(player, Integer.parseInt(word[1]),
							player.getPosition().getX(), player.getPosition().getY(), 0, 0,
							Integer.parseInt(word[2]),
							Integer.parseInt(word[3]));
					} catch (Exception e) {
						String packetData = "say 1 S_Server (NOTICE) @drop failed";
						client.SendData(packetData);
					}
				}
			} else if (word[0].equals("@addmob")) { //Adds a NPC 
				if (word.length == 2) {
					G_Spawn spawn = new G_Spawn();
					spawn.setCenterX(player.getPosition().getX() + 10);
					spawn.setCenterY(player.getPosition().getY() + 10);
					spawn.setMap(player.getPosition().getMap());
					spawn.setMobType(Integer.parseInt(word[1]));
					spawn.setRadius(300);
					spawn.setRespawnTime(10);
					spawn.spawnMob();

				} else if (word.length == 6) {
					G_Mob mob = S_Server.getInstance().getWorldModule()
							.getMobManager()
							.createMob(Integer.parseInt(word[1]));
					mob.getPosition().setX(player.getPosition().getX() + 10);
					mob.getPosition().setY(player.getPosition().getY() + 10);
					mob.setRunning(true);
					mob.setMutant(Integer.parseInt(word[2]));
					mob.setUnknown1(Integer.parseInt(word[3]));
					mob.setNeoProgmare(Integer.parseInt(word[4]));
					mob.setUnknown2(Integer.parseInt(word[5]));
					S_Server.getInstance().getWorldModule().getMobManager()
							.addMob(mob);
				}
			} else if (word[0].equals("@addnpc")) {
				try {
				if (word.length == 2) {
					G_Npc npc = S_Server.getInstance().getWorldModule()
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
			} else if (word[0].equals("@worldgoto")) {
				try {
				String map1 = word[1];
				String map2 = word[2];
				String map3 = word[3];			
				client.SendData(
						"go_world 127.0.0.1 "+map1+" " + map2 + " " + map3
								+ "\n");
				} catch (Exception e) {
					String packetData = "say 1 S_Server (NOTICE) @worldgoto failed > port map1 map2";
					client.SendData(packetData);
				}
			} else if (word[0].equals("@goto")) {
				if (word[1].equals("pos")) {
					com.GoToPos(player, Integer.parseInt(word[2]),
							Integer.parseInt(word[3]));
				}
				if (word[1].equals("char")) {
					com.GoToChar(player, word[2]);
				}
			} else if (word[0].equals("@spawn")||word[0].equals("@s")) {
				BufferedWriter bw = null;
			      try {
			         bw = new BufferedWriter(new FileWriter("OutSpawns.dta", true));
			         bw.write("["+word[2]+"]");			         
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
			    	 
				G_Spawn spawn = new G_Spawn();
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
