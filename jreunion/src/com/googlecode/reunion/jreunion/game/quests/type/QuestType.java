package com.googlecode.reunion.jreunion.game.quests.type;

public class QuestType {

	private int type;
	
	public QuestType(int type) {
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
		CURSED(1),
		KILL(2),
		POINTS(3);

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