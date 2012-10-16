package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
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
	Long id;
	
    Integer typeId;
	
    Integer durability;
    
    Long gemNumber;
    
    public ItemImpl(T itemType) {
		super(itemType);
	}

    @Column(name="durability")
	public Integer getDurability() {
		return durability;
	}

	@Column(name="gemnumber")
	public long getGemNumber() {
		return gemNumber;
	}
	
	@Id @GeneratedValue
	public Long getId(){
		return id;
	}
	
	@Column(name="type")
	public Integer getTypeId() {
		return typeId;
	}

	public void setDurability(Integer durability) {
		this.durability = durability;
	}
	
	public void setGemNumber(Long gemNumber) {
		this.gemNumber = gemNumber;
	}

	public void setId(Long id){
    	this.id = id;
    }

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	
}