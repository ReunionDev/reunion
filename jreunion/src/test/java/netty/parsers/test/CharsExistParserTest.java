package netty.parsers.test;

import static org.junit.Assert.*;

import java.util.regex.*;

import netty.Packet;
import netty.packets.CharsExistPacket;
import netty.parsers.CharsExistParser;

import org.junit.Test;
import org.reunionemu.jreunion.game.Player.Race;
import org.reunionemu.jreunion.game.Player.Sex;

public class CharsExistParserTest {

	@Test
	public void test() {
		CharsExistParser parser = new CharsExistParser();
		Pattern pattern = parser.getPattern();

		String msg = "chars_exist 1 2 myname 2 1 4 100 101 102 103 104 105 106 107 108 5 6 7 8 9 1 201 -1 203 -1 205 206 2";
		Matcher matcher = pattern.matcher(msg);
		assertTrue(matcher.matches());

		Packet packet = parser.parse(matcher, msg);
		assertNotNull(packet);
		assertTrue(packet instanceof CharsExistPacket);
		assertEquals(msg, packet.toString());

		CharsExistPacket charPacket = (CharsExistPacket) packet;
		assertEquals(1, charPacket.getSlot());
		assertEquals(2, charPacket.getId());
		assertEquals("myname", charPacket.getName());
		assertEquals(Race.byValue(2), charPacket.getRace());
		assertEquals(Sex.byValue(1), charPacket.getSex());
		assertEquals(4, charPacket.getHair());
		assertEquals(100, charPacket.getLevel());

		assertEquals(101, charPacket.getHp());
		assertEquals(102, charPacket.getMaxHp());
		assertEquals(103, charPacket.getMana());
		assertEquals(104, charPacket.getMaxMana());
		assertEquals(105, charPacket.getStamina());
		assertEquals(106, charPacket.getMaxStamina());
		assertEquals(107, charPacket.getElectricity());
		assertEquals(108, charPacket.getMaxElectricity());

		assertEquals(5, charPacket.getStrength());
		assertEquals(6, charPacket.getIntellect());
		assertEquals(7, charPacket.getDexterity());
		assertEquals(8, charPacket.getConstitution());
		assertEquals(9, charPacket.getLeadership());

		assertEquals(1, charPacket.getUnknown1());

		assertEquals(201, charPacket.getHelmetTypeId());
		assertEquals(-1, charPacket.getChestTypeId());
		assertEquals(203, charPacket.getPantsTypeId());
		assertEquals(-1, charPacket.getShoulderTypeId());
		assertEquals(205, charPacket.getBootsTypeId());
		assertEquals(206, charPacket.getOffhandTypeId());

		assertEquals(2, charPacket.getUnknown2());

	}

	/*
	 * 
	 * 
	 * charlist += "chars_exist " + slot + " " + (client.getVersion() >= 2000 ?
	 * rs.getString("id") + " " : "") // nga client have this extra value in the
	 * packet + rs.getString("name") + " " + rs.getString("race") + " " +
	 * rs.getString("sex") + " " + rs.getString("hair") + " " +
	 * rs.getString("level") + " " + 1 + " " //hp + 1 + " " //hp max + 1 + " "
	 * //mana + 1 + " " //mana max + 1 + " " //stamina + 1 + " " //stamina max +
	 * 1 + " " //electricity + 1 + " " //electricity max +
	 * rs.getString("strength") + " " + rs.getString("wisdom") + " " +
	 * rs.getString("dexterity") + " " + rs.getString("constitution") + " " +
	 * rs.getString("leadership") + " " + "0" + " " // unknown value +
	 * eq.getTypeId(Slot.HELMET) + " " + eq.getTypeId(Slot.CHEST) + " " +
	 * eq.getTypeId(Slot.PANTS) + " " + eq.getTypeId(Slot.SHOULDER) + " " +
	 * eq.getTypeId(Slot.BOOTS) + " " + eq.getTypeId(Slot.OFFHAND) + " 0\n";
	 * //unknown value
	 */

}
