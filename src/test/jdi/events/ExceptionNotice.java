/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi.events;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.request.ExceptionRequest;
import java.util.Objects;

/**
 *
 * @author Matthew Tambara
 */
public class ExceptionNotice extends ClassVisableEvent {
	public ExceptionNotice(Class cls) {
		this(cls, null);
	}

	public void addClassExclusionFilter(String name) {
		ExceptionRequest exceptionRequest = (ExceptionRequest)getRequest();
		
		if (exceptionRequest.isEnabled()) {
			exceptionRequest.disable();
			exceptionRequest.addClassExclusionFilter(name);
			exceptionRequest.enable();
		}
		else {
			exceptionRequest.addClassFilter(name);
		}
	}

	public void addClassFilter(String name) {
		ExceptionRequest exceptionRequest = (ExceptionRequest)getRequest();
		
		if (exceptionRequest.isEnabled()) {
			exceptionRequest.disable();
			exceptionRequest.addClassFilter(name);
			exceptionRequest.enable();
		}
		else {
			exceptionRequest.addClassFilter(name);
		}
	}

	public void addClassFilter(Class cls) {
		addClassFilter(cls.getName());
	}
	public ExceptionNotice(Class cls, ObjectReference instance) {

		_declaringClass = cls.getName();

		_requestType = RequestType.EXCEPTION;

		_instance = instance;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final ExceptionNotice other = (ExceptionNotice) obj;

		return Objects.equals(_requestType, other.getRequestType());
	}

}