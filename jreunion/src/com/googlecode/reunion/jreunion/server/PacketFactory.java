package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;

import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PacketFactory {

	public static enum Type{
		VERSION_ERROR,
		FAIL,
		OK,
		GO_WORLD,
		GOTO,
		PARTY_DISBAND
		
	}
	
	//public static final int PT_VERSION_ERROR = 1001;
	//public static final int PT_OK = 1002;
	//parametered arguments
	
	// PacketFactory.createPacket(Type.FAIL);

	public static String createPacket(Type type, Object... args) {
		switch (type) {
		case VERSION_ERROR: {
			if (args.length == 1) {
				
				
				String clientVersion = (String) args[0];
				String requiredVersion = Reference.getInstance().getServerReference().getItem("Server").getMemberValue("Version");
				String message = "Wrong clientversion: current version "
						+ clientVersion + " required version "
						+ requiredVersion;

				return PacketFactory.createPacket(Type.FAIL, message);
			}
			break;

		}
		case FAIL:{
			String message = "";
			for(Object o: args){
				message+=" "+o;
			}
			return "fail"+message;
		}
		
		case OK: {
			return "OK";
		}
		
		case GO_WORLD:{
			
			if(args.length>0){				
				Map map = (Map)args[0];
				int unknown =  args.length>1?(Integer)args[1]:0;
				InetSocketAddress address = map.getAddress();
				return "go_world "+address.getAddress().getHostAddress()+" "+address.getPort()+" " + map.getId()+" "+unknown;
			}
			break;
		}
		case GOTO:{
			if(args.length>0){
				Position position = (Position)args[0];
			
				return "goto " + position.getX() + " " + position.getY() + " "+position.getZ()+" "	+ position.getRotation();
			
			}
			break;
			
		}
		case PARTY_DISBAND:{
			
			
			return "party disband";
			
		}

		}
		throw new RuntimeException("Invalid parameters for "+type+" message");
	}

	public PacketFactory() {
		super();

	}

}
