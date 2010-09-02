package com.googlecode.reunion.jreunion.server;

//import java.util.*;
import com.googlecode.reunion.jreunion.game.G_Merchant;
import com.googlecode.reunion.jreunion.game.G_Mob;
import com.googlecode.reunion.jreunion.game.G_Npc;
import com.googlecode.reunion.jreunion.game.G_Player;
import com.googlecode.reunion.jreunion.game.G_Quest;
import com.googlecode.reunion.jreunion.game.G_Trader;
import com.googlecode.reunion.jreunion.game.G_Warehouse;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class S_PacketParser {

	private S_Server server;

	private S_MessageParser messageParser;

	public S_PacketParser(S_Server server) {
		super();
		this.server = server;
		messageParser = new S_MessageParser();
	}

	private void HandleMessage(S_Client client, String message[]) {
		S_Command com = server.getWorldModule().getWorldCommand();
		System.out.println("Parsing " + message[0] + " command on Client("
				+ client.networkId + ") with state: " + client.getState() + "");
		switch (client.getState()) {
		case S_Enums.CS_DISCONNECTED: {

			break;
		}
		case S_Enums.CS_ACCEPTED: {
			if (Integer.parseInt(message[0]) == S_DatabaseUtils.getInstance()
					.getVersion()) {
				System.out.println("Got Version");
				client.setState(S_Enums.CS_GOT_VERSION);

				break;
			} else {
				System.out.println("Inconsistent version detected on: "
						+ client.networkId);
				client.sendWrongVersion(Integer.parseInt(message[0]));
				client.setState(S_Enums.CS_DISCONNECTED);
				break;
			}

		}
		case S_Enums.CS_GOT_VERSION: {
			if (message[0].equals("login")) {
				System.out.println("Got Login");
				client.setState(S_Enums.CS_GOT_LOGIN);

				break;
			} else {
				System.out.println("Inconsistent protocol detected on: "
						+ client.networkId);
				client.setState(S_Enums.CS_DISCONNECTED);
				break;
			}

		}
		case S_Enums.CS_GOT_LOGIN: {
			if (message[0].length() < 28) {
				client.username = new String(message[0]);
				System.out.println("Got Username");
				client.setState(S_Enums.CS_GOT_USERNAME);
				break;
			} else {
				System.out.println("Inconsistent protocol detected on: "
						+ client.networkId);
				client.setState(S_Enums.CS_DISCONNECTED);
				break;

			}
		}
		case S_Enums.CS_GOT_USERNAME: {
			if (message[0].length() < 28) {
				client.password = new String(message[0]);
				System.out.println("Got Password");
				client.setState(S_Enums.CS_GOT_PASSWORD);
				com.authClient(client.networkId, client.username,
						client.password);
				break;
			} else {
				System.out.println("Inconsistent protocol detected on: "
						+ client.networkId);
				client.setState(S_Enums.CS_DISCONNECTED);
				break;

			}
		}
		case S_Enums.CS_CHAR_LIST: {

			if (message[0].equals("char_exist")) {
				if (S_DatabaseUtils.getInstance().getCharNameFree(message[1])) {
					com.sendSuccess(client.networkId);
				} else {
					com.sendFail(client.networkId);
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
				com.sendSuccess(client.networkId);
				com.sendCharList(client.networkId, client.accountId);
			} else if (message[0].equals("char_del")) {

				com.delChar(Integer.parseInt(message[1]), client.accountId);
				com.sendSuccess(client.networkId);

				com.sendCharList(client.networkId, client.accountId);
			} else if (message[0].equals("start")) {
				client.setState(S_Enums.CS_CHAR_SELECTED);
				com.loginChar(Integer.parseInt(message[1]), client.accountId,
						client.networkId);
			}

			break;
		}
		case S_Enums.CS_CHAR_SELECTED: {
			if (message[0].equals("start_game")) {
				G_Player pl = client.playerObject;

				pl.setPosX(6655);
				pl.setPosY(5224);
				pl.setPosZ(0);

				server.getNetworkModule().SendPacket(client.networkId,
						"status 11 " + pl.getTotalExp() + " 0\n");
				server.getNetworkModule().SendPacket(client.networkId,
						"status 12 " + pl.getLvlUpExp() + " 0\n");
				server.getNetworkModule().SendPacket(client.networkId,
						"status 13 " + pl.getStatusPoints() + " 0\n");
				server.getNetworkModule().SendPacket(client.networkId,
						"status 10 " + pl.getLime() + " 0\n");
				server.getNetworkModule().SendPacket(client.networkId,
						"status 19 " + pl.getPenaltyPoints() + " 0\n");
				server.getNetworkModule().SendPacket(
						client.networkId,
						"at " + client.playerObject.getEntityId() + " "
								+ pl.getPosX() + " " + pl.getPosY() + " "
								+ pl.getPosZ() + " 0\n");

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

				pl.updateStatus(0, pl.getCurrHp(),
						pl.getStr() * 1 + pl.getCons() * 2);
				pl.updateStatus(1, pl.getCurrMana(),
						pl.getWis() * 2 + pl.getDex() * 1);
				pl.updateStatus(2, pl.getCurrStm(),
						pl.getStr() * 2 + pl.getCons() * 1);
				pl.updateStatus(3, pl.getCurrElect(),
						pl.getWis() * 1 + pl.getDex() * 2);
				pl.updateStatus(13, -pl.getStatusPoints(), 0);

				int statusPoints = pl.getStr() + pl.getWis() + pl.getDex()
						+ pl.getCons() + pl.getLead() - 80;
				pl.updateStatus(13, (pl.getLevel() - 1) * 3 - statusPoints, 0);

				client.setState(S_Enums.CS_INGAME);
			}
			break;
		}
		case S_Enums.CS_INGAME: {
			if (message[0].equals("walk")) {
				client.playerObject.walk(Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]),
						Integer.parseInt(message[4]));
			} else if (message[0].equals("place")) {

				double rotation = Double.parseDouble(message[4]);

				client.playerObject.place(Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]), rotation / 1000,
						Integer.parseInt(message[5]),
						Integer.parseInt(message[6]));
			} else if (message[0].equals("stop")) {

				double rotation = Double.parseDouble(message[4]);

				client.playerObject.stop(Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]), rotation / 1000);
			} else if (message[0].equals("stamina")) {
				client.playerObject.loseStamina(Integer.parseInt(message[1]));
			}

			else if (message[0].equals("say")) {

				String text = message[1];

				for (int i = 2; i < message.length; i++) {
					text += " " + message[i];
				}

				text = messageParser.parse(client.playerObject, text);

				if (text != null && text.length() > 0) {
					client.playerObject.say(text);
				}

			}

			else if (message[0].equals("tell")) {

				String text = message[2];

				for (int i = 3; i < message.length; i++) {
					text += " " + message[i];
				}

				// client.playerObject.tell(message[1], text);
			} else if (message[0].equals("combat")) {
				client.playerObject.charCombat(Integer.parseInt(message[1]));
			} else if (message[0].equals("social")) {
				client.playerObject.social(Integer.parseInt(message[1]));
			} else if (message[0].equals("levelup")) {
				client.playerObject.updateStatus(
						Integer.parseInt(message[1]) + 10, 1, 0);
			} else if (message[0].equals("pick")) {
				client.playerObject.pickupItem(Integer.parseInt(message[1]));
			} else if (message[0].equals("inven")) {
				client.playerObject.getInventory().moveItem(
						client.playerObject, Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]));
				// S_DatabaseUtils.getInstance().saveInventory(client.playerObject);
			} else if (message[0].equals("drop")) {
				client.playerObject.dropItem(Integer.parseInt(message[1]));
			} else if (message[0].equals("attack")) {
				com.normalAttack(client.playerObject,
						Integer.parseInt(message[2]));
			} else if (message[0].equals("subat")) {
				if (message[1].equals("char")) {
					com.subAttackChar(client.playerObject,
							Integer.parseInt(message[2]));
				}
				if (message[1].equals("npc")) {
					com.subAttackNpc(client.playerObject,
							Integer.parseInt(message[2]));
				}
			} else if (message[0].equals("pulse")) {
				if (Integer.parseInt(message[2].substring(0,
						message[2].length() - 1)) == -1) {
					client.playerObject.setMinDmg(1);
					client.playerObject.setMaxDmg(2);
				} else {
					com.playerWeapon(
							client.playerObject,
							Integer.parseInt(message[3].substring(0,
									message[3].length() - 1)));
				}
			} else if (message[0].equals("wear")) {
				client.playerObject.wearSlot(Integer.parseInt(message[1]));
				// com.playerWear(client.playerObject,Integer.parseInt(message[1]));
			} else if (message[0].equals("use_skill")) {
				// if (message.length > 2){
				if (message[2].equals("npc")) {
					G_Mob mob = S_Server.getInstance().getWorldModule()
							.getMobManager()
							.getMob(Integer.parseInt(message[3]));
					client.playerObject.useSkill(mob,
							Integer.parseInt(message[1]));
				} else if (message[2].equals("char")) {
					G_Player player = S_Server.getInstance().getWorldModule()
							.getPlayerManager()
							.getPlayer(Integer.parseInt(message[3]));
					client.playerObject.useSkill(player,
							Integer.parseInt(message[1]));
				}

				// client.playerObject.useSkill(Integer.parseInt(message[1]));
			} else if (message[0].equals("skillup")) {
				client.playerObject.skillUp(Integer.parseInt(message[1]));
			} else if (message[0].equals("revival")) {
				client.playerObject.revive();
			} else if (message[0].equals("quick")) {
				client.playerObject.getQuickSlot().quickSlot(
						client.playerObject, Integer.parseInt(message[1]));
			} else if (message[0].equals("go_world")) {
				com.GoWorld(client.playerObject, Integer.parseInt(message[1]),
						Integer.parseInt(message[2]));
			} else if (message[0].equals("use_quick")) {
				client.playerObject.getQuickSlot().useQuickSlot(
						client.playerObject, Integer.parseInt(message[1]));
			} else if (message[0].equals("move_to_quick")) {
				client.playerObject.getQuickSlot().MoveToQuick(
						client.playerObject, Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]));
			} else if (message[0].equals("quest")) {
				if (message[1].equals("cancel")) {
					client.playerObject.getQuest().cancelQuest(
							client.playerObject);
				} else {
					if (client.playerObject.getQuest() == null) {
						client.playerObject.setQuest(new G_Quest(
								client.playerObject, Integer
										.parseInt(message[1])));
					} else {
						com.serverTell(client.playerObject,
								"Impossible to receive a quest");
					}
				}
			} else if (message[0].equals("stash_open")) {
				G_Npc[] npc = S_Server.getInstance().getWorldModule()
						.getNpcManager().getNpcList(139);
				G_Warehouse warehouse = (G_Warehouse) npc[0];
				warehouse.openStash(client.playerObject);
			} else if (message[0].equals("stash_click")) {
				G_Npc[] npc = S_Server.getInstance().getWorldModule()
						.getNpcManager().getNpcList(139);
				G_Warehouse warehouse = (G_Warehouse) npc[0];

				if (message.length == 5) {
					warehouse.stashClick(client.playerObject,
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]),
							Integer.parseInt(message[4]));
				}
				if (message.length == 4) {
					warehouse.stashClick(client.playerObject,
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]), 0);
				}
			} else if (message[0].equals("shop")) {
				int npcId = Integer.parseInt(message[1]);
				G_Merchant npc = (G_Merchant) S_Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				if (npc!=null) {
					npc.openShop(client.playerObject, Integer.parseInt(message[1]));
				} else {
					System.err.println("Npc not found: " + npcId);				
				}
			} else if (message[0].equals("buy")) {
				G_Merchant npc = (G_Merchant) S_Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				npc.buyItem(client.playerObject, Integer.parseInt(message[1]),
						Integer.parseInt(message[4]),
						Integer.parseInt(message[5]), 1);
			} else if (message[0].equals("sell")) {
				G_Merchant npc = (G_Merchant) S_Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				npc.sellItem(client.playerObject, Integer.parseInt(message[1]));
			} else if (message[0].equals("pbuy")) {
				G_Merchant npc = (G_Merchant) S_Server.getInstance()
						.getWorldModule().getNpcManager()
						.getNpc(Integer.parseInt(message[1]));
				npc.buyItem(client.playerObject, Integer.parseInt(message[1]),
						Integer.parseInt(message[2]),
						Integer.parseInt(message[3]), 10);
			} else if (message[0].equals("chip_exchange")) {
				G_Npc[] npc;

				if (Integer.parseInt(message[1]) == 0) {
					npc = S_Server.getInstance().getWorldModule()
							.getNpcManager().getNpcList(166);
					G_Trader trader = (G_Trader) npc[0];
					trader.chipExchange(client.playerObject,
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]), 0);
				} else {
					npc = S_Server.getInstance().getWorldModule()
							.getNpcManager().getNpcList(167);
					G_Trader trader = (G_Trader) npc[0];
					trader.chipExchange(client.playerObject,
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]));
				}
			} else if (message[0].equals("exch")) {
				client.playerObject.itemExchange(Integer.parseInt(message[2]),
						Integer.parseInt(message[3]));
			} else if (message[0].equals("ichange")) {
				G_Npc[] npc = S_Server.getInstance().getWorldModule()
						.getNpcManager().getNpcList(117);
				G_Trader trader = (G_Trader) npc[0];

				if (message.length == 1) {
					trader.exchangeArmor(client.playerObject, 0);
				} else {
					trader.exchangeArmor(client.playerObject,
							Integer.parseInt(message[1]));
				}
			}

			break;
		}
		default: {
			System.out.println("State Conflict");
			client.setState(S_Enums.CS_DISCONNECTED);
		}
		}
	}

	public void Parse(S_Client client, char[] packet) {

		String[] messages = ParsePacket(packet);
		if (client.getState() != S_Enums.CS_DISCONNECTED) {
			for (String message2 : messages) {
				String[] message = ParseMessage(message2);
				if (client.getState() != S_Enums.CS_DISCONNECTED) {
					HandleMessage(client, message);
				}
			}
		}

	}

	private String[] ParseMessage(String message) {
		return message.split(" ");
	}

	private String[] ParsePacket(char[] packet) {
		return new String(packet).split("\n");
	}
}