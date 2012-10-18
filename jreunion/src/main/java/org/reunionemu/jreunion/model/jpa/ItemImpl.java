package org.reunionemu.jreunion.model.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.server.TypeLoader;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name="items",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "id" })
})
public class ItemImpl<T extends ItemType> extends Item<T> {
	Long id;
	
    int typeId;
	
    Integer durability;
    
    long gemNumber;
    
    long extraStats;
    
    private T type;
    
    public ItemImpl(){
    	
    }
    
    public ItemImpl(T itemType) {
		super();
		setType(itemType);
		
	}
    
    public void setTypeId(int typeId) {
		this.typeId = typeId;		
	}

    protected void setType(T itemType) {
    	this.type = itemType;
	}

    @Override
	@Column(name="durability")
	public Integer getDurability() {
		return durability;
	}

	@Override
	@Column(name="gemnumber")
	public long getGemNumber() {
		return gemNumber;
	}
	
	@Override
	@Id @GeneratedValue
	@Column(name="id")
	public Long getItemId(){
		return id;
	}
	
	@Column(name="type")
	public Integer getTypeId() {
		if(type!=null){
	    	return type.getTypeId();
		}
		return typeId;
	}

	@Override
	public void setDurability(Integer durability) {
		this.durability = durability;
	}
	
	@Override
	public void setGemNumber(long gemNumber) {
		this.gemNumber = gemNumber;
	}

	public void setItemId(long id){
    	this.id = id;
    }

	@Transient
	@Override
	public T getType() {
		if(type==null){
			type = new TypeLoader<T>().load(typeId);
		}
		return type;
		
	}

	@Column(name="extrastats")
	@Override
	public long getExtraStats() {
		return extraStats;
	}
	
}