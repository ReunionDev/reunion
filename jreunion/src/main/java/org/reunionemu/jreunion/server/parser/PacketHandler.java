package org.reunionemu.jreunion.server.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.reunionemu.jreunion.server.Client;

public abstract class PacketHandler {
	
	public abstract void handle(Client client, Matcher matcher);
	
	
	public abstract Pattern [] getPatterns();


}
