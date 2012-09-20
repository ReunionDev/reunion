package org.reunionemu.jreunion.server.beans;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;


@Configuration()
public class PropertiesLocationsBean implements ApplicationContextAware {

	ApplicationContext context;
	
	@Bean(name="propertiesLocations")
	public List<Resource> propertiesLocations(){
		try {
			Resource[] classpathPropertiesFiles = context.getResources("classpath*:/properties/**/*.xml");		
			Resource[] fileSystemPropertiesFiles = context.getResources("file:config/**/*.xml");
			return Arrays.asList(ArrayUtils.addAll(classpathPropertiesFiles, fileSystemPropertiesFiles));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}
}
