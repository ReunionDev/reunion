package netty;

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
}
