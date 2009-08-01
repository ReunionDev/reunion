package com.googlecode.reunion.jreunion.server;

import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.game.G_Spawn;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_MessageParser {
	
	public S_MessageParser() {
		super();
		
	}
	
	String parse(G_Player player, String text)
	{
		int userlvl = player.getAdminState();
		text = text.trim();
		String word[] = text.split(" ");
		S_Command com = S_Server.getInstance().getWorldModule().getWorldCommand();
		S_Client client = S_Server.getInstance().getNetworkModule().getClient(player);
		
		if (userlvl>-1)
		{
			if (word[0].equals("@stats")) 
			{
				if (word.length>=2&&word[1].equals("dump"))
				{
					if (word.length>=3)
						S_PerformanceStats.getInstance().dumpPerformance(word[2]);
					else
						S_PerformanceStats.getInstance().dumpPerformance();
				}
				
				
				
				
			}else if (word[0].equals("@testcol")) 
			{
				G_Player p = player;
				
				String s1 = ""+S_Server.getInstance().getWorldModule().getMapManager().getPlayerArea().get((p.getPosX()/10)-300,p.getPosY()/10);
				String s2 = ""+S_Server.getInstance().getWorldModule().getMapManager().getMobArea().get((p.getPosX()/10)-300,p.getPosY()/10);
				String s3 = ""+S_Server.getInstance().getWorldModule().getMapManager().getPvpArea().get((p.getPosX()/10)-300,p.getPosY()/10);
				
				com.serverSay("(" +p.getPosX()/10+","+p.getPosY()/10+")"+"collision test: "+s1+" "+s2+" "+s3);
			}
			
			else if (word[0].equals("@drop")) {
				if (word.length == 2)
					com.dropItem(player, Integer.parseInt(word[1]),
							player.getPosX(),
							player.getPosY(),0,0,0,0);
				
				if (word.length == 4)
					com.dropItem(player, Integer.parseInt(word[1]),
							player.getPosX(),
							player.getPosY(), 0, 0,
							Integer.parseInt(word[2]), Integer.parseInt(word[3]));
			}
			else if (word[0].equals("@addmob")) {
				if(word.length == 2) {
					G_Spawn spawn = new G_Spawn();
					spawn.setCenterX(player.getPosX()+10);
					spawn.setCenterY(player.getPosY()+10);
					spawn.setMap(player.getMap());
					spawn.setMobType(Integer.parseInt(word[1]));
					spawn.setRadius(300);
					spawn.setRespawnTime(10);
					spawn.spawnMob();
					
				}
				else if(word.length == 6) {
					G_Mob mob = S_Server.getInstance().getWorldModule().getMobManager().createMob(Integer.parseInt(word[1])); 
					mob.setPosX(player.getPosX()+10);
					mob.setPosY(player.getPosY()+10);
					mob.setRunning(true);
					mob.setMutant(Integer.parseInt(word[2]));
					mob.setUnknown1(Integer.parseInt(word[3]));
					mob.setNeoProgmare(Integer.parseInt(word[4]));
					mob.setUnknown2(Integer.parseInt(word[5]));
					S_Server.getInstance().getWorldModule().getMobManager().addMob(mob);
				}
			}
			else if (word[0].equals("@addnpc")) {
				if(word.length == 2) {
					G_Npc npc = S_Server.getInstance().getWorldModule().getNpcManager().createNpc(Integer.parseInt(word[1]));
					npc.setPosX(player.getPosX()+10);
					npc.setPosY(player.getPosY()+10);
					npc.setRotation(0.0);
					com.npcIn(player,npc);
				}
			}
			else if (word[0].equals("@worldgoto")) {
				String map1 = word[1];
				String map2 = word[2];
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, "go_world 127.0.0.1 4001 "+map1+" "+map2+"\n");
			}
			else if (word[0].equals("@goto")) {
				if(word[1].equals("pos"))
					com.GoToPos(player,Integer.parseInt(word[2]),
							Integer.parseInt(word[3]));
				if(word[1].equals("char"))
					com.GoToChar(player,word[2]);
			}
			else if (word[0].equals("@com")) {
				String packetData = "";
				for(int i=1; i<word.length; i++){
					packetData = packetData + word[i];
					if(i < (word.length-1))
						packetData = packetData + " ";
				}
				S_Server.getInstance().getNetworkModule().SendPacket(client.networkId, packetData+"\n");
			}
			else if (word[0].equals("@spot")) {
				com.serverSay("X:"+player.getPosX()+"; Y:"+player.getPosY());
			}
		}
		
		
		return text;
	}
	
}
