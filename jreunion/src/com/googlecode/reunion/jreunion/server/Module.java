package com.googlecode.reunion.jreunion.server;

import java.util.List;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
interface Module {
	void addChild(Module childModule);

	void doStart() throws Exception;

	void doStop() throws Exception;

	void doWork() throws Exception;

	List<Module> getChildren();

	Module getParent();

	void start() throws Exception;

	void stop() throws Exception;

	void Work() throws Exception;
}