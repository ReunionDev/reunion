package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PacketFactory {

	public static enum Type{
		FAIL,
		OK,
		GO_WORLD,
		GOTO,
		PARTY_DISBAND, 
		HOUR, 
		IN_CHAR, 
		OUT_CHAR, 
		SAY, 
		IN_ITEM,
		OUT_ITEM, 
		DROP, 
		OUT_NPC,
		IN_NPC, 
		AT, 
		PLACE, 
		S_CHAR, 
		WALK_CHAR, 
		SOCIAL, 
		COMBAT,
		JUMP, 
		ATTACK_NPC
		
	}
	
	//public static final int PT_VERSION_ERROR = 1001;
	//public static final int PT_OK = 1002;
	//parametered arguments
	
	// PacketFactory.createPacket(Type.FAIL);

	public static String createPacket(Type packetType, Object... args) {
		switch (packetType) {
		
		case FAIL:
			String message = "";
			for(Object o: args){
				message+=" "+o;
			}
			return "fail"+message;
		
		case OK: 
			return "OK";
		
		case GO_WORLD:
			if(args.length>0){				
				LocalMap map = (LocalMap)args[0];
				int unknown =  args.length>1?(Integer)args[1]:0;
				InetSocketAddress address = map.getAddress();
				return "go_world "+address.getAddress().getHostAddress()+" "+address.getPort()+" " + map.getId()+" "+unknown;
			}
			break;
		
		case GOTO:
			if(args.length>0){
				Position position = (Position)args[0];
				return "goto " + position.getX() + " " + position.getY() + " "+position.getZ()+" "	+ position.getRotation();
			}
			break;
		
		case PARTY_DISBAND:
			return "party disband";
		
		case HOUR:
			if(args.length>0){
				int hour = (Integer)args[0];
				return "hour " + hour;
			}
			break;
		
		case IN_CHAR:
			if(args.length>0){
				Player player = (Player)args[0];
				boolean warping = false;
				if(args.length>1){
					warping = (Boolean)args[1];					
				}
				int combat = player.getCombatMode() ? 1 : 0;
				Equipment eq = player.getEquipment();
				
				int percentageHp = player.getHp() * 100 / player.getMaxHp();
				String packetData = warping?"appear ":"in ";

				packetData += "char " + player.getId() + " " + player.getName()
						+ " " + player.getRace().ordinal() + " " + player.getSex().ordinal() + " "
						+ player.getHairStyle() + " " + player.getPosition().getX()
						+ " " + player.getPosition().getY() + " "
						+ player.getPosition().getZ() + " "
						+ player.getPosition().getRotation() + " " + eq.getType(Slot.HELMET) + " "
						+ eq.getType(Slot.CHEST) + " " + eq.getType(Slot.PANTS) + " " + eq.getType(Slot.SHOULDER) + " "
						+ eq.getType(Slot.BOOTS) + " " + eq.getType(Slot.OFFHAND) + " " + eq.getType(Slot.MAINHAND) + " "
						+ percentageHp + " " + combat + " 0 0 0 0 0 0";
				// in char [UniqueID] [Name] [Race] [Gender] [HairStyle] [XPos]
				// [YPos] [ZPos] [Rotation] [Helm] [Armor] [Pants] [ShoulderMount]
				// [Boots] [Shield] [Weapon] [Hp%] [CombatMode] 0 0 0 [Boosted] [PKMode]
				// 0 [Guild]
				// [MemberType] 1
				return packetData;
			}
			break;
		
		case OUT_CHAR:
			if(args.length>0){
				Player player = (Player)args[0];
				return "out char " + player.getId();
			}
			break;
		
		case SAY:
			if(args.length>0){
				String text = (String)args[0];
				LivingObject from = null;
				
				if(args.length>1){
					from = (LivingObject)args[1];
				}
				
				if(from==null) {
					return "say "+ -1 +" "+text;
				} else { 
					if(args.length>2){
						boolean admin = false;
						String name = (String)args[2];
						if(args.length>3) {
							admin = (Boolean)args[3];
							return "say "+from.getId()+" "+name+" " + text + " "+(admin?1:0);	
						}
					}
				}				
			}
			break;		
		
		case DROP:
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Position position = roamingItem.getPosition();
				Item item = roamingItem.getItem();

				return "drop " + item.getId() + " " + item.getType() + " "
				+ position.getX() + " " + position.getY() + " " + position.getZ() + " "+position.getRotation()+" " + item.getGemNumber() + " "
				+ item.getExtraStats();
			}			
			break;
		
		case IN_ITEM:
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Item item = roamingItem.getItem();
				Position position = roamingItem.getPosition();
				return "in item " + roamingItem.getId() + " " + item.getType() + " " + position.getX()
				+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation() + " " + item.getGemNumber() + " "
				+ item.getExtraStats();
			}
			break;
		
		case OUT_ITEM:
			if(args.length>0){
				RoamingItem item = (RoamingItem)args[0];
				return "out item " + item.getId();				
			}			
			break;
		
		case IN_NPC:			
			if(args.length>0){
				Npc npc = (Npc)args[0];
				Boolean spawn = false;
				if(args.length>1){
					spawn = (Boolean)args[1];
				}
				int percentageHp = (int)(((double)npc.getHp()/ (double)npc.getMaxHp())* 100);
					
				return "in npc " + npc.getId() + " " + npc.getType()
						+ " " + npc.getPosition().getX() + " "
						+ npc.getPosition().getY() + " "+npc.getPosition().getZ()+" "
						+ npc.getPosition().getRotation() + " " + percentageHp + " "
						+ npc.getMutant() + " " + npc.getUnknown1() + " "
						+ npc.getNeoProgmare() + " 0 " + (spawn ? 1 : 0) + " "
						+ npc.getUnknown2();
			}
			break;
			
		case OUT_NPC:
			if(args.length>0){
				Npc npc = (Npc)args[0];
				return "out npc " + npc.getId();
			}
			break;
			
		case AT:
			if(args.length>0){
				Player player = (Player)args[0];
			return
					"at " + player.getId() + " "
							+ player.getPosition().getX() + " " + player.getPosition().getY() + " "
							+ player.getPosition().getZ() + " 0";
			}
			break;
			
		case PLACE:
			if(args.length>1){
			Player player = (Player)args[0];
			Position position = player.getPosition();
			
			int unknown = (Integer)args[1];
			return "place char " + player.getId() + " " + position.getX()
			+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation() + " "
			+ unknown + " " + (player.isRunning()?1:0);
			}
			break;
			
		case S_CHAR:
			if(args.length>0){
				Player player = (Player)args[0];
				Position position = player.getPosition();
				return "s char " + player.getId() + " " + position.getX()
					+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation();
			}
			break;
			
		case WALK_CHAR:
			if(args.length>0){
				Player player = (Player)args[0];
				Position position = player.getPosition();
				return "walk char " + player.getId() + " " + position.getZ()
				+ " " + position.getZ() + " " + position.getZ() + " " + (player.isRunning()?1:0);
			}
			break;
			
		case SOCIAL:
			if(args.length>1){
				Player player = (Player)args[0];
				int emotionId = (Integer)args[1];
				return "social char " + player.getId() + " "
				+ emotionId;
			}
			break;
			
		case COMBAT:
			if(args.length>0){
				Player player = (Player)args[0];
				return "combat " + player.getId() + " " + (player.getCombatMode()?1:0);
			}
			break;
			
		case JUMP:
			if(args.length>0){
				Player player = (Player)args[0];
				return "jump " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " " + player.getId();
			}
			break;
			
		case ATTACK_NPC:
			if(args.length>1){
				Player player = (Player)args[0];
				Npc npc = (Npc)args[1];
				
				return "attack npc " + npc.getId() + " char "
				+ player.getId() + " " + npc.getPercentageHp() + " "
				+ npc.getDmgType() + " 0 0 0";
			}
			break;
		
		default:			
			throw new NotImplementedException();
		

		}
		throw new RuntimeException("Invalid parameters for "+packetType+" message");
	}

	public PacketFactory() {
		super();

	}

}
