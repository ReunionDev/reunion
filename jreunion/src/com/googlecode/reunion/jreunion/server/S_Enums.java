/**
 * 
 */
package com.googlecode.reunion.jreunion.server;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_Enums {
	public enum S_ClientState {
		
		DISCONNECTED(-1),
	
		ACCEPTED( 0),
	
		GOT_VERSION (1),
	
		GOT_LOGIN ( 2),
	
		GOT_USERNAME( 3),
	
		GOT_PASSWORD( 4),
	
		GOT_AUTH( 5),
	
		CHAR_LIST( 6),
	
		CHAR_SELECTED(7),
		
		PORTING(9),
	
		INGAME( 10);
		
		int value;
		
		S_ClientState(int value){
			this.value = value;
			
		}
		public int value(){
			return value;			
		
		}
	}
	
	
	
	public enum S_LoginType{
		PLAY,
		LOGIN
		
	}
}
