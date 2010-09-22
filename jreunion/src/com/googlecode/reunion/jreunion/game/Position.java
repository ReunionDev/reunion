package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Map;

public class Position {
	
	private int x;
	private int y;
	private int z;
	
	private LocalMap map;
	
	public Position(){
		
	}

	public Position(int x, int y, int z, LocalMap map, double rotation) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.map = map;
		this.rotation = rotation;
	}
	
	public Position clone()
	{		
		return this.clone();
		
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public LocalMap getMap() {
		return map;
	}

	public void setMap(LocalMap map) {
		this.map = map;
	}
	
	public double rotation;

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	

}
