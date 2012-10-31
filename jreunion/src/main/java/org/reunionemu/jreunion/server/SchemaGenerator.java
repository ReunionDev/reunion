package org.reunionemu.jreunion.server;

import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.cfg.Configuration;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public class SchemaGenerator {

	public SchemaGenerator() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:/spring/**/*-context.xml");

		LocalContainerEntityManagerFactoryBean emf = context.getBean(LocalContainerEntityManagerFactoryBean.class);
		  PersistenceUnitInfo persistenceUnitInfo = emf.getPersistenceUnitInfo();
		    java.util.Map<String, Object> jpaPropertyMap = emf.getJpaPropertyMap();

		    Configuration configuration = new Ejb3Configuration().configure( persistenceUnitInfo, jpaPropertyMap ).getHibernateConfiguration();

		    SchemaExport schema = new SchemaExport(configuration);
		    schema.setOutputFile("schema.sql");
		    schema.create(true, false);
		    context.destroy();

	}

}
