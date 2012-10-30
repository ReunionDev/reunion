package org.reunionemu.jreunion.server;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.reunionemu.jreunion.game.Player;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public class TeleportManager {
	static final float timeout = 30;
	List<TeleportBucket> teleports = new Vector<TeleportBucket>();
	public TeleportManager(){
	}
	
	public void register(Player player, Map map){
		synchronized(teleports){
			TeleportBucket bucket = new TeleportBucket();
			bucket.from = player.getPosition().getLocalMap();
			bucket.to = map;
			bucket.player = player;
			teleports.add(bucket);
		}
	}
	public void remove(Player player){
		synchronized(teleports){
			TeleportBucket remove= null;
			for(TeleportBucket bucket:teleports){
				if(bucket.player.getEntityId() == player.getEntityId())
					remove = bucket;
			}
			if(remove!=null)
				teleports.remove(remove);		
		}
	}
	
	public Map getDestination(Player player){
		synchronized(teleports){			
			for(TeleportBucket bucket:teleports){
				if(bucket.player.getEntityId() == player.getEntityId())
					return bucket.to;			
			}
		}
		return null;	
	}
	
	
	public class TeleportBucket extends TimerTask{
		public TeleportBucket(){
			
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
