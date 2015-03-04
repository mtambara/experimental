/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.ListeningConnector;
import com.sun.jdi.connect.spi.TransportService;
import com.sun.jdi.request.EventRequest;
import com.sun.tools.jdi.SocketTransportService;
import java.io.InputStream;
import java.io.OutputStream;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import test.jdi.events.Breakpoint;
import test.jdi.events.ClassVisableEvent;
import test.jdi.events.ExceptionNotice;
import test.jdi.events.FieldEvent;
import test.jdi.events.JVMEvent;
import test.jdi.events.MethodEvent;
import test.jdi.events.RequestType;
import test.jdi.events.ThreadEvent;

/**
 *
 * @author Matthew Tambara
 */
public class JDIUtil {

	public static java.lang.reflect.Method JDI2Reflection(
		com.sun.jdi.Method method) {

		List<String> parameterNames = method.argumentTypeNames();

		java.lang.reflect.Method reflectionMethod = null;

		try {
			Class[] parameters = getClassArray(parameterNames);

			String className = method.declaringType().name();

			Class clz = Class.forName(className);

			reflectionMethod = clz.getDeclaredMethod(
				method.name(), parameters);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return reflectionMethod;
	}

	public static FieldEvent addAccessWatchpoint(Field field) {
		return addAccessWatchpoint(field, null);
	}

	public static FieldEvent addAccessWatchpoint(Field field, Object instance) {
		return addFieldEvent(RequestType.ACCESS_WATCHPOINT, field, instance);
	}

	public static Breakpoint addBreakpoint(Method method) {
		return addBreakpoint(method, null);
	}

	public static Breakpoint addBreakpoint(Method method, Object instance) {
		ObjectReference objectReference = createObjectReference(instance);

		Breakpoint breakpoint = new Breakpoint(method, objectReference);

		JDIDemoExp.addClassVisibleEvent(breakpoint);

		_eventSet.add(breakpoint);

		return breakpoint;
	}

	public static ExceptionNotice addExceptionNotice(Class cls) {
		return addExceptionNotice(cls, null);
	}

	public static ExceptionNotice addExceptionNotice(
		Class cls, Object instance) {

		ObjectReference objectReference = createObjectReference(instance);

		ExceptionNotice exceptionNotice = new ExceptionNotice(
			cls, objectReference);

		JDIDemoExp.addClassVisibleEvent(exceptionNotice);

		_eventSet.add(exceptionNotice);

		return exceptionNotice;
	}

	public static FieldEvent addFieldEvent(
		RequestType requestType, Field field, Object instance) {

		ObjectReference objectReference = createObjectReference(instance);

		FieldEvent fieldEvent = new FieldEvent(
			requestType, field, objectReference);

		JDIDemoExp.addClassVisibleEvent(fieldEvent);

		_eventSet.add(fieldEvent);

		return fieldEvent;
	}

	public static MethodEvent addMethodEntry(Class cls) {
		return addMethodEntry(cls, null);
	}

	public static MethodEvent addMethodEntry(
		Class cls, Object instance) {

		return addMethodEvent(RequestType.METHOD_ENTRY, cls, instance);
	}

	public static MethodEvent addMethodEvent(
		RequestType requestType, Class cls, Object instance) {

		ObjectReference objectReference = createObjectReference(instance);

		MethodEvent methodEvent = new MethodEvent(
			requestType, cls, objectReference);

		JDIDemoExp.addClassVisibleEvent(methodEvent);

		_eventSet.add(methodEvent);

		return methodEvent;
	}

	public static MethodEvent addMethodExit(Class cls) {
		return addMethodExit(cls, null);
	}

	public static MethodEvent addMethodExit(Class cls, Object instance) {

		return addMethodEvent(RequestType.METHOD_EXIT, cls, instance);
	}

	public static FieldEvent addModificationWatchpoint(Field field) {
		return addModificationWatchpoint(field, null);
	}

	public static FieldEvent addModificationWatchpoint(
		Field field, Object instance) {

		return addFieldEvent(
			RequestType.MODIFICATION_WATCHPOINT, field, instance);
	}

	public static ThreadEvent addThreadDeath() {
		return addThreadEvent(RequestType.THREAD_DEATH);
	}

	public static ThreadEvent addThreadEvent(RequestType requestType) {
		ThreadEvent threadEvent = new ThreadEvent(requestType);

		JDIDemoExp.addThreadEvent(threadEvent);

		_eventSet.add(threadEvent);

		return threadEvent;
	}

	public static ThreadEvent addThreadStart() {
		return addThreadEvent(RequestType.THREAD_START);
	}

	public static void attach() throws Exception {

		boolean debug = true;

		ServerSocket serverSocket = new ServerSocket(8000);
//
		Socket socket = null;
//
		if (debug) {
			socket = serverSocket.accept();
		}

//		String testName =
//			Thread.currentThread().getStackTrace()[2].getClassName();
		Thread JDIThread = new Thread(new JDIRunnable(socket));

		JDIThread.setDaemon(true);
		JDIThread.setName("JDIThread");

		JDIThread.start();

		if (socket != null) {
			synchronized (socket) {
				socket.wait();
			}
		}

		JDIDemoExp.setObjectReferenceMap(_objectReferenceMap);

	}

	public static ObjectReference createObjectReference(Object obj) {
		if (obj == null) {
			return null;
		}

		Class clazz = obj.getClass();

		ObjectReference objectReference = null;

		try {
			Method method = clazz.getMethod("toString");

			Breakpoint breakpoint = addObjectReferenceBreakpoint(method);

			obj.toString();

			objectReference = _objectReferenceMap.get(breakpoint);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return objectReference;
	}

	public static void start() throws Exception {
		JDIDemoExp.debug();
	}

	public static void disableAll(RequestType requestType) {
		for (JVMEvent jvmEvent : _eventSet) {
			if (jvmEvent.getRequestType() == requestType) {
				jvmEvent.disable();
			}
		}
	}

	public static void disableAll() {
		for (JVMEvent jvmEvent : _eventSet) {
			jvmEvent.disable();
		}
	}

	public static void disconnect() throws Exception {
		JDIDemoExp.disconnect();
	}

	public static void enableAll(RequestType requestType) {
		for (JVMEvent jvmEvent : _eventSet) {
			if (jvmEvent.getRequestType() == requestType) {
				jvmEvent.enable();
			}
		}
	}

	public static void enableAll() {
		for (JVMEvent jvmEvent : _eventSet) {
			jvmEvent.enable();
		}
	}

	public static List<JVMEvent> getAll(RequestType requestType) {
		List<JVMEvent> ret = new ArrayList<>();

		for (JVMEvent jvmEvent : _eventSet) {
			if (jvmEvent.getRequestType() == requestType) {
				ret.add(jvmEvent);
			}
		}

		return ret;
	}

	public static List<JVMEvent> getAll() {
		List<JVMEvent> ret = new ArrayList<>();

		for (JVMEvent jvmEvent : _eventSet) {
			ret.add(jvmEvent);
		}

		return ret;
	}

	public static String getJNISignature(Method method) {
		StringBuilder sb = new StringBuilder();

		sb.append("(");

		Class<?>[] args = method.getParameterTypes();

		if (args.length > 0) {
			for (Class arg : args) {
				if (arg.isArray()) {
					sb.append(arg.getName().replace(".", "/"));
				}
				else {
					sb.append(getJNISignature(arg.getName()));
				}
			}
		}

		sb.append(")");

		Class returnValue = method.getReturnType();

		if (returnValue.isArray()) {
			sb.append(returnValue.getName().replace(".", "/"));
		}
		else {
			sb.append(getJNISignature(returnValue.getName()));
		}

		return sb.toString();
	}

	protected static Breakpoint addObjectReferenceBreakpoint(Method method) {
		Breakpoint breakpoint = new Breakpoint(method);

		_objectReferenceMap.put(breakpoint, null);

		JDIDemoExp.addClassVisibleEvent(breakpoint);

		return breakpoint;
	}

	protected static Class arrayString2Class(String className)
		throws Exception {

		StringBuilder sb = new StringBuilder();

		while (className.contains("[]")) {
			sb.append("[");

			className = className.substring(0, (className.length() - 2));
		}

		String name = getJNISignature(className);

		name = name.replace("/", ".");

		sb.append(name);

		return Class.forName(sb.toString());

	}

	protected static Class[] getClassArray(List<String> parameters)
		throws Exception {

		List<Class> classList = new ArrayList<>();

		for (String className : parameters) {
			if (className.contains("[]")) {
				classList.add(arrayString2Class(className));
			}
			else {
				classList.add(string2Class(className));
			}
		}
		return classList.toArray(new Class[classList.size()]);
	}

	protected static String getJNISignature(String className) {
		switch (className) {
			case "boolean":
				return "Z";

			case "byte":
				return "B";

			case "char":
				return "C";

			case "short":
				return "S";

			case "int":
				return "I";

			case "long":
				return "J";

			case "float":
				return "F";

			case "double":
				return "D";

			default:
				return ("L" + className.replace(".", "/") + ";");
		}
	}

	protected static Class string2Class(String className) {
		switch (className) {
			case "boolean":
				return boolean.class;

			case "byte":
				return byte.class;

			case "char":
				return char.class;

			case "short":
				return short.class;

			case "int":
				return int.class;

			case "long":
				return long.class;

			case "float":
				return float.class;

			case "double":
				return double.class;

			default:
				try {
					return Class.forName(className);
				}
				catch (ClassNotFoundException cnfe) {
					throw new RuntimeException(cnfe);
				}
		}
	}

	static class JDIRunnable implements Runnable {

		private Socket _socket;

		protected JDIRunnable(Socket socket) {
			_socket = socket;
		}

		@Override
		public void run() {
			try {
				System.out.println("Begin Debug");
				JDIDemoExp.attach(_socket);

				if (_socket != null) {
					synchronized(_socket) {
						_socket.notify();
					}
				}

				System.out.println("Set up done");
				JDIUtil.start();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	private static final Set<JVMEvent> _eventSet = new HashSet<>();

	private static final Map<ClassVisableEvent, ObjectReference> _objectReferenceMap = Collections.synchronizedMap(
		new HashMap<ClassVisableEvent, ObjectReference>());

	public static int SUSPEND_EVENT_THREAD = EventRequest.SUSPEND_EVENT_THREAD;
	public static int SUSPEND_NONE = EventRequest.SUSPEND_NONE;

	private static final VirtualMachineManager _virtualMachineManager = Bootstrap.virtualMachineManager();

}
