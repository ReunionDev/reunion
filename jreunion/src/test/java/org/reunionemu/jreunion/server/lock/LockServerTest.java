package org.reunionemu.jreunion.server.lock;

import static org.junit.Assert.*;

import java.util.Collections;

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
		
		con.setOpen(true);
		Item item1 = new Item(1L);
		Item item2 = new Item(2L);
		assertTrue(server.aquire(Item.class, item1.getId(), Collections.singletonList(condition)));
		
		assertFalse(server.aquire(Item.class, item1.getId(), Collections.singletonList(condition)));
		con.setOpen(false);
		assertTrue(server.aquire(Item.class, item1.getId(), Collections.singletonList(condition)));

		
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
