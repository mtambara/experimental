/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi.events;

import com.sun.jdi.BooleanValue;
import com.sun.jdi.ByteValue;
import com.sun.jdi.CharValue;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.IntegerValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ShortValue;
import com.sun.jdi.Value;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Tambara
 */
public class FieldEvent extends ClassVisableEvent {

	public FieldEvent(RequestType requestType, Field field) {
		this(requestType, field, null);
	}

	public FieldEvent(
		RequestType requestType, Field field, ObjectReference instance) {

		if (requestType == RequestType.ACCESS_WATCHPOINT ||
			requestType == RequestType.MODIFICATION_WATCHPOINT) {
		}
		else {
			throw new RuntimeException("Request type must be access " +
				"watchpoint or modification watchpoint");
		}

		_declaringClass = field.getDeclaringClass().getName();

		_field = field;

		_requestType = requestType;

		_instance = instance;
	}

	public Field getField() {
		return _field;
	}

	public <T> T getValueToBe() {
		if (_requestType == RequestType.MODIFICATION_WATCHPOINT) {
			return _convertValue(_valueToBe);
		}
		
		throw new RuntimeException("Access does not have a value to be");
	}
	
	private <T> T _convertValue(Value value) {
		if (value instanceof PrimitiveValue) {
			PrimitiveValue primitiveValue = (PrimitiveValue)value;

			if (primitiveValue instanceof BooleanValue) {
				return (T)(new Boolean(primitiveValue.booleanValue()));
			}
			if (primitiveValue instanceof ByteValue) {
				return (T)(new Byte(primitiveValue.byteValue()));
			}
			if (primitiveValue instanceof CharValue) {
				return (T)(new Character(primitiveValue.charValue()));
			}
			if (primitiveValue instanceof DoubleValue) {
				return (T)(new Double(primitiveValue.doubleValue()));
			}
			if (primitiveValue instanceof FloatValue) {
				return (T)(new Float(primitiveValue.floatValue()));
			}
			if (primitiveValue instanceof IntegerValue) {
				return (T)(new Integer(primitiveValue.intValue()));
			}
			if (primitiveValue instanceof LongValue) {
				return (T)(new Long(primitiveValue.longValue()));
			}
			if (primitiveValue instanceof ShortValue) {
				return (T)(new Short(primitiveValue.shortValue()));
			}
			
			throw new RuntimeException();
		}
		return null;
	}

	public List<Method> getTrace() {
		return _trace;
	}

	public void setCurrentValue(Value value) {
		_currentValue = value;
	}

	public void setValueToBe(Value value) {
		_valueToBe = value;
	}

	public void trace(Method method) {
		_trace.add(method);
	}

	protected Value _currentValue;
	protected Field _field;
	protected List<Method> _trace = new ArrayList<>();
	protected Value _valueToBe;

}
