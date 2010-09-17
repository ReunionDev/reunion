package com.googlecode.reunion.jreunion.server;

import java.util.Random;
import java.util.regex.Pattern;

import com.googlecode.reunion.jcommon.S_ParsedItem;
import com.googlecode.reunion.jreunion.events.Event;
import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;
import com.googlecode.reunion.jreunion.events.NetworkDataEvent;
import com.googlecode.reunion.jreunion.game.Enums.G_EquipmentSlot;
import com.googlecode.reunion.jreunion.game.Merchant;
import com.googlecode.reunion.jreunion.game.Mob;
import com.googlecode.reunion.jreunion.game.Npc;
import com.googlecode.reunion.jreunion.game.Player;
import com.googlecode.reunion.jreunion.game.Quest;
import com.googlecode.reunion.jreunion.game.Trader;
import com.googlecode.reunion.jreunion.game.Warehouse;
import com.googlecode.reunion.jreunion.server.Enums.S_LoginType;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PacketParser extends EventBroadcaster implements EventListener{

	private Server server;

	private MessageParser messageParser;

	public PacketParser(Server server) {
		super();
		this.server = server;
		messageParser = new MessageParser();
		//add a listener for the event type NetworkDataEvent
		server.getNetworkModule().addEventListener(NetworkDataEvent.class, this);
		
	}

	private void HandleMessage(Client client, String message[]) {
		Command com = server.getWorldModule().getWorldCommand();
		System.out.println("Parsing " + message[0] + " command on "
				+ client + " with state: " + client.getState() + "");
		switch (client.getState()) {
		case DISCONNECTED: {
			break;
		}
		case ACCEPTED: {
			try{
			if (message[0].equals(Reference.getInstance().getServerReference().getItem("Server").getMemberValue("Version"))) {
				System.out.println("Got Version");
				client.setState(Client.State.GOT_VERSION);
				break;
			} else {
				System.out.println("Inconsistent version (err 1) detected on: "
						+ client);
				client.sendWrongVersion(Integer.parseInt(message[0]));
				client.setState(Client.State.DISCONNECTED);
				break;
			}
			}catch(Exception e)
			{
				e.printStackTrace();
				client.disconnect();
			}

		}
		case GOT_VERSION: {
			if (message[0].equals("login")) {
				System.out.println("Got login");
				client.setState(Client.State.GOT_LOGIN);
				client.setLoginType(S_LoginType.LOGIN);
				break;
			} else if(message[0].equals("play")) {
				System.out.println("Got play");
				client.setState(Client.State.GOT_LOGIN);
				client.setLoginType(S_LoginType.PLAY);
				break;
			} else {
				System.out.println("Inconsistent protocol (err 2) detected on: "
						+ client);
				client.setState(Client.State.DISCONNECTED);
				break;
			}

		}
		case GOT_LOGIN: {
			if (message[0].length() < 28) {
				client.setUsername(new String(message[0]));
				System.out.println("Got Username");
				client.setState(Client.State.GOT_USERNAME);
				break;
			} else {
				System.out.println("Inconsistent protocol (err 3) detected on: "
						+ client);
				client.setState(Client.State.DISCONNECTED);
				break;

			}
		}
		case GOT_USERNAME: {
			if (message[0].length() < 28) {
				client.setPassword(new String(message[0]));
				System.out.println("Got Password");
				client.setState(Client.State.GOT_PASSWORD);
				com.authClient(client, client.getUsername(),
						client.getPassword());
				break;
			} else {
				System.out.println("Inconsistent protocol (err 4) detected on: "
						+ client);
				client.setState(Client.State.DISCONNECTED);
				break;

			}
		}
		case CHAR_LIST: {

			if (message[0].equals("char_exist")) {
				if (DatabaseUtils.getInstance().getCharNameFree(message[1])) {
					com.sendSuccess(client);
				} else {
					com.sendFail(client);
				}
				break;
			} else if (message[0].equals("char_new")) {

				com.createChar(client, Integer.parseInt(message[1]),
						message[2], Integer.parseInt(message[3]),
						Integer.parseInt(message[4]),
						Integer.parseInt(message[5]),
						Integer.parseInt(message[6]),
						Integer.parseInt(message[7]),
						Integer.parseInt(message[8]),
						Integer.parseInt(message[9]),
						Integer.parseInt(message[10]));
				com.sendSuccess(client);
				com.sendCharList(client, client.getAccountId());
			} else if (message[0].equals("char_del")) {

				com.delChar(Integer.parseInt(message[1]), client.getAccountId());
				com.sendSuccess(client);

				com.sendCharList(client, client.getAccountId());
			} else if (message[0].equals("start")) {
				client.setState(Client.State.CHAR_SELECTED);
				Player player = com.loginChar(Integer.parseInt(message[1]), client.getAccountId(),
						client);
				if(player==null){
					client.SendData("fail Cannot log in");
					client.disconnect();
				}
			}

			break;
		}
		
		case CHAR_SELECTED: {
			if (message[0].equals("start_game")) {
				Player player = client.getPlayer();

				player.getPosition().setX(6655);
				player.getPosition().setY(5224);//we need to implement spawnpoints here
				player.getPosition().setZ(0);

				client.SendData(
						"status 11 " + player.getTotalExp() + " 0\n");
				client.SendData(
						"status 12 " + player.getLvlUpExp() + " 0\n");
				client.SendData(
						"status 13 " + player.getStatusPoints() + " 0\n");
				client.SendData(
						"status 10 " + player.getLime() + " 0\n");
				client.SendData(
						"status 19 " + player.getPenaltyPoints() + " 0\n");
				
				
				
				
				int defaultSpawnId = Integer.parseInt(Reference.getInstance().getMapReference().getItemById(player.getPosition().getMap().getId()).getMemberValue("DefaultSpawnId"));
				S_ParsedItem spawn =player.getPosition().getMap().getPlayerSpawnReference().getItemById(defaultSpawnId);
				
				int x = Integer.parseInt(spawn.getMemberValue("X"));
				int y = Integer.parseInt(spawn.getMemberValue("Y"));
				int width = Integer.parseInt(spawn.getMemberValue("Width"));
				int height = Integer.parseInt(spawn.getMemberValue("Height"));
				
				Random rand = new Random(System.currentTimeMillis());
				int spawnX = x+(width>0?rand.nextInt(width):0);
				int spawnY = y+(height>0?rand.nextInt(height):0);
				
				player.getPosition().setX(spawnX);
				player.getPosition().setY(spawnY);				
				
				client.SendData(
						"at " + client.getPlayer().getEntityId() + " "
								+ player.getPosition().getX() + " " + player.getPosition().getY() + " "
								+ player.getPosition().getZ() + " 0\n");

				/*
				 * server.getNetworkModule().SendPacket(client.networkId,
				 * "hour 3\n" + "weather 0\n" + "gwar_prize  0\n" +
				 * "guild_level 0\n" +
				 * "on_battle 0 3 BlackSouls 7 SlAcKeRs 0 DELTA 700 pSyKo 701 SlAcKeRs 702 Iron_Fist 703 pSyKo\n"
				 * +
				 * "mypet 99935 PHOENIX 7049 5297 0.0 585 591 595 597 602 605 7219 6181\n"
				 * + "pstatus 0 20790 20000 0\n" + "pstatus 1 10000 0 0\n" +
				 * "pstatus 2 10000 0 0\n" + "pstatus 3 10000 0 0\n" +
				 * "pstatus 4 10000 0 0\n" + "pstatus 5 15 0 0\n" +
				 * "pstatus 6 39 0 0\n" + "pstatus 7 4 0 0\n" +
				 * "pstatus 8 100 0 0\n" + "pstatus 9 100 0 0\n" +
				 * "pstatus 10 100 0 0\n" + "pstatus 12 4190441 0 0\n" +
				 * "pstatus 11 16826042 0 0\n" + "pstatus 17 693315 0 0\n" +
				 * "pstatus 13 14 0 0\n" + "pstatus 15 619 0 0\n" +
				 * "pstatus 16 6 0 0\n" + "pstatus 18 967 0 0\n");
				 */
				// "pstatus 13 2 0 0\n");

				player.updateStatus(0, player.getCurrHp(),
						player.getStr() * 1 + player.getConstitution() * 2);
				player.updateStatus(1, player.getCurrMana(),
						player.getWis() * 2 + player.getDexterity() * 1);
				player.updateStatus(2, player.getCurrStm(),
						player.getStr() * 2 + player.getConstitution() * 1);
				player.updateStatus(3, player.getCurrElect(),
						player.getWis() * 1 + player.getDexterity() * 2);
				player.updateStatus(13, -player.getStatusPoints(), 0);

				int statusPoints = player.getStr() + player.getWis() + player.getDexterity()
						+ player.getConstitution() + player.getLeadership() - 80;
				player.updateStatus(13, (player.getLevel() - 1) * 3 - statusPoints, 0);

				Server.getInstance().getWorldModule().getTeleportManager().remove(player);
				client.setState(Client.State.INGAME);
				
			}
			break;
		}
		case INGAME: {
			if (message[0].equals("walk")) {
				client.getPlayer().walk(Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]),
						Integer.parseInt(message[4]));
			} else if (message[0].equals("place")) {

				double rotation = Double.parseDouble(message[4]);

				client.getPlayer().place(Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]), rotation / 1000,
						Integer.parseInt(message[5]),
						Integer.parseInt(message[6]));
			} else if (message[0].equals("stop")) {

				double rotation = Double.parseDouble(message[4]);

				client.getPlayer().stop(Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]), rotation / 1000);
			} else if (message[0].equals("stamina")) {
				client.getPlayer().loseStamina(Integer.parseInt(message[1]));
			}

			else if (message[0].equals("say")) {

				String text = message[1];

				for (int i = 2; i < message.length; i++) {
					text += " " + message[i];
				}

				text = messageParser.parse(client.getPlayer(), text);

				if (text != null && text.length() > 0) {
					client.getPlayer().say(text);
				}

			}

			else if (message[0].equals("tell")) {

				String text = message[2];

				for (int i = 3; i < message.length; i++) {
					text += " " + message[i];
				}

				// client.getPlayer()Object.tell(message[1], text);
			} else if (message[0].equals("combat")) {
				client.getPlayer().charCombat(Integer.parseInt(message[1]));
			} else if (message[0].equals("social")) {
				client.getPlayer().social(Integer.parseInt(message[1]));
			} else if (message[0].equals("levelup")) {
				client.getPlayer().updateStatus(
						Integer.parseInt(message[1]) + 10, 1, 0);
			} else if (message[0].equals("pick")) {
				client.getPlayer().pickupItem(Integer.parseInt(message[1]));
			} else if (message[0].equals("inven")) {
				client.getPlayer().getInventory().moveItem(
						client.getPlayer(), Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]));
				// S_DatabaseUtils.getInstance().saveInventory(client.getPlayer()Object);
			} else if (message[0].equals("drop")) {
				client.getPlayer().dropItem(Integer.parseInt(message[1]));
			} else if (message[0].equals("attack")) {
				com.normalAttack(client.getPlayer(),
						Integer.parseInt(message[2]));
			} else if (message[0].equals("subat")) {
				if (message[1].equals("char")) {
					com.subAttackChar(client.getPlayer(),
							Integer.parseInt(message[2]));
				}
				if (message[1].equals("npc")) {
					com.subAttackNpc(client.getPlayer(),
							Integer.parseInt(message[2]));
				}
			} else if (message[0].equals("pulse")) {
				if (Integer.parseInt(message[2].substring(0,
						message[2].length() - 1)) == -1) {
					client.getPlayer().setMinDmg(1);
					client.getPlayer().setMaxDmg(2);
				} else {
					com.playerWeapon(
							client.getPlayer(),
							Integer.parseInt(message[3].substring(0,
									message[3].length() - 1)));
				}
			} else if (message[0].equals("wear")) {
				int slotId = Integer.parseInt(message[1]);
				G_EquipmentSlot slot =G_EquipmentSlot.byValue(slotId);
				client.getPlayer().wearSlot(slot);
				// com.getPlayer()Wear(client.getPlayer()Object,Integer.parseInt(message[1]));
			} else if (message[0].equals("use_skill")) {
				try {
				// if (message.length > 2){
				if (message[2].equals("npc")) {
					Mob mob = Server.getInstance().getWorldModule()
							.getMobManager()
							.getMob(Integer.parseInt(message[3]));
					client.getPlayer().useSkill(mob,
							Integer.parseInt(message[1]));
				} else if (message[2].equals("char")) {
					Player player = Server.getInstance().getWorldModule()
							.getPlayerManager()
							.getPlayer(Integer.parseInt(message[3]));
					client.getPlayer().useSkill(player,
							Integer.parseInt(message[1]));
				}
				} catch (Exception e) {
					System.out.println("oh skill bug");
				}
				// client.getPlayer()Object.useSkill(Integer.parseInt(message[1]));
			} else if (message[0].equals("skillup")) {
				client.getPlayer().skillUp(Integer.parseInt(message[1]));
			} else if (message[0].equals("revival")) {
				client.getPlayer().revive();
			} else if (message[0].equals("quick")) {
				client.getPlayer().getQuickSlot().quickSlot(
						client.getPlayer(), Integer.parseInt(message[1]));
			} else if (message[0].equals("go_world")) {
				com.GoWorld(client.getPlayer(), Integer.parseInt(message[1]),
						Integer.parseInt(message[2]));
			} else if (message[0].equals("use_quick")) {
				client.getPlayer().getQuickSlot().useQuickSlot(
						client.getPlayer(), Integer.parseInt(message[1]));
			} else if (message[0].equals("move_to_quick")) {
				client.getPlayer().getQuickSlot().MoveToQuick(
						client.getPlayer(), Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]));
			} else if (message[0].equals("quest")) {
				if (message[1].equals("cancel")) {
					client.getPlayer().getQuest().cancelQuest(
							client.getPlayer());
				} else {
					if (client.getPlayer().getQuest() == null) {
						client.getPlayer().setQuest(new Quest(
								client.getPlayer(), Integer
										.parseInt(message[1])));
					} else {
						com.serverTell(client.getPlayer(),
								"Impossible to receive a quest");
					}
				}
			} else if (message[0].equals("stash_open")) {
				Npc[] npc = Server.getInstance().getWorldModule()
						.getNpcManager().getNpcList(139);
				Warehouse warehouse = (Warehouse) npc[0];
				warehouse.openStash(client.getPlayer());
			} else if (message[0].equals("stash_click")) {
				Npc[] npc = Server.getInstance().getWorldModule()
						.getNpcManager().getNpcList(139);
				Warehouse warehouse = (Warehouse) npc[0];

				if (message.length == 5) {
					warehouse.stashClick(client.getPlayer(),
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]),
							Integer.parseInt(message[4]));
				}
				if (message.length == 4) {
					warehouse.stashClick(client.getPlayer(),
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]), 0);
				}
			} else if (message[0].equals("shop")) {
				int npcId = Integer.parseInt(message[1]);
				Merchant npc = (Merchant) Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				if (npc!=null) {
					npc.openShop(client.getPlayer(), Integer.parseInt(message[1]));
				} else {
					System.err.println("Npc not found: " + npcId);				
				}
			} else if (message[0].equals("buy")) {
				Merchant npc = (Merchant) Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				npc.buyItem(client.getPlayer(), Integer.parseInt(message[1]),
						Integer.parseInt(message[4]),
						Integer.parseInt(message[5]), 1);
			} else if (message[0].equals("sell")) {
				Merchant npc = (Merchant) Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				npc.sellItem(client.getPlayer(), Integer.parseInt(message[1]));
			} else if (message[0].equals("pbuy")) {
				Merchant npc = (Merchant) Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				npc.buyItem(client.getPlayer(), Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]), 10);
			} else if (message[0].equals("chip_exchange")) {
				Npc[] npc;

				if (Integer.parseInt(message[1]) == 0) {
					npc = Server.getInstance().getWorldModule()
							.getNpcManager().getNpcList(166);
					Trader trader = (Trader) npc[0];
					trader.chipExchange(client.getPlayer(),
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]), 0);
				} else {
					npc = Server.getInstance().getWorldModule()
							.getNpcManager().getNpcList(167);
					Trader trader = (Trader) npc[0];
					trader.chipExchange(client.getPlayer(),
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]));
				}
			} else if (message[0].equals("exch")) {
				client.getPlayer().itemExchange(Integer.parseInt(message[2]),
						Integer.parseInt(message[3]));
			} else if (message[0].equals("ichange")) {
				Npc[] npc = Server.getInstance().getWorldModule()
						.getNpcManager().getNpcList(117);
				Trader trader = (Trader) npc[0];

				if (message.length == 1) {
					trader.exchangeArmor(client.getPlayer(), 0);
				} else {
					trader.exchangeArmor(client.getPlayer(),
							Integer.parseInt(message[1]));
				}
			}

			break;
		}
		default: {
			System.out.println("State Conflict");
			client.setState(Client.State.DISCONNECTED);
		}
		}
	}

	public void Parse(Client client, String packet) {

		String[] messages = ParsePacket(packet);
		if (client.getState() != Client.State.DISCONNECTED) {
			for (String message2 : messages) {
				String[] message = ParseMessage(message2);
				if (client.getState() != Client.State.DISCONNECTED) {
					HandleMessage(client, message);
				}
			}
		}
	}
	static Pattern regexParseMessage = Pattern.compile(" ");
	private String[] ParseMessage(String message) {
		return regexParseMessage.split(message);
	}

	static Pattern regexParsePacket = Pattern.compile("\n");
	private String[] ParsePacket(String packet) {
		return regexParsePacket.split(packet);
	}

	@Override
	public void handleEvent(Event event) {
		//System.out.println(event);
		if(event instanceof NetworkDataEvent){
			NetworkDataEvent e = (NetworkDataEvent)event; 
			Parse(e.getClient(),e.getData());
		}
		
	}
}