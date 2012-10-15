package org.reunionemu.jreunion.api;

import java.util.Collection;
import java.util.Iterator;

import org.reunionemu.jreunion.server.LocalMap;
import org.reunionemu.jreunion.server.Map;
import org.reunionemu.jreunion.server.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/online")
public class PlayerController {
	
	@Autowired
	Server server;
	
	//@Cacheable(value="default")
	@RequestMapping(value="{mapId}", method = RequestMethod.GET)
	public @ResponseBody int getCount(@PathVariable int mapId) {
		
		Map map = server.getWorld().getMap(mapId);
		if(map!=null && map instanceof LocalMap){
			return ((LocalMap) map).getPlayerList().size();
		}
		throw new RuntimeException("");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody int getCountTotal() {
 
		Collection<Map> maps = server.getWorld().getMaps();
		Iterator<Map> iter = maps.iterator();
		int total = 0;
		
		while(iter.hasNext()){
			Map map = iter.next();

			if( map instanceof LocalMap){
				total += ((LocalMap) map).getPlayerList().size();
			}
			
		}
		return total;
	}

}
