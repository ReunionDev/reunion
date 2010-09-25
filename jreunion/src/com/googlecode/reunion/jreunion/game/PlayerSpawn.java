package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.PacketFactory;

public class PlayerSpawn extends Spawn 
{
	public PlayerSpawn(){
		setType(Type.PLAYER);
		
	}
	
	int targetX =-1;
	int targetY =-1;
	int targetWidth =-1; 
	int targetHeight =-1;
	
	public int getTargetX() {
		return targetX;
	}

	public void setTargetX(int targetX) {
		this.targetX = targetX;
	}
	public int getTargetY() {
		return targetY;
	}

	public void setTargetY(int targetY) {
		this.targetY = targetY;
	}
	public int getTargetWidth() {
		return targetWidth;
	}

	public void setTargetWidth(int targetWidth) {
		this.targetWidth = targetWidth;
	}
	public int getTargetHeight() {
		return targetHeight;
	}
	public void setTargetHeight(int targetHeight) {
		this.targetHeight = targetHeight;
	}

	public void spawn(Player player) {

		Position position = generatePosition();
		this.getPosition().getMap().getWorld().getCommand().GoToPos(player, position);
		player.getClient().sendPacket(PacketFactory.Type.AT, player);
		super.spawn(player);
	}

}
