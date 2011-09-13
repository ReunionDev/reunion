package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;

import org.apache.log4j.Logger;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Inventory;
import com.googlecode.reunion.jreunion.game.InventoryItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Merchant;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.QuickSlotItem;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.StashItem;
import com.googlecode.reunion.jreunion.game.VendorItem;
import com.googlecode.reunion.jreunion.game.WorldObject;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PacketFactory {

	public static enum Type{
		FAIL,
		INFO,
		OK,
		OUT,
		GO_WORLD,
		GOTO,
		PARTY_DISBAND,
		HOUR,
		IN_CHAR,
		SAY,
		IN_ITEM,
		DROP,
		IN_NPC,
		AT,
		PLACE,
		S_CHAR,
		WALK,
		SOCIAL,
		COMBAT,
		JUMP,
		LEVELUP,
		STATUS, 
		EFFECT,
		CHAR_REMOVE, 
		CHAR_WEAR, 
		ATTACK,
		ATTACK_VITAL,
		SKILLLEVEL,
		PICKUP,
		PICK,
		SHOP_RATE, 
		SHOP_ITEM, 
		SUCCESS,
		MSG,
		STASH,
		STASH_END,
		INVEN,
		SKILLLEVEL_ALL, 
		A_,
		SKILL,
		MULTI_SHOT,
		QUICK,
		WEARING
	}
	
	public static String createPacket(Type packetType, Object... args) {
		switch (packetType) {
		
		case FAIL:
			String message = "";
			for(Object o: args){
				message+=" "+o;
			}
			return "fail"+message;
		case INFO:
			String infomsg = "";
			for(Object o: args){
				infomsg+=" "+o;
			}
			return "info"+infomsg;
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
				int combat = player.isInCombat() ? 1 : 0;
				Equipment eq = player.getEquipment();
				
				String packetData = warping?"appear ":"in ";

				packetData += "char " + player.getEntityId() + " " + player.getName()
						+ " " + player.getRace().ordinal() + " " + player.getSex().ordinal() + " "
						+ player.getHairStyle() + " " + player.getPosition().getX()
						+ " " + player.getPosition().getY() + " "
						+ player.getPosition().getZ() + " "
						+ player.getPosition().getRotation() + " " + eq.getType(Slot.HELMET) + " "
						+ eq.getType(Slot.CHEST) + " " + eq.getType(Slot.PANTS) + " " + eq.getType(Slot.SHOULDER) + " "
						+ eq.getType(Slot.BOOTS) + " " + eq.getType(Slot.OFFHAND) + " " + eq.getType(Slot.MAINHAND) + " "
						+ player.getPercentageHp() + " " + combat + " 0 0 0 0 0 0";
				// in char [UniqueID] [Name] [Race] [Gender] [HairStyle] [XPos]
				// [YPos] [ZPos] [Rotation] [Helm] [Armor] [Pants] [ShoulderMount]
				// [Boots] [Shield] [Weapon] [Hp%] [CombatMode] 0 0 0 [Boosted] [PKMode]
				// 0 [Guild]
				// [MemberType] 1
				return packetData;
			}
			break;
		
		case OUT:
			if(args.length>0){
				WorldObject object = (WorldObject)args[0];
				return "out "+getObjectType(object)+" " + object.getEntityId();
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
							return "say "+from.getEntityId()+" "+name+" " + text + " "+(admin?1:0);	
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
				
				return "drop " + roamingItem.getEntityId() + " " + item.getType() + " "
				+ position.getX() + " " + position.getY() + " " + position.getZ() + " "+position.getRotation()+" " + item.getGemNumber() + " "
				+ item.getExtraStats();
			}			
			break;
		
		case IN_ITEM:
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Item item = roamingItem.getItem();
				Position position = roamingItem.getPosition();
				return "in item " + roamingItem.getEntityId() + " " + item.getType() + " " + position.getX()
				+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation() + " " + item.getGemNumber() + " "
				+ item.getExtraStats();
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
				Position npcPosition = npc.getPosition();
				return "in npc " + npc.getEntityId() + " " + npc.getType()
						+ " " + npcPosition.getX() + " "
						+ npcPosition.getY() + " "+npcPosition.getZ()+" "
						+ (Double.isNaN(npcPosition.getRotation())?0.0:npcPosition.getRotation()) + " " + percentageHp + " "
						+ npc.getMutant() + " " + npc.getUnknown1() + " "
						+ npc.getNeoProgmare() + " 0 " + (spawn ? 1 : 0) + " "
						+ npc.getUnknown2();
			}
			break;
			
		case AT:
			if(args.length>0){
				Player player = (Player)args[0];
			return
					"at " + player.getEntityId() + " "
							+ player.getPosition().getX() + " " + player.getPosition().getY() + " "
							+ player.getPosition().getZ() + " 0";
			}
			break;
			
		case PLACE:
			if(args.length>1){
			Player player = (Player)args[0];
			Position position = player.getPosition();
			
			int unknown = (Integer)args[1];
			return "place char " + player.getEntityId() + " " + position.getX()
			+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation() + " "
			+ unknown + " " + (player.isRunning()?1:0);
			}
			break;
			
		case S_CHAR:
			if(args.length>0){
				Player player = (Player)args[0];
				Position position = player.getPosition();
				return "s char " + player.getEntityId() + " " + position.getX()
					+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation();
			}
			break;
			
		case WALK:
			if(args.length>1){
				LivingObject livingObject = (LivingObject)args[0];
				Position position = (Position)args[1];
					
				return "walk "+getObjectType(livingObject)+" " + livingObject.getEntityId() + " " + position.getX()
				+ " " + position.getY() + " " + position.getZ() + " " + (livingObject.isRunning()?1:0);
			}
			break;
			
		case SUCCESS:
			return "success";
			
		case SOCIAL:
			if(args.length>1){
				Player player = (Player)args[0];
				int emotionId = (Integer)args[1];
				return "social char " + player.getEntityId() + " "
				+ emotionId;
			}
			break;
			
		case SKILLLEVEL_ALL:
			if(args.length>0){
				String packetData = "skilllevel_all";
				Player player = (Player)args[0];
				for(Skill skill: player.getSkills().keySet()) {
					packetData += " " + skill.getId() + " " + player.getSkillLevel(skill);
				}
				return packetData;
			}
			break;
		case A_:
			if(args.length>1){
				String type = (String)args[0];
				Object value = args[1];				
				return "a_" + type + " " + value;
			}
			break;
				
		case COMBAT:
			if(args.length>0){
				Player player = (Player)args[0];
				return "combat " + player.getEntityId() + " " + (player.isInCombat()?1:0);
			}
			break;
			
		case JUMP:
			if(args.length>0){
				Player player = (Player)args[0];
				return "jump " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " " + player.getEntityId();
			}
			break;
				
		case LEVELUP:
			if(args.length>0){
				Player player = (Player)args[0];
				return "levelup " + player.getEntityId();
			}
			break;
			
		case STATUS:
			if(args.length>1){
				int id = (Integer)args[0];
				Object arg1 = (Integer)args[1];
				Object arg2 = 0;
				if(args.length > 2){
					arg2 = (Integer)args[2];
				}
				return "status " + id + " " + arg1 + " " + arg2;
			}
			break;
		
		case MSG:
			if(args.length>0){
				String msg = (String)args[0];
				return "msg "+msg;
			}
			break;
			
		case EFFECT: //attack skill
			if(args.length>2){
				LivingObject source = (LivingObject) args[0];				
				LivingObject target = (LivingObject)args[1];
				Skill skill = (Skill)args[2];
				
				return "effect " + skill.getId() + " "+getObjectType(source)+" "
				+ source.getEntityId() + " "+getObjectType(target)+" " + target.getEntityId() + " "
				+ target.getPercentageHp() + " 0 0";
	
				// S> effect [SkillID] char [charID] npc [npcID] [RemainNpcHP%] 0 0
			}
			break;
			
		case SKILL: //self usable skill
			if(args.length>1){
				LivingObject source = (LivingObject) args[0];				
				Skill skill = (Skill)args[1];
	
				return "skill " + ((Effectable)skill).getEffectModifier() + " char "+ source.getEntityId() + " "
						+skill.getId();
				
				// S> skill [Duration/Activated] char [CharID] [SkillID]
			}
			break;
			
		case CHAR_REMOVE:
			if(args.length>1){
				Player player = (Player)args[0];
				Slot slot = (Slot)args[1];
				return "char_remove " + player.getEntityId() + " " + slot.ordinal();
			}
			break;
			
		case CHAR_WEAR:
			if(args.length>1){
				Player player = (Player)args[0];
				Slot slot = (Slot)args[1];
				Item item = (Item)args[2];
				return "char_wear " + player.getEntityId() + " " + slot.ordinal() + " "
				+ item.getType() + " " + item.getGemNumber();
			}
			break;
			
		case ATTACK:
			if(args.length>1){
				LivingObject source = (LivingObject) args[0];				
				LivingObject target = (LivingObject)args[1];
				
				return "attack "+getObjectType(source)+" " +
					  source.getEntityId() + " "+getObjectType(target)+" " + target.getEntityId() + " " + target.getPercentageHp() +
					  " "+source.getDmgType()+" 0 0 0";
					  // S> attack char [CharID] npc [NpcID] [RemainHP%] 0 0 0 0
			}
			break;
		
		case ATTACK_VITAL:
			if(args.length>0){
				LivingObject target = (LivingObject)args[0];
				return 
				"attack_vital "+getObjectType(target)+" " + target.getEntityId() + " "
				+ target.getPercentageHp() + " 0 0";
			}
			break;
			
		case SKILLLEVEL:
			if(args.length>1){
				Skill skill = (Skill)args[0];
				int currentSkillLevel =  (Integer)args[1];
				return "skilllevel " + skill.getId() + " " + currentSkillLevel;
			}
			break;
		case PICKUP:
			if(args.length>0){
				Player player = (Player)args[0];
				return "pickup " + player.getEntityId();
			}
			break;
			
		case PICK:
			if(args.length>0){
				InventoryItem invItem = (InventoryItem)args[0];
				Item item = invItem.getItem();
				
				return "pick " + item.getEntityId() + " " + item.getType()
				+ " "+invItem.getPosX()+" "+invItem.getPosY()+" "+invItem.getTab()+" " + item.getGemNumber() + " "
				+ item.getExtraStats();
			}
			break;
		case SHOP_RATE:
			if(args.length>0){
				Merchant merchant = (Merchant)args[0];
				return "shop_rate " + merchant.getBuyRate() + " "+ merchant.getSellRate();
			}
			break;
			
		case SHOP_ITEM:
			if(args.length>0){
				VendorItem vendorItem = (VendorItem)args[0];
				return "shop_item " + vendorItem.getType();			
			}
			break;
			
		case STASH:
			if(args.length>1){
				Player player = (Player) args[0];
				StashItem stashItem = (StashItem)args[1];
				Item item = stashItem.getItem();
				return "stash " + stashItem.getPos() + " "
				+ item.getType() + " "
				+ item.getGemNumber() + " "
				+ item.getExtraStats() + " "
				+ player.getStash().getQuantity(stashItem.getPos());
			}
			break;
		case STASH_END:
			return "stash_end";
			
		case INVEN:
			if(args.length > 0){
				InventoryItem invItem = (InventoryItem)args[0];
				Item item = invItem.getItem();
				
				return "inven " + invItem.getTab() + " "
				+ item.getEntityId() + " "
				+ item.getType() + " " + invItem.getPosX()
				+ " " + invItem.getPosY() + " "
				+ item.getGemNumber() + " "
				+ item.getExtraStats();
			}
			break;
			
		case MULTI_SHOT: //human semi-automatic skill
			if(args.length >= 1){
				String source = (String) args[0]; //me or char
				int numberOfShots = (Integer) args[1];
				String charId = args.length == 3 ? (String) args[2]+" " : "";
				
				return "multi_shot "+source+" " +charId+""+numberOfShots;
					  // S> multi_shot me [numberOfShots]
					  // S> multi_shot char [charID] [numberOfShots]
			}
			break;
			
		case QUICK:
			if(args.length > 0){
				QuickSlotItem qsItem = (QuickSlotItem)args[0];
				Item item = qsItem.getItem();
				
				return "quick " + qsItem.getSlot() + " "
						+ item.getEntityId() + " "
						+ item.getType() + " "
						+ item.getGemNumber() + " "
						+ item.getExtraStats() + "\n";
			}
			break;
			
		case WEARING:
			if(args.length > 0){
				Equipment eq = (Equipment)args[0];
				
				return "wearing " + eq.getEntityId(Slot.HELMET) + " " + eq.getType(Slot.HELMET) + " "
						+ eq.getGemNumber(Slot.HELMET) + " " + eq.getExtraStats(Slot.HELMET) + " " + eq.getEntityId(Slot.CHEST) + " "
						+ eq.getType(Slot.CHEST) + " " + eq.getGemNumber(Slot.CHEST) + " " + eq.getExtraStats(Slot.CHEST) + " "
						+ eq.getEntityId(Slot.PANTS) + " " + eq.getType(Slot.PANTS) + " " + eq.getGemNumber(Slot.PANTS) + " "
						+ eq.getExtraStats(Slot.PANTS) + " " + eq.getEntityId(Slot.SHOULDER) + " "
						+ eq.getType(Slot.SHOULDER) + " " + eq.getGemNumber(Slot.SHOULDER) + " "
						+ eq.getExtraStats(Slot.SHOULDER) + " " + eq.getEntityId(Slot.BOOTS) + " " + eq.getType(Slot.BOOTS)
						+ " " + eq.getGemNumber(Slot.BOOTS) + " " + eq.getExtraStats(Slot.BOOTS) + " " + eq.getEntityId(Slot.OFFHAND)
						+ " " + eq.getType(Slot.OFFHAND) + " " + eq.getGemNumber(Slot.OFFHAND) + " " + eq.getExtraStats(Slot.OFFHAND)
						+ " " + eq.getEntityId(Slot.NECKLACE) + " " + eq.getType(Slot.NECKLACE) + " "
						+ eq.getGemNumber(Slot.NECKLACE) + " " + eq.getExtraStats(Slot.NECKLACE) + " " + eq.getEntityId(Slot.BRACELET)
						+ " " + eq.getType(Slot.BRACELET) + " " + eq.getGemNumber(Slot.BRACELET) + " "
						+ eq.getExtraStats(Slot.BRACELET) + " " + eq.getEntityId(Slot.RING) + " " + eq.getType(Slot.RING) + " "
						+ eq.getGemNumber(Slot.RING) + " " + eq.getExtraStats(Slot.RING) + " " + eq.getEntityId(Slot.MAINHAND) + " "
						+ eq.getType(Slot.MAINHAND) + " " + eq.getGemNumber(Slot.MAINHAND) + " " + eq.getExtraStats(Slot.MAINHAND);
				// wearing [Helm] [Armor] [Pants] [ShoulderMount] [Boots] [Shield]
				// [Necklace] [Bracelet] [Ring] [Weapon]
			}
			break;
			
			
		default:			
			throw new UnsupportedOperationException();
		}
		throw new RuntimeException("Invalid parameters for "+packetType+" message");
	}
	
	private static String getObjectType(WorldObject object){
		if (object instanceof Player){
			return "char";
		}
		if(object instanceof RoamingItem){
			return "item";			
		}
		else if (object instanceof Npc) {
			return "npc";
		}
		
		throw new RuntimeException("Invalid Object: "+object);
	}

	public PacketFactory() {
		super();

	}

}
