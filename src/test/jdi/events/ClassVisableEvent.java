/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi.events;

import com.sun.jdi.ObjectReference;
import test.jdi.JDIUtil;

/**
 *
 * @author Matthew Tambara
 */
public class ClassVisableEvent extends JVMEventImpl {

	public String getDeclaringClass() {
		return _declaringClass;
	}

	public ObjectReference getInstance() {
		return _instance;
	}

	protected String _declaringClass;
	protected ObjectReference _instance;

}
