package org.reunionemu.jreunion.model.jpa;

import java.io.Serializable;

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

@Entity
@Table(name="items",
uniqueConstraints={
		@UniqueConstraint(columnNames = { "id" })
})
public class ItemImpl<T extends ItemType> extends Item<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	Long id;
	
    int typeId;
	
    //Default value of 1 (equals MaxDurability)
    double durability = 1;
    
    long gemNumber;
    
    long extraStats;
    
    private T type;
    
    public ItemImpl(){
    	
    }
    
    public ItemImpl(T itemType) {
		super();
		setType(itemType);
		
	}
    
	@Transient
	public int getDurability() {
		return (int)Math.ceil(durability * getType().getMaxDurability());
	}
	
	@Override
	@Column(name="durability", nullable=false)
	public double getDurabilityValue() {
		return durability;
	}
	
	@Override
	public void setDurabilityValue(double durability) {
		this.durability = durability;
	}

    @Column(name="extrastats", nullable=false)
	@Override
	public long getExtraStats() {
		return extraStats;
	}

    @Override
	@Column(name="gemnumber", nullable=false)
	public long getGemNumber() {
		return gemNumber;
	}

	@Override
	@Id @GeneratedValue
	@Column(name="id")
	public Long getItemId(){
		return id;
	}
	
	@Transient
	@Override
	public T getType() {
		if(type==null){
			type = new TypeLoader<T>().load(typeId);
		}
		return type;
		
	}

	@Column(name="type", nullable=false)
	public Integer getTypeId() {
		if(type!=null){
	    	return type.getTypeId();
		}
		return typeId;
	}
	
	@Override
	public void setGemNumber(long gemNumber) {
		this.gemNumber = gemNumber;
	}
	
	public void setItemId(long id){
    	this.id = id;
    }

	protected void setType(T itemType) {
    	this.type = itemType;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;		
	}
	
}