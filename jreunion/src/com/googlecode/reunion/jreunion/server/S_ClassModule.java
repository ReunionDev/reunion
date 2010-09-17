package com.googlecode.reunion.jreunion.server;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
abstract class S_ClassModule implements S_Module {
	protected S_Module parentModule;

	private List<S_Module> childModules = new Vector<S_Module>();

	public boolean activeModule;

	public S_ClassModule() {
		activeModule = false;
		parentModule = null;
	}

	public S_ClassModule(S_Module parent) {
		super();
		activeModule = false;

		parentModule = parent;
		parentModule.addChild(this);

	}

	@Override
	public void addChild(S_Module childModule) {
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
	public List<S_Module> getChildren() {
		List<S_Module> children = new Vector<S_Module>();		
		Iterator<S_Module> iter = childModules.iterator();
		while (iter.hasNext()) {
			children.add(iter.next());
		}
		return children;
	}

	@Override
	public S_Module getParent() {
		return parentModule;
	}
}