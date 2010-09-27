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
import com.googlecode.reunion.jreunion.game.Equipment.Slot;
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

	void authClient(Client client) {
		String username = client.getUsername();
		String password = client.getPassword();
		
		//Handling for a client that doesn't want to behave
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
		entry.sendPacket(Type.IN_CHAR, player, true);
		
		client.sendPacket(Type.GOTO, position);
		
	}

	/****** change map ******/
	public void GoToWorld(Player player, Map map, int unknown) {
		Client client = player.getClient();
		
		//Disband party
		client.sendPacket(Type.PARTY_DISBAND);
				
		client.sendPacket(Type.JUMP, player);
		
		//TODO: Cross server implementation
		Server.getInstance().getWorld().getTeleportManager()
				.register(player, map);

		Session session = player.getSession();

		// flush the session
		session.empty();
		
		client.sendPacket(Type.GO_WORLD, map, unknown);

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

		client.sendData(packetData);

		packetData = "a_idx " + client.getAccountId() + "\n";
		client.sendData(packetData);

		packetData = "a_idn " + client.getUsername() + "\n";
		client.sendData(packetData);

		packetData = "a_lev " + player.getAdminState() + "\n";
		client.sendData(packetData);

		packetData = "wearing " + eq.getId(Slot.HELMET) + " " + eq.getType(Slot.HELMET) + " "
				+ eq.getGemNumber(Slot.HELMET) + " " + eq.getExtraStats(Slot.HELMET) + " " + eq.getId(Slot.CHEST) + " "
				+ eq.getType(Slot.CHEST) + " " + eq.getGemNumber(Slot.CHEST) + " " + eq.getExtraStats(Slot.CHEST) + " "
				+ eq.getId(Slot.PANTS) + " " + eq.getType(Slot.PANTS) + " " + eq.getGemNumber(Slot.PANTS) + " "
				+ eq.getExtraStats(Slot.PANTS) + " " + eq.getId(Slot.SHOULDER) + " "
				+ eq.getType(Slot.SHOULDER) + " " + eq.getGemNumber(Slot.SHOULDER) + " "
				+ eq.getExtraStats(Slot.SHOULDER) + " " + eq.getId(Slot.BOOTS) + " " + eq.getType(Slot.BOOTS)
				+ " " + eq.getGemNumber(Slot.BOOTS) + " " + eq.getExtraStats(Slot.BOOTS) + " " + eq.getId(Slot.OFFHAND)
				+ " " + eq.getType(Slot.OFFHAND) + " " + eq.getGemNumber(Slot.OFFHAND) + " " + eq.getExtraStats(Slot.OFFHAND)
				+ " " + eq.getId(Slot.NECKLACE) + " " + eq.getType(Slot.NECKLACE) + " "
				+ eq.getGemNumber(Slot.NECKLACE) + " " + eq.getExtraStats(Slot.NECKLACE) + " " + eq.getId(Slot.BRACELET)
				+ " " + eq.getType(Slot.BRACELET) + " " + eq.getGemNumber(Slot.BRACELET) + " "
				+ eq.getExtraStats(Slot.BRACELET) + " " + eq.getId(Slot.RING) + " " + eq.getType(Slot.RING) + " "
				+ eq.getGemNumber(Slot.RING) + " " + eq.getExtraStats(Slot.RING) + " " + eq.getId(Slot.MAINHAND) + " "
				+ eq.getType(Slot.MAINHAND) + " " + eq.getGemNumber(Slot.MAINHAND) + " " + eq.getExtraStats(Slot.MAINHAND);
		// wearing [Helm] [Armor] [Pants] [ShoulderMount] [Boots] [Shield]
		// [Necklace] [Bracelet] [Ring] [Weapon]
		client.sendData(packetData);

		player.loadInventory();
		player.loadExchange();
		player.loadQuickSlot();

		client.sendPacket(PacketFactory.Type.OK);

		return player;
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
		
		client.sendPacket(Type.ATTACK_VITAL, livingObject);	
		
		player.getInterested().sendPacket(Type.ATTACK, player, livingObject);
		
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


		client.sendPacket(Type.ATTACK, mob, player);
		player.getInterested().sendPacket(Type.ATTACK, mob, player);
		
		// S> attack npc [NpcID] char [CharID] [RemainCharHP%] 0 0 0 0
	
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

		client.sendData(DatabaseUtils.getInstance().getCharList(client));

		client.setState(Client.State.CHAR_LIST);
		return;
	}

	void sendSuccess(Client client) {
		client.sendData("success\n");
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
	public void subAttack(Player player, LivingObject target) {
		Client client = player.getClient();
		
		client.sendPacket(Type.ATTACK_VITAL, target);
		
		Skill skill = player.getCharSkill().getSkill(40);
		
		player.getInterested().sendPacket(Type.EFFECT, player, target, skill);
		
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

		int percentageHp = mob.getPercentageHp();

		String packetData = "sav npc " + uniqueId + " " + percentageHp
				+ " 1 0 " + item.getExtraStats() + "\n";
		client.sendData(packetData);
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