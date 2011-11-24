package org.reunionemu.jreunion.login.handlers;

import java.util.LinkedList;
import java.util.List;

import org.reunionemu.jreunion.server.ClassCollector;
import org.reunionemu.jreunion.server.packets.Packet;

public abstract class LoginHandler {
	
	@SuppressWarnings("unchecked")
	public static List<Class<? extends LoginHandler>> findAllHandlers(){
		List<Class<? extends LoginHandler>> handlers = new LinkedList<Class<? extends LoginHandler>>();
		for(Class<?> cls: ClassCollector.getClasses(LoginHandler.class.getPackage())){
			if(LoginHandler.class.isAssignableFrom(cls)&&!LoginHandler.class.equals(cls)){
				handlers.add((Class<? extends LoginHandler>) cls);
			}
		}
		return handlers;
	}
	
	public static void main(String []args) {
		for(Class<?> cls: LoginHandler.findAllHandlers()){
			System.out.println(cls);
		}
	}
	
	abstract List<Class<? extends Packet>> register();
}
