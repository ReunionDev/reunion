package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;



@Entity
@Table(name="items",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "id" })
})
public class ItemImpl<T extends ItemType> extends Item<T> {
	public ItemImpl(T itemType) {
		super(itemType);
	}


	@Id @GeneratedValue
    Long id;


}