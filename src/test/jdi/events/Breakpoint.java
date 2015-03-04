/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi.events;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.request.BreakpointRequest;
import java.lang.reflect.Method;
import test.jdi.JDIUtil;

/**
 *
 * @author Matthew Tambara
 */
public class Breakpoint extends MethodEvent {

	public Breakpoint(Method method) {
		this(method, null);
	}

	public Breakpoint(Method method, ObjectReference instance) {
		super(RequestType.BREAKPOINT, method.getDeclaringClass(), instance);

		setMethod(method);
	}

	public Method getMethod() {
		return _method;
	}

	public String getMethodName() {
		return _method.getName();
	}

	public String getMethodSignature() {
		return _methodSignature;
	}

	public BreakpointRequest getRequest() {
		return (BreakpointRequest) _eventRequest;
	}

	protected void setDeclaringClass(String declaringClass) {
		_declaringClass = declaringClass;
	}

	protected void setMethod(Method method) {
		_method = method;

		setDeclaringClass(_method.getDeclaringClass().getName());

		setMethodSignature(JDIUtil.getJNISignature(_method));
	}

	protected void setMethodSignature(String methodSignature) {
		_methodSignature = methodSignature;
	}

	private Method _method;
	private String _methodSignature;

}
