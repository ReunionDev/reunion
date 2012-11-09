package netty.packets;

import netty.Packet;

public class LoginPacket implements Packet {

	private static final long serialVersionUID = 1L;

	String username;
	String password;
	int version;
	boolean reconnect;

	public boolean isReconnect() {
		return reconnect;
	}

	public void setReconnect(boolean reconnect) {
		this.reconnect = reconnect;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getVersion());
		builder.append("\n");
		builder.append(isReconnect() ? "play" : "login");
		builder.append("\n");
		builder.append(getUsername());
		builder.append("\n");
		builder.append(getPassword());
		return builder.toString();
	}

}
