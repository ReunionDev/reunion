package org.reunionemu.jreunion.model;

import java.io.Serializable;

import org.reunionemu.jreunion.game.*;

public interface MemoryWarpSlot {

	public interface MemoryWarpSlotId extends Serializable {
		Player getPlayer();
		int getSlot();
		void setPlayer(Player player);
		void setSlot(int slot);

	}
	
	Player getPlayer();
	void setPlayer(Player player);
	
	int getSlot();
	void setSlot(int slot);
	
	Position getPosition();
	void setPosition(Position position);

}
