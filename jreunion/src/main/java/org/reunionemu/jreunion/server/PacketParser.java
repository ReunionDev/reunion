package org.reunionemu.jreunion.server;

import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.events.EventDispatcher;
import org.reunionemu.jreunion.events.EventListener;
import org.reunionemu.jreunion.events.client.ClientConnectEvent;
import org.reunionemu.jreunion.events.client.ClientDisconnectEvent;
import org.reunionemu.jreunion.events.client.ClientEvent;
import org.reunionemu.jreunion.events.client.ClientReceiveEvent;
import org.reunionemu.jreunion.events.map.PlayerLoginEvent;
import org.reunionemu.jreunion.game.Equipment;
import org.reunionemu.jreunion.game.Equipment.Slot;
import org.reunionemu.jreunion.game.ExchangeItem;
import org.reunionemu.jreunion.game.Inventory;
import org.reunionemu.jreunion.game.InventoryItem;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Npc;
import org.reunionemu.jreunion.game.Pet;
import org.reunionemu.jreunion.game.Shop;
import org.reunionemu.jreunion.game.Pet.PetStatus;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;
import org.reunionemu.jreunion.game.Player.Status;
import org.reunionemu.jreunion.game.Position;
import org.reunionemu.jreunion.game.RoamingItem;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.items.etc.MissionReceiver;
import org.reunionemu.jreunion.game.items.pet.PetEgg;
import org.reunionemu.jreunion.game.npc.Merchant;
import org.reunionemu.jreunion.game.npc.Trader;
import org.reunionemu.jreunion.game.npc.Warehouse;
import org.reunionemu.jreunion.protocol.OtherProtocol;
import org.reunionemu.jreunion.server.Client.LoginType;
import org.reunionemu.jreunion.server.Client.State;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class PacketParser extends EventDispatcher implements EventListener{

	private MessageParser messageParser;
	
	private static Logger logger = LoggerFactory.getLogger(PacketParser.class);				


	public PacketParser() {
		super();
		messageParser = new MessageParser();
	}

	private void HandleMessage(Client client, String message[]) {
		
		synchronized(client){
			Player player = client.getPlayer();
			World world = client.getWorld();
			Command com = world.getCommand();			
			
			LoggerFactory.getLogger(PacketParser.class).info("Parsing " + message[0] + " command on client: "
					+ client);
			switch (client.getState()) {
			case DISCONNECTED: {
				break;
			}
			case ACCEPTED: {
				
				//TODO: Better version handling
				/*
				try{
					
				if (message[0].equals(Reference.getInstance().getServerReference().getItem("Server").getMemberValue("Version"))) {
					LoggerFactory.getLogger(PacketParser.class).info("Got Version");
					client.setState(Client.State.GOT_VERSION);
					break;
				} else {
					LoggerFactory.getLogger(PacketParser.class).info("Inconsistent version (err 1) detected on: "
							+ client);
					client.sendWrongVersion(Integer.parseInt(message[0]));
					client.setState(Client.State.DISCONNECTED);
					break;
				}
				}catch(Exception e)
				{
					LoggerFactory.getLogger(this.getClass()).warn("Exception",e);
					client.disconnect();
				}
				*/
				int version = Integer.parseInt(message[0]);
				client.setVersion(version);
				
				if(client.getProtocol() instanceof OtherProtocol)
					((OtherProtocol)client.getProtocol()).setVersion(version);
				
				LoggerFactory.getLogger(PacketParser.class).info("Got version");
				client.setState(State.GOT_VERSION);
				break;
	
			}
			case GOT_VERSION: {
				if (message[0].equals("login")) {
					LoggerFactory.getLogger(PacketParser.class).info("Got login");
					client.setState(State.GOT_LOGIN);
					client.setLoginType(LoginType.LOGIN);
					break;
				} else if(message[0].equals("play")) {
					LoggerFactory.getLogger(PacketParser.class).info("Got play");
					client.setState(State.GOT_LOGIN);
					client.setLoginType(LoginType.PLAY);
					break;
				} else {
					LoggerFactory.getLogger(PacketParser.class).info("Inconsistent protocol (err 2) detected on: "
							+ client);
					client.setState(State.DISCONNECTED);
					break;
				}
	
			}
			case GOT_LOGIN: {
				if (message[0].length() < 28) {
					client.setUsername(new String(message[0]));
					LoggerFactory.getLogger(PacketParser.class).info("Got Username");
					client.setState(State.GOT_USERNAME);
					break;
				} else {
					LoggerFactory.getLogger(PacketParser.class).info("Inconsistent protocol (err 3) detected on: "
							+ client);
					client.setState(State.DISCONNECTED);
					break;
				}
			}
			case GOT_USERNAME: {
				
				Server.State serverState = Server.getInstance().getState();
				if(serverState!=Server.State.RUNNING){
					String failMessage = null;
					switch(serverState) {
					case LOADING:
						failMessage="Server loading, try again in a few minutes.";					
						break;
					case CLOSING:
						failMessage="Server closing, try again in a few minutes.";
						break;
					}
					client.sendPacket(Type.FAIL, failMessage);
					return;
				}
				
				if (message[0].length() < 28) {
					client.setPassword(new String(message[0]));
					LoggerFactory.getLogger(PacketParser.class).info("Got Password");
					client.setState(State.GOT_PASSWORD);
					com.authClient(client);
					break;
				} else {
					LoggerFactory.getLogger(PacketParser.class).info("Inconsistent protocol (err 4) detected on: "
							+ client);
					client.setState(State.DISCONNECTED);
					break;
				}
			}
			case CHAR_LIST: {
	
				if (message[0].equals("char_exist")) {
					if (DatabaseUtils.getDinamicInstance().getCharNameFree(message[1])) {
						com.sendSuccess(client);
					} else {
						client.sendPacket(Type.FAIL);
					}
					break;
					
				} else if (message[0].equals("char_new")) {
					int slot = Integer.parseInt(message[1]);
					
					java.util.Map<SocketChannel,Client> clients = world.getClients();
					synchronized(clients){
						for(Client cl: clients.values()){
							if(cl.equals(client))
								continue;
							if(cl.getAccountId()==client.getAccountId()){
								Player p1 = cl.getPlayer();
								
								if(p1!=null&&p1.getSlot()==slot){								
									//return because we've reserved this slot for a logged in user
									//TODO:find out if we can do some feedback here to the client
									client.sendPacket(Type.FAIL);
									return;
								}							
							}	
						}
					}
					Race race = Race.values()[Integer.parseInt(message[3])];
					Sex sex = Sex.values()[Integer.parseInt(message[4])];
		
					com.createChar(client, slot,
							message[2], race,
							sex,
							Integer.parseInt(message[5]),
							Integer.parseInt(message[6]),
							Integer.parseInt(message[7]),
							Integer.parseInt(message[8]),
							Integer.parseInt(message[9]),
							Integer.parseInt(message[10]));
					
					
					com.sendSuccess(client);
					com.sendCharList(client);
				} else if (message[0].equals("char_del")) {
	
					com.delChar(Integer.parseInt(message[1]), client.getAccountId());
					com.sendSuccess(client);
	
					com.sendCharList(client);
				} else if (message[0].equals("start")) {
					client.setState(Client.State.LOADING);
					int slot = Integer.parseInt(message[1]);
					player = com.loginChar(slot, client.getAccountId(),	client);
					if(player==null){
						client.sendPacket(Type.FAIL, "Cannot log in");
						client.disconnect();
					}
				}
				break;
			}
			case LOADING: {
				if (message[0].equals("start_game")) {
					
					Map map = null;
					
					for (Map m : Server.getInstance().getWorld().getMaps()) {
						if (m.getAddress().equals(client.getSocketChannel().socket().getLocalSocketAddress())) {
							map = m;
							break;
						}
					}
					
					if (map == null || !map.isLocal()) {
						LoggerFactory.getLogger(Command.class).error("Invalid Map: " + map);
						player.getClient().disconnect();
						return;
					}
					
					Position savedPosition = DatabaseUtils.getDinamicInstance().getSavedPosition(player);
					
					if(savedPosition != null) {
						Map savedMap = savedPosition.getMap();
						
						if(client.getLoginType()==LoginType.LOGIN){
							if(map!= savedMap) {
								com.GoToWorld(player, savedMap, 0);
								return;
							}
						}
						else{
							if(map!= savedMap) {
								savedPosition = null;
							}
						}
							
					}
					
					player.getPosition().setMap((LocalMap)map);
					world.getPlayerManager().addPlayer(player);
					
					DatabaseUtils.getDinamicInstance().loadStash(client);
					DatabaseUtils.getDinamicInstance().loadQuickSlot(player);
					DatabaseUtils.getDinamicInstance().loadInventory(player);
					DatabaseUtils.getDinamicInstance().loadExchange(player);
					
					player.loadInventory();
					player.loadExchange();
					player.loadQuickSlot();
					client.sendPacket(PacketFactory.Type.OK);
					
					
					//System.out.println(savedPosition);
					map.fireEvent(PlayerLoginEvent.class, player, savedPosition);
					
					/*
					 * server.getNetworkModule().SendPacket(client.networkId,
					 * "hour 3\n" + "weather 0\n" + "gwar_prize  0\n" +
					 * "guild_level 0\n" +
					 * "on_battle 0 3 BlackSouls 7 SlAcKeRs 0 DELTA 700 pSyKo 701 SlAcKeRs 702 Iron_Fist 703 pSyKo\n"
					 * +
					 * "mypet 99935 PHOENIX 7049 5297 0.0 585 591 595 597 602 605 7219 6181\n" +
					 * "pstatus 0 20790 20000 0\n" + "pstatus 1 10000 0 0\n" +
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
					/*
					player.updateStatus(0, player.getHp(),
							player.getStr() * 1 + player.getConstitution() * 2);
					player.updateStatus(1, player.getMana(),
							player.getWis() * 2 + player.getDexterity() * 1);
					player.updateStatus(2, player.getStm(),
							player.getStr() * 2 + player.getConstitution() * 1);
					player.updateStatus(3, player.getElect(),
							player.getWis() * 1 + player.getDexterity() * 2);
					player.updateStatus(13, -player.getStatusPoints(), 0);
	
					int statusPoints = player.getStr() + player.getWis() + player.getDexterity()
							+ player.getConstitution() + player.getLeadership() - 80;
					player.updateStatus(13, (player.getLevel() - 1) * 3 - statusPoints, 0);
	
					world.getTeleportManager().remove(player);
					*/
					
					client.setState(Client.State.INGAME);
					
					if(player.getPosition().getMap().getId() == 4)
						client.sendPacket(Type.INFO, world.getServerSetings().getWelcomeMessage());
					
					world.sendPacket(Type.SAY, player.getName() + " (" +player.getPlayerId()+ ") logged in on Map "+player.getPosition().getMap().getName());

					player.setHp(player.getMaxHp());
					player.setStamina(player.getMaxStamina());				
					player.setMana(player.getMaxMana());				
					player.setElectricity(player.getMaxElectricity());
					player.sendStatus(Status.TOTALEXP);					
					player.sendStatus(Status.LEVELUPEXP);					
					player.sendStatus(Status.STATUSPOINTS);					
					player.sendStatus(Status.LIME);
					player.sendStatus(Status.PENALTYPOINTS);
					player.setQuestState(DatabaseUtils.getDinamicInstance().loadQuestState(player));
					player.update();
					
					//handle with player pet loading
					Pet pet = world.getPetManager().getPet(player);
					if(pet != null){
						Item<?> item = player.getEquipment().getShoulderMount();
						player.setPet(pet);
						pet.setOwner(player);
						pet.sendStatus(PetStatus.STATE);
						if(pet.getState() > 1){
							pet.setEquipment(DatabaseUtils.getDinamicInstance().loadPetEquipment(player));
						
							if(pet.getState() == 12){
								pet.load();
							}
						} else if(item != null && item.getType() instanceof PetEgg){
							pet.setBreeding(true);
							pet.startBreeding();
						}
					} else {
						player.getClient().sendPacket(Type.PSTATUS, 13, 0l, 0l, 0);
					}
		
					if(map.getId() == 6){
						int flyStatus = player.getEquipment().getBoots().getExtraStats() >= 268435456 ? 1 : 0;
						player.getClient().sendPacket(Type.SKY, player, flyStatus);
					}
					
					Player playerobj = client.getPlayer();
					
					if(playerobj.getGuildId() != 0)
					{
						client.sendPacket(Type.GUILD_NAME, playerobj);
						client.sendPacket(Type.GUILD_GRADE, playerobj);
						client.sendPacket(Type.GUILD_LEVEL, playerobj);
					}
				}
				break;
			}
			case LOADED: {
				LoggerFactory.getLogger(PacketParser.class).info("Received" +message[0]+" while loaded state");
				
				
				break;
			}
			case INGAME: {
				if (message[0].equals("walk")) {
					
					Position position = new Position(
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]),
							 player.getPosition().getLocalMap(),
							 Double.NaN
					);
					
					client.getPlayer().walk(position,
							Integer.parseInt(message[4])==1);
					client.getPlayer().update();
					
				} else if (message[0].equals("place")) {
	
					double rotation = Double.parseDouble(message[4]);
					Position position = new Position(
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]),
							 player.getPosition().getLocalMap(),
							 rotation/10000
					);
									
					client.getPlayer().place(position,
							Integer.parseInt(message[5]),
							Integer.parseInt(message[6])==1);
					
					client.getPlayer().update();
					
				} else if (message[0].equals("stop")) {
	
					double rotation = Double.parseDouble(message[4]);
					Position position = new Position(
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]),
							player.getPosition().getLocalMap(),
							rotation/10000
					);
	
					client.getPlayer().stop(position);
				} else if (message[0].equals("stamina")) {
					client.getPlayer().loseStamina((Integer.parseInt(message[1]) > 0) ? Integer.parseInt(message[1]) : 2);
				}
	
				else if (message[0].equals("say")) {
					String text = message[1];
					for (int i = 2; i < message.length; i++) {
						text += " " + message[i];
					}
					text = messageParser.parse(client.getPlayer(), text);
					if (text != null && text.length() > 0) {
						player.say(text);
					}
				}
				
				else if (message[0].equals("upgrade")) {	
					Slot slot = Slot.byValue(Integer.parseInt(message[1]));
					Item<?> item = player.getEquipment().getItem(slot);
					
					item.upgrade(player,slot);
				}
				
				else if (message[0].equals("tell")) {
					String text = "";
					for (int i = 2; i < message.length; i++) {
						text += " " + message[i];
					}
					player.tell(message[1], text); //message[1]: playername
				}  else if (message[0].equals(":")) {
					Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
					
					long guildId = player.getGuildId();
					
					if(guildId != 0)
					{
						String text = "";
						for (int i = 1; i < message.length; i++) {
							text += " " + message[i];
						}
						boolean guildmemberonline = false;
						while(iterPlayer.hasNext())
						{
							Player currplayer = iterPlayer.next();
							
							if(currplayer.getGuildId() == guildId && currplayer != player)
							{
								guildmemberonline = true;
								currplayer.getClient().sendPacket(Type.GUILD_SAY, text, player);
							}
						}
						if(guildmemberonline)
							client.sendPacket(Type.GUILD_SAY, text, player);
						else
							client.sendPacket(Type.SAY, "Currently there is no online guild member");
					}
					else
					{
						client.sendPacket(Type.SAY, "You dont have a guild");
					}
				
				} else if (message[0].equals("combat")) {
					player.setIsInCombat(Integer.parseInt(message[1])==1);
				} else if (message[0].equals("social")) {
					player.setSocial(Long.parseLong(message[1]));
				} else if (message[0].equals("levelup")) {
					Status status = Status.byValue(Integer.parseInt(message[1])+10);
					player.addStatus(status);
				} else if (message[0].equals("pick")) {
					int entityId = Integer.parseInt(message[1]);
					LocalMap map = player.getPosition().getLocalMap();
					RoamingItem roamingItem = (RoamingItem)map.getRoamingItem(entityId);
					
					if(roamingItem!=null && player.getInventory().getHoldingItem()==null ){
						client.getPlayer().pickupItem(roamingItem);							
					}
					
				} else if (message[0].equals("use_inven")) {
					
					int tab = Integer.parseInt(message[1]);
					int x = Integer.parseInt(message[2]);
					int y = Integer.parseInt(message[3]);
					
					Inventory inventory = player.getInventory();
					InventoryItem inventoryItem = inventory.getItem(tab, x, y);
					Item<?> item = inventoryItem.getItem();
					com.useItem(player, item, -1);					
					inventory.deleteInventoryItem(inventoryItem);
					
					//used when we click in any position inside the inventory
				} else if (message[0].equals("inven")) {
					
					int tab = Integer.parseInt(message[1]);
					int x = Integer.parseInt(message[2]);
					int y = Integer.parseInt(message[3]);
					
					client.getPlayer().getInventory().handleInventory(tab, x, y);

				} else if (message[0].equals("drop")) {
					client.getPlayer().dropItem(Integer.parseInt(message[1]));
							
				} else if (message[0].equals("subat")) {
					Skill skill = world.getSkillManager().getSkill(Integer.parseInt(message[3]));
					skill.handle(player, message);
					
					/*
					LivingObject singleTarget = (LivingObject)player.getPosition().getLocalMap().getEntity(Integer.parseInt(message[2]));
					int skillId = Integer.parseInt(message[3]);
					int unknown1 = Integer.parseInt(message[4]);
					
					List<LivingObject> targets = new Vector<LivingObject>(); //TODO: add several targets
					
					targets.add(singleTarget);
					com.subAttack(player,targets,skillId,unknown1);
					*/
					
				} else if (message[0].equals("pulse")) {
					if (Integer.parseInt(message[2].substring(0,
							message[2].length() - 1)) == -1) {
						//client.getPlayer().setMinDmg(1);
						//client.getPlayer().setMaxDmg(2);
					} else {
						com.playerWeapon(
								client.getPlayer(),
								Integer.parseInt(message[3].substring(0,
										message[3].length() - 1)));
					}
				} else if (message[0].equals("wear")) {
					int slotId = Integer.parseInt(message[1]);
					Equipment.Slot slot = Equipment.Slot.byValue(slotId);
					client.getPlayer().wearSlot(slot);
					// com.getPlayer()Wear(client.getPlayer()Object,Integer.parseInt(message[1]));
					
				} else if (message[0].equals("use_skill") || message[0].equals("attack")) {
					// if attack command sent, then use SkillID 0 (Basic Attack)
					int skillId = message[0].equals("attack") ? 0 : Integer.parseInt(message[1]);
					
					Skill skill = player.getSkill(skillId);
					if(skill!=null){
						
						skill.handle(player, message);
					}
					else{
						logger.error("Skill with id %d not found.", skillId);
						
					}
					
				} else if (message[0].equals("skillup")) {
					synchronized(player) {
						float statusPoints = player.getStatusPoints();
						
						if(statusPoints>0){
							int skillId = Integer.parseInt(message[1]);
							Skill skill = player.getSkill(skillId);
							if(skill.levelUp(player)){
								player.setStatusPoints(player.getStatusPoints() - 1);
							}
						}
					}
				} else if (message[0].equals("revival")) {
					client.getPlayer().revive();
				} else if (message[0].equals("g_pos")) {
					if(message[1].equals("req"))
					{
						Iterator<Player> iterPlayer = Server.getInstance().getWorld().getPlayerManager().getPlayerListIterator();
						
						long guildId = player.getGuildId();
						
						client.sendPacket(Type.G_POS_START);
						
						if(guildId != 0)
						{
							while(iterPlayer.hasNext())
							{
								Player currplayer = iterPlayer.next();
								
								if(currplayer.getGuildId() == guildId)
								{
									if(currplayer.getPosition().getMap() == player.getPosition().getMap() && currplayer != player)
										client.sendPacket(Type.G_POS_BODY, currplayer);
								}
							}

							client.sendPacket(Type.G_POS_END);
						}
						else
						{
							client.sendPacket(Type.SAY, "You dont have a guild");
						}
					}
				} else if (message[0].equals("quick")) {
					client.getPlayer().getQuickSlotBar().quickSlot(
							client.getPlayer(), Integer.parseInt(message[1]));
				} else if (message[0].equals("go_world")) {
					
					int mapId = Integer.parseInt(message[1]);
					Map map = world.getMap(mapId);
					com.GoToWorld(client.getPlayer(), map,
							Integer.parseInt(message[2]));
					
				} else if (message[0].equals("trans")) {
					int posx = (int) Float.parseFloat(message[1]);
					int posy = (int) Float.parseFloat(message[2]);
	
					Position position = player.getPosition().clone();
					position.setX(posx);
					position.setY(posy);
					com.GoToPos(player, position);
					
				} else if (message[0].equals("use_quick")) { //2007 client
					player.getQuickSlotBar().useQuickSlot(
							player, Integer.parseInt(message[1]));	
				} else if (message[0].equals("using_item")) {
					int unknown = Integer.parseInt(message[1]);
					int quickSlotBarPosition = Integer.parseInt(message[2]);
					int itemEntityId = Integer.parseInt(message[3]);
					int unknown2 = message.length == 5 ? Integer.parseInt(message[4]): 0;
					
					player.getQuickSlotBar().useQuickSlot(
							player, quickSlotBarPosition, unknown2, itemEntityId);
					
				} else if (message[0].equals("move_to_quick")) { // old 2007 client
					player.getQuickSlotBar().MoveToQuick(
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]));
				} else if (message[0].equals("moving_item")) {
					player.getQuickSlotBar().MovingItem(
							Integer.parseInt(message[1]),
							Integer.parseInt(message[2]),
							Integer.parseInt(message[3]));
				} else if (message[0].equals("quest")) {
					if (message[1].equals("cancel")) {
						client.getPlayer().setQuest(null);
					} else if (message[1].equals("click")) {
						//TODO: use archeologist (npcType=257) to give quest scroll to meta players
					} else {
						if (client.getPlayer().getQuest() == null) {
							Item<?> item = player.getQuickSlotBar().getItem(Integer.parseInt(message[1])).getItem();
							
							if(item.is(MissionReceiver.class)){
								((MissionReceiver)item.getType()).use(item, player,Integer.parseInt(message[1]),0);
							}
						} else {
							player.getClient().sendPacket(Type.SAY, "Player already has an ongoing quest.");
						}
					}
				} else if (message[0].equals("stash_open")) {
					if (message.length == 1) {
						Warehouse warehouse = (Warehouse)client.getWorld().getNpcManager().getNpcType(Warehouse.class);
						warehouse.openStash(client.getPlayer());
					} else {
						logUnknownCommand(message);
					}
				} else if (message[0].equals("stash_click")) {
					if(message.length >= 4){
						Warehouse warehouse = (Warehouse)client.getWorld().getNpcManager().getNpcType(Warehouse.class);
						int special = message.length == 5 ? Integer.parseInt(message[4]) : 0;
					
						warehouse.stashClick(client.getPlayer(),
								Integer.parseInt(message[1]),
								Integer.parseInt(message[2]),
								Integer.parseInt(message[3]),
								special);
					}
				} else if (message[0].equals("stash_put")) {
					Warehouse warehouse = (Warehouse)client.getWorld().getNpcManager().getNpcType(Warehouse.class);
					int length = message.length > 23 ? 23 : message.length;
					int[] packetData = new int[length];
					int index = 0;
					
					while(index < packetData.length -1){
						packetData[index] = Integer.parseInt(message[index++ + 1]);
					}
					
					warehouse.stashPut(player, packetData);
					
				} else if (message[0].equals("stash_get")) {
					if(message.length >= 5){
						Warehouse warehouse = (Warehouse)client.getWorld().getNpcManager().getNpcType(Warehouse.class);
						warehouse.stashGet(player,
								Integer.parseInt(message[1]),
								Integer.parseInt(message[2]),
								Integer.parseInt(message[3]),
								Integer.parseInt(message[4]));
					}
				} else if (message[0].equals("stash_close")) {
					//nothing is returned to the client here, so we can just save the stash in the DB.
					if (message.length == 1) {
						//LoggerFactory.getLogger(PacketParser.class).info("Saving "+player+" stash...");
						//DatabaseUtils.getDinamicInstance().saveStash(client);
						player.save();
					} else {
						logUnknownCommand(message);
					}
				} else if (message[0].equals("shop")) {
					int npcId = Integer.parseInt(message[1]);
					Npc<?> npc = (Npc<?>) player.getPosition().getLocalMap().getEntity(npcId);
			
					if (npc!=null) {
						if(npc.getType() instanceof Merchant){
							npc.getShop().openShop(client.getPlayer());
							//((Merchant)npc.getType()).openShop(client.getPlayer());
						}
					} else {
						LoggerFactory.getLogger(PacketParser.class).warn("Npc not found: " + npcId);				
					}
				} else if (message[0].equals("buy")) {
					int npcId = Integer.parseInt(message[1]);
					Npc<?> npc = (Npc<?>) player.getPosition().getLocalMap().getEntity(npcId);
							
					if (npc!=null) {
						if(npc.getType() instanceof Merchant){
							npc.getShop().buyItem(client.getPlayer(), Integer.parseInt(message[4]),
											Integer.parseInt(message[5]), 1);
								//((Merchant)npc.getType()).buyItem(client.getPlayer(), Integer.parseInt(message[4]),
								//		Integer.parseInt(message[5]), 1);
						}
					} else {
						LoggerFactory.getLogger(PacketParser.class).warn("Npc not found: " + npcId);				
					}
				} else if (message[0].equals("sell")) {
					int npcId = Integer.parseInt(message[1]);
					Npc<?> npc = (Npc<?>) player.getPosition().getLocalMap().getEntity(npcId);
					
					if (npc!=null) {
						if(npc.getType() instanceof Merchant){
							npc.getShop().sellItem(client.getPlayer());
							//((Merchant)npc.getType()).sellItem(client.getPlayer());
						}
					} else {
						LoggerFactory.getLogger(PacketParser.class).warn("Npc not found: " + npcId);				
					}
				} else if (message[0].equals("pbuy")) {
					int npcId = Integer.parseInt(message[1]);
					Npc<?> npc = (Npc<?>) player.getPosition().getLocalMap().getEntity(npcId);
					int itemType = Integer.parseInt(message[2]);
					int tab = Integer.parseInt(message[3]);
					int quantity = 10;
					
					if (npc!=null) {
						if(npc.getType() instanceof Merchant){
							npc.getShop().buyItem(client.getPlayer(), itemType, tab, quantity);
							//((Merchant)npc.getType()).buyItem(client.getPlayer(), itemType, tab, quantity);
						}
					} else {
						LoggerFactory.getLogger(PacketParser.class).warn("Npc not found: " + npcId);				
					}
				} else if (message[0].equals("chip_exchange")) { //exchange 5 chips any grade
					
					Trader trader = (Trader)world.getNpcManager().getNpcType(Trader.class);
					
					if (message.length == 3) {
						trader.chipExchange(player,
								Integer.parseInt(message[1]),
								Integer.parseInt(message[2]),
								-1);
					} else {
						trader.chipExchange(player,
								Integer.parseInt(message[1]),
								Integer.parseInt(message[2]),
								Integer.parseInt(message[3]));
					}
				} else if (message[0].equals("exch")) {	//handles exchange inventory
					if (message.length == 2) {
						if(message[1].equals("cancel")){	// cancel request
							player.getExchange().cancelRequest();
						} else if(message[1].equals("ok")) {	//accept request
							player.getExchange().acceptRequest();
						} else if(message[1].equals("trade")) {	//trade confirmation
							player.getExchange().tradeConfirmation();
						}
					} else if (message.length == 3) {
						if(message[1].equals("money")){	// add money
							long money = Long.parseLong(message[2]);
							player.getExchange().addMoney(money);
						}
					} else if (message.length == 4) {  
						if(message[1].equals("inven")){ //exchange inventory click
								client.getPlayer().itemExchange(Integer.parseInt(message[2]),
										Integer.parseInt(message[3]));
						}
					}
				} else if (message[0].equals("ichange")) {	//armor exchange	
					Trader trader = (Trader)world.getNpcManager().getNpcType(Trader.class);
					
					if (message.length == 1) {
						trader.exchangeArmor(player, 0);
					} else {
						trader.exchangeArmor(player, Integer.parseInt(message[1]));
					}
				} else if (message[0].equals("q_ex")) {
					if(player.getExchange().listSize() > 0){
						
						if(!player.getExchange().isItemsScrolls()){
							player.getClient().sendPacket(Type.MSG, "Wrong item.");
							return;
						}
						
						int limeAmmount = 0;
						Iterator<ExchangeItem> exchangeIter = player.getExchange().itemListIterator();
						
						while(exchangeIter.hasNext()){
							ExchangeItem exchangeItem = exchangeIter.next();
							limeAmmount += exchangeItem.getItem().getExtraStats();
							DatabaseUtils.getDinamicInstance().deleteItem(exchangeItem.getItem().getItemId());
						}
						
						player.getExchange().clearExchange();
						player.setLime(player.getLime() + limeAmmount);
						player.getClient().sendPacket(Type.Q_EX, limeAmmount);
					}
				} else if (message[0].equals("up_sky")) {
					Item<?> boots = player.getEquipment().getBoots();
					if(boots != null){
						boots.update(player, Slot.BOOTS);
						if(player.getPosition().getLocalMap().getId() == 6)
							client.sendPacket(Type.SKY, player, 1);
					}
				} else if (message[0].equals("use_sub")) {
					if (message.length == 2) {
						player.getQuickSlotBar().useQuickSlot(player, Integer.parseInt(message[1]));
					}
				} else if (message[0].equals("go_zone")) {
					if (message.length == 4) {
						Item<?> item = player.getQuickSlotBar().getItem(Integer.parseInt(message[1])).getItem();
						item.setGemNumber(Integer.parseInt(message[2]));
						item.setExtraStats(Integer.parseInt(message[3]));
						player.getQuickSlotBar().useQuickSlot(player, Integer.parseInt(message[1]));
					}
				} else if (message[0].equals("p_walk")) {
					if (message.length == 5) {
						Position position = new Position(
								Integer.parseInt(message[1]),
								Integer.parseInt(message[2]),
								Integer.parseInt(message[3]),
								 player.getPosition().getLocalMap(),
								 Double.NaN
						);
						
						client.getPlayer().getPet().walk(position,
								Integer.parseInt(message[4])==1);
					}
				} else if (message[0].equals("p_drop")) {
					if (message.length == 2) {
						Pet pet = player.getPet();
						pet.getPosition().getLocalMap().removeEntity(pet);
						world.getPetManager().removePet(pet);
						player.setPet(null);
						DatabaseUtils.getDinamicInstance().deletePet(pet);
						client.sendPacket(Type.MYPET, "del");
						player.getInterested().sendPacket(Type.OUT, pet);
						LoggerFactory.getLogger(this.getClass()).info("Player: "+player+" deleted Pet: "+pet);
						
						int tab = Integer.parseInt(message[1]);
						pet = new Pet(player, 1);
						world.getPetManager().buyEgg(pet, tab);
					}
				} else if (message[0].equals("p_keep")) {
					if (message.length == 2) {
						Pet pet = world.getPetManager().getPet(player);
						if(message[1].equals("open")){
							if(pet != null){
								if(pet.getState() == 1){
									client.sendPacket(Type.P_KEEP, "fail", pet);
								} else if(pet.getState() >= 2){
									client.sendPacket(Type.P_KEEP, "info", pet);
								}
							}
						} else if(message[1].equals("in")){
							pet.setState(2);
							
							pet.getPosition().getLocalMap().removeEntity(pet);
							client.sendPacket(Type.MYPET, "del");
							pet.sendStatus(PetStatus.STATE);
							player.getInterested().sendPacket(Type.OUT, pet);
							LoggerFactory.getLogger(this.getClass()).info("Pet: "+pet+" stored at Npc.");
						}else if(message[1].equals("out")){
							player.setLime(player.getLime()-15000);
							pet.setState(12);
							pet.setSatiety(100);
							pet.setHp(pet.getMaxHp());
							pet.load();
							player.getInterested().sendPacket(Type.IN_PET, player, true);
							LoggerFactory.getLogger(this.getClass()).info("Pet: "+pet+" removed from Npc.");
						}
					}
				} else if (message[0].equals("buy_egg")) {
					if (message.length == 2) {
						int tab = Integer.parseInt(message[1]);
						Pet pet = new Pet(player, 1);
						world.getPetManager().buyEgg(pet, tab);
					}
				} else if (message[0].equals("party")) {
					if(message.length > 1){
						if(message[1].equals("request")){ //party invitation
							LocalMap map = player.getPosition().getLocalMap();
							Player newMember = map.getWorld().getPlayerManager().getPlayer(message[2]);
							map.inviteParty(player, newMember, Integer.parseInt(message[3]), Integer.parseInt(message[4]));
						} else if(message[1].equals("consist")){ //party invitation accepted
							int inviterEntityId = Integer.parseInt(message[3]);
							LocalMap map = player.getPosition().getLocalMap();
							Player inviterPlayer = (Player)map.getEntity(inviterEntityId);
							inviterPlayer.getParty().accept(inviterEntityId, player);
						} else if (message[1].equals("secession")){
							if(message.length > 2){ // party invitation rejected
								int inviterEntityId = Integer.parseInt(message[2]);
								player.getPosition().getLocalMap().getParty(inviterEntityId).reject(inviterEntityId, player);
							} else{ //request party exit
								player.getParty().exit(player);
							}
						}
					}
				} else if (message[0].equals("exchange")) {	//exchange request with another player
					if (message.length == 2) {
						String targetName = message[1];
						Player target = world.getPlayerManager().getPlayer(targetName);
						
						player.getExchange().request(target);
						
					}
				} else if (message[0].equals("u_shop")) {	//exchange request with another player
					if (message.length > 1) {
						if(message[1].equals("close")){
							if(player.getShop() != null){
								Shop buyingFromShop = player.getPosition().getLocalMap().getShopBuying(player);
								if(buyingFromShop != null){
									buyingFromShop.removePlayerBuying(player.getShop());
								}
								player.getShop().close();
							}
						} else if(message[1].equals("reg")){
							int shopPosition = Integer.parseInt(message[2]);
							int invTab = Integer.parseInt(message[3]);
							int goldBars =  Integer.parseInt(message[4]);
							int silverBars =  Integer.parseInt(message[5]);
							int bronzeBars =  Integer.parseInt(message[6]);
							long price =  Long.parseLong(message[7]);
							int invPosX =  Integer.parseInt(message[8]);
							int invPosY =  Integer.parseInt(message[9]);
							
							player.getShop().regItem(shopPosition, invTab, goldBars, silverBars, bronzeBars, price, invPosX, invPosY);
						} else if(message[1].equals("unreg")){
							Integer position = Integer.parseInt(message[2]);
							Integer ammount = Integer.parseInt(message[4]);
							
							player.getShop().unRegItem(position, ammount);
						}  else if(message[1].equals("start")){
							player.getShop().start(message);
						} else if(message[1].equals("modify")){
							for(Shop shop : player.getShop().getPlayersBuying()){
								shop.getOwner().getClient().sendPacket(Type.MSG, player.getName()+" closed shop.");
								shop.close();
							}
							player.getShop().getPlayersBuying().clear();
							player.getShop().modify();
						} else if(message[1].equals("open")){
							int shopOwnerEntityId = Integer.parseInt(message[3]);
							Player shopOwner = world.getPlayerManager().getPlayer(shopOwnerEntityId);
							shopOwner.getShop().open(player);
						} else if(message[1].equals("buy")){
							Integer position = Integer.parseInt(message[2]);
							Integer ammount = Integer.parseInt(message[4]);
							player.getShop().buy(position, ammount);
						}
					}
				} else if (message[0].equals("..")) {
					//client keep alive
				} else {
					logUnknownCommand(message);
					
					String command = "";
					for(String str: message){
						command += str + " ";
					}
					if(!command.contains("encrypt_") && player.getAdminState()==255)
						client.sendPacket(Type.SAY,"Unknown: "+command);
				}
	
				break;
			}
			default: {
				LoggerFactory.getLogger(PacketParser.class).info("State Conflict: "+client.getState());
				client.setState(Client.State.DISCONNECTED);
			}
			}
		}
	}
	
	public void logUnknownCommand(String[] message){
		String command = "";
		
		for(String str: message){
			command += str + " ";
		}
		LoggerFactory.getLogger(PacketParser.class).error("Unknown command: "+command);
	}

	public void Parse(Client client, String packet) {

		String[] messages = ParsePacket(packet);
		if (client.getState() != Client.State.DISCONNECTED) {
			for (String message : messages) {
				String[] words = ParseMessage(message);
				if (client.getState() != Client.State.DISCONNECTED) {
					HandleMessage(client, words);
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
		if (event instanceof ClientEvent){
			Client client =((ClientEvent) event).getClient();
			if(event instanceof ClientReceiveEvent){
				ClientReceiveEvent e = (ClientReceiveEvent)event;
				String data = null;
				synchronized(client){
					StringBuffer inputBuffer = client.getInputBuffer();
					data = inputBuffer.toString();
					inputBuffer.setLength(0);				
				}
				
				if(data!=null&&!data.isEmpty())
					Parse(client, data);
			}else
			if(event instanceof ClientConnectEvent){
				ClientConnectEvent e = (ClientConnectEvent)event;
				
				client.addEventListener(ClientReceiveEvent.class, this);
			}else
			if(event instanceof ClientDisconnectEvent){
				client.removeEventListener(ClientReceiveEvent.class, this);
			}
		}
		
	}
}