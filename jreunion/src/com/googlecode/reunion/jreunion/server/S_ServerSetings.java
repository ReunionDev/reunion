package com.googlecode.reunion.jreunion.server;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_ServerSetings {
	
	private float xp;
	
	private float lime;
		
	public S_ServerSetings() {
		loadFromReference();
	}
	
	public void setXp(float xp){
		this.xp = xp;
	}
	public float getXp(){
		return this.xp;
	}
	
	public void setLime(float lime){
		this.lime = lime;
	}
	public float getLime(){
		return this.lime;
	}
	
	public void loadFromReference() 
	{
		S_ParsedItem server = S_Reference.getInstance().getServerReference().getItem("Server");
		
		if (server==null)
		{
			// cant find Item in the reference continue to load defaults:
			setXp(1);
			setLime(1);
		}
		else {
			
			if(server.checkMembers(new String[]{"xp"}))
			{
				// use member from file
				setXp(Float.parseFloat(server.getMemberValue("xp")));
			}
			else
			{
				// use default
				setXp(1);
			}
			if(server.checkMembers(new String[]{"lime"}))
			{
				// use member from file
				setLime(Float.parseFloat(server.getMemberValue("lime")));
			}
			else
			{
				// use default
				setLime(1);
			}
		}
	}
}
