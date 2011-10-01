package com.googlecode.reunion.jcommon;

import java.net.InetAddress;

import org.junit.Test;

import com.googlecode.reunion.jcommon.ServerList.ServerListItem;

import junit.framework.TestCase;


public class ServerListTest extends TestCase {

	@Test
	public void testCreation() throws Exception{
		ServerList serverList = new ServerList();
		assertNotNull(serverList);
		assertNotNull(serverList.getItems());
		assertEquals(serverList.getItems().size(), 0);
	}
	
	@Test
	public void testLoadSave() throws Exception{
		String filename = "testServerList.dta";
		ServerList serverList1 = new ServerList(),serverList2 = new ServerList();
		ServerListItem item1 = new ServerListItem("Test", InetAddress.getLocalHost(), 666);
		serverList1.getItems().add(item1);
		serverList1.Save(filename);
		
		serverList2.Load(filename);
		assertEquals(1, serverList2.getItems().size());
		ServerListItem item2 = serverList1.getItems().get(0);
		
		assertEquals(item1.getName(), item2.getName());
		assertEquals(item1.getPort(), item2.getPort());
		assertEquals(item1.getAddress(), item2.getAddress());
		
	}
	
	@Test
	public void testGetBytesTest(){
		short test = 0;
		byte [] result = ServerList.getBytes(test);
		assertEquals(0x00, result[0]);
		assertEquals(0x00, result[1]);
	}
}
