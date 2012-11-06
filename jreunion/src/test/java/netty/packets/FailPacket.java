package netty.packets;

import netty.Packet;

public class FailPacket implements Packet {
	
	public FailPacket(String message){
		
		this.message = message;
	}

	String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("fail "+getMessage());
		return builder.toString();
	}
}
