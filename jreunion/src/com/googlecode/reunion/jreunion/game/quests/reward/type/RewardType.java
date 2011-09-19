package com.googlecode.reunion.jreunion.game.quests.reward.type;

public class RewardType {

	private int type;
	
	public RewardType(int type) {
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
		EXPERIENCE(1),
		LIME(2),
		ITEM(3);

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