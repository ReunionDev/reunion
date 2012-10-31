package org.reunionemu.jreunion.model.jpa;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;

import org.reunionemu.jreunion.game.*;
import org.reunionemu.jreunion.model.MemoryWarpSlot;
import org.reunionemu.jreunion.server.*;
import org.springframework.beans.factory.annotation.*;

@Entity
@Table(name = "memorywarpslot", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"charid", "slotid" }) })
public class MemoryWarpSlotImpl implements MemoryWarpSlot, Serializable {

	@Configurable
	private static class MapLoader {
		@Autowired
		World world;

		public Map getMapFromId(int mapId) {
			return world.getMap(mapId);
		}
	}

	@Configurable
	@Embeddable
	public static class MemoryWarpSlotIdImpl implements MemoryWarpSlotId {

		private static final long serialVersionUID = 1L;

		@Autowired
		private PlayerManager playerManager;

		private Player player;

		private Long playerId;

		private int slot;

		public MemoryWarpSlotIdImpl() {

		}

		public MemoryWarpSlotIdImpl(Long playerId, int slot) {
			setSlot(slot);
			setPlayerId(playerId);
		}

		public MemoryWarpSlotIdImpl(Player player, int slot) {
			setSlot(slot);
			setPlayer(player);
		}

		@Transient
		@Override
		public Player getPlayer() {
			if (player == null && playerId != null) {
				player = playerManager.getPlayerByDbId(playerId);
			}
			return player;
		}

		@Column(name = "charid", nullable = false)
		public Long getPlayerId() {
			return playerId;
		}

		@Column(name = "slotid", nullable = false)
		public int getSlot() {
			return slot;
		}

		public void setPlayer(Player player) {
			this.player = player;
			if (player != null) {
				setPlayerId((long) player.getPlayerId());
			} else {
				setPlayerId(null);
			}
		}

		public void setPlayerId(Long playerId) {
			this.playerId = playerId;
		}

		public void setSlot(int slot) {
			this.slot = slot;
		}
		@Override
		public boolean equals(Object obj) {
			if(obj instanceof MemoryWarpSlotId){
				MemoryWarpSlotId other =((MemoryWarpSlotId)obj);
				Player otherPlayer = other.getPlayer();
				Player player = this.getPlayer();
				if(otherPlayer==null||player==null){
					if(!(otherPlayer==null&&this.getPlayer()==null)){
						return false;
					}					
				}
				return player.getPlayerId()==otherPlayer.getPlayerId()&& getSlot()== other.getSlot();				
			}
			return false;
		}

	}

	private static final long serialVersionUID = 1L;

	MemoryWarpSlotIdImpl id = new MemoryWarpSlotIdImpl();

	private Position position = Position.ZERO;

	protected MemoryWarpSlotImpl() {
	}

	public MemoryWarpSlotImpl(Player player, int slot, Position position) {
		setPlayer(player);
		setSlot(slot);
		setPosition(position);
	}

	@EmbeddedId
	public MemoryWarpSlotIdImpl getId() {
		return id;
	}

	@Column
	public int getMapId() {
		if (getPosition() != null && getPosition().getMap() != null) {
			return getPosition().getMap().getId();
		}
		return -1;
	}

	@Transient
	@Override
	public Player getPlayer() {
		return getId().getPlayer();
	}

	@Transient
	public Position getPosition() {
		return position;
	}

	@Column
	public double getRotation() {
		return getPosition().getRotation();
	}

	@Transient
	@Override
	public int getSlot() {
		return getId().getSlot();
	}

	@Column
	public int getX() {
		return getPosition().getX();
	}

	@Column
	public int getY() {
		return getPosition().getY();
	}

	public void setId(MemoryWarpSlotIdImpl id) {
		this.id = id;
	}

	public void setMapId(int mapId) {
		this.setPosition(getPosition().setMap(
				new MapLoader().getMapFromId(mapId)));
	}

	public void setPlayer(Player player) {
		this.getId().setPlayer(player);
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setRotation(double rotation) {
		this.setPosition(getPosition().setRotation(rotation));
		;
	}

	@Override
	public void setSlot(int slot) {
		getId().setSlot(slot);

	}

	public void setX(int x) {
		this.setPosition(getPosition().setX(x));
	}

	public void setY(int y) {
		this.setPosition(getPosition().setY(y));
	}

}
