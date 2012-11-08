package netty.packets;

import java.util.*;

import netty.Packet;

public class CharListEndPacket implements Packet {
	
	private static final long serialVersionUID = 1L;

	int unknown;
	
	int accountId;
	
	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("chars_end ");

		builder.append(getUnknown());
		
		builder.append(' ');
		
		builder.append(getAccountId());
		
		return builder.toString();
	}
}
