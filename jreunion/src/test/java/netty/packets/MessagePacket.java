package netty.packets;

import netty.Packet;

public class MessagePacket implements Packet {
	
	private static final long serialVersionUID = 1L;
	
	String message;

	public MessagePacket(){
		
	}
	
	public MessagePacket(String message){
		
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("msg ");
		builder.append(getMessage());
		return builder.toString();
	}
}
