package com.googlecode.reunion.jreunion.server;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Iterator;

import com.googlecode.reunion.jreunion.game.G_EntityManager;
import com.googlecode.reunion.jreunion.game.G_Equipment;
import com.googlecode.reunion.jreunion.game.G_Item;
import com.googlecode.reunion.jreunion.game.G_LivingObject;
import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.game.G_RoamingItem;
import com.googlecode.reunion.jreunion.game.G_Skill;
import com.googlecode.reunion.jreunion.game.G_SlayerWeapon;
import com.googlecode.reunion.jreunion.game.G_Weapon;
import com.googlecode.reunion.jreunion.server.Enums.S_LoginType;
import com.googlecode.reunion.jreunion.server.PacketFactory.S_PacketType;

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

	void authClient(Client client, String username, String password) {
		int accountId = DatabaseUtils.getInstance().Auth(username, password);
		if (accountId == -1) {
			System.out.println("Invalid Login");
			// S_Server.getInstance().networkModule.Disconnect(networkId);
			client.SendData("fail Username and password combination is invalid\n");
		} else {

			System.out.println("" + client + " authed as account(" + accountId
					+ ")");
			client.setAccountId(accountId);
			sendCharList(client, accountId);
		}

	}

	public void charIn(G_Player sessionOwner, G_Player player) {
		charIn(sessionOwner, player, false);

	}

	/****** Manages the Char In ******/
	public void charIn(G_Player player1, G_Player player2, boolean warping) {
		int combat = 0;

		if (player1 == null || player2 == null) {
			return;
		}

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player1);

		if (client == null) {
			return;
		}

		if (player2.getCombatMode() == false) {
			combat = 0;
		} else {
			combat = 1;
		}

		G_Equipment eq = DatabaseUtils.getInstance().loadEquipment(
				player2.getEntityId());

		int eqHelmet = -1;
		int eqArmor = -1;
		int eqPants = -1;
		int eqShoulderMount = -1;
		int eqBoots = -1;
		int eqFirstHand = -1;
		int eqSecondHand = -1;
		if (eq.getHelmet() != null) {
			eqHelmet = eq.getHelmet().getType();
		}
		if (eq.getArmor() != null) {
			eqArmor = eq.getArmor().getType();
		}
		if (eq.getPants() != null) {
			eqPants = eq.getPants().getType();
		}
		if (eq.getShoulderMount() != null) {
			eqShoulderMount = eq.getShoulderMount().getType();
		}
		if (eq.getBoots() != null) {
			eqBoots = eq.getBoots().getType();
		}
		if (eq.getMainHand() != null) {
			eqFirstHand = eq.getMainHand().getType();
		}
		if (eq.getOffHand() != null) {
			eqSecondHand = eq.getOffHand().getType();
		}

		int percentageHp = player2.getCurrHp() * 100 / player2.getMaxHp();
		String packetData = new String();

		if (!warping) {
			packetData = "in ";
		} else {
			packetData = "appear ";
		}

		packetData += "char " + player2.getEntityId() + " " + player2.getName()
				+ " " + player2.getRace() + " " + player2.getSex() + " "
				+ player2.getHairStyle() + " " + player2.getPosition().getX()
				+ " " + player2.getPosition().getY() + " "
				+ player2.getPosition().getZ() + " "
				+ player2.getPosition().getRotation() + " " + eqHelmet + " "
				+ eqArmor + " " + eqPants + " " + eqShoulderMount + " "
				+ eqBoots + " " + eqSecondHand + " " + eqFirstHand + " "
				+ percentageHp + " " + combat + " 0 0 0 0 0 0\n";
		// S> in char [UniqueID] [Name] [Race] [Gender] [HairStyle] [XPos]
		// [YPos] [ZPos] [Rotation] [Helm] [Armor] [Pants] [ShoulderMount]
		// [Boots] [Shield] [Weapon] [Hp%] [CombatMode] 0 0 0 [Boosted] [PKMode]
		// 0 [Guild]
		// [MemberType] 1
		client.SendData(packetData);
		// serverTell(player, "char in id "+ePlayer.getEntityId());
	}

	/****** Manages the Char Out ******/
	public void charOut(G_Player player1, G_Player player2) {
		if (player1 == null) {
			return;
		}
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player1);
		if (client == null) {
			return;
		}

		String packetData = "out char " + player2.getEntityId() + "\n";
		client.SendData(packetData);
		// serverTell(player, "char out id "+enteringPlayer.getEntityId());
	}

	public void createChar(Client client, int slotNumber, String charName,
			int race, int sex, int hair, int str, int intel, int dex, int con,
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
	public void dropItem(G_Player player, int itemtype, int posX, int posY,
			int posZ, int rotation, int gems, int special) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_Item item = new G_Item(itemtype);
		item.setGemNumber(gems);
		item.setExtraStats(special);
		G_EntityManager.getEntityManager().createEntity(item);
		DatabaseUtils.getInstance().updateItemInfo(item);
		DatabaseUtils.getInstance().addItem(item);

		// G_Item item = S_ItemFactory.createItem(itemtype);

		String packetData = "drop " + item.getEntityId() + " " + itemtype + " "
				+ posX + " " + posY + " " + posZ + " 0.0 " + gems + " "
				+ special + "\n";

		client.SendData(packetData);

		G_RoamingItem roamingItem = new G_RoamingItem(item);
		player.getSession().enter(roamingItem);

		// send to all near //TODO: fix
		// client.SendData( packetData);
		/*
		 * if (player.getSession().getPlayerListSize() > 0) { Iterator<G_Player>
		 * iter = world.getPlayerManager() .getPlayerListIterator();
		 * 
		 * while (iter.hasNext()) { G_Player pl = iter.next();
		 * 
		 * client = S_Server.getInstance().getNetworkModule() .getClient(pl);
		 * 
		 * if (client == null) { continue; }
		 * 
		 * 
		 * } }
		 */

		// S> drop [ItemID] [ItemType] [PosX] [PosY] 0 0.0 0 0
	}

	/****** teleport player to player2 position ******/
	public void GoToChar(G_Player player, String charName) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);
		G_Player player2 = Server.getInstance().getWorldModule()
				.getPlayerManager().getPlayer(charName);

		if (client == null) {
			return;
		}

		player.getPosition().setX(player2.getPosition().getX() + 10);
		player.getPosition().setY(player2.getPosition().getY() + 10);
		player.getPosition().setZ(player2.getPosition().getZ());

		String packetData = "goto " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " "
				+ player.getPosition().getZ() + " "
				+ player.getPosition().getRotation() + "\n";
		client.SendData(packetData);
	}

	/****** teleport player to position (posX,posY) in the current map ******/
	public void GoToPos(G_Player player, int posX, int posY) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		player.getPosition().setX(posX);
		player.getPosition().setY(posY);

		String packetData = "goto " + posX + " " + posY + " 0 "
				+ player.getPosition().getRotation() + "\n";
		client.SendData(packetData);

		Iterator<Session> sessionIter = Server.getInstance()
				.getWorldModule().getSessionManager().getSessionListIterator();

		while (sessionIter.hasNext()) {
			Session session = sessionIter.next();
			G_Player pl = session.getOwner();

			if (session.contains(player)) {
				session.enter(player);
				player.getSession().exit(pl);
			}

			if (pl.getPosition().getMap() != player.getPosition().getMap()
					|| pl == player) {
				continue;
			}

			client = Server.getInstance().getNetworkModule().getClient(pl);

			if (client == null) {
				continue;
			}

			int distance = pl.getDistance(player);

			if (distance <= session.getOwner().getSessionRadius()) {
				session.enter(player); // TODO: fix warp
				player.getSession().enter(pl);
			}
		}
	}

	/****** change map ******/
	public void GoWorld(G_Player player, int mapId, int unknown) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);
		Map map = Server.getInstance().getWorldModule().getMap(mapId);
		// jump 7024 5551 227505
		// party disband
		// go_world 62.26.131.215 4001 0 0

		String packetData = "jump " + player.getPosition().getX() + " "
				+ player.getPosition().getY() + " " + player.getEntityId()
				+ "\n";

		client.SendData(packetData);

		Server.getInstance().getWorldModule().getTeleportManager()
				.register(player, map);

		Session session = player.getSession();

		// flush the session
		session.empty();

		InetSocketAddress address = map.getAddress();

		packetData = "go_world " + address.getAddress().getHostAddress() + " "
				+ address.getPort() + " " + mapId + " " + unknown + "\n";

		client.SendData(packetData);

		// client.setState(7);
		// System.out.print("Removing Player...\n");
		// charLogout(player);
		// System.out.print("Removing Client...\n");
		// S_Server.getInstance().getNetworkModule().clientList.remove(client);
	}

	public void itemIn(G_Player sessionOwner, G_RoamingItem roamingItem) {
		G_Item item = roamingItem.getItem();
		this.itemIn(sessionOwner, item.getUniqueId(), item.getType(),
				roamingItem.getPosition().getX(), roamingItem.getPosition()
						.getY(), roamingItem.getPosition().getZ(), roamingItem
						.getPosition().getRotation(), item.getGemNumber(), item
						.getExtraStats());

	}

	/****** Manages the Item In ******/
	public void itemIn(G_Player owner, int uniqueid, int itemtype, int posX,
			int posY, int posZ, double rotation, int gems, int special) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(owner);

		String packetData = "in item " + uniqueid + " " + itemtype + " " + posX
				+ " " + posY + " " + posZ + " " + rotation + " " + gems + " "
				+ special + "\n";
		client.SendData(packetData);

		serverTell(owner, "Item in itemid " + uniqueid + " type " + itemtype
				+ "");
		// S> in item [UniqueID] [TypeID] [XPos] [YPos] [ZPos] [Rotation]
		// [gemNumber] [Special]
	}

	public void itemOut(G_Player sessionOwner, G_RoamingItem roamingItem) {
		this.itemOut(sessionOwner, roamingItem.getItem().getUniqueId());

	}

	/****** Manages the Item Out ******/
	public void itemOut(G_Player player, int uniqueid) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		String packetData = "out item " + uniqueid + "\n";

		client.SendData(packetData);
		serverTell(player, "out: id  " + uniqueid);

		// S> out item [UniqueID]
	}

	public G_Player loginChar(int slotNumber, int accountId, Client client) {
		G_Player player = DatabaseUtils.getInstance().loadChar(slotNumber,
				accountId, client);
		
		if (client == null) {
			return null;
		}
		Socket socket = client.getSocket();
		/*
		 * S_Map map =
		 * S_Server.getInstance().getWorldModule().getTeleportManager
		 * ().getDestination(player); if (map==null) {
		 * if(client.getLoginType()==S_LoginType.PLAY) { System.err.println(
		 * "Got a play login while no teleport for this player was pending"
		 * +client.getPlayer()); client.disconnect();
		 * 
		 * return null; } int mapId =
		 * Integer.parseInt(S_Reference.getInstance().
		 * getServerReference().getItem("Server").getMemberValue("DefaultMap"));
		 * map = S_Server.getInstance().getWorldModule().getMap(mapId);
		 * System.out.println("Loading default map "+ map); }
		 */
		// TODO: Fix hack and prevent teleport hack
		Map map = null;
		System.out.println(socket.getLocalSocketAddress());
		for (Map m : Server.getInstance().getWorldModule().getMaps()) {
			System.out.println(m.getAddress());
			if (m.getAddress().equals(socket.getLocalSocketAddress())) {
				map = m;
				break;
			}
		}

		// TODO: do we really need this here?
		if (map == null || !map.isLocal()) {
			System.err.println("Invalid Map: " + map);
			return null;
		}

		

		DatabaseUtils.getInstance().loadEquipment(player);
		G_Equipment eq = player.getEquipment();

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
			eqHelmetId = eq.getHelmet().getEntityId();
			eqHelmetGem = eq.getHelmet().getGemNumber();
			eqHelmetExtra = eq.getHelmet().getExtraStats();
		}
		if (eq.getArmor() != null) {
			eqArmorType = eq.getArmor().getType();
			eqArmorId = eq.getArmor().getEntityId();
			eqArmorGem = eq.getArmor().getGemNumber();
			eqArmorExtra = eq.getArmor().getExtraStats();
		}
		if (eq.getPants() != null) {
			eqPantsType = eq.getPants().getType();
			eqPantsId = eq.getPants().getEntityId();
			eqPantsGem = eq.getPants().getGemNumber();
			eqPantsExtra = eq.getPants().getExtraStats();
		}
		if (eq.getShoulderMount() != null) {
			eqShoulderMountType = eq.getShoulderMount().getType();
			eqShoulderMountId = eq.getShoulderMount().getEntityId();
			eqShoulderMountGem = eq.getShoulderMount().getGemNumber();
			eqShoulderMountExtra = eq.getShoulderMount().getExtraStats();
		}
		if (eq.getBoots() != null) {
			eqBootsType = eq.getBoots().getType();
			eqBootsId = eq.getBoots().getEntityId();
			eqBootsGem = eq.getBoots().getGemNumber();
			eqBootsExtra = eq.getBoots().getExtraStats();
		}
		if (eq.getOffHand() != null) {
			eqShieldType = eq.getOffHand().getType();
			eqShieldId = eq.getOffHand().getEntityId();
			eqShieldGem = eq.getOffHand().getGemNumber();
			eqShieldExtra = eq.getOffHand().getExtraStats();
		}
		if (eq.getRing() != null) {
			eqRingType = eq.getRing().getType();
			eqRingId = eq.getRing().getEntityId();
			eqRingGem = eq.getRing().getGemNumber();
			eqRingExtra = eq.getRing().getExtraStats();
		}
		if (eq.getNecklace() != null) {
			eqNecklaceType = eq.getRing().getType();
			eqNecklaceId = eq.getRing().getEntityId();
			eqNecklaceGem = eq.getRing().getGemNumber();
			eqNecklaceExtra = eq.getRing().getExtraStats();
		}
		if (eq.getBracelet() != null) {
			eqBraceletType = eq.getBracelet().getType();
			eqBraceletId = eq.getBracelet().getEntityId();
			eqBraceletGem = eq.getBracelet().getGemNumber();
			eqBraceletExtra = eq.getBracelet().getExtraStats();
		}
		if (eq.getMainHand() != null) {
			eqWeaponType = eq.getMainHand().getType();
			eqWeaponId = eq.getMainHand().getEntityId();
			eqWeaponGem = eq.getMainHand().getGemNumber();
			eqWeaponExtra = eq.getMainHand().getExtraStats();
		}

		player.getCharSkill().loadSkillList(player.getRace());
		DatabaseUtils.getInstance().loadSkills(player);
		DatabaseUtils.getInstance().loadInventory(player);
		DatabaseUtils.getInstance().loadExchange(player);
		DatabaseUtils.getInstance().loadStash(client);
		DatabaseUtils.getInstance().loadQuickSlot(player);

		world.getSessionManager().newSession(player);

		player.getPosition().setMap(map);

		world.getPlayerManager().addPlayer(player);

		serverSay(player.getName() + " is logging in (ID: "
				+ player.getEntityId() + ")\n");

		String packetData = "skilllevel_all";
		Iterator<G_Skill> skillIter = player.getCharSkill()
				.getSkillListIterator();
		while (skillIter.hasNext()) {
			G_Skill skill = skillIter.next();
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

		client.SendPacket(S_PacketType.OK);

		return player;
	}

	/****** Manages the Mob In ******/
	public void mobIn(G_Player player, G_Mob mob, boolean spawn) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		int percentageHp = mob.getCurrHp() * 100 / mob.getMaxHp();

		String packetData = "in npc " + mob.getEntityId() + " " + mob.getType()
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
	public void mobOut(G_Player player, G_Mob mob) {
		// if (player == null)
		// return;
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);
		if (client == null) {
			return;
		}

		String packetData = "out npc " + mob.getEntityId() + "\n";
		// S> out npc [UniqueID]
		client.SendData(packetData);
	}

	/****** player normal attacks ******/
	public void normalAttack(G_Player player, int uniqueId) {

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_LivingObject livingObject = Server.getInstance().getWorldModule()
				.getMobManager().getMob(uniqueId);

		if (livingObject == null) {
			livingObject = Server.getInstance().getWorldModule()
					.getPlayerManager().getPlayer(uniqueId);

			if (livingObject == null) {
				return;
			}
		}

		player.meleeAttack(livingObject);

		int percentageHp = livingObject.getCurrHp() * 100
				/ livingObject.getMaxHp();

		if (percentageHp == 0 && livingObject.getCurrHp() > 0) {
			percentageHp = 1;
		}

		// if(percentageHp > 0 && percentageHp <= 1)
		// percentageHp = 1;
		String packetData = new String();

		if (livingObject instanceof G_Mob) {
			packetData = "attack_vital npc " + livingObject.getEntityId() + " "
					+ percentageHp + " 0 0\n";
		} else {
			packetData = "attack_vital char " + livingObject.getEntityId()
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
	public void NpcAttackChar(G_Player player, G_Mob mob) { // mob attacks
															// player
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);
		String packetData = "";
		int newHp = player.getCurrHp();

		if (client == null) {
			return;
		}

		int dmg = mob.getDmg() - player.getDef();

		if (dmg > 0) {
			newHp = player.getCurrHp() - dmg;
		}
		if (newHp < 0) {
			newHp = 0;
		}

		player.updateStatus(0, newHp, player.getMaxHp());

		double percentageHp = player.getCurrHp() * 100 / player.getMaxHp();

		if (percentageHp > 0 && percentageHp < 1) {
			percentageHp = 1;
		}

		switch (mob.getAttackType()) {
		case 0: {
			packetData = "attack npc " + mob.getEntityId() + " char "
					+ player.getEntityId() + " " + (int) percentageHp + " "
					+ mob.getDmgType() + " 0 0 0\n";
			break;
		}
		case 1: {
			packetData = "attack npc " + mob.getEntityId() + " char "
					+ player.getEntityId() + " " + (int) percentageHp + " "
					+ mob.getDmgType() + " 0 0 0\n";
			break;
		}
		case 2: {
			packetData = "attack npc " + mob.getEntityId() + " char "
					+ player.getEntityId() + " " + (int) percentageHp + " "
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
	public void npcIn(G_Player player, G_Npc npc) {

		if (player == null) {
			return;
		}

		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);
		if (client == null) {
			return;
		}

		String packetData = "in npc " + npc.getEntityId() + " " + npc.getType()
				+ " " + npc.getPosition().getX() + " "
				+ npc.getPosition().getY() + " 0 "
				+ npc.getPosition().getRotation() + " 100 0 0 0 0 0 10\n";

		// in npc [UniqueID] [type] [XPos] [YPos] [ZPos] [Rotation] [HP]
		// [MutantType] 0 [NeoProgmare] 0 0
		client.SendData(packetData);
	}

	/****** Manages the Npc Out ******/
	public void npcOut(G_Player player, G_Npc npc) {
		// if (player == null)
		// return;
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);
		if (client == null) {
			return;
		}

		String packetData = "out npc " + npc.getEntityId() + "\n";
		// S> out npc [UniqueID]
		client.SendData(packetData);
	}

	/****** Manages the player wear Weapon ******/
	public void playerWeapon(G_Player player, int uniqueId) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}
		System.out.println(uniqueId);
		G_Item item = (G_Item) G_EntityManager.getEntityManager().getEnt(
				uniqueId);
		if (item == null) {
			return;
		}
		G_Weapon weapon = new G_Weapon(item.getType());
		weapon.loadFromReference(item.getType());

		player.setMinDmg(weapon.getMinDamage());
		player.setMaxDmg(weapon.getMaxDamage());
		// C> pulse [SystemTime], [WeaponType], [WeaponUniqueID], [WeaponSpeed],
		// 120

		weapon.removeEntity();
	}

	void sendCharList(Client client, int accountId) {

		client.SendData(DatabaseUtils.getInstance().getCharList(accountId));

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
		Iterator<G_Player> iter = world.getPlayerManager()
				.getPlayerListIterator();

		while (iter.hasNext()) {
			G_Player pl = iter.next();

			Client client = Server.getInstance().getNetworkModule()
					.getClient(pl);
			if (client == null) {
				continue;
			}
			String packetData = "say -1 " + text + "\n";
			client.SendData(packetData);

		}
	}

	public void serverTell(G_Player player, String text) {

		if (player == null) {
			return;
		}
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);
		if (client == null) {
			return;
		}

		String packetData = "say 1 Server " + text + " 1\n";
		client.SendData(packetData);
	}

	/****** player1 attacks player2 with Sub Attack ******/
	public void subAttackChar(G_Player player1, int uniqueId) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player1);
		// G_Player player2 =
		// (G_Player)G_EntityManager.getEntityManager().getEnt(uniqueId);

		String packetData = "attack_vital char " + player1.getEntityId()
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
	public void subAttackNpc(G_Player player, int uniqueId) {
		Client client = Server.getInstance().getNetworkModule()
				.getClient(player);

		if (client == null) {
			return;
		}

		G_Mob mob = Server.getInstance().getWorldModule().getMobManager()
				.getMob(uniqueId);
		G_Item item = player.getEquipment().getShoulderMount();
		G_SlayerWeapon spWeapon = new G_SlayerWeapon(item.getType());

		item.setExtraStats(spWeapon.getExtraStats() - 20);
		spWeapon.loadFromReference(item.getType());
		spWeapon.setExtraStats(item.getExtraStats());

		double slayerDmg = 0;

		while (slayerDmg < spWeapon.getSpecialWeaponMinDamage()
				|| slayerDmg > spWeapon.getSpecialWeaponMaxDamage()) {
			slayerDmg = Math.random() * 100
					+ spWeapon.getSpecialWeaponMinDamage();
		}

		System.out.print("Skill Level: "
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

		int newHp = mob.getCurrHp() - (int) dmg;

		if (newHp <= 0) {
			mob.setCurrHp(0);
			serverSay("Experience: " + mob.getExp() + " Lime: " + mob.getLime());
			player.updateStatus(12, player.getLvlUpExp() - mob.getExp(), 0);
			player.updateStatus(11, mob.getExp(), 0);
			player.updateStatus(10, mob.getLime(), 0);
			// S_Server.getInstance().getWorldModule().getMobManager().removeMob(mob);

			if (mob.getType() == 324) {
				G_Item item2 = new G_Item(1054);

				item2.loadFromReference(1054);
				item2.setExtraStats(1080);
				item2.setGemNumber(0);

				player.pickupItem(item.getEntityId());
				player.getQuest().questEnd(player, 669);
				player.getQuest().questEff(player);
			}
		} else {
			mob.setCurrHp(newHp);
		}

		double percentageHp = mob.getCurrHp() * 100 / mob.getMaxHp();

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
			Server.getInstance().getWorldModule().getMobManager()
					.removeMob(mob);
		}
	}

}