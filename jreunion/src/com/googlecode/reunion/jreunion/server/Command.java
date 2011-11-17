package com.googlecode.reunion.jreunion.server;

import java.nio.channels.SocketChannel;

import org.apache.log4j.Logger;

import com.googlecode.reunion.jreunion.events.map.ItemDropEvent;
import com.googlecode.reunion.jreunion.game.Equipment;
import com.googlecode.reunion.jreunion.game.Item;
import com.googlecode.reunion.jreunion.game.ItemType;
import com.googlecode.reunion.jreunion.game.LivingObject;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Player.Race;
import com.googlecode.reunion.jreunion.game.Player.Sex;
import com.googlecode.reunion.jreunion.game.Position;
import com.googlecode.reunion.jreunion.game.QuickSlotBar;
import com.googlecode.reunion.jreunion.game.RoamingItem;
import com.googlecode.reunion.jreunion.game.Skill;
import com.googlecode.reunion.jreunion.game.Usable;
import com.googlecode.reunion.jreunion.game.items.equipment.SlayerWeapon;
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
		int accountId = DatabaseUtils.getDinamicInstance().Auth(username, password);
		
		//if(client.getVersion()==101&&client.getLoginType()!=LoginType.PLAY){
		if (accountId == -1 && client.getLoginType()!=LoginType.PLAY) {
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
			accountId = DatabaseUtils.getDinamicInstance().Auth(username, password);
		}
		
		//int accountId = DatabaseUtils.getDinamicInstance().Auth(username, password);
		if (accountId == -1) {
			Logger.getLogger(Command.class).info("Invalid Login");
			client.sendPacket(Type.FAIL,"Username and password combination is invalid");
			client.disconnect();
		} else {
			
			Logger.getLogger(Command.class).info("" + client + " authed as account(" + accountId + ")");
			client.setAccountId(accountId);
			
			java.util.Map<SocketChannel,Client> clients = world.getClients();
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
		if (DatabaseUtils.getDinamicInstance().getCharNameFree(charName)) {
			DatabaseUtils.getDinamicInstance().createChar(client, slotNumber,
					charName, race, sex, hair, str, intel, dex, con, lea);
		}
	}

	public void delChar(int slotNumber, int accountId) {
		int charId = DatabaseUtils.getDinamicInstance().getCharId(slotNumber, accountId);
		String charName = DatabaseUtils.getDinamicInstance().getCharName(charId);
		
		Logger.getLogger(Command.class).info("Player {id:"+charId+", name:"+charName+"} deleted from account {id:"
				+accountId+"}");
		
		DatabaseUtils.getDinamicInstance().deleteCharSlot(charId);
		DatabaseUtils.getDinamicInstance().deleteCharacter(charId);
		DatabaseUtils.getDinamicInstance().deleteCharSkills(charId);
		DatabaseUtils.getDinamicInstance().deleteCharQuickSlot(charId);
		DatabaseUtils.getDinamicInstance().deleteCharQuestState(charId);
		DatabaseUtils.getDinamicInstance().deleteCharInventory(charId);
		DatabaseUtils.getDinamicInstance().deleteCharExchange(charId);
		DatabaseUtils.getDinamicInstance().deleteCharEquipment(charId);
		
	}
	
	public RoamingItem dropItem(Position position, Item<?> item) {

		RoamingItem roamingItem = new RoamingItem(item);
		roamingItem.setPosition(position);
		
		DatabaseUtils.getDinamicInstance().saveItem(roamingItem);
		
		LocalMap map = position.getLocalMap();
		
		map.fireEvent(ItemDropEvent.class, roamingItem);
		
		return roamingItem;
	
	}

	/****** teleport player to player2 position ******/
	public void GoToChar(Player player, String charName) {
		Player target = world.getPlayerManager().getPlayer(charName);
		
		Map mapTargetPlayer = target.getPosition().getMap();
		Map currPlayer = player.getPosition().getMap();
		
		if(mapTargetPlayer.getId() == currPlayer.getId())
			GoToPos(player,target.getPosition());
		else
			player.getClient().sendPacket(Type.SAY, "Player is on other map. You must teleport to "+mapTargetPlayer.getName());
	}
	
	
	public void GoToPos(Player player, Position position){
		
		Client client = player.getClient();
				
		SessionList<Session> exit = player.getInterested().getSessions();
		exit.exit(player);
		player.setPosition(position);
		
		SessionList<Session> entry = player.getPosition().getLocalMap().GetSessions(position);
		
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
		if(session!=null)
			session.empty();
		
		client.sendPacket(Type.GO_WORLD, map, unknown);

	}

	public Player loginChar(int slotNumber, int accountId, Client client) {
		
		Player player = DatabaseUtils.getDinamicInstance().loadChar(slotNumber, accountId, client);
		LocalMap localMap = DatabaseUtils.getDinamicInstance().getSavedPosition(player).getLocalMap();
		Equipment equipment = DatabaseUtils.getDinamicInstance().loadEquipment(player);
		
		world.getSkillManager().loadSkills(player);
		DatabaseUtils.getDinamicInstance().loadSkills(player);	
		player.loadEquipment(localMap);
		player.loadStash(localMap);
		player.setDefense();

		client.sendPacket(Type.SKILLLEVEL_ALL,player);
		
		client.sendPacket(Type.A_, "idx", client.getAccountId());

		client.sendPacket(Type.A_, "idn", client.getUsername());
		
		client.sendPacket(Type.A_, "lev", player.getAdminState());

		client.sendPacket(Type.WEARING, equipment);

		client.sendPacket(PacketFactory.Type.OK);

		return player;
	}

	/****** Manages the player wear Weapon ******/
	public void playerWeapon(Player player, int uniqueId) {
		//Client client = player.getClient();

		//Logger.getLogger(Command.class).info(uniqueId);

		ItemType item = null;
		//TODO: FIX
		//item = (Item) ItemManager.getEntityManager().getEnt(uniqueId);
		if (item == null) {
			return;
		}
		/*
		Weapon weapon = new Weapon(item.getType());
		weapon.loadFromReference(item.getType());

		player.setMinDmg(weapon.getMinDamage());
		player.setMaxDmg(weapon.getMaxDamage());
		*/
		// C> pulse [SystemTime], [WeaponType], [WeaponUniqueID], [WeaponSpeed],
		// 120

	}

	void sendCharList(Client client) {

		client.sendData(DatabaseUtils.getDinamicInstance().getCharList(client));

		client.setState(Client.State.CHAR_LIST);
		return;
	}

	void sendSuccess(Client client) {
		
		client.sendPacket(Type.SUCCESS);
		
	}

	public void serverSay(String text) {
		
		world.sendPacket(Type.SAY,text);

	}
	
	public void serverTell(Sendable sendable, String text) {
		
		sendable.sendPacket(Type.SAY, text);
	}

	/****** player1 attacks player2 with Sub Attack 
	 * @param skillid ******/
	public void subAttack(Player player, LivingObject target, String effectid) {
		Client client = player.getClient();
		
		client.sendPacket(Type.ATTACK_VITAL, target);
		
		player.getInterested().sendPacket(Type.SECONDATACK,player,target,effectid);
		
	}
	/*
	/****** player attacks mob with Sub Attack ******/
	public void subAttackNpc(Player player, int uniqueId) {
		Client client = player.getClient();

		if (client == null) {
			return;
		}
		
		ItemManager itemManager = world.getItemManager();
		Skill skill = world.getSkillManager().getSkill(40);

		LivingObject livingObject = (LivingObject) player.getPosition().getLocalMap().getEntity(uniqueId);
		Item<?> item = player.getEquipment().getShoulderMount();
		SlayerWeapon spWeapon = (SlayerWeapon)item.getType();
		//SlayerWeapon spWeapon = new SlayerWeapon(item.getType().getTypeId());

		item.setExtraStats(item.getExtraStats() - 20);
		//spWeapon.loadFromReference(item.getType().getTypeId());
		//spWeapon.setExtraStats(item.getExtraStats());

		double slayerDmg = 0;

		while (slayerDmg < spWeapon.getMinDamage()
				|| slayerDmg > spWeapon.getMaxDamage()) {
			slayerDmg = Math.random() * 100
					+ spWeapon.getMinDamage();
		}
/*
		Logger.getLogger(Command.class).info("Skill Level: "
				+ player.getCharSkill().getSkill(40).getCurrLevel() + "\n");
*/
		// Max normal attack damage * memory of the slayer * % skill (40)
		// increase +
		// slayer attack damage * % skill (40) increase * 1 (if no demolition
		// and *
		// 1.8 if demolition hit occurs)

		double dmg = player.getBestAttack()
				* spWeapon.getMemoryDmg()
				/ 100
				* (player.getSkillLevel(skill) * 20 / 100)
				+ slayerDmg
				* (player.getSkillLevel(skill) * 20 / 100)
				* 1;

		player.clearAttackQueue();

		long newHp = livingObject.getHp() - (long)dmg;

		if (newHp <= 0) {
			livingObject.setHp(0);
			//serverSay("Experience: " + livingObject.getExp() + " Lime: " + livingObject.getLime());
			//player.updateStatus(12, player.getLvlUpExp() - livingObject.getExp(), 0);
			//player.updateStatus(11, livingObject.getExp(), 0);
			//player.updateStatus(10, livingObject.getLime(), 0);
			// S_Server.getInstance().getWorldModule().getMobManager().removeMob(mob);

			//if (livingObject.getType() == 324) {
				
				Item<?> item2 = itemManager.create(1054);
				item2.setExtraStats(1080);
				item2.setGemNumber(0);

				player.getInventory().storeItem(item2, -1);
				//player.pickupItem(item);
				player.getQuest().end(player, 669);
				player.getQuest().eff(player);
			//}
		} else {
			livingObject.setHp(newHp);
		}

		int percentageHp = livingObject.getPercentageHp();

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
/*
		if (percentageHp == 0) {
			Server.getInstance().getWorld().getMobManager()
					.removeMob(livingObject);
		}
	*/
	}

	public boolean useItem(Player player ,Item<?> item, int slot) {
		
		if(Usable.class.isInstance(item.getType())){
			
			
			((Usable)item.getType()).use(item, player);
			
			player.getPosition().getLocalMap().removeEntity(item);			
			DatabaseUtils.getDinamicInstance().deleteItem(item.getItemId());
			return true;
			
		}
		else{
			
			Logger.getLogger(QuickSlotBar.class).error(item.getType().getName()+ " not Usable");
			
		}
		return false;
		
	}

}