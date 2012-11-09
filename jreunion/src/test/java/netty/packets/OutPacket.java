package netty.packets;

import netty.Packet;

public class OutPacket implements Packet {
	
	private static final long serialVersionUID = 1L;


	EntityType entityType;
	
	long id;
	
	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("out ");
		if(entityType!=null){
			switch(getEntityType()){
			case CHAR:
				builder.append('c');
				break;
			case ITEM:
				builder.append("item");
				break;
			case NPC:
				builder.append('n');
				break;
			case PET:
				builder.append('p');
				break;
			}
		}
		builder.append(' ');
		builder.append(getId());

		return builder.toString();
	}
	
	public enum EntityType{
		NPC,
		CHAR,
		ITEM,
		PET
	}
}
