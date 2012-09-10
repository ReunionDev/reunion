package org.reunionemu.jreunion.server;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.reunionemu.jreunion.game.Effectable;
import org.reunionemu.jreunion.game.Equipment;
import org.reunionemu.jreunion.game.Equipment.Slot;
//import org.reunionemu.jreunion.game.items.pet.PetEquipment;
//import org.reunionemu.jreunion.game.items.pet.PetEquipment.PetSlot;
import org.reunionemu.jreunion.game.items.pet.PetEquipment;
import org.reunionemu.jreunion.game.items.pet.PetEquipment.PetSlot;
import org.reunionemu.jreunion.game.npc.Merchant;
import org.reunionemu.jreunion.game.npc.NpcShop;
import org.reunionemu.jreunion.game.ExchangeItem;
import org.reunionemu.jreunion.game.InventoryItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.Party;
import org.reunionemu.jreunion.game.Pet;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Position;
import org.reunionemu.jreunion.game.QuickSlotItem;
import org.reunionemu.jreunion.game.RoamingItem;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.StashItem;
import org.reunionemu.jreunion.game.VendorItem;
import org.reunionemu.jreunion.game.WorldObject;

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
		STASH_GET,
		STASH_PUT,
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
		SECONDATTACK,
		SAV,
		K,
		ICHANGE,
		CHIP_EXCHANGE,
		SKY,
		UPDATE_ITEM,
		USQ,				// old 2007 client
		UQ_ITEM,
		MT_ITEM,
		AV,
		PSTATUS,
		MYPET,
		PARTY_REQUEST,
		PARTY_SECESSION,
		PARTY_LIST,
		PARTY_MEMBER,
		PARTY_INFO,
		PARTY_CHANGE,
		P_KEEP,
		IN_PET,
		EXTRA,
	 	G_POS_START,
	 	G_POS_BODY,
	 	G_POS_END,
	 	EVENTNOTICE,
		GUILD_SAY, 
		GUILD_LEVEL, 
		GUILD_GRADE, 
		GUILD_NAME,
		EXCH,
		EXCH_ASK,
		EXCH_START,
		EXCH_INVEN_TO,
		EXCH_INVEN_FROM,
		EXCH_MONEY
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
				return "goto " + position.getX() + " " + position.getY() + " "+position.getZ()+" "	
						+ (position.getRotation()*1000)/1000;
			}
			break;

		case EVENTNOTICE:
			if(args.length>0){
				String msg = "";
				for(Object o: args){
					msg+=" "+o;
				}
				return "event"+msg;
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

				packetData += "c " + player.getEntityId() + " " + player.getName()
						+ " " + player.getRace().value() + " " + player.getSex().ordinal() + " "
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
				
				if(player.getGuildId() != 0)
				{
					String[] guildLevelText = {"","Member","","","","","","Sub-General","General","Sub-Master","Master"};
					
					packetData += " "+player.getGuildName()+" "+guildLevelText[(int) player.getGuildLvl()]+" "+player.getGuildLvl();
				}
				
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
			case GUILD_SAY:
				if(args.length>0){
					String text = (String)args[0];
					Player from = (Player)args[1];
					
					return "say "+from.getEntityId()+" *GUILD*"+from.getName()+" "+text;
					
				}
				break;
				
			case GUILD_NAME:
				if(args.length == 1){
					Player player = (Player)args[0];
					return "guild_name "+player.getEntityId()+" "+player.getGuildName();
				}
				break;
				
			case GUILD_GRADE:
				if(args.length == 1){
					
					String[] guildLevelText = {"","Member","","","","","","Sub-General","General","Sub-Master","Master"};
					
					
					Player player = (Player)args[0];
					return "guild_grade "+player.getEntityId()+" 0 "+guildLevelText[(int) player.getGuildLvl()] + " "+player.getGuildLvl();
				}
				break;
				
			case GUILD_LEVEL:
				if(args.length == 1){
					Player player = (Player)args[0];
					return "guild_level "+player.getGuildLvl();
				}
				break;
				
		case WISPER:
			if(args.length == 3)
			{
				String text = (String)args[0];
				Player player = (Player)args[1];
				String direction = (String)args[2];
				
				return "say "+player.getEntityId()+" "+direction+player.getName()+" " + text + " "+((player.getAdminState() >= 200) ? 1 : 0);
			}
			break;
			
		case DROP:
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Position position = roamingItem.getPosition();
				Item<?> item = roamingItem.getItem();
				
				return "drop " + item.getEntityId() + " " + item.getType().getTypeId() + " "
				+ position.getX() + " " + position.getY() + " " + position.getZ() + " "+position.getRotation()
				+" " + item.getGemNumber() + " "+ item.getExtraStats()+ " " + item.getUnknown1() + " " + item.getUnknown2();
			}			
			break;
		
		case IN_ITEM:
			if(args.length>0){
				RoamingItem roamingItem = (RoamingItem)args[0];
				Item<?> item = roamingItem.getItem();
				Position position = roamingItem.getPosition();
				
				return "in item " + item.getEntityId() + " " + item.getType().getTypeId() + " " + position.getX()
				+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation() + " " + item.getGemNumber()
				+ " " + item.getExtraStats()+ " " + item.getUnknown1() + " " + item.getDurability() + " "
				+ item.getType().getMaxDurability();
			}
			break;
		
		case IN_NPC:			
			if(args.length>0){
				Npc<?> npc = (Npc<?>)args[0];
				Boolean spawn = false;
				if(args.length>1){
					spawn = (Boolean)args[1];
				}
				int percentageHp = (int)(((double)npc.getHp()/ (double)npc.getMaxHp())* 100);
				Position npcPosition = npc.getPosition();
				return "in n " + npc.getEntityId() + " " + npc.getType().getTypeId()
						+ " " + npcPosition.getX() + " "
						+ npcPosition.getY() + " "+npcPosition.getZ()+" "
						+ npcPosition.getRotation() + " "
						+ percentageHp + " "
						+ npc.getMutantType() + " " + npc.getUnknown1() + " "
						+ npc.getType().getNeoProgmare() + " " + npc.getUnknown2() + " "+ (spawn ? 1 : 0) + " "
						+ npc.getUnknown3();
			}
			break;
			
		case AT:
			if(args.length>0){
				Player player = (Player)args[0];
			return
					"at " + player.getEntityId() + " "
							+ player.getPosition().getX() + " " + player.getPosition().getY() + " "
							+ player.getPosition().getZ() + " " + (float)player.getPosition().getRotation();
			}
			break;
			
		case PLACE:
			if(args.length>1){
			Player player = (Player)args[0];
			Position position = player.getPosition();
			
			int unknown = (Integer)args[1];
			return "p c " + player.getEntityId() + " " + position.getX()
			+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation() + " "
			+ unknown + " " + (player.isRunning()?1:0);
			}
			break;
			
		case S_CHAR:
			if(args.length>0){
				Player player = (Player)args[0];
				Position position = player.getPosition();
				return "s c " + player.getEntityId() + " " + position.getX()
					+ " " + position.getY() + " " + position.getZ() + " " + position.getRotation();
			}
			break;
			
		case WALK: 
			if(args.length>1){
				LivingObject livingObject = (LivingObject)args[0];
				Position position = (Position)args[1];
				
				return "w "+getObjectType(livingObject)+" " + livingObject.getEntityId() + " " + position.getX()
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
								+ player.getPosition().getY() + " "
								+ player.getEntityId();
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
				long arg1 = (Long)args[1];
				long arg2 = 0;
				if(args.length > 2){
					arg2 = (Long)args[2];
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
				+ target.getPercentageHp() + " " + source.getDmgType() + " 0 0 0";
	
				// S> effect [SkillID] [n/c] [1STEntityId] [n/c] [2NDEntityID] [RemainingHP%] [Critical] 0 0 0
			}
			break;
			
		case SECONDATTACK: //or Subattack
			//sa c 547782 c 589654 0 3 0 0 40
			
			if(args.length == 3)
			{
				LivingObject source = (LivingObject) args[0];				
				LivingObject target = (LivingObject)args[1];
				int skillId = (Integer)args[2];
				
				return "sa "+getObjectType(source)+" "+source.getEntityId()+" "+getObjectType(target)+" "
				+target.getEntityId()+" "+target.getPercentageHp()+" "+source.getDmgType()+" 0 0 "+skillId;
			}
			break;
			
		case SAV: 
			//sav n 26128 75 2 0 4900 3
			
			if(args.length == 5)
			{
				LivingObject target = (LivingObject) args[0];				
				int damageType = (Integer)args[1];
				int unknown1 = (Integer)args[2];
				int itemStatusRemain = (Integer)args[3];
				int unknown2 = (Integer)args[4];
				
				return "sav "+ getObjectType(target) + " "
							+ target.getEntityId() + " "
							+ target.getPercentageHp() + " "
							+ damageType + " "
							+ unknown1 + " "
							+ itemStatusRemain + " "
							+ unknown2;
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
				return "char_remove " + player.getEntityId() + " " + slot.value();
			}
			break;
			
		case CHAR_WEAR:
			if(args.length>1){
				Player player = (Player)args[0];
				Slot slot = (Slot)args[1];
				Item<?> item = (Item<?>)args[2];
				return "char_wear " + player.getEntityId() + " " + slot.value() + " "
				+ item.getType().getTypeId() + " " + item.getGemNumber();
			}
			break;
			
		case ATTACK:
			if(args.length>1){
				LivingObject source = (LivingObject) args[0];				
				LivingObject target = (LivingObject)args[1];
				int isCritical = (Integer)args[2];
				
				return "attack " + getObjectType(source) + " "
						+ source.getEntityId() + " " + getObjectType(target)
						+ " " + target.getEntityId() + " "
						+ target.getPercentageHp() + " " + isCritical
						+ " 0 0 0 0";
					  // S> attack c [CharEntityID] npc [NpcEntityID] [RemainHP%] [isCritical] 0 0 0 0
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
				NpcShop npcShop = (NpcShop)args[0];
				return "shop_rate " + npcShop.getBuyRate() + " "+ npcShop.getSellRate();
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
				StashItem stashItem = (StashItem)args[0];
				int itemQuantity = (Integer)args[1];
				Item<?> item = stashItem.getItem();
				int slot = stashItem.getStashPosition().getSlot();
				
				if(slot == 12)
					return "stash " + slot + " 0 " + (item.getGemNumber()/100) + " 0 0"; 
				else
					return "stash "
							+ slot + " "
							+ item.getType().getTypeId() + " "
							+ item.getGemNumber() + " "
							+ item.getExtraStats() + " "
							+ item.getUnknown1() + " "
							+ item.getDurability() + " "
							+ item.getType().getMaxDurability() + " "
							+ itemQuantity + " "
							+ item.getUnknown2();
			}
			break;
			
		case STASH_TO:
			if(args.length>1){
				StashItem stashItem = (StashItem)args[0];
				int itemQuantity = (Integer)args[1];
				Item<?> item = stashItem.getItem();
				int slot = stashItem.getStashPosition().getSlot();
				
				if(slot == 12)
					return "stash_to "
							+ slot + " 0 "
							+ (item.getGemNumber()/100) + " 0";
				else
					return "stash_to "
							+ slot + " "
							+ item.getType().getTypeId() + " "
							+ item.getGemNumber() + " "
							+ item.getExtraStats() + " "
							+ item.getUnknown1() + " "
							+ item.getDurability() + " "
							+ item.getType().getMaxDurability()	+ " "
							+ itemQuantity + " "
							+ item.getUnknown2();
			}
			break;
		
		case STASH_FROM:
			if(args.length>0){
				StashItem stashItem = (StashItem)args[0];
				int itemQuantity = (Integer)args[1];
				Item<?> item = stashItem.getItem();
				int slot = stashItem.getStashPosition().getSlot();
				
				if(slot == 12)
					return "stash_from "
							+ slot + " 0 "
							+ (item.getGemNumber()/100) + " 0";
				else
					return "stash_from "
							+ slot + " "
							+ item.getEntityId() + " "
							+ item.getType().getTypeId() + " "
							+ item.getGemNumber() + " "
							+ item.getExtraStats() + " "
							+ item.getUnknown1() + " "
							+ item.getDurability() + " "
							+ item.getType().getMaxDurability() + " "
							+ itemQuantity;
			}
			break;
			
		case STASH_GET:
			if(args.length>0){
				List<int[]> itemList = (List<int[]>)args[0];
				int itemTypeId = (Integer)args[1];
				int inventoryTab = (Integer)args[2];
				int unknown2 = (Integer)args[3];
				int slot = (Integer)args[4];
				int itemQuantity = (Integer)args[5];
				int unknown1 = 1;

				String packet = "stash_get "
						+ unknown1 + " "
						+ itemTypeId + " "
						+ inventoryTab + " "
						+ unknown2 + " "
						+ slot + " "
						+ itemQuantity;
				
				for(int[] itemData : itemList){
					packet += " " + itemData[0] + " "
								+ itemData[1] + " "
								+ itemData[2];
				}
				
				return packet;
			}
			break;
		case STASH_PUT:
			if(args.length>1){
				//stash_put [?] [TypeId] [InvTab] [StashTab] [StashPos] [ItemAmmount] [InvPosX] [InvPosY]
				int itemTypeId = (Integer)args[0];
				int invTab = (Integer)args[1];
				int stashTab = (Integer)args[2];
				int stashPos = (Integer)args[3];
				int itemAmmount = (Integer)args[4];
				int[] itemsData = (int[])args[5];
				int index = 3;
				
				String packet = "stash_put 1 "
							+ itemTypeId + " "
							+ invTab + " "
							+ stashTab + " "
							+ stashPos + " "
							+ itemAmmount;
							
				while(index < itemsData.length){
					packet += " " + itemsData[index++];
				}
				
				return packet;
			}
			break;
			
		case STASH_END:
			return "stash_end";
			
		case INVEN:
			if(args.length > 0){
				InventoryItem invItem = (InventoryItem)args[0];
				int version = (Integer)args[1];
				Item<?> item = invItem.getItem();
				
				return "inven " + invItem.getPosition().getTab() + " "
				+ item.getEntityId() + " "	+ item.getType().getTypeId() + " " + invItem.getPosition().getPosX() + " "
				+ invItem.getPosition().getPosY() + " " + item.getGemNumber() + " "	+ item.getExtraStats() + " "
				+ item.getUnknown1() + (version >= 2000 ? " " + item.getUnknown2() + " " + item.getUnknown3() : "");
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
				
				return "kill " +getObjectType(target)+ " " + target.getEntityId() + "\n";
				
			}
			break;
			
		case WEARING:
			if(args.length > 0){
				Equipment eq = (Equipment)args[0];
				int version = (Integer)args[1];
				
				/* 
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
				 * 1524780 445 6 1 0 0 0 0 0        [Necklace] 6
				 * -1 -1 0 0 0 0 0 0 0              [Ring]     7
				 * 1524781 455 1 1 0 0 0 0 0        [Bracelet] 8
				 * -1 -1 0 0 0 0 0 0 0              [Weapon]   9
				 * -1 -1 0 0 0 0 0 0 0              [?]        10 PET1 ?
				 * -1 -1 0 0 0 0 0 0 0              [?]        11 PET2 ?
				 * 
				 */
				
				return "wearing " + eq.getEntityId(Slot.HELMET) + " " + eq.getTypeId(Slot.HELMET) + " "
						+ eq.getGemNumber(Slot.HELMET) + " " + eq.getExtraStats(Slot.HELMET) + " "
						+ eq.getUnknown1(Slot.HELMET) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.HELMET) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.HELMET) + " " : "") 
						+ eq.getDurability(Slot.HELMET) + " " + eq.getMaxDurability(Slot.HELMET) + " "
						
						+ eq.getEntityId(Slot.CHEST) + " " + eq.getTypeId(Slot.CHEST) + " "
						+ eq.getGemNumber(Slot.CHEST) + " " + eq.getExtraStats(Slot.CHEST) + " "
						+ eq.getUnknown1(Slot.CHEST) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.CHEST) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.CHEST) + " " :"") 
						+ eq.getDurability(Slot.CHEST) + " " + eq.getMaxDurability(Slot.CHEST) + " "
						
						+ eq.getEntityId(Slot.PANTS) + " " + eq.getTypeId(Slot.PANTS) + " "
						+ eq.getGemNumber(Slot.PANTS) + " " + eq.getExtraStats(Slot.PANTS) + " "
						+ eq.getUnknown1(Slot.PANTS) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.PANTS) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.PANTS) + " " : "") 
						+ eq.getDurability(Slot.PANTS) + " " + eq.getMaxDurability(Slot.PANTS) + " "
						
						+ eq.getEntityId(Slot.SHOULDER) + " " + eq.getTypeId(Slot.SHOULDER) + " "
						+ eq.getGemNumber(Slot.SHOULDER) + " " + eq.getExtraStats(Slot.SHOULDER) + " "
						+ eq.getUnknown1(Slot.SHOULDER) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.SHOULDER) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.SHOULDER) + " " : "") 
						+ eq.getDurability(Slot.SHOULDER) + " "	+ eq.getMaxDurability(Slot.SHOULDER) + " "
						
						+ eq.getEntityId(Slot.BOOTS) + " " + eq.getTypeId(Slot.BOOTS)
						+ " " + eq.getGemNumber(Slot.BOOTS) + " " + eq.getExtraStats(Slot.BOOTS) + " "
						+ eq.getUnknown1(Slot.BOOTS) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.BOOTS) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.BOOTS) + " " : "") 
						+ eq.getDurability(Slot.BOOTS) + " " + eq.getMaxDurability(Slot.BOOTS) + " "
						
						+ eq.getEntityId(Slot.OFFHAND) + " " + eq.getTypeId(Slot.OFFHAND) + " "
						+ eq.getGemNumber(Slot.OFFHAND) + " " + eq.getExtraStats(Slot.OFFHAND) + " "
						+ eq.getUnknown1(Slot.OFFHAND) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.OFFHAND) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.OFFHAND) + " " : "") 
						+ eq.getDurability(Slot.OFFHAND) + " " + eq.getMaxDurability(Slot.OFFHAND) + " "
						
						+ eq.getEntityId(Slot.NECKLACE) + " " + eq.getTypeId(Slot.NECKLACE) + " "
						+ eq.getGemNumber(Slot.NECKLACE) + " " + eq.getExtraStats(Slot.NECKLACE) + " "
						+ eq.getUnknown1(Slot.NECKLACE) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.NECKLACE) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.NECKLACE) + " " : "") 
						+ eq.getDurability(Slot.NECKLACE) + " "	+ eq.getMaxDurability(Slot.NECKLACE) + " "
						
						+ eq.getEntityId(Slot.RING) + " " + eq.getTypeId(Slot.RING) + " "
						+ eq.getGemNumber(Slot.RING) + " " + eq.getExtraStats(Slot.RING) + " "
						+ eq.getUnknown1(Slot.RING) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.RING) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.RING) + " " : "") 
						+ eq.getDurability(Slot.RING) + " "	+ eq.getMaxDurability(Slot.RING) + " "
						
						+ eq.getEntityId(Slot.BRACELET)	+ " " + eq.getTypeId(Slot.BRACELET) + " "
						+ eq.getGemNumber(Slot.BRACELET) + " " + eq.getExtraStats(Slot.BRACELET) + " "
						+ eq.getUnknown1(Slot.BRACELET) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.BRACELET) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.BRACELET) + " " : "") 
						+ eq.getDurability(Slot.BRACELET) + " "	+ eq.getMaxDurability(Slot.BRACELET) + " "
						
						+ eq.getEntityId(Slot.MAINHAND) + " " + eq.getTypeId(Slot.MAINHAND) + " "
						+ eq.getGemNumber(Slot.MAINHAND) + " " + eq.getExtraStats(Slot.MAINHAND) + " "
						+ eq.getUnknown1(Slot.MAINHAND) + " " 
						//+ (version >= 2000 ? eq.getUnknown2(Slot.MAINHAND) + " " : "")
						//+ (version >= 2000 ? eq.getUnknown3(Slot.MAINHAND) + " " : "") 
						+ eq.getDurability(Slot.MAINHAND) + " "	+ eq.getMaxDurability(Slot.MAINHAND)
						
						+ " -1 -1 0 0 0 0 0 0 0"
						+ " -1 -1 0 0 0 0 0 0 0";
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
			
		case K:
			if(args.length > 0){
				int isActivated = (Integer) args[0];
				LivingObject livingObject = (LivingObject) args[1];
				int typeId = (Integer) args[2];
				
				return "k "+isActivated+" "+getObjectType(livingObject)+" "+livingObject.getEntityId()+" "+typeId;
			}
			break;
			
		case ICHANGE:
			if(args.length > 0){
				Item<?> oldItem = (Item<?>) args[0];
				Item<?> newItem = (Item<?>) args[1];
				
				if(oldItem == null || newItem == null)
					return "ichange 0 0 0 0 0 0 0 0";
				else {
					return "ichange "
						+ oldItem.getEntityId() + " "
						+ newItem.getEntityId() + " "
						+ newItem.getType().getTypeId() + " " 
						+ newItem.getGemNumber() + " "
						+ newItem.getExtraStats() + " 0 0 0";
				}
			}
			break;
			
		case CHIP_EXCHANGE:
			if(args.length > 0){
				int gemTraderType = (Integer) args[0];
				String betResult = (String) args[1];
				Item<?> item = (Item<?>) args[2];
				String serverBet = ((String) args[3]);
				
				if(item == null)
					return "chip_exchange "+gemTraderType+" ok "+betResult+"-1 "+serverBet;
				else
					return "chip_exchange "+gemTraderType+" ok "+betResult+""
						+item.getType().getTypeId()+" "+serverBet+""+item.getEntityId();
			}
			break;
		
		case SKY:
			if(args.length == 2){
				Player player = (Player) args[0];
				int state = (Integer) args[1];
				
				return "sky "+player.getEntityId()+" "+state;
			}
			break;
				
		case UPDATE_ITEM:
			if(args.length > 0){
				Item<?> item = (Item<?>) args[0];
				int upgraderesult = (Integer) args[1];
				
				return "update_item " + item.getEntityId() +" "+ upgraderesult + " "
						+ item.getGemNumber() + " "
						+ item.getExtraStats();
			}
			break;
		case USQ:
			if(args.length > 0){
				String type = (String) args[0];
				int quickSlotPosition = (Integer) args[1];
				int equipmentPosition = (Integer) args[2];
				Item<?> item = (Item<?>) args[3];
				int equipmentGemNumber = item.getGemNumber();
				int equipmentExtraStatus = item.getExtraStats();
				
				return "usq " + type + " " + quickSlotPosition +" "
						+ equipmentPosition + " "
						+ equipmentGemNumber + " "
						+ equipmentExtraStatus;
			}
			break;
		case UQ_ITEM:
			if(args.length > 0){
					int updateResult = (Integer) args[0]; //need confirmation about this
					int quickSlotPosition = (Integer) args[1];
					int itemEntityId = (Integer) args[2];
					
					String serverPacket = "uq_item " + updateResult + " " + quickSlotPosition
							+ " " + itemEntityId;

				if (args.length == 4) {
					
					int unknown = (Integer) args[3];

					serverPacket += " " + unknown;
				}
				if (args.length == 6) {
					int gemNumber = (Integer) args[3];
					int extraStats = (Integer) args[4];
					int unknown = (Integer) args[5];

					serverPacket += " " + gemNumber + " " + extraStats + " " + unknown;
				}
				return serverPacket;
			}
			break;
		case MT_ITEM:
			if(args.length > 0){
				int updateResult = (Integer) args[0]; 
				int quickSlotPosition = (Integer) args[1];
				int itemEntityId = (Integer) args[2];
				int unknown = (Integer) args[2];

				return "mt_item " + updateResult + " " + quickSlotPosition + " "
						+ itemEntityId + " " + unknown;
			}
			break;
		case AV:
			if(args.length > 0){
				LivingObject victim = (LivingObject) args[0];
				int isCritical = (Integer) args[1];

				return "av " + getObjectType(victim) + " " + victim.getEntityId() + " "
						+ victim.getPercentageHp() + " " + isCritical + " 0";
			}
			break;
		case PARTY_REQUEST:
			if(args.length > 0){
				Party party = (Party)args[0];
				Player leader = party.getLeader();
				String packet = "party request 0 "+leader.getEntityId()+" "+leader.getName();
				
				for(Player member : party.getMembers()){
					packet += " "+member.getEntityId()+" "+member.getName();
				}
				
				return packet += " "+party.getExpOption()+" "+party.getItemOption();
			}
			break;
		case PARTY_SECESSION:
			if(args.length > 0){
				int entityId = (Integer)args[0];
				
				return "party secession "+ entityId;
			}
			break;
		case PARTY_LIST:
			if(args.length > 0){
				int membersAmmount = (Integer)args[0];
				
				return "party list "+ membersAmmount;
			}
			break;
		case PARTY_MEMBER:
			if(args.length > 0){
				Player member = (Player)args[0];
				
				return "party member "+ member.getEntityId()+" "+member.getName();
			}
			break;
		case PARTY_INFO:
			if(args.length > 0){
				Player member = (Player)args[0];
				
				return "party info "+ member.getEntityId()+" "+member.getHp()+" "+member.getMaxHp();
			}
			break;
		case PARTY_CHANGE:
			if(args.length > 0){
				int optionPosition = (Integer)args[0];
				int optionValue = (Integer)args[1];
				
				return "party change "+optionPosition+" "+optionValue;
			}
			break;
	
		case PSTATUS:
			if(args.length>1){
				int id = (Integer)args[0];
				long arg1 = (Long)args[1];
				long arg2 = (Long)args[2];
				int arg3 = (Integer)args[3];
				
				return "pstatus " + id + " " + arg1 + " " + arg2 + " " + arg3;
			}
			break;
		case MYPET:
			if(args.length == 1){
				if(((String)args[0]).equals("del")){
					return "mypet del";
				}
			} else if(args.length == 2){
				Player player = (Player)args[0];
				Pet pet = (Pet)args[1];
				PetEquipment equipment = pet.getEquipment();
				
				return "mypet " 
						+ pet.getEntityId() + " " 
						+ pet.getName() + " " 
						+ (player.getClient().getVersion()>=2000 ? "0 " : "") 
						+ pet.getPosition().getX() + " "
						+ pet.getPosition().getY() + " " 
						+ (pet.getPosition().getRotation()*1000)/1000 + " "
						+ equipment.getTypeId(PetSlot.HORN) + " "
						+ equipment.getTypeId(PetSlot.HEAD) + " "
						+ equipment.getTypeId(PetSlot.BODY) + " "
						+ equipment.getTypeId(PetSlot.WING) + " "
						+ equipment.getTypeId(PetSlot.FOOT) + " "
						+ equipment.getTypeId(PetSlot.TAIL) + " "
						+ pet.getHp() + " "
						+ pet.getMaxHp();
				}
			break;
		case P_KEEP:
			if(args.length == 2){
				String pKeepType = (String)args[0];
				Pet pet = (Pet)args[1];
				
				if(pKeepType.equals("fail")){
					int timeRemain = pet.getBreederTimer() + 60; //we add 60 to have the correct minutes value
					return "p_keep fail " + timeRemain;
				} else if(pKeepType.equals("info")){
					//p_keep info [PetName] [isStored] [Level] [Stamina] [Loyalty] [Satiety] [LimeCharge] [TimeLeft(seconds)]
					return "p_keep info " + pet.getName() + " "
										  + (pet.getState() == 2 ? 1 : 0) + " "
										  + pet.getLevel() + " "
										  + pet.getMaxHp() + " "
										  + pet.getLoyalty() + " "
										  + pet.getSatiety() + " "
										  + (pet.getState() == 2 ? 15000 : 0) +" 0";
				}
			}
			break;
		case IN_PET:
			if(args.length>0){
				
				Player player = (Player)args[0];
				boolean warping = (Boolean)args[1];
				Pet pet = player.getPet();
				PetEquipment equipment = pet.getEquipment();

				return (warping ? "ap " : "in ")
						+"p " + pet.getEntityId() + " "
						+ pet.getName() + " "
						+ (player.getClient().getVersion()>=2000 ? "0 " : "")
						+ pet.getPosition().getX() + " "
						+ pet.getPosition().getY() + " "
						+ pet.getPosition().getRotation() + " " 
						+ equipment.getTypeId(PetSlot.HORN) + " "
						+ equipment.getTypeId(PetSlot.HEAD) + " "
						+ equipment.getTypeId(PetSlot.BODY) + " "
						+ equipment.getTypeId(PetSlot.WING) + " "
						+ equipment.getTypeId(PetSlot.FOOT) + " "
						+ equipment.getTypeId(PetSlot.TAIL) + " 100 14 "
						+ player.getName() + " " +
						+ player.isMeta();
				
				//in p [PetEntityId] [PetName] 0 [PetPosX] [PetPosY] [PetRotation]
				//[PetHornType] [PetHeadType] [PetBodyType] [PetWingType] [PetFootType] [PetTailType]
				//100 14 [PlayerName] 0
			}
			break;
		case EXTRA:
			if(args.length==1){
				Item<?> item = (Item<?>)args[0];
				
				return "extra " + item.getEntityId() + " " + item.getType().getTypeId() + " " + item.getGemNumber() + " "
						+ item.getExtraStats() + " " + item.getUnknown1() + " " + item.getUnknown2() + " "
						+ item.getUnknown3();
			}
			break;
			
		case G_POS_START:
			if(args.length == 0){
				return "g_pos start";
			}
			break;
		case G_POS_BODY:
			if(args.length == 1){
				Player player = (Player)args[0];
				return "g_pos body "+player.getEntityId()+" "+player.getName()+" "+player.getPosition().getX()+" "+player.getPosition().getY();
			}
			break;
		case G_POS_END:
			if(args.length == 0){
				return "g_pos end";
			}
			break;
		case EXCH:
			if(args.length==1){
				String exchType = (String)args[0];	/*	TYPES:
													 *	cancel
													 *	trade
													 *	disable
													 */
				return "exch " + exchType;
			}
			break;
		case EXCH_ASK:
			if(args.length==1){
				Player source = (Player)args[0];
								
				return "exch_ask "+source.getName();
			}
			break;
		case EXCH_START:
			if(args.length==1){
				Player player = (Player)args[0];
								
				return "exch_start "+player.getName()+" "+player.getLevel();
			}
			break;
		case EXCH_INVEN_TO:
			if(args.length==1){
				ExchangeItem exchangeItem = (ExchangeItem)args[0];
				Item<?> item = exchangeItem.getItem();
								
				return "exch_inven_to " + item.getEntityId() + " "
										+ item.getType().getTypeId() + " "
										+ exchangeItem.getPosition().getPosX() + " "
										+ exchangeItem.getPosition().getPosY() + " "
										+ item.getGemNumber() +  " "
										+ item.getExtraStats()+ " "
										+ item.getUnknown1() + " "
										+ item.getDurability() + " "
										+ item.getType().getMaxDurability();
			}
			break;
		case EXCH_INVEN_FROM:
			if(args.length==1){
				ExchangeItem exchangeItem = (ExchangeItem)args[0];
								
				return "exch_inven_from " + exchangeItem.getPosition().getPosX() + " "
										  + exchangeItem.getPosition().getPosY();
			}
			break;
		case EXCH_MONEY:
			if(args.length==1){
				long money = (long)args[0];
								
				return "exch_money "+money;
			}
			break;
			
		default:			
			throw new UnsupportedOperationException();
		}
		throw new RuntimeException("Invalid parameters for "+packetType+" message");
	}
	
	private static String getObjectType(WorldObject object){
		if (object instanceof Player){
			return "c";
		}
		else if(object instanceof RoamingItem){
			return "i";			
		}
		else if (object instanceof Npc) {
			return "n";
		}
		else if (object instanceof Pet) {
			return "p";
		}
		
		throw new RuntimeException("Invalid Object: "+object);
	}

	public PacketFactory() {
		super();

	}

}
