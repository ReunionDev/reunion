package org.reunionemu.jreunion.game;

import java.util.Random;

import org.reunionemu.jreunion.events.map.SpawnEvent;
import org.reunionemu.jreunion.game.npc.Mob;
import org.reunionemu.jreunion.server.Area;
import org.reunionemu.jreunion.server.Area.Field;
import org.reunionemu.jreunion.server.Client.State;
import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.reunionemu.jreunion.server.Server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Spawn {


	private int id;	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int radius;
	
	private Position position;

	public Spawn() {
	}	
	
	public Spawn(Position position) {
		this.setPosition(position);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
		
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Position generateSpawnPosition(){
		
		Random rand = Server.getRand();
		
		Position position = getPosition();		
		LocalMap map = position.getLocalMap();
		int posX = position.getX();
		int posY = position.getY();
		
		if(radius>0){
			posX += rand.nextInt(radius * 2) - radius;
			posY += rand.nextInt(radius * 2) - radius;
		}
		double rotation = position.getRotation();
		
		if(Double.isNaN(rotation)){
			rotation = Server.getRand().nextDouble() * Math.PI * 2;	
		}
		
		return new Position(posX, posY, position.getZ(), map, rotation);
	}

	protected Position spawn(LivingObject entity) {		
	
		Position position = generateSpawnPosition();
		Area entityArea = getPosition().getLocalMap().getArea(); 
		int spawnAttempts = (int)Server.getInstance().getWorld().getServerSetings().getSpawnAttempts();
		
		// TODO: Improve the mob spawn area
		if( entity instanceof Npc){
			Npc<?> npc = (Npc<?>)entity;
			if(npc.getType() instanceof Mob){
				Mob mob = (Mob)npc.getType();
				
				while((!entityArea.get(position.getX() / 10, position.getY() / 10,Field.MOB)) && (spawnAttempts-- > 0)){
					position = generateSpawnPosition();
				}
				float mobMutantChance = Server.getInstance().getWorld().getServerSetings().getMobMutantChance();
				
				if(Server.getRand().nextFloat() < mobMutantChance){
					/* Mutant mobs, by default have:
					 * - increased hp
					 * - increased damage
					 * - defence resistance
					 */
					float mobMutantModifier = Server.getInstance().getWorld().getServerSetings().getMobMutantModifier();
					npc.setMaxHp((long)(npc.getMaxHp()*(mobMutantModifier+1)));
					npc.setHp(npc.getMaxHp());
					npc.setMutantType(npc.getRandomMutantType());
				}
			}
		}
		entity.setPosition(position);
		
		LocalMap map = position.getLocalMap();
		
		if(map.getEntity(entity.getEntityId())!=entity){
			map.createEntityId(entity);
			map.fireEvent(SpawnEvent.class, entity);
		}
		
		return position;
		
	}
}