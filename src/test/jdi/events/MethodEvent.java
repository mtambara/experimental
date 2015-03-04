/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi.events;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import test.jdi.JDIUtil;

/**
 *
 * @author Matthew Tambara
 */
public class MethodEvent extends ClassVisableEvent {

	public MethodEvent(RequestType requestType, Class cls) {
		this(requestType, cls, null);
	}

	public MethodEvent(
		RequestType requestType, Class cls, ObjectReference instance) {

		if (!(requestType == RequestType.BREAKPOINT ||
			 requestType == RequestType.METHOD_ENTRY ||
			 requestType == RequestType.METHOD_EXIT)) {
			throw new RuntimeException("Request type must be breakpoint, " +
				"method exit, or method entry");
		}

		_declaringClass = cls.getName();

		_requestType = requestType;

		_instance = instance;
	}

	public void addMethod(com.sun.jdi.Method method) {
		_trace.add(method);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final MethodEvent other = (MethodEvent) obj;

		return Objects.equals(_requestType, other.getRequestType());
	}

	public java.lang.reflect.Method getMethod() {
		return _reflectionMethod;
	}

	public List<com.sun.jdi.Method> getTrace() {
		return _trace;
	}

	public void setMethod(com.sun.jdi.Method method) {
		_JDIMethod = method;
		_reflectionMethod = JDIUtil.JDI2Reflection(method);
	}

	@Override
	public String toString() {
		return _requestType + "{class=" + _declaringClass + '}';
	}

	protected com.sun.jdi.Method _JDIMethod;
	protected java.lang.reflect.Method _reflectionMethod;
	protected List<com.sun.jdi.Method> _trace = new ArrayList<>();

}
