package netty;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.regex.*;

import org.reunionemu.jreunion.protocol.*;
import org.reunionemu.jreunion.protocol.old.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class FilteredParser extends ArrayList<PacketParser> implements Parser{

	private static final long serialVersionUID = 1L;

	@Autowired
	public void setParsers(List<PacketParser> parsers){
		this.clear();
		List <Class<?>> filters = getAnnotationFilter();
		for(PacketParser parser: parsers){
			boolean passesFilters = true;
			List<Annotation> annots = Arrays.asList(parser.getClass().getAnnotations());
			for(Class<?> filter : filters){
				boolean filterFound = false;
				for(Annotation annot:annots){
					if(annot.annotationType().equals(filter)){
						filterFound = true;
						break;
					}
				}
				if(!filterFound){
					passesFilters = false;
				}
			}
			if(passesFilters){
				add(parser);
			}
		}
	}
	
	public List<Class<?>> getAnnotationFilter(){
		return Collections.emptyList();
	}

	@Override
	public Packet parse(String input) {
		for(PacketParser parser: this){
			Pattern pattern = parser.getPattern();
			Matcher matcher = pattern.matcher(input);
			if(matcher.matches()){
				return parser.parse(matcher, input);
			}
		}
		throw new IllegalStateException("Unknown packet: "+input);
	}

}
