package org.reunionemu.jreunion.server.test.model;

import static org.junit.Assume.*;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reunionemu.jreunion.dao.ItemDao;
import org.reunionemu.jreunion.game.Item;
import org.reunionemu.jreunion.game.ItemType;
import org.reunionemu.jreunion.server.ItemManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:/spring/**/*-context.xml", 
	"classpath*:/spring/**/*-context-test.xml"})
public class ItemTest {

	@Autowired 
	ItemDao<Item<?>> itemDao;
	
	@Autowired
	ItemManager itemManager;
	
	@Test
	public void test() {
		
		assumeNotNull(itemManager);
		
		int typeId = 724;
		ItemType type = itemManager.getItemType(typeId);
		
		assumeNotNull(type);
		
		Item<?> item = itemManager.create(724);
		itemDao.save(item);
		
		item = itemDao.findOne(item.getItemId());
		
		Assert.assertNotNull(item);		
		
	}
	
	
	@Test
	public void test2() {
		
		assumeNotNull(itemManager);
		
		int typeId = 724;
		ItemType type = itemManager.getItemType(typeId);		
		assumeNotNull(type);
		
		Item<?> item = itemManager.create(724);
		item.save();
		long id = item.getItemId();
		
		item = itemDao.findOne(id);		
		Assert.assertNotNull(item);
		item.delete();
		item = itemDao.findOne(id);
		Assert.assertNull(item);
	}

}
