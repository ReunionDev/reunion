package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.BitSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.events.map.ItemDropEvent;
import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.Player.Sex;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.SlayerWeapon;
import com.googlecode.reunion.jreunion.game.Weapon;
import com.googlecode.reunion.jreunion.server.Client.LoginType;
import com.googlecode.reunion.jreunion.server.Client.State;
import com.googlecode.reunion.jreunion.server.PacketFactory.Type;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class Command {

	private World world;

	public Command(World parent) {
		super();
		world = parent;
	}
	// Returns a bitset containing the values in bytes.
	// The byte-ordering of bytes must be big-endian which means the most significant bit is in element 0.
	public static BitSet fromByteArray(byte[] bytes) {
	    BitSet bits = new BitSet();
	    for (int i=0; i<bytes.length*8; i++) {
	        if ((bytes[bytes.length-i/8-1]&(1<<(i%8))) > 0) {
	            bits.set(i);
	        }
	    }
	    return bits;
	}

	// Returns a byte array of at least length 1.
	// The most significant bit in the result is guaranteed not to be a 1
	// (since BitSet does not support sign extension).
	// The byte-ordering of the result is big-endian which means the most significant bit is in element 0.
	// The bit at index 0 of the bit set is assumed to be the least significant bit.
	public static byte[] toByteArray(BitSet bits) {
	    byte[] bytes = new byte[bits.length()/8+1];
	    for (int i=0; i<bits.length(); i++) {
	        if (bits.get(i)) {
	            bytes[bytes.length-i/8-1] |= 1<<(i%8);
	        }
	    }
	    return bytes;
	}

	void authClient(Client client) {
		String username = client.getUsername();
		String password = client.getPassword();
		if(client.getVersion()==101&&client.getLoginType()!=LoginType.PLAY){
			
			byte key = 0x03;
			byte [] input = username.getBytes();
			byte [] output = new byte[input.length];
			for(int i = 0; i<input.length;i++){
				output[i]= (byte) ((byte) (input[i]^key)%256);				
			}
			username = new String(output);
			
			input = password.getBytes();
			output = new byte[input.length];
			for(int i = 0; i<input.length;i++){
				output[i]= (byte) ((byte) (input[i]^key)%256);				
			}
			password = new String(output);
			
			client.setUsername(username);
			client.setPassword(password);
		}
		
		
		
		
		int accountId = DatabaseUtils.getInstance().Auth(username, password);
		if (accountId == -1) {
			Logger.getLogger(Command.class).info("Invalid Login");
			client.sendPacket(Type.FAIL,"Username and password combination is invalid");
			//client.disconnect();
		} else {
			
			Logger.getLogger(Command.class).info("" + client + " authed as account(" + accountId + ")");
			client.setAccountId(accountId);
			
			java.util.Map<Socket,Client> clients = world.getClients();
			synchronized(clients){
				for(Client cl: clients.values()){
					if(cl.equals(client))
						continue;					
					if(cl.getAccountId()==client.getAccountId()){
						
						if(cl.getState()==State.CHAR_LIST) {
							client.sendPacket(Type.FAIL, "Only one client can use the charlist at the same time.");
							return;
						}									
					}	
				}
			}
			sendCharList(client);
		}

	}

	public void charIn(Player sessionOwner, Player player) {
		charIn(sessionOwner.getClient(), player, false);

	}

	/****** Manages the Char In ******/
	public void charIn(Sendable sendable, Player player, boolean warping) {
	
		
		sendable.sendPacket(Type.CHAR_IN, player, warping);
		// serverTell(player, "char in id "+ePlayer.getEntityId());
	}

	/****** Manages the Char Out ******/
	public void charOut(Sendable sendable, Player player) {

		sendable.sendPacket(Type.OUT_CHAR, player);
		// serverTell(player, "char out id "+enteringPlayer.getEntityId());
	}

	public void createChar(Client client, int slotNumber, String charName,
			Race race, Sex sex, int hair, int str, int intel, int dex, int con,
			int lea) {
		if (DatabaseUtils.getInstance().getCharNameFree(charName)) {
			DatabaseUtils.getInstance().createChar(client, slotNumber,
					charName, race, sex, hair, str, intel, dex, con, lea);
		}

	}

	public void delChar(int slotNumber, int accountId) {
		DatabaseUtils.getInstance().delChar(slotNumber, accountId);
	}

	// debug command
	public RoamingItem dropItem(Position position, Item item) {

		RoamingItem roamingItem = new RoamingItem(item);
		roamingItem.setPosition(position);
		
		DatabaseUtils.getInstance().saveItem(roamingItem);
		
		LocalMap map = position.getMap();
		
		map.fireEvent(ItemDropEvent.class, roamingItem);
		
		return roamingItem;
	
	}

	/****** teleport player to player2 position ******/
	public void GoToChar(Player player, String charName) {
		Client client = player.getClient();
		Player target = Server.getInstance().getWorld()
				.getPlayerManager().getPlayer(charName);
		GoToPos(player,target.getPosition());
	}
	
	
	public void GoToPos(Player player, Position position){
		
		Client client = player.getClient();
		
		SessionList<Session> exit = player.getInterested().getSessions();
		exit.exit(player);
		player.setPosition(position);
		
		SessionList<Session> entry = player.getPosition().getMap().GetSessions(position);
		
		entry.enter(player, false);		
		entry.sendPacket(Type.CHAR_IN, player, true);
		
		client.sendPacket(Type.GOTO, position);
		
	}

	/****** change map ******/
	public void GoToWorld(Player player, Map map, int unknown) {
		Client client = player.getClient();
		// jump 7024 5551 227505
		
		
		//Disband party
		client.sendPacket(Type.PARTY_DISBAND);
		
		// go_world 62.26.131.215 4001 0 0

		String packetData = "jump " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " " + player.getId()
				+ "\n";

		client.SendData(packetData);
		
		
		//TODO: Cross server implementation
		Server.getInstance().getWorld().getTeleportManager()
				.register(player, map);

		Session session = player.getSession();

		// flush the session
		try {
		session.empty();
		} catch (Exception e) {
			//TODO: Fix Teleporting
			Logger.getLogger(Command.class).warn("teleportbug",e);
		}
		
		client.sendPacket(Type.GO_WORLD, map, unknown);


	}

	public void itemIn(Player sessionOwner, RoamingItem roamingItem) {
		Item item = roamingItem.getItem();
		this.itemIn(sessionOwner, item.getId(), item.getType(),
				roamingItem.getPosition().getX(), roamingItem.getPosition()
						.getY(), roamingItem.getPosition().getZ(), roamingItem
						.getPosition().getRotation(), item.getGemNumber(), item
						.getExtraStats());

	}

	/****** Manages the Item In ******/
	public void itemIn(Player owner, int uniqueid, int itemtype, int posX,
			int posY, int posZ, double rotation, int gems, int special) {
		Client client = owner.getClient();

		String packetData = "in item " + uniqueid + " " + itemtype + " " + posX
				+ " " + posY + " " + posZ + " " + rotation + " " + gems + " "
				+ special + "\n";
		client.SendData(packetData);

		serverTell(owner.getClient(), "Item in itemid " + uniqueid + " type " + itemtype
				+ "");
		// S> in item [UniqueID] [TypeID] [XPos] [YPos] [ZPos] [Rotation]
		// [gemNumber] [Special]
	}

	public void itemOut(Sendable sendable, RoamingItem roamingItem) {
		this.itemOut(sendable, roamingItem.getItem());

	}

	/****** Manages the Item Out ******/
	public void itemOut(Sendable sendable, Item item) {

		
		sendable.sendPacket(Type.OUT_ITEM, item);				
		
		serverTell(sendable, "out: id  " + item.getId());

		// S> out item [UniqueID]
	}

	public Player loginChar(int slotNumber, int accountId, Client client) {
		
		Player player = DatabaseUtils.getInstance().loadChar(slotNumber,
				accountId, client);
		
		Socket socket = client.getSocket();
	
		// TODO: Fix hack and prevent teleport hack
		Map map = null;
		Logger.getLogger(Command.class).info(socket.getLocalSocketAddress());
		for (Map m : Server.getInstance().getWorld().getMaps()) {
			Logger.getLogger(Command.class).info(m.getAddress());
			if (m.getAddress().equals(socket.getLocalSocketAddress())) {
				map = m;
				break;
			}
		}

		
		if (map == null || !map.isLocal()) {
			Logger.getLogger(Command.class).error("Invalid Map: " + map);
			player.getClient().disconnect();
			return null;
		}
		player.getPosition().setMap((LocalMap)map);

		DatabaseUtils.getInstance().loadEquipment(player);
		Equipment eq = player.getEquipment();

		int eqHelmetType = -1, eqHelmetId = -1, eqHelmetGem = 0, eqHelmetExtra = 0;
		int eqArmorType = -1, eqArmorId = -1, eqArmorGem = 0, eqArmorExtra = 0;
		int eqPantsType = -1, eqPantsId = -1, eqPantsGem = 0, eqPantsExtra = 0;
		int eqShoulderMountType = -1, eqShoulderMountId = -1, eqShoulderMountGem = 0, eqShoulderMountExtra = 0;
		int eqBootsType = -1, eqBootsId = -1, eqBootsGem = 0, eqBootsExtra = 0;
		int eqShieldType = -1, eqShieldId = -1, eqShieldGem = 0, eqShieldExtra = 0;
		int eqRingType = -1, eqRingId = -1, eqRingGem = 0, eqRingExtra = 0;
		int eqNecklaceType = -1, eqNecklaceId = -1, eqNecklaceGem = 0, eqNecklaceExtra = 0;
		int eqBraceletType = -1, eqBraceletId = -1, eqBraceletGem = 0, eqBraceletExtra = 0;
		int eqWeaponType = -1, eqWeaponId = -1, eqWeaponGem = 0, eqWeaponExtra = 0;

		if (eq.getHelmet() != null) {
			eqHelmetType = eq.getHelmet().getType();
			eqHelmetId = eq.getHelmet().getId();
			eqHelmetGem = eq.getHelmet().getGemNumber();
			eqHelmetExtra = eq.getHelmet().getExtraStats();
		}
		if (eq.getArmor() != null) {
			eqArmorType = eq.getArmor().getType();
			eqArmorId = eq.getArmor().getId();
			eqArmorGem = eq.getArmor().getGemNumber();
			eqArmorExtra = eq.getArmor().getExtraStats();
		}
		if (eq.getPants() != null) {
			eqPantsType = eq.getPants().getType();
			eqPantsId = eq.getPants().getId();
			eqPantsGem = eq.getPants().getGemNumber();
			eqPantsExtra = eq.getPants().getExtraStats();
		}
		if (eq.getShoulderMount() != null) {
			eqShoulderMountType = eq.getShoulderMount().getType();
			eqShoulderMountId = eq.getShoulderMount().getId();
			eqShoulderMountGem = eq.getShoulderMount().getGemNumber();
			eqShoulderMountExtra = eq.getShoulderMount().getExtraStats();
		}
		if (eq.getBoots() != null) {
			eqBootsType = eq.getBoots().getType();
			eqBootsId = eq.getBoots().getId();
			eqBootsGem = eq.getBoots().getGemNumber();
			eqBootsExtra = eq.getBoots().getExtraStats();
		}
		if (eq.getOffHand() != null) {
			eqShieldType = eq.getOffHand().getType();
			eqShieldId = eq.getOffHand().getId();
			eqShieldGem = eq.getOffHand().getGemNumber();
			eqShieldExtra = eq.getOffHand().getExtraStats();
		}
		if (eq.getRing() != null) {
			eqRingType = eq.getRing().getType();
			eqRingId = eq.getRing().getId();
			eqRingGem = eq.getRing().getGemNumber();
			eqRingExtra = eq.getRing().getExtraStats();
		}
		if (eq.getNecklace() != null) {
			eqNecklaceType = eq.getRing().getType();
			eqNecklaceId = eq.getRing().getId();
			eqNecklaceGem = eq.getRing().getGemNumber();
			eqNecklaceExtra = eq.getRing().getExtraStats();
		}
		if (eq.getBracelet() != null) {
			eqBraceletType = eq.getBracelet().getType();
			eqBraceletId = eq.getBracelet().getId();
			eqBraceletGem = eq.getBracelet().getGemNumber();
			eqBraceletExtra = eq.getBracelet().getExtraStats();
		}
		if (eq.getMainHand() != null) {
			eqWeaponType = eq.getMainHand().getType();
			eqWeaponId = eq.getMainHand().getId();
			eqWeaponGem = eq.getMainHand().getGemNumber();
			eqWeaponExtra = eq.getMainHand().getExtraStats();
		}

		player.getCharSkill().loadSkillList(player.getRace());
		DatabaseUtils.getInstance().loadSkills(player);
		DatabaseUtils.getInstance().loadInventory(player);
		DatabaseUtils.getInstance().loadExchange(player);
		DatabaseUtils.getInstance().loadStash(client);
		DatabaseUtils.getInstance().loadQuickSlot(player);

		serverSay(player.getName() + " is logging in (ID: "
				+ player.getId() + ")\n");

		String packetData = "skilllevel_all";
		Iterator<Skill> skillIter = player.getCharSkill()
				.getSkillListIterator();
		while (skillIter.hasNext()) {
			Skill skill = skillIter.next();
			packetData = packetData + " " + skill.getId() + " "
					+ skill.getCurrLevel();
			// "skilllevel_all 1 25 2 25 17 0 18 25 19 0 31 0 37 0 38 0 39 0 40 25 41 0 60 0 61 0 71 0 75 0\n");
		}
		packetData = packetData + "\n";

		client.SendData(packetData);

		packetData = "a_idx " + client.getAccountId() + "\n";
		client.SendData(packetData);

		packetData = "a_idn " + client.getUsername() + "\n";
		client.SendData(packetData);

		packetData = "a_lev " + player.getAdminState() + "\n";
		client.SendData(packetData);

		packetData = "wearing " + eqHelmetId + " " + eqHelmetType + " "
				+ eqHelmetGem + " " + eqHelmetExtra + " " + eqArmorId + " "
				+ eqArmorType + " " + eqArmorGem + " " + eqArmorExtra + " "
				+ eqPantsId + " " + eqPantsType + " " + eqPantsGem + " "
				+ eqPantsExtra + " " + eqShoulderMountId + " "
				+ eqShoulderMountType + " " + eqShoulderMountGem + " "
				+ eqShoulderMountExtra + " " + eqBootsId + " " + eqBootsType
				+ " " + eqBootsGem + " " + eqBootsExtra + " " + eqShieldId
				+ " " + eqShieldType + " " + eqShieldGem + " " + eqShieldExtra
				+ " " + eqNecklaceId + " " + eqNecklaceType + " "
				+ eqNecklaceGem + " " + eqNecklaceExtra + " " + eqBraceletId
				+ " " + eqBraceletType + " " + eqBraceletGem + " "
				+ eqBraceletExtra + " " + eqRingId + " " + eqRingType + " "
				+ eqRingGem + " " + eqRingExtra + " " + eqWeaponId + " "
				+ eqWeaponType + " " + eqWeaponGem + " " + eqWeaponExtra + "\n";
		// wearing [Helm] [Armor] [Pants] [ShoulderMount] [Boots] [Shield]
		// [Necklace] [Bracelet] [Ring] [Weapon]
		client.SendData(packetData);

		player.loadInventory();
		player.loadExchange();
		player.loadQuickSlot();

		client.sendPacket(PacketFactory.Type.OK);

		return player;
	}

	/****** Manages the Mob In ******/
	public void mobIn(Player player, Mob mob, boolean spawn) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		int percentageHp = mob.getHp() * 100 / mob.getMaxHp();

		String packetData = "in npc " + mob.getId() + " " + mob.getType()
				+ " " + mob.getPosition().getX() + " "
				+ mob.getPosition().getY() + " 0 "
				+ mob.getPosition().getRotation() + " " + percentageHp + " "
				+ mob.getMutant() + " " + mob.getUnknown1() + " "
				+ mob.getNeoProgmare() + " 0 " + (spawn ? 1 : 0) + " "
				+ mob.getUnknown2() + "\n";
		// in npc [UniqueID] [type] [XPos] [YPos] [ZPos] [Rotation] [HP]
		// [MutantType] 0 [NeoProgmare] 0 0
		client.SendData(packetData);
	}

	/****** Manages the Mob Out ******/
	public void mobOut(Player player, Mob mob) {
		// if (player == null)
		// return;
		Client client = player.getClient();
		if (client == null) {
			return;
		}

		String packetData = "out npc " + mob.getId() + "\n";
		// S> out npc [UniqueID]
		client.SendData(packetData);
	}

	/****** player normal attacks ******/
	public void normalAttack(Player player, int uniqueId) {

		Client client = player.getClient();

		if (client == null) {
			return;
		}

		LivingObject livingObject = Server.getInstance().getWorld()
				.getMobManager().getMob(uniqueId);

		if (livingObject == null) {
			livingObject = Server.getInstance().getWorld()
					.getPlayerManager().getPlayer(uniqueId);

			if (livingObject == null) {
				return;
			}
		}

		player.meleeAttack(livingObject);

		int percentageHp = livingObject.getHp() * 100
				/ livingObject.getMaxHp();

		if (percentageHp == 0 && livingObject.getHp() > 0) {
			percentageHp = 1;
		}

		// if(percentageHp > 0 && percentageHp <= 1)
		// percentageHp = 1;
		String packetData = new String();

		if (livingObject instanceof Mob) {
			packetData = "attack_vital npc " + livingObject.getId() + " "
					+ percentageHp + " 0 0\n";
		} else {
			packetData = "attack_vital char " + livingObject.getId()
					+ " " + percentageHp + " 0 0\n";
		}
		// S> attack_vital npc [NpcID] [RemainHP%] 0 0
		client.SendData(packetData);
		// TODO: Fix attack
		/*
		 * if (player.getSession().getPlayerListSize() > 0) { Iterator<G_Player>
		 * playerIter = player.getSession() .getPlayerListIterator();
		 * 
		 * while (playerIter.hasNext()) { G_Player pl = playerIter.next();
		 * 
		 * client = S_Server.getInstance().getNetworkModule() .getClient(pl);
		 * 
		 * if (client == null) { continue; }
		 * 
		 * if (livingObject instanceof G_Mob) { packetData = "attack char " +
		 * player.getEntityId() + " npc " + uniqueId + " " + percentageHp +
		 * " 0 0 0 0\n"; } else { packetData = "attack char " +
		 * player.getEntityId() + " char " + uniqueId + " " + percentageHp +
		 * " 0 0 0 0\n"; }
		 * 
		 * // S> attack char [CharID] npc [NpcID] [RemainHP%] 0 0 0 0
		 * client.SendData( packetData); } }
		 */
	}

	/****** mob attacks player with normal attack ******/
	public void NpcAttackChar(Player player, Mob mob) { // mob attacks
															// player
		Client client = player.getClient();
		String packetData = "";
		int newHp = player.getHp();

		if (client == null) {
			return;
		}

		int dmg = mob.getDmg() - player.getDef();

		if (dmg > 0) {
			newHp = player.getHp() - dmg;
		}
		if (newHp < 0) {
			newHp = 0;
		}

		player.updateStatus(0, newHp, player.getMaxHp());

		double percentageHp = player.getHp() * 100 / player.getMaxHp();

		if (percentageHp > 0 && percentageHp < 1) {
			percentageHp = 1;
		}

		switch (mob.getAttackType()) {
		case 0: {
			packetData = "attack npc " + mob.getId() + " char "
					+ player.getId() + " " + (int) percentageHp + " "
					+ mob.getDmgType() + " 0 0 0\n";
			break;
		}
		case 1: {
			packetData = "attack npc " + mob.getId() + " char "
					+ player.getId() + " " + (int) percentageHp + " "
					+ mob.getDmgType() + " 0 0 0\n";
			break;
		}
		case 2: {
			packetData = "attack npc " + mob.getId() + " char "
					+ player.getId() + " " + (int) percentageHp + " "
					+ mob.getDmgType() + " 0 0 0\n";
			break;
		}
		default:
			break;
		}
		client.SendData(packetData);
		// S> attack npc [NpcID] char [CharID] [RemainCharHP%] 0 0 0 0
		// TODO: Fix attack from mob
		/*
		 * if (player.getSession().getPlayerListSize() > 0) { for (int i = 0; i
		 * < player.getSession().getPlayerListSize(); i++) { client =
		 * S_Server.getInstance().getNetworkModule()
		 * .getClient(player.getSession().getPlayer(i)); if (client == null) {
		 * return; } client.SendData( packetData); } }
		 */
	}

	/****** Manages the Npc In ******/
	public void npcIn(Player player, Npc npc) {

			String packetData = "in npc " + npc.getId() + " " + npc.getType()
				+ " " + npc.getPosition().getX() + " "
				+ npc.getPosition().getY() + " 0 "
				+ npc.getPosition().getRotation() + " 100 0 0 0 0 0 10\n";

		// in npc [UniqueID] [type] [XPos] [YPos] [ZPos] [Rotation] [HP]
		// [MutantType] 0 [NeoProgmare] 0 0
		player.getClient().SendData(packetData);
	}

	/****** Manages the Npc Out ******/
	public void npcOut(Player player, Npc npc) {
		// if (player == null)
		// return;
		Client client = player.getClient();
	
		String packetData = "out npc " + npc.getId() + "\n";
		// S> out npc [UniqueID]
		client.SendData(packetData);
	}

	/****** Manages the player wear Weapon ******/
	public void playerWeapon(Player player, int uniqueId) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		Logger.getLogger(Command.class).info(uniqueId);

		Item item = null;
		//TODO: FIX
		//item = (Item) ItemManager.getEntityManager().getEnt(uniqueId);
		if (item == null) {
			return;
		}
		Weapon weapon = new Weapon(item.getType());
		weapon.loadFromReference(item.getType());

		player.setMinDmg(weapon.getMinDamage());
		player.setMaxDmg(weapon.getMaxDamage());
		// C> pulse [SystemTime], [WeaponType], [WeaponUniqueID], [WeaponSpeed],
		// 120

	}

	void sendCharList(Client client) {

		client.SendData(DatabaseUtils.getInstance().getCharList(client));

		client.setState(Client.State.CHAR_LIST);
		return;
	}

	void sendFail(Client client) {
		client.SendData("fail\n");
		return;
	}

	void sendSuccess(Client client) {
		client.SendData("success\n");
		return;
	}

	public void serverSay(String text) {
		
		world.sendPacket(Type.SAY,text);

	}
	
	public void playerSay(Player player, String text){
		
		boolean admin = player.getAdminState()==255;
		String name = player.getName();		
		if(admin)
		{
			name = "<GM>"+name;
		}
		world.sendPacket(Type.SAY, text, player, name, admin);
		
	}

	public void serverTell(Sendable sendable, String text) {
		
		sendable.sendPacket(Type.SAY, text);
	}

	/****** player1 attacks player2 with Sub Attack ******/
	public void subAttackChar(Player player1, int uniqueId) {
		Client client = player1.getClient();
		// G_Player player2 =
		// (G_Player)G_EntityManager.getEntityManager().getEnt(uniqueId);

		String packetData = "attack_vital char " + player1.getId()
				+ " 100 0 0\n";
		client.SendData(packetData);

		// TODO: fix sub attack
		/*
		 * if (player1.getSession().getPlayerListSize() > 0) { for (int i = 0; i
		 * < player1.getSession().getPlayerListSize(); i++) { client =
		 * S_Server.getInstance().getNetworkModule()
		 * .getClient(player1.getSession().getPlayer(i)); if (client == null) {
		 * return; } packetData = "effect 40 char " + player1.getEntityId() +
		 * " char " + uniqueId + " 100 0 0\n"; client.SendData( packetData); } }
		 */

	}

	/****** player attacks mob with Sub Attack ******/
	public void subAttackNpc(Player player, int uniqueId) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}

		Mob mob = Server.getInstance().getWorld().getMobManager()
				.getMob(uniqueId);
		Item item = player.getEquipment().getShoulderMount();
		SlayerWeapon spWeapon = new SlayerWeapon(item.getType());

		item.setExtraStats(spWeapon.getExtraStats() - 20);
		spWeapon.loadFromReference(item.getType());
		spWeapon.setExtraStats(item.getExtraStats());

		double slayerDmg = 0;

		while (slayerDmg < spWeapon.getSpecialWeaponMinDamage()
				|| slayerDmg > spWeapon.getSpecialWeaponMaxDamage()) {
			slayerDmg = Math.random() * 100
					+ spWeapon.getSpecialWeaponMinDamage();
		}

		Logger.getLogger(Command.class).info("Skill Level: "
				+ player.getCharSkill().getSkill(40).getCurrLevel() + "\n");

		// Max normal attack damage * memory of the slayer * % skill (40)
		// increase +
		// slayer attack damage * % skill (40) increase * 1 (if no demolition
		// and *
		// 1.8 if demolition hit occurs)

		double dmg = player.getBestAttack()
				* spWeapon.getMemoryDmg()
				/ 100
				* (player.getCharSkill().getSkill(40).getCurrLevel() * 20 / 100)
				+ slayerDmg
				* (player.getCharSkill().getSkill(40).getCurrLevel() * 20 / 100)
				* 1;

		player.clearAttackQueue();

		int newHp = mob.getHp() - (int) dmg;

		if (newHp <= 0) {
			mob.setHp(0);
			serverSay("Experience: " + mob.getExp() + " Lime: " + mob.getLime());
			player.updateStatus(12, player.getLvlUpExp() - mob.getExp(), 0);
			player.updateStatus(11, mob.getExp(), 0);
			player.updateStatus(10, mob.getLime(), 0);
			// S_Server.getInstance().getWorldModule().getMobManager().removeMob(mob);

			if (mob.getType() == 324) {
				
				Item item2 = ItemFactory.create(1054);
				item2.setExtraStats(1080);
				item2.setGemNumber(0);

				player.getInventory().addItem(item2);
				//player.pickupItem(item);
				player.getQuest().questEnd(player, 669);
				player.getQuest().questEff(player);
			}
		} else {
			mob.setHp(newHp);
		}

		double percentageHp = mob.getHp() * 100 / mob.getMaxHp();

		if (percentageHp > 0 && percentageHp <= 1) {
			percentageHp = 1;
		}

		String packetData = "sav npc " + uniqueId + " " + percentageHp
				+ " 1 0 " + item.getExtraStats() + "\n";
		client.SendData(packetData);
		// TODO: player attacks mob with Sub Attack
		/*
		 * if (player.getSession().getPlayerListSize() > 0) { for (int i = 0; i
		 * < player.getSession().getPlayerListSize(); i++) { client =
		 * S_Server.getInstance().getNetworkModule()
		 * .getClient(player.getSession().getPlayer(i)); if (client == null) {
		 * continue; } packetData = "sav npc " + uniqueId + " " + percentageHp +
		 * " 1 0 " + item.getExtraStats() + "\n"; client.SendData( packetData);
		 * } }
		 */

		if (percentageHp == 0) {
			Server.getInstance().getWorld().getMobManager()
					.removeMob(mob);
		}
	}

}