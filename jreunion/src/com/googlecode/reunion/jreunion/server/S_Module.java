package com.googlecode.reunion.jreunion.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aidamina
 * @license http://reunion.googlecode.com/svn/trunk/license.txt
 */
interface S_Module {
	void AddChild(S_Module childModule);

	void DoStart() throws Exception;

	void DoStop() throws Exception;

	void DoWork() throws Exception;

	List<S_Module> GetChildren();

	S_Module GetParent();

	void Start() throws Exception;

	void Stop() throws Exception;

	void Work() throws Exception;
}
