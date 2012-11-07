package netty;

import java.util.*;

import netty.parsers.PacketParser.Server;

public class ClientsideParser extends FilteredParser {

	private static final long serialVersionUID = 1L;

	@Override
	public List<Class<?>> getAnnotationFilter() {
		return Arrays.asList(new Class<?>[]{Server.class});
	}
}
