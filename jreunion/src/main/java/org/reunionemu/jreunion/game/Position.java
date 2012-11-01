package org.reunionemu.jreunion.game;

import org.reunionemu.jreunion.server.*;
/**
 * @author Aidamina
 * @license https://raw.github.com/ReunionDev/reunion/master/license.txt
 */
public final class Position implements BasePosition {
	
	private final int x;
	private final int y;
	private final int z;	
	private final Map map;
	private final double rotation;
	
	public final static Position ZERO = new Position(0, 0, 0, null, 0);
	
	public Position(){
		x = 0;
		y = 0;
		z = 0;
		map = null;
		rotation = 0;		
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
		
		double xd = this.getX() - position.getX();
				
		double yd = this.getY() - position.getY();
		
		double zd = this.getZ() - position.getZ();
		
		return  Math.sqrt((xd * xd) + (yd * yd)+ (zd * zd));
		
	}
	// *within* doesn't use distance because Math.sqrt is expensive and unnecesary for this
	public boolean within(Position position, double range) {
		
		double xd =this.getX() - position.getX();
		
		double yd = this.getY() - position.getY();
		
		double zd = this.getZ() - position.getZ();
		
		return (xd * xd) + (yd * yd)  + (zd * zd) <= (range*range);
		
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

	public Position setX(int x) {
		return new Position(x, y, z, map, rotation);
	}

	public int getY() {
		return y;
	}

	public Position setY(int y) {
		return new Position(x, y, z, map, rotation);
	}

	public int getZ() {
		return z;
	}

	public Position setZ(int z) {
		return new Position(x, y, z, map, rotation);
	}

	public LocalMap getLocalMap() {
		return (LocalMap)map;
	}
	
	public Map getMap() {
		return map;
	}

	public Position setMap(Map map) {
		return new Position(x, y, z, map, rotation);
	}
	
	public double getRotation() {
		return this.rotation;
	}

	public Position setRotation(double rotation) {
		return new Position(x, y, z, map, rotation);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Position)){
			return false;
		}
		if(obj==this){
			return true;
		}
		Position other = (Position)obj;
		if(getX()!=other.getX()){
			return false;
		}
		if(getY()!=other.getY()){
			return false;
		}
		if(getZ()!=other.getZ()){
			return false;
		}
		if(getRotation()!=other.getRotation()){
			return false;
		}
		if(getMap()==null&&other.getMap()!=null){
			return false;
		}
		if(getMap()!=null&&other.getMap()==null){
			return false;
		}
		if(getMap()!=null&&other.getMap()!=null){
			return getMap().equals(other.getMap());
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + x;
		hash = 31 * hash + y;
		hash = 31 * hash + z;
		hash = (int) (31 * hash + Double.doubleToLongBits(rotation));
		hash = 31 * hash + (null == map ? 0 : map.hashCode());
		return hash;
	}
	

}
