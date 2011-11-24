package com.googlecode.reunion.jreunion.server.packets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.parser.Parseable;

public class InventoryPacket extends Packet implements Parseable<InventoryPacket> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InventoryPacket() {
	}
	
	private int x;
	private int y;
	private int tab;
	
	public int getTab() {
		return tab;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public Pattern[] getPatterns() {
		return new Pattern [] {Pattern.compile("^inven (\\d+) (\\d+) (\\d+)$")};
	}

	@Override
	public InventoryPacket parse(Matcher matcher) {

		InventoryPacket packet = new InventoryPacket();
		packet.tab = Integer.parseInt(matcher.group(1));
		packet.x = Integer.parseInt(matcher.group(2));
		packet.y = Integer.parseInt(matcher.group(3));
		return packet;
	}

}
