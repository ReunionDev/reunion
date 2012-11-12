package org.reunionemu.jreunion.server.lock;

import java.io.Serializable;
import java.util.*;

public class LockServer {
	
	HashMap<Class<?>, HashMap<Serializable,List<Condition>>> locks = new HashMap<Class<?>, HashMap<Serializable,List<Condition>>>();
	
	public HashMap<Serializable,List<Condition>> getMapForType(Class<?> clz){
		
		if(!locks.containsKey(clz)){
			locks.put(clz, new HashMap<Serializable, List<Condition>>());
		}
		return locks.get(clz);
	}
	
	
	public boolean aquire(Class<?> clz, Serializable id, List<Condition> conditions){
		
		HashMap<Serializable,List<Condition>> map = getMapForType(clz);
		boolean foundAndValid = false;
		if(map.containsKey(id)){
			List<Condition> clist = map.get(id);
			foundAndValid = true;
			if(clist!=null){
				for(Condition cond: clist){
					if(!cond.condition()){
						foundAndValid = false;
					}
					break;					
				}
			}
		}
		
		if(foundAndValid){
			return false;
		}else{
			map.put(id, conditions);
			return true;			
		}
	}
	
	public void release(Class<?> clz, Serializable id){
		HashMap<Serializable,List<Condition>> map = getMapForType(clz);
		map.remove(id);
		
	}
	
	public interface Condition{
		public boolean condition();
	}

}
