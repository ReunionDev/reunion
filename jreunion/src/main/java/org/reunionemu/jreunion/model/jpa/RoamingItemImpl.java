package org.reunionemu.jreunion.model.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;
import javax.persistence.Entity;

import org.reunionemu.jreunion.game.*;
import org.reunionemu.jreunion.server.*;
import org.springframework.beans.factory.annotation.*;

@Entity
@Table(name="roaming",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "itemid" })
})
public class RoamingItemImpl extends RoamingItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	/*
	Long itemId;	
	
	
	@Id
	@Column(name = "itemid", nullable = false)
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}*/

	@Configurable
	private static class MapLoader {
		@Autowired
		World world;
		
		public Map getMapFromId(int mapId){
			return world.getMap(mapId);
		}
	}
	
	Date created;
	
	Player owner;
	
	private Item<?> item;

	protected RoamingItemImpl(){		
	}
	
	public RoamingItemImpl(Item<?> item, Position position) {
		setItem(item);
		setPosition(position);
		setCreated(new Date());
	}

	@Column
	@Override
	public Date getCreated() {
		return created;
	}	

	@Id
	@OneToOne(targetEntity=ItemImpl.class,cascade={})
    @JoinColumn(name = "itemid")
	public Item<?> getItem() {
		return item;
	}
	
	@Column
	public int getMapId() {
		if(getPosition()!=null&&getPosition().getMap()!=null){
			return getPosition().getMap().getId();
		}
		return -1;
	}
	@Transient
	@Override
	public Player getOwner() {
		return owner;
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
	@Column
	public int getZ() {
		return getPosition().getZ();
	}

	@Override
	public void setCreated(Date created) {
		this.created = created;
		
	}
	public void setItem(Item<?> item) {
		this.item = item;
	}

	public void setMapId(int mapId) {
		this.setPosition(getPosition().setMap(new MapLoader().getMapFromId(mapId)));
	}
	
	@Override
	public void setOwner(Player owner) {
		this.owner = owner;
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

	public void setZ(int z) {
		this.setPosition(getPosition().setZ(z));
	}	
	
}
