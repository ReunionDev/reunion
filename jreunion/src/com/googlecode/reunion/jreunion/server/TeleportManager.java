package com.googlecode.reunion.jreunion.server;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.googlecode.reunion.jreunion.game.Player;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class TeleportManager {
	static final float timeout = 30;
	List<S_TeleportBucket> teleports = new Vector<S_TeleportBucket>();
	public TeleportManager(){
	}
	
	public void register(Player player, Map map){
		synchronized(teleports){
			S_TeleportBucket bucket = new S_TeleportBucket();
			bucket.from = player.getPosition().getLocalMap();
			bucket.to = map;
			bucket.player = player;
			teleports.add(bucket);
		}
	}
	public void remove(Player player){
		synchronized(teleports){
			S_TeleportBucket remove= null;
			for(S_TeleportBucket bucket:teleports){
				if(bucket.player.getId() == player.getId())
					remove = bucket;
			}
			if(remove!=null)
				teleports.remove(remove);		
		}
	}
	
	public Map getDestination(Player player){
		synchronized(teleports){			
			for(S_TeleportBucket bucket:teleports){
				if(bucket.player.getId() == player.getId())
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
		public Map from;
		public Map to;
		public Player player;
		
		@Override
		public void run() {
			synchronized(teleports){
				teleports.remove(this);
			}
		}		
		
	
	}

}
