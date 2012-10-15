package org.reunionemu.jreunion.game.items;

import org.apache.log4j.Logger;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.LivingObject;
import org.reunionemu.jreunion.game.Player;
import org.reunionemu.jreunion.game.QuickSlotItem;
import org.reunionemu.jreunion.game.QuickSlotPosition;
import org.reunionemu.jreunion.game.Skill;
import org.reunionemu.jreunion.game.Usable;
import org.reunionemu.jreunion.game.items.etc.ReinforcingAgent;
import org.reunionemu.jreunion.game.skills.human.GemCutting;
import org.reunionemu.jreunion.server.Client;
import org.reunionemu.jreunion.server.DatabaseUtils;
import org.reunionemu.jreunion.server.ItemManager;
import org.reunionemu.jreunion.server.Server;
import org.reunionemu.jreunion.server.World;
import org.reunionemu.jreunion.server.PacketFactory.Type;
import org.slf4j.LoggerFactory;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
public class GemStone extends ReinforcingAgent implements Usable {
	public GemStone(int id) {
		super(id);
		loadFromReference(id);
	}

	@Override
	public boolean use(final Item<?> item, final LivingObject user,
			int quickSlotPosition, int unknown) {

		if (user instanceof Player) {
			Player player = (Player) user;
			Client client = player.getClient();
			World world = client.getWorld();
			ItemManager itemManager = world.getItemManager();

			int targetGem = 0;
			int sourceGem = 0;
			float chance = 1;
			float currentTry = 0;

			Skill skill = player.getSkill(21);

			if (skill instanceof GemCutting) {
				GemCutting gemCuttingSkill = (GemCutting) skill;
				chance = gemCuttingSkill.getSuccessRateModifier(player);
			}

			currentTry = Server.getRand().nextFloat();

			if (currentTry <= chance) {

				sourceGem = player.getQuickSlotBar().getItem(quickSlotPosition)
						.getItem().getType().getTypeId();

				player.getClient().sendPacket(Type.SAY,
						"Cutting " + item.getType().getName() + "...");

				if (sourceGem == 215) {
					targetGem = 222;
				} else if (sourceGem == 216) {
					targetGem = 223;
				} else if (sourceGem == 217) {
					targetGem = 224;
				} else if (sourceGem == 218) {
					targetGem = 225;
				} else if (sourceGem == 219) {
					targetGem = 226;
				} else if (sourceGem == 220) {
					targetGem = 227;
				} else if (sourceGem == 221) {
					targetGem = 228;
				} else if (sourceGem == 1067) {
					targetGem = 1068;
				}

				Item<?> newItem = itemManager.create(targetGem);
				player.getPosition().getLocalMap().createEntityId(newItem);

				QuickSlotItem oldQSItem = player.getQuickSlotBar().getItem(
						quickSlotPosition);
				QuickSlotItem newQSItem = new QuickSlotItem(newItem,
						new QuickSlotPosition(player.getQuickSlotBar(),
								quickSlotPosition));
				player.getQuickSlotBar().removeItem(oldQSItem);
				player.getQuickSlotBar().addItem(newQSItem);

				//player.getClient().sendPacket(Type.SAY,
				//		"Cutting " + item.getType().getName() + " was successfull.");
				Logger.getLogger(this.getClass()).debug("Player "+player+" cut Gem "+item+" successfully.");
				
				player.getClient().sendPacket(Type.WORKED, quickSlotPosition,
						1, item.getEntityId(), newItem.getEntityId());
				
				player.getPosition().getLocalMap().removeEntity(item);
				DatabaseUtils.getDinamicInstance().deleteItem(item.getItemId());
				return true;
			}
			else {
				//player.getClient().sendPacket(Type.SAY,
				//		"Cutting " + item.getType().getName() + " failed.");
				Logger.getLogger(this.getClass()).debug("Player "+player+" cut Gem "+item+" failed.");
				player.getClient().sendPacket(Type.WORKED, quickSlotPosition,
						0, item.getEntityId(), item.getEntityId());
				
				return false;
			}
		} else {
			LoggerFactory.getLogger(this.getClass()).warn(
					this.getName() + " not implemented for " + user.getName());
			return false;
		}

	}

	@Override
	public void loadFromReference(int id) {
		super.loadFromReference(id);
	}
}