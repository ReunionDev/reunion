package com.googlecode.reunion.jreunion.game.quests.objective.type;

public class ObjectiveType {

	private int type;
	
	public ObjectiveType(int type) {
		this.type = type;
	}
	
	public int byValue(){
		return this.type;
	}
	
	public void setValue(int type){
		this.type = type;
	}
	
	public static enum Type
	{
		MOB(1),
		POINTS(2);

		int value;
		Type(int value){
			this.value = value;
			
		}
		public int value(){
			return value;			
		
		}
		
		public static Type byValue(int typeId){
			
			for(Type type:Type.values())
			{
				if(type.value()==typeId){					
					return type;
				}
			}
			return null;
		}		
	}	

}