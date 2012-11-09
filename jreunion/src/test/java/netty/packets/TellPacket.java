package netty.packets;

import netty.Packet;

public class TellPacket implements Packet {
	
	private static final long serialVersionUID = 1L;
	
	String name;
	
	String message;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public TellPacket(){
		
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("tell ");
		builder.append(getName());
		builder.append(' ');
		builder.append(getMessage());		
		return builder.toString();
	}

}
