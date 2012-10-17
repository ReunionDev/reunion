package org.reunionemu.jreunion.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class TypeLoader<T> {
	
	@Autowired
	ItemManager itemManager;
	
	@SuppressWarnings("unchecked")
	public T load(int typeId){
		
		return (T)itemManager.getItemType(typeId);
		
	}

}
