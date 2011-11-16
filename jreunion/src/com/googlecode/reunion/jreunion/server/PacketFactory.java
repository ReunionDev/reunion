package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;

import com.googlecode.reunion.jreunion.game.Effectable;
import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
import com.googlecode.reunion.jreunion.game.InventoryItem;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Merchant;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.QuickSlotItem;
import com.googlecode.reunion.jreunion.game.RoamingItem;
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
		STASH_TO,
		STASH_FROM,
		STASH_END,
		INVEN,
		SKILLLEVEL_ALL, 
		A_,
		SKILL,
		MULTI_SHOT,
		QUICK,
		WEARING,
		UPGRADE,
		QT,
		KILL,
		Q_EX,
		WISPER,
		SECONDATACK
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
						+ player.getPosition().getRotation() + " " + eq.getTypeId(Slot.HELMET) + " "
						+ eq.getTypeId(Slot.CHEST) + " " + eq.getTypeId(Slot.PANTS) + " " + eq.getTypeId(Slot.SHOULDER) + " "
						+ eq.getTypeId(Slot.BOOTS) + " " + eq.getTypeId(Slot.OFFHAND) + " " + eq.getTypeId(Slot.MAINHAND) + " "
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
				Player from = null;
				if(args.length>1){
					from = (Player)args[1];
				}
				
				if(from==null) {
					return "say "+ -1 +" "+text;
				} else { 
					if(args.length == 2){
						boolean admin = from.getAdminState() == 255;
						String name = from.getName();
						if(admin)
							name = "<GM>"+name;
						return "say "+from.getEntityId()+" "+name+" " + text + " "+(admin ? 1 : 0);	
					}
				}				
			}
			break;
		
		case WISPER:
			if(args.length == 4){
				String text = (String)args[0];
				String eintyID = (String)args[1];
				String name = (String)args[2];
				String direction = (String)args[3];
				
				return "say "+eintyID+" "+direction+name+" " + text + " 0";
			}
			break;
			
		case DROP:
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Position position = roamingItem.getPosition();
				Item<?> item = roamingItem.getItem();
				
				return "drop " + roamingItem.getEntityId() + " " + item.getType().getTypeId() + " "
				+ position.getX() + " " + position.getY() + " " + position.getZ() + " "+position.getRotation()+" " + item.getGemNumber() + " "
				+ item.getExtraStats()+ " " + item.getUnknown1() + " " + item.getUnknown2();
			}			
			break;
		
		case IN_ITEM:
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Item<?> item = roamingItem.getItem();
				Position position = roamingItem.getPosition();
				
				return "in item " + roamingItem.getEntityId() + " " + item.getType().getTypeId() + " " + position.getX()
				+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation() + " " + item.getGemNumber()
				+ " " + item.getExtraStats()+ " " + item.getUnknown1() + " " + item.getDurability() + " "
				+ item.getType().getMaxDurability();
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
				return "in n " + npc.getEntityId() + " " + npc.getType()
						+ " " + npcPosition.getX() + " "
						+ npcPosition.getY() + " "+npcPosition.getZ()+" "
						+ (Double.isNaN(npcPosition.getRotation())?0.0:npcPosition.getRotation()) + " " + percentageHp + " "
						+ npc.getMutant() + " " + npc.getUnknown1() + " "
						+ npc.getNeoProgmare() + " " + npc.getUnknown2() + " "+ (spawn ? 1 : 0) + " "
						+ npc.getUnknown3();
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
			
		case SECONDATACK: //or Subattack
			//sa c 547782 c 589654 0 3 0 0 40
			
			if(args.length == 3)
			{
				LivingObject source = (LivingObject) args[0];				
				LivingObject target = (LivingObject)args[1];
				String effectid = (String)args[2];
				
				return "sa "+getObjectType(source)+" "+source.getEntityId()+" "+getObjectType(target)+" "+target.getEntityId()+" "+target.getPercentageHp()+" 3 0 0 "+effectid;
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
				Item<?> item = (Item<?>)args[2];
				return "char_wear " + player.getEntityId() + " " + slot.ordinal() + " "
				+ item.getType().getTypeId() + " " + item.getGemNumber();
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
				Item<?> item = invItem.getItem();
				
				return "pick " + item.getEntityId() + " " + item.getType().getTypeId() + " "
				+ invItem.getPosition().getPosX()+" "+invItem.getPosition().getPosY() + " "
				+ invItem.getPosition().getTab()+" " + item.getGemNumber() + " "
				+ item.getExtraStats() + " " + item.getUnknown1() + " " + item.getDurability() + " "
				+ item.getType().getMaxDurability();
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
				Item<?> item = stashItem.getItem();
				
				return "stash " + stashItem.getStashPosition().getSlot() + " "
				+ item.getType().getTypeId() + " " + item.getGemNumber() + " "
				+ item.getExtraStats() + " " + item.getUnknown1() + " "
				+ item.getDurability() + " " + item.getType().getMaxDurability() + " "
				+ player.getStash().getQuantity(stashItem.getStashPosition().getSlot()) + " "
				+ item.getUnknown2();
			}
			break;
			
		case STASH_TO:
			if(args.length>1){
				Player player = (Player) args[0];
				StashItem stashItem = (StashItem)args[1];
				Item<?> item = stashItem.getItem();
				String packet = "stash_to "+stashItem.getStashPosition().getSlot()+" ";
				
				/*int gemNumber = (Integer)args[2];
				
				if(stashItem.getStashPosition().getSlot() == 12){
					packet += " 0 " + gemNumber + " 0";
				} else {
				*/
				 packet += item.getType().getTypeId() + " " + item.getGemNumber() + " "
				+ item.getExtraStats() + " " + item.getUnknown1() + " "
				+ item.getDurability() + " " + item.getType().getMaxDurability() + " "
				+ player.getStash().getQuantity(stashItem.getStashPosition().getSlot()) + " "
				+ item.getUnknown2();
				 
				//}
				
				return packet;
			}
			break;
		
		case STASH_FROM:
			if(args.length>0){
				StashItem stashItem = (StashItem)args[0];
				Item<?> item = stashItem.getItem();
				
				return "stash_from " + stashItem.getStashPosition().getSlot() + " "
				+ item.getEntityId() + " " + item.getType().getTypeId() + " "
				+ item.getGemNumber() + " "	+ item.getExtraStats() + " "
				+ item.getUnknown1() + " " + item.getDurability() + " "
				+ item.getType().getMaxDurability() + " " + item.getUnknown2();
			}
			break;
			
		case STASH_END:
			return "stash_end";
			
		case INVEN:
			if(args.length > 0){
				InventoryItem invItem = (InventoryItem)args[0];
				Item<?> item = invItem.getItem();
				
				return "inven " + invItem.getPosition().getTab() + " "
				+ item.getEntityId() + " "	+ item.getType().getTypeId() + " " + invItem.getPosition().getPosX() + " "
				+ invItem.getPosition().getPosY() + " " + item.getGemNumber() + " "	+ item.getExtraStats() + " "
				+ item.getUnknown1() + " " + item.getUnknown2();
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
				Item<?> item = qsItem.getItem();
				
				return "quick " + qsItem.getPosition().getSlot() + " "
						+ item.getEntityId() + " "
						+ item.getType().getTypeId() + " "
						+ item.getGemNumber() + " "
						+ item.getExtraStats() + " "
						+ item.getUnknown1();
			}
			break;
		
		case UPGRADE:
			if(args.length > 0){
				Item<?> item = (Item<?>) args[0];
				Slot slot = (Slot) args[1];
				int upgraderesult = (Integer) args[2];
				
				return "upgrade " + upgraderesult + " "
						+ slot.value() + " "
						+ item.getEntityId() + " "
						+ item.getGemNumber();
			}
			break;
			
		case KILL:
			if(args.length > 0){ //Kill is used on 2007+ client
				LivingObject target = (LivingObject) args[0];
				
				if(target instanceof Player)
				{
					return "kill c " + target.getEntityId() + "\n";
				}
				else
					return "kill n " + target.getEntityId() + "\n";
			}
			break;
			
		case WEARING:
			if(args.length > 0){
				Equipment eq = (Equipment)args[0];
				/* 
				 * NGA
				 * [entity] [typeid] [gemnumber] [extrast] [unknown1] [unknown 2] [unknown 3] [cur_dur] [max_dur]
				 * 1524774       318           2         0          0           0           0      4449      4453
				 * 
				 * wearing
				 * 1524774 318 2 0 0 0 0 4449 4453  [Helmet]   0
				 * 1524775 333 9 0 0 0 0 4322 4509  [Armor]    1
				 * 1524776 351 15 0 0 0 0 3778 4029 [Pants]    2
				 * 1524777 196 0 0 0 0 0 0 0        [Cloak]    3
				 * 1524778 373 3 0 0 0 0 3113 3203  [Shoes]    4
				 * 1524779 121 6 0 0 0 0 0 0        [Shield]   5
				 * 1524780 445 6 1 0 0 0 0 0        [Necklace aka Amulet] 6
				 * -1 -1 0 0 0 0 0 0 0              [Ring]     7
				 * 1524781 455 1 1 0 0 0 0 0        [Bracelet] 8
				 * -1 -1 0 0 0 0 0 0 0              [Weapon]   9
				 * -1 -1 0 0 0 0 0 0 0              [?]        10 PET1 ?
				 * -1 -1 0 0 0 0 0 0 0              [?]        11 PET2 ?
				 * 
				 */
				
				//return "wearing 1101 1214 13 4095 4260868 112 130 1102 1215 14 4095 1 113 130 1103 1216 14 4095 1 123 130 1104 752 334 41856 0 0 0 1105 1217 13 268439551 1 119 130 1106 876 0 255 0 0 0 1107 448 3 512 0 0 0 1108 453 0 192 0 0 0 1109 1087 1 38535169 0 0 0 1110 1321 6 50335743 262 110 110";
				
				return "wearing " + eq.getEntityId(Slot.HELMET) + " " + eq.getTypeId(Slot.HELMET) + " "
						+ eq.getGemNumber(Slot.HELMET) + " " + eq.getExtraStats(Slot.HELMET) + " "
						+ eq.getUnknown1(Slot.HELMET) + " " + eq.getDurability(Slot.HELMET) + " "
						+ eq.getMaxDurability(Slot.HELMET) + " "
						
						+ eq.getEntityId(Slot.CHEST) + " " + eq.getTypeId(Slot.CHEST) + " "
						+ eq.getGemNumber(Slot.CHEST) + " " + eq.getExtraStats(Slot.CHEST) + " "
						+ eq.getUnknown1(Slot.CHEST) + " " + eq.getDurability(Slot.CHEST) + " "
						+ eq.getMaxDurability(Slot.CHEST) + " "
						
						+ eq.getEntityId(Slot.PANTS) + " " + eq.getTypeId(Slot.PANTS) + " "
						+ eq.getGemNumber(Slot.PANTS) + " " + eq.getExtraStats(Slot.PANTS) + " "
						+ eq.getUnknown1(Slot.PANTS) + " " + eq.getDurability(Slot.PANTS) + " "
						+ eq.getMaxDurability(Slot.PANTS) + " "
						
						+ eq.getEntityId(Slot.SHOULDER) + " " + eq.getTypeId(Slot.SHOULDER) + " "
						+ eq.getGemNumber(Slot.SHOULDER) + " " + eq.getExtraStats(Slot.SHOULDER) + " "
						+ eq.getUnknown1(Slot.SHOULDER) + " " + eq.getDurability(Slot.SHOULDER) + " "
						+ eq.getMaxDurability(Slot.SHOULDER) + " "
						
						+ eq.getEntityId(Slot.BOOTS) + " " + eq.getTypeId(Slot.BOOTS)
						+ " " + eq.getGemNumber(Slot.BOOTS) + " " + eq.getExtraStats(Slot.BOOTS) + " "
						+ eq.getUnknown1(Slot.BOOTS) + " " + eq.getDurability(Slot.BOOTS) + " "
						+ eq.getMaxDurability(Slot.BOOTS) + " "
						
						+ eq.getEntityId(Slot.OFFHAND) + " " + eq.getTypeId(Slot.OFFHAND) + " "
						+ eq.getGemNumber(Slot.OFFHAND) + " " + eq.getExtraStats(Slot.OFFHAND) + " "
						+ eq.getUnknown1(Slot.OFFHAND) + " " + eq.getDurability(Slot.OFFHAND) + " "
						+ eq.getMaxDurability(Slot.OFFHAND) + " "
						
						+ eq.getEntityId(Slot.NECKLACE) + " " + eq.getTypeId(Slot.NECKLACE) + " "
						+ eq.getGemNumber(Slot.NECKLACE) + " " + eq.getExtraStats(Slot.NECKLACE) + " "
						+ eq.getUnknown1(Slot.NECKLACE) + " " + eq.getDurability(Slot.NECKLACE) + " "
						+ eq.getMaxDurability(Slot.NECKLACE) + " "
						
						+ eq.getEntityId(Slot.RING) + " " + eq.getTypeId(Slot.RING) + " "
						+ eq.getGemNumber(Slot.RING) + " " + eq.getExtraStats(Slot.RING) + " "
						+ eq.getUnknown1(Slot.RING) + " " + eq.getDurability(Slot.RING) + " "
						+ eq.getMaxDurability(Slot.RING) + " "
						
						+ eq.getEntityId(Slot.BRACELET)	+ " " + eq.getTypeId(Slot.BRACELET) + " "
						+ eq.getGemNumber(Slot.BRACELET) + " " + eq.getExtraStats(Slot.BRACELET) + " "
						+ eq.getUnknown1(Slot.BRACELET) + " " + eq.getDurability(Slot.BRACELET) + " "
						+ eq.getMaxDurability(Slot.BRACELET) + " "
						
						+ eq.getEntityId(Slot.MAINHAND) + " " + eq.getTypeId(Slot.MAINHAND) + " "
						+ eq.getGemNumber(Slot.MAINHAND) + " " + eq.getExtraStats(Slot.MAINHAND) + " "
						+ eq.getUnknown1(Slot.MAINHAND) + " " + eq.getDurability(Slot.MAINHAND) + " "
						+ eq.getMaxDurability(Slot.MAINHAND);
			
			}
			break;
			
		case QT:
			if(args.length > 0){
				String packetData = (String) args[0];
				
				return "qt " + packetData + "\n";
			}
			break;
			
		case Q_EX:
			if(args.length > 0){
				Integer limeAmmount = (Integer) args[0];
				
				return "q_ex " + limeAmmount + "\n";
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
