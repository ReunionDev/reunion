package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.googlecode.reunion.jreunion.events.EventBroadcaster;
import com.googlecode.reunion.jreunion.events.EventListener;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
abstract class ClassModule extends EventBroadcaster implements Module {
	protected Module parentModule;

	private List<Module> childModules = new Vector<Module>();

	public boolean activeModule;

	public ClassModule() {
		activeModule = false;
		parentModule = null;
	}

	public ClassModule(Module parent) {
		super();
		activeModule = false;

		parentModule = parent;
		parentModule.addChild(this);

	}

	@Override
	public void addChild(Module childModule) {
		childModules.add(childModule);
	}

	@Override
	public void doStart() throws Exception {
		try {
			activeModule = true;
			start();
			for (int i = 0; i < childModules.size(); i++) {
				childModules.get(i).doStart();
			}
		} catch (Exception e) {

			throw e;
		}

	}

	@Override
	public void doStop() throws Exception {
		try {
			activeModule = false;
			stop();
			for (int i = 0; i < childModules.size(); i++) {
				childModules.get(i).doStop();
			}
		} catch (Exception e) {

			throw e;
		}
	}

	@Override
	public void doWork() throws Exception {
		try {
			Work();
			for (int i = 0; i < childModules.size(); i++) {
				childModules.get(i).doWork();
			}
		} catch (Exception e) {

			throw e;
		}
	}

	@Override
	public List<Module> getChildren() {
		List<Module> children = new Vector<Module>();		
		Iterator<Module> iter = childModules.iterator();
		while (iter.hasNext()) {
			children.add(iter.next());
		}
		return children;
	}

	@Override
	public Module getParent() {
		return parentModule;
	}
}