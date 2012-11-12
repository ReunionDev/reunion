package org.reunionemu.jreunion.server.lock;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.reunionemu.jreunion.server.lock.LockServer.Condition;


public class LockServerTest {

	
	
	@Test
	public void test() {
		LockServer server = new LockServer();
		final Connection con = new Connection();
		Condition condition = new LockServer.Condition() {
			
			@Override
			public boolean condition() {
				return con.isOpen();
			}
		};
		List<Condition> conditions = Collections.singletonList(condition);
		
		con.setOpen(true);
		Item item1 = new Item(1L);
		Item item2 = new Item(2L);
		assertTrue(server.aquire(Item.class, item1.getId(), conditions));
		
		assertFalse(server.aquire(Item.class, item1.getId(), conditions));
		con.setOpen(false);
		assertTrue(server.aquire(Item.class, item1.getId(), conditions));
		con.setOpen(true);
		assertFalse(server.aquire(Item.class, item1.getId(), conditions));
		server.release(Item.class, item1.getId());
		assertTrue(server.aquire(Item.class, item1.getId(), conditions));

	}
	
	
	public class Connection{
		private boolean open;
		
		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}		
	}
	
	public class Item {
		public Item(Long id){
			setId(id);
		}
		
		private Long id;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}		
	}

}
