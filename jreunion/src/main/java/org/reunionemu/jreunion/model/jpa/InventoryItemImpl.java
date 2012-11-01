package org.reunionemu.jreunion.model.jpa;

import java.io.Serializable;

import javax.persistence.*;
import javax.persistence.Entity;

import org.reunionemu.jreunion.dao.ItemDao;
import org.reunionemu.jreunion.game.*;
import org.reunionemu.jreunion.server.*;
import org.springframework.beans.factory.annotation.*;

/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt Item
 *          wrapper for Inventory
 */
@Entity
@Table(name = "inventory", uniqueConstraints = { @UniqueConstraint(columnNames = { "itemid" }) })
@Configurable
public class InventoryItemImpl extends InventoryItem implements Serializable {

	@Autowired
	PlayerManager playerManager;

	private static final long serialVersionUID = 1L;

	private Long itemId;

	private Long playerId;

	private Player player;

	private Item<?> item = null;

	private InventoryPosition position = new InventoryPosition(0, 0, 0);

	public InventoryItemImpl() {

	}

	public InventoryItemImpl(Item<?> item, InventoryPosition position,
			Player player) {
		super(item, position, player);
	}

	@Transient
	public Item<?> getItem() {
		if (item == null && itemId != null) {
			item = (Item<?>) new GenericLoader().getObject(ItemDao.class)
					.findOne(itemId);
		}
		return item;
	}

	@Id
	@Column(name = "itemid", unique = true, nullable = false)
	public Long getItemId() {
		return itemId;
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

	@Transient
	@Override
	public InventoryPosition getPosition() {
		return position;
	}

	public int getTab() {
		return getPosition().getTab();
	}

	public int getX() {

		return getPosition().getPosX();
	}

	public int getY() {
		return getPosition().getPosY();
	}

	public void setItem(Item<?> item) {
		this.item = item;
		if (item == null) {
			this.itemId = null;
		} else {
			this.itemId = item.getItemId();
		}
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Override
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

	@Transient
	public void setPosition(InventoryPosition position) {
		this.position = position;
	}

	public void setTab(int tab) {
		if (tab == InventoryPosition.EXCHANGE_TAB) {
			setPosition(new ExchangePosition(getPosition().getPosX(),
					getPosition().getPosY()));
		} else if(tab == InventoryPosition.EXCHANGE_TAB) {
			setPosition(new HandPosition());
			
		} else {
			getPosition().setTab(tab);
		}
	}

	public void setX(int x) {
		getPosition().setPosX(x);
	}

	public void setY(int y) {
		getPosition().setPosY(y);
	}
	
}
