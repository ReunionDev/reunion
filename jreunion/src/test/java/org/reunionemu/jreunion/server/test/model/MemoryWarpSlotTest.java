package org.reunionemu.jreunion.server.test.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.*;
import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.game.*;
import org.reunionemu.jreunion.model.MemoryWarpSlot;
import org.reunionemu.jreunion.model.jpa.MemoryWarpSlotImpl;
import org.reunionemu.jreunion.server.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
	"classpath*:/spring/**/*-context-test.xml"})
public class MemoryWarpSlotTest {

	@Autowired 
	ItemDao<Item<?>> itemDao;
	
	Map map = new MockMap(1);
	
	Player player = new MockPlayer();
	
	@Autowired
	MemoryWarpSlotDao<MemoryWarpSlot, MemoryWarpSlot.MemoryWarpSlotId> memoryWarpSlotDao;
	
	@Autowired
	PlayerManager playerManager;
	
	@Autowired
	ItemManager itemManager;
	
	@Test
	public void test() {
		
		assumeNotNull(playerManager);
		
		long playerId = 1;
		
		int slot = 4;
		
		assumeThat((long)player.getPlayerId(), is(playerId));
		playerManager.addPlayer(player);
						
		assumeNotNull(playerManager.getPlayerByDbId(playerId));
		
		assumeNotNull(map);		
		
		Position position = new Position(1, 2, 3, map, Math.PI);
		
		
		MemoryWarpSlot memoryWarpSlot = new MemoryWarpSlotImpl(player, slot, position);
		
		assertEquals(slot,memoryWarpSlot.getSlot());
		assertEquals(position, memoryWarpSlot.getPosition());
		assertEquals(playerId, memoryWarpSlot.getPlayer().getPlayerId());
		
		memoryWarpSlotDao.save(memoryWarpSlot);
		memoryWarpSlot = memoryWarpSlotDao.findOne(new MemoryWarpSlotImpl.MemoryWarpSlotIdImpl(playerId,slot));
		assertNotNull(memoryWarpSlot);
		assertEquals(slot, memoryWarpSlot.getSlot());
		assertEquals(playerId, memoryWarpSlot.getPlayer().getPlayerId());
		
		List <MemoryWarpSlot> memoryWarpSlots = memoryWarpSlotDao.findByIdPlayerId(playerId);
		assertNotNull(memoryWarpSlots);
		assertThat(memoryWarpSlots.size(), greaterThan(0));

		MemoryWarpSlot memoryWarpSlot2 = new MemoryWarpSlotImpl(player, slot+1, position);
		memoryWarpSlotDao.delete(memoryWarpSlot);

		memoryWarpSlots.get(0).setPosition(memoryWarpSlot.getPosition().setX(10));
		memoryWarpSlots.add(memoryWarpSlot2);
		memoryWarpSlotDao.save(memoryWarpSlots);
		memoryWarpSlots = memoryWarpSlotDao.findByIdPlayerId(playerId);
		assertNotNull(memoryWarpSlots);
		assertThat(memoryWarpSlots.size(), greaterThan(1));
		memoryWarpSlotDao.delete(memoryWarpSlots);
		memoryWarpSlots = memoryWarpSlotDao.findByIdPlayerId(playerId);
		assertNotNull(memoryWarpSlots);
		assertThat(memoryWarpSlots.size(), is(0));
		
	}
	
	private static class MockMap extends Map {

		public MockMap(int id) {
			
			super(id);
		}

		@Override
		public void handleEvent(Event event) {

		}

	}
	private static class MockPlayer extends Player{

		
		@Override
		public int getPlayerId() {
			return 1;
		}
		
		public MockPlayer() {
		}

		@Override
		public long getMaxElectricity() {
			return 0;
		}

		@Override
		public long getMaxMana() {
			return 0;
		}

		@Override
		public long getMaxStamina() {
			return 0;
		}

		@Override
		public long getBaseDamage() {
			return 0;
		}

		@Override
		public List<Skill> getDefensiveSkills() {
			return null;
		}

	
		
	}
	
}
