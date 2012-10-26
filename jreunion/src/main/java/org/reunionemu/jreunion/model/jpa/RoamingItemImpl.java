package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.Position;
import org.reunionemu.jreunion.game.RoamingItem;
import org.reunionemu.jreunion.server.Map;
import org.reunionemu.jreunion.server.World;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
@Entity
@Table(name="roaming",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "itemid" })
})
public class RoamingItemImpl extends RoamingItem {

	protected RoamingItemImpl(){		
	}
	
	public RoamingItemImpl(Item<?> item, Position position) {
		setItem(item);
		setPosition(position);
	}

	private Item<?> item;
	
	@Id
	@OneToOne(cascade = {}, fetch = FetchType.EAGER, optional = false, targetEntity = ItemImpl.class)
	public Item<?> getItem() {
		return item;
	}

	public void setItem(Item<?> item) {
		this.item = item;
	}

	@Column
	public int getMapId() {
		return getPosition().getMap().getId();
	}
	public void setMapId(int mapId) {
		this.setPosition(getPosition().setMap(new MapLoader().getMapFromId(mapId)));
	}
	@Column
	public int getX() {
		return getPosition().getX();
	}
	public void setX(int x) {
		this.setPosition(getPosition().setX(x));
	}
	@Column
	public int getY() {
		return getPosition().getY();
	}
	public void setY(int y) {
		this.setPosition(getPosition().setY(y));
	}
	@Column
	public int getZ() {
		return getPosition().getZ();
	}

	public void setZ(int z) {
		this.setPosition(getPosition().setZ(z));
	}
	@Column
	public double getRotation() {
		return getPosition().getRotation();
	}

	public void setRotation(double rotation) {
		this.setPosition(getPosition().setRotation(rotation));;
	}
	
	@Configurable
	private static class MapLoader {
		@Autowired
		World world;
		
		public Map getMapFromId(int mapId){
			return world.getMap(mapId);
		}	
		
	}	
	
}
