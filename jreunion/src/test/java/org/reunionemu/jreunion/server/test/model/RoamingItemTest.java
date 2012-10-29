package org.reunionemu.jreunion.server.test.model;

import static org.junit.Assume.assumeNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.*;
import org.reunionemu.jreunion.events.Event;
import org.reunionemu.jreunion.game.*;
import org.reunionemu.jreunion.model.jpa.RoamingItemImpl;
import org.reunionemu.jreunion.server.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
	"classpath*:/spring/**/*-context-test.xml"})
public class RoamingItemTest {

	@Autowired 
	ItemDao<Item<?>> itemDao;
	
	Map map = new MockMap(1);
	
	@Autowired
	RoamingItemDao<RoamingItem> roamingItemDao;
	
	@Autowired
	ItemManager itemManager;
	
	@Test
	public void test() {
		
		assumeNotNull(itemManager);
		
		int typeId = 724;
		ItemType type = itemManager.getItemType(typeId);
		
		assumeNotNull(type);
		
		Item<?> old = itemManager.create(724);
		Item<?> item = old.save();
		assumeNotNull(item);
		
		assumeNotNull(item.getItemId());
		
		
		RoamingItem ri = new RoamingItemImpl(item, new Position(0,0,0,map,0));
				
		roamingItemDao.save(ri);
		
		
	}
	
	private static class MockMap extends Map {

		public MockMap(int id) {
			
			super(id);
		}

		@Override
		public void handleEvent(Event event) {

		}

	}
	
}
