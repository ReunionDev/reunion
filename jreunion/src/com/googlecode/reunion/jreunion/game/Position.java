package com.googlecode.reunion.jreunion.game;

import com.googlecode.reunion.jreunion.server.LocalMap;
import com.googlecode.reunion.jreunion.server.Map;

public class Position {
	
	private int x;
	private int y;
	private int z;	
	private Map map;
	private double rotation;
	
	public Position(){
		
	}

	public Position(int x, int y, int z, Map map, double rotation) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.map = map;
		this.rotation = rotation;
	}
	
	public Position clone()
	{				
		return new Position(x, y, z, map, rotation);
	
	}
	
	public double distance(Position position){
		
		if(this.getMap()!=position.getMap()){
			throw new RuntimeException("Can not calculate distance between two positions on different maps: "+this.getLocalMap()+", "+position.getLocalMap());			
		}
		
		double xd =this.getX() - position.getX();
				
		double yd = this.getY() - position.getY();
		
		double zd = this.getZ() - position.getZ();
		
		return  Math.sqrt((xd * xd) + (yd * yd)  + (zd * zd));
		
	}
	
	public boolean within(Position position, double range) {
		
		double xd =this.getX() - position.getX();
		
		double yd = this.getY() - position.getY();
		
		double zd = this.getZ() - position.getZ();
		
		return (xd * xd) + (yd * yd)  + (zd * zd) < (range*range);
		
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		if(map!=null){
			
			buffer.append("map: ");
			buffer.append(map);
			buffer.append(", ");
		}
		buffer.append("x: ");
		buffer.append(x);
		buffer.append(", ");
		buffer.append("y: ");
		buffer.append(y);
		buffer.append(", ");
		buffer.append("z: ");
		buffer.append(z);
		buffer.append(", ");
		buffer.append("rotation: ");
		buffer.append(rotation);
		buffer.append("}");
		return buffer.toString();
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

	public LocalMap getLocalMap() {
		return (LocalMap)map;
	}
	
	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}
	

}
