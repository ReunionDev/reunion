package com.googlecode.reunion.jreunion.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.googlecode.reunion.jreunion.game.G_Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_TeleportManager {
	static final float timeout = 30;
	List<S_TeleportBucket> teleports = new ArrayList<S_TeleportBucket>();
	public S_TeleportManager(){
	}
	
	public void register(G_Player player, S_Map target){
		synchronized(teleports){
			S_TeleportBucket bucket = new S_TeleportBucket();
			bucket.from = player.getMap();
			bucket.to = target;
			bucket.player = player;
			teleports.add(bucket);
		}
	}
	public S_Map getDestination(G_Player player){
		synchronized(teleports){			
			for(S_TeleportBucket bucket:teleports){
				if(bucket.player.getEntityId() == player.getEntityId())
					return bucket.to;			
			}
		}
		return null;	
	}
	
	
	public class S_TeleportBucket extends TimerTask{
		public S_TeleportBucket(){
			
			timer = new Timer();
			timer.schedule(this, (long)(timeout*1000));
		}
		
		Timer timer;
		public S_Map from;
		public S_Map to;
		public G_Player player;
		
		@Override
		public void run() {
			synchronized(teleports){
				teleports.remove(this);
			}
		}		
		
	
	}

}
