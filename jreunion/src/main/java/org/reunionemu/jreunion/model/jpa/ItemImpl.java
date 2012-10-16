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
	public ItemImpl(T itemType) {
		super(itemType);
	}

	
    Long id;
	
    @Id @GeneratedValue
	public Long getId(){
		return id;
	}
    
    public void setId(Long id){
    	this.id = id;
    }
    
    Integer typeId;

    @Column(name="type_id")
	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	
	Integer durability;
	
	@Column(name="durability")
	public Integer getDurability() {
		return durability;
	}

	public void setDurability(Integer durability) {
		this.durability = durability;
	}
	
	@Column(name="gemnumber")
	public long getGemNumber() {
		return gemNumber;
	}

	public void setGemNumber(Long gemNumber) {
		this.gemNumber = gemNumber;
	}

	Long gemNumber;

	
}