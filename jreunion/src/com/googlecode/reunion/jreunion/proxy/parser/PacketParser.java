package com.googlecode.reunion.jreunion.proxy.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.reunion.jreunion.server.Client;

public abstract class PacketParser {
	
	public abstract void handle(Client client, Matcher matcher);
	
	
	public abstract Pattern [] getPatterns();


}
