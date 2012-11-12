package org.reunionemu.jreunion.protocol.parsers.client.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import org.junit.Test;
import org.reunionemu.jreunion.protocol.Packet;
import org.reunionemu.jreunion.protocol.packets.server.LevelUpPacket;
import org.reunionemu.jreunion.protocol.parsers.client.LevelUpParser;

public class LevelUpTest {

	@Test
	public void test() {
		LevelUpParser parser = new LevelUpParser();
		Pattern pattern = parser.getPattern();

		String msg = "levelup 1";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("levelup").matches());

		assertFalse(pattern.matcher("levelup" + msg).matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof LevelUpPacket);
		assertEquals(1, ((LevelUpPacket) packet).getId());

	}

}
