package org.reunionemu.jreunion.server.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reunionemu.jreunion.server.packets.Packet;


public interface Parseable<T extends Packet> {
	
	public Pattern [] getPatterns();
	
	public T parse(Matcher matcher);
}
