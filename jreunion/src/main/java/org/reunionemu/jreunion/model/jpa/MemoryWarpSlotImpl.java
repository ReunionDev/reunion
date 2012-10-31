package org.reunionemu.jreunion.model.jpa;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;

import org.reunionemu.jreunion.game.*;
import org.reunionemu.jreunion.model.MemoryWarpSlot;
import org.reunionemu.jreunion.server.*;
import org.springframework.beans.factory.annotation.*;

@Entity
@Table(name="memorywarpslot",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "charid","slotid" })
})
public class MemoryWarpSlotImpl implements  MemoryWarpSlot, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Configurable
	@Embeddable
	public static class MemoryWarpSlotIdImpl implements MemoryWarpSlotId {

		private static final long serialVersionUID = 1L;
		

		@Transient
		@Autowired
		private PlayerManager playerManager;
		
		private Player player;
		
		private Long playerId;
		
		private int slot;
		
		public MemoryWarpSlotIdImpl(){
			
		}
		
		public MemoryWarpSlotIdImpl(Player player, int slot){
			setSlot(slot);
			setPlayer(player);
		}
		public MemoryWarpSlotIdImpl(Long playerId, int slot){
			setSlot(slot);
			setPlayerId(playerId);
		}

		@Column(name = "slotid", nullable = false)
		public int getSlot() {
			return slot;
		}

		public void setSlot(int slot) {
			this.slot = slot;
		}
		
		@Column(name = "charid", nullable = false)
		public Long getPlayerId() {
			return playerId;
		}

		public void setPlayerId(Long playerId) {
			this.playerId = playerId;		
		}
		@Transient
		@Override
		public Player getPlayer() {
			if(player==null&&playerId!=null){
				player = playerManager.getPlayerByDbId(playerId);
			}
			return player;
		}
		
		public void setPlayer(Player player) {
			this.player = player;
			if(player!=null){
				setPlayerId((long)player.getPlayerId());
			}else{
				setPlayerId(null);
			}
		}
		
		
	}
	
	
	
	 
	MemoryWarpSlotIdImpl id = new MemoryWarpSlotIdImpl();

	@EmbeddedId
	public MemoryWarpSlotIdImpl getId() {
		return id;
	}

	public void setId(MemoryWarpSlotIdImpl id) {
		this.id = id;
	}

	
	
	private Position position = Position.ZERO;	
	

			
	@Transient
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}


	@Configurable
	private static class MapLoader {
		@Autowired
		World world;
		
		public Map getMapFromId(int mapId){
			return world.getMap(mapId);
		}
	}	
	
	protected MemoryWarpSlotImpl(){		
	}
	
	public MemoryWarpSlotImpl(Player player, int slot, Position position) {
		setPlayer(player);
		setSlot(slot);
		setPosition(position);
	}
	
	@Column
	public int getMapId() {
		if(getPosition()!=null&&getPosition().getMap()!=null){
			return getPosition().getMap().getId();
		}
		return -1;
	}
	
	
	@Column
	public double getRotation() {
		return getPosition().getRotation();
	}
	@Column
	public int getX() {
		return getPosition().getX();
	}
	@Column
	public int getY() {
		return getPosition().getY();
	}
	
	public void setMapId(int mapId) {
		this.setPosition(getPosition().setMap(new MapLoader().getMapFromId(mapId)));
	}
	
	public void setPlayer(Player player) {
		this.getId().setPlayer(player);
	}

	public void setRotation(double rotation) {
		this.setPosition(getPosition().setRotation(rotation));;
	}

	public void setX(int x) {
		this.setPosition(getPosition().setX(x));
	}

	public void setY(int y) {
		this.setPosition(getPosition().setY(y));
	}

	@Transient
	@Override
	public Player getPlayer() {
		return getId().getPlayer();
	}
	
	@Transient
	@Override
	public int getSlot() {
		return getId().getSlot();
	}

	@Override
	public void setSlot(int slot) {
		getId().setSlot(slot);
		
	}

	
}
