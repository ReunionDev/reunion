package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.LevelUpPacket;
import netty.parsers.LevelUpParser;

import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Status;

public class LevelUpParserTest {

	@Test
	public void test() {
		LevelUpParser parser = new LevelUpParser();
		Pattern pattern = parser.getPattern();

		int statusId = Status.DEXTERITY.value() - 10;
		String msg = "levelup " + statusId;
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());
		assertFalse(pattern.matcher("levelup" + statusId).matches());
		assertFalse(pattern.matcher("levelup").matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof LevelUpPacket);
		assertEquals(Status.DEXTERITY, ((LevelUpPacket) packet).getStatusType());
		assertEquals(msg, packet.toString());

	}

}
