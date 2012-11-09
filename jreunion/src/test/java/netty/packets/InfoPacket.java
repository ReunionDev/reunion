package netty.packets;

import netty.Packet;

public class InfoPacket implements Packet {
	
	private static final long serialVersionUID = 1L;

	public InfoPacket(){
		
		
	}
	public InfoPacket(String message){
		
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
		builder.append("info");
		String message = getMessage();
		builder.append(' ');
		builder.append(message);
		return builder.toString();
	}
}
