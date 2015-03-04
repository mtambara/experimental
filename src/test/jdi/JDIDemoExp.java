package test.jdi;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.ClassType;
import com.sun.jdi.Field;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.spi.Connection;
import com.sun.jdi.event.AccessWatchpointEvent;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.ExceptionEvent;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.ModificationWatchpointEvent;
import com.sun.jdi.event.ThreadDeathEvent;
import com.sun.jdi.event.ThreadStartEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.AccessWatchpointRequest;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.ExceptionRequest;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ModificationWatchpointRequest;
import com.sun.jdi.request.ThreadDeathRequest;
import com.sun.jdi.request.ThreadStartRequest;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.jdi.SocketTransportService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import test.jdi.events.Breakpoint;
import test.jdi.events.ClassVisableEvent;
import test.jdi.events.ExceptionNotice;
import test.jdi.events.FieldEvent;
import test.jdi.events.MethodEvent;
import test.jdi.events.RequestType;
import test.jdi.events.ThreadEvent;

/**
 *
 * @author Matthew Tambara
 */
public class JDIDemoExp {

	public static void addClassVisibleEvent(ClassVisableEvent classVisableEvent) {
		System.out.println("Submitting");

		String className = classVisableEvent.getDeclaringClass();

		List<ReferenceType> loadedClasses = vm.classesByName(className);

		RequestType requestType = classVisableEvent.getRequestType();

		if (loadedClasses.isEmpty()) {
			ClassPrepareRequest classPrepareRequest = getClassRequest(
				className);

			classPrepareRequest.enable();

			Set<ClassVisableEvent> classVisableEvents =
				(Set)classPrepareRequest.getProperty(requestType);

			if (classVisableEvents == null) {
				classVisableEvents = new HashSet<>();
			}

			classVisableEvents.add(classVisableEvent);

			classPrepareRequest.putProperty(requestType, classVisableEvents);
		}
		else {
			switch (requestType) {
				case ACCESS_WATCHPOINT:
					submitAccessWatchpointRequest(
						(ClassType)loadedClasses.get(0),
						(FieldEvent)classVisableEvent);
					break;

				case BREAKPOINT:
					BreakpointRequest breakpointRequest =
						submitBreakpoint(
							(ClassType)loadedClasses.get(0),
							(Breakpoint)classVisableEvent);

					if (_objectReferenceMap.keySet().contains(
						classVisableEvent)) {

						breakpointRequest.putProperty(
							_IS_OBJECT_REFERENCE, true);
					}
					break;

				case EXCEPTION:
					submitException(
						(ClassType)loadedClasses.get(0),
						(ExceptionNotice)classVisableEvent);
					break;

				case METHOD_ENTRY:
					submitMethodEntry(
						(ClassType)loadedClasses.get(0),
						(MethodEvent)classVisableEvent);
					break;

				case METHOD_EXIT:
					submitMethodExit(
						(ClassType)loadedClasses.get(0),
						(MethodEvent)classVisableEvent);
					break;

				case MODIFICATION_WATCHPOINT:
					submitModificationWatchpointRequest(
						(ClassType)loadedClasses.get(0),
						(FieldEvent)classVisableEvent);
					break;

				default:
					throw new RuntimeException("Invalid request type");
			}
		}
	}

	public static void addThreadEvent(ThreadEvent threadEvent) {
		if (threadEvent.getRequestType() == RequestType.THREAD_START) {
			submitThreadStartRequest(threadEvent);
		}
		else {
			submitThreadDeathRequest(threadEvent);
		}
	}

	private static void printPacket(String header, byte[] packet, File file) {
		byte[] size, id, flag, commandSet, command, data, errorCode;

		size = Arrays.copyOfRange(packet, 0, 4);

		id = Arrays.copyOfRange(packet, 4, 8);

		flag = Arrays.copyOfRange(packet, 8, 9);

		StringBuilder sb = new StringBuilder();

		sb.append("\n" + header);
		sb.append("\n\tSize: " + Arrays.toString(size));
		sb.append("\n\tId: " + Arrays.toString(id));
		sb.append("\n\tflag: " + Arrays.toString(flag));

		if (flag[0] == -128) {
			errorCode = Arrays.copyOfRange(packet, 9, 11);

			sb.append("\n\tError code: " + Arrays.toString(errorCode));
		}
		else {
			commandSet = Arrays.copyOfRange(packet, 9, 10);

			command = Arrays.copyOfRange(packet, 10, 11);

			sb.append("\n\tCommand set: " + Arrays.toString(commandSet));
			sb.append("\n\tCommand: " + Arrays.toString(command));
		}

		data = Arrays.copyOfRange(packet, 11, packet.length);

		sb.append("\n\tData: " + Arrays.toString(data));

		String out = sb.toString();

		try (FileWriter fileWriter = new FileWriter(file, true)) {
			fileWriter.write(out);
			fileWriter.flush();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		System.out.println(out);
	}

	private static String _address;

	//Debugger
	public static void attach(Socket socket) throws Exception {
		SocketTransportService socketTransportService = 
			new SocketTransportService();

		Connection connection = socketTransportService.attach(
			"9000", 0, 0);

		if (socket != null) {
			JDIProxy jdiProxy = new JDIProxy(connection, socket);

			connection = jdiProxy.init();
			jdiProxy.start();
		}

		vm = vmMgr.createVirtualMachine(connection);

	}

	public static void check() {
		String name = ClassNotFoundException.class.getName();

		System.out.println(vm.classesByName(name));
	}

	public static void debug() throws Exception {
		EventQueue eventQueue = vm.eventQueue();

		_eventRequestManager = vm.eventRequestManager();

		while (true) {
			EventSet eventSet = eventQueue.remove();

			if (eventSet == null) {
				continue;
			}

			EventIterator eventItr = eventSet.eventIterator();

			while (eventItr.hasNext()) {
				try {
					Event event = eventItr.next();

//					System.out.println("Event: " + event);

					EventRequest eventRequest = event.request();

					if (eventRequest instanceof BreakpointRequest) {
						Boolean objectRequest =
							(Boolean)eventRequest.getProperty(
								_IS_OBJECT_REFERENCE);

						if ((objectRequest != null) && objectRequest) {
							doObjectReferenceEvent(event);
						}
						else {
							doBreakpointEvent(event);
						}
					}
					else if (eventRequest instanceof AccessWatchpointRequest) {
						doAccessWatchpointEvent(event);
					}
					else if (eventRequest instanceof ClassPrepareRequest) {
						doClassPrepareEvent(event);
					}
					else if (eventRequest instanceof ExceptionRequest) {
						doExceptionEvent(event);
					}
					else if (eventRequest instanceof MethodEntryRequest) {
						doMethodEntryEvent(event);
					}
					else if (eventRequest instanceof MethodExitRequest) {
						doMethodExitEvent(event);
					}
					else if (eventRequest instanceof ModificationWatchpointRequest) {
						doModificationWatchpointEvent(event);
					}
					else if (eventRequest instanceof ThreadDeathRequest) {
						doThreadDeathEvent(event);
					}
					else if (eventRequest instanceof ThreadStartRequest) {
						doThreadStartEvent(event);
					}
					else if ((event instanceof VMDisconnectEvent) ||
						(event instanceof VMDeathEvent)) {

						System.out.println("VM disconnected");

						return;
					}
				}
				finally {
					eventSet.resume();
				}
			}
		}
	}

	public static void disconnect() {
		vm.dispose();
	}

	public static void setObjectReferenceMap(
		Map<ClassVisableEvent, ObjectReference> objectReferenceMap) {
		_objectReferenceMap = objectReferenceMap;
	}

	private static void doBreakpointEvent(Event event) {
		BreakpointEvent breakpointEvent = (BreakpointEvent)event;

		BreakpointRequest breakpointRequest =
			(BreakpointRequest)event.request();

		Breakpoint breakpoint = (Breakpoint)breakpointRequest.getProperty(
			RequestType.BREAKPOINT);

		synchronized (breakpoint) {
			breakpoint.notify();
		}

		breakpoint.setHit(true, breakpointEvent.thread());

		breakpointRequest.disable();

		breakpointRequest.putProperty(RequestType.BREAKPOINT, breakpoint);
	}

	private static void doClassPrepareEvent(Event event) {
		ClassPrepareEvent classPrepareEvent = (ClassPrepareEvent)event;

		ClassPrepareRequest classPrepareRequest =
			(ClassPrepareRequest)event.request();

		ClassType classType = (ClassType)classPrepareEvent.referenceType();

		Set<Breakpoint> breakpoints =
			(Set)classPrepareRequest.getProperty(RequestType.BREAKPOINT);

		if (breakpoints != null) {
			for (Breakpoint breakpoint : breakpoints) {
				submitBreakpoint(classType, breakpoint);
			}
		}

		Set<MethodEvent> methodEvents = (Set)classPrepareRequest.getProperty(
			RequestType.METHOD_ENTRY);

		if (methodEvents != null) {
			for (MethodEvent methodEntryEvent : methodEvents) {
				submitMethodEntry(classType, methodEntryEvent);
			}
		}

		methodEvents = (Set)classPrepareRequest.getProperty(
			RequestType.METHOD_EXIT);

		if (methodEvents != null) {
			for (MethodEvent methodExitEvent : methodEvents) {
				submitMethodExit(classType, methodExitEvent);
			}
		}

		Set<FieldEvent> fieldEvents = (Set)classPrepareRequest.getProperty(
			RequestType.ACCESS_WATCHPOINT);

		if (fieldEvents != null) {
			for (FieldEvent accessFieldEvent : fieldEvents) {
				submitAccessWatchpointRequest(classType, accessFieldEvent);
			}
		}

		fieldEvents = (Set)classPrepareRequest.getProperty(
			RequestType.ACCESS_WATCHPOINT);

		if (fieldEvents != null) {
			for (FieldEvent modificationFieldEvent : fieldEvents) {
				submitModificationWatchpointRequest(classType, modificationFieldEvent);
			}
		}

		Set<ExceptionNotice> exceptionNotices =
			(Set)classPrepareRequest.getProperty(RequestType.EXCEPTION);

		if (exceptionNotices != null) {
			for (ExceptionNotice exceptionNotice : exceptionNotices) {
				submitException(classType, exceptionNotice);
			}
		}

		classPrepareRequest.disable();
	}

	private static void doExceptionEvent(Event event) {
		ExceptionEvent exceptionEvent = (ExceptionEvent)event;

		ExceptionRequest exceptionRequest = (ExceptionRequest)event.request();

		ExceptionNotice exceptionNotice =
			(ExceptionNotice)exceptionRequest.getProperty(
				RequestType.EXCEPTION);

		exceptionNotice.setHit(true, exceptionEvent.thread());

		System.out.println("Exception event hit");
	}

	private static void doAccessWatchpointEvent(Event event) {
		AccessWatchpointEvent accessWatchpointEvent =
			(AccessWatchpointEvent)event;

		AccessWatchpointRequest accessWatchpointRequest =
			(AccessWatchpointRequest)event.request();

		FieldEvent fieldEvent = (FieldEvent)accessWatchpointRequest.getProperty(
			RequestType.ACCESS_WATCHPOINT);

		fieldEvent.setHit(true, accessWatchpointEvent.thread());

		try{
			System.out.println(accessWatchpointEvent.valueCurrent() instanceof PrimitiveValue);
		}
		catch (Exception e) {

		}
//		System.out.println(
//			"Field accessed: " + accessWatchpointEvent.field().name());
	}

	private static void doModificationWatchpointEvent(Event event) {
		ModificationWatchpointEvent modificationWatchpointEvent =
			(ModificationWatchpointEvent)event;

		ModificationWatchpointRequest modificationWatchpointRequest =
			(ModificationWatchpointRequest)event.request();

		FieldEvent fieldEvent =
			(FieldEvent)modificationWatchpointRequest.getProperty(
				RequestType.MODIFICATION_WATCHPOINT);

		fieldEvent.setHit(true, modificationWatchpointEvent.thread());

		System.out.println(
			"Field modified: " + modificationWatchpointEvent.field().name());
	}

	private static void doMethodEntryEvent(Event event) {
		MethodEntryEvent methodEntryEvent = (MethodEntryEvent)event;

		MethodEntryRequest methodEntryRequest =
			(MethodEntryRequest)event.request();

		MethodEvent methodEvent = (MethodEvent)methodEntryRequest.getProperty(
			RequestType.METHOD_ENTRY);

		methodEvent.setHit(true, methodEntryEvent.thread());

		System.out.println(
			"Method hit in " + methodEntryEvent.method().declaringType() + ": " +
			methodEntryEvent.method().name());

		methodEvent.addMethod(methodEntryEvent.method());
	}

	private static void doMethodExitEvent(Event event) {
		MethodExitEvent methodExitEvent = (MethodExitEvent)event;

		MethodExitRequest methodExitRequest =
			(MethodExitRequest)event.request();

		System.out.println(
			"Method hit in " + methodExitEvent.method().declaringType() + ": " +
			methodExitEvent.method().name());

		MethodEvent methodEvent =
			(MethodEvent)methodExitRequest.getProperty(
				RequestType.METHOD_EXIT);

		methodEvent.setHit(true, methodExitEvent.thread());

		System.out.println("\nMethod: " + methodExitEvent.method());

		System.out.println("MethodEvent: " + methodEvent);
		if (!methodExitEvent.method().isConstructor()) {
			methodEvent.setMethod(methodExitEvent.method());
		}
		methodEvent.addMethod(methodExitEvent.method());
	}

	private static void doThreadDeathEvent(Event event) {
		ThreadDeathEvent threadDeathEvent = (ThreadDeathEvent)event;

		ThreadDeathRequest threadDeathRequest =
			(ThreadDeathRequest)event.request();

		System.out.println("Thread death: " + threadDeathEvent.thread().name());

		ThreadEvent threadEvent =
			(ThreadEvent)threadDeathRequest.getProperty(
				RequestType.THREAD_DEATH);

		threadEvent.setHit(true, threadDeathEvent.thread());
	}

	private static void doThreadStartEvent(Event event) {
		ThreadStartEvent threadStartEvent = (ThreadStartEvent)event;

		ThreadStartRequest threadStartRequest =
			(ThreadStartRequest)event.request();

		System.out.println("Thread start: " + threadStartEvent.thread().name());

		ThreadEvent threadEvent =
			(ThreadEvent)threadStartRequest.getProperty(
				RequestType.THREAD_START);

		threadEvent.setHit(true, threadStartEvent.thread());
	}

	private static void doObjectReferenceEvent(Event event)
		throws IncompatibleThreadStateException {

		BreakpointEvent breakpointEvent = (BreakpointEvent)event;

		BreakpointRequest breakpointRequest =
			(BreakpointRequest)event.request();

		ThreadReference threadReference = breakpointEvent.thread();

		threadReference.suspend();

		Breakpoint breakpoint =
			(Breakpoint)breakpointRequest.getProperty(RequestType.BREAKPOINT);

		_objectReferenceMap.put(
			breakpoint, threadReference.frames().get(0).thisObject());

		breakpointRequest.putProperty(_IS_OBJECT_REFERENCE, null);

		breakpointRequest.disable();

		threadReference.resume();
	}

	private static ClassPrepareRequest getClassRequest(String name) {
		if (_nameToClass.keySet().contains(name)) {
			return _nameToClass.get(name);
		}

		ClassPrepareRequest classPrepareRequest =
			_eventRequestManager.createClassPrepareRequest();

		classPrepareRequest.addClassFilter(name);
		classPrepareRequest.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);

		return classPrepareRequest;
	}

	private static AccessWatchpointRequest submitAccessWatchpointRequest(
		ClassType classType, FieldEvent fieldAccess) {

		Field field = classType.fieldByName(fieldAccess.getField().getName());

		if (field == null) {
			throw new RuntimeException(
				new NoSuchFieldException(fieldAccess.getField().getName()));
		}
		AccessWatchpointRequest accessWatchpointRequest =
			_eventRequestManager.createAccessWatchpointRequest(field);

		ObjectReference objectReference = fieldAccess.getInstance();

		if (objectReference != null) {
			accessWatchpointRequest.addInstanceFilter(objectReference);
		}

		accessWatchpointRequest.setSuspendPolicy(
			EventRequest.SUSPEND_EVENT_THREAD);

		accessWatchpointRequest.putProperty(
			RequestType.ACCESS_WATCHPOINT, fieldAccess);

		System.out.println(_eventRequestManager.accessWatchpointRequests());

		fieldAccess.setRequest(accessWatchpointRequest);

		return accessWatchpointRequest;
	}

	private static BreakpointRequest submitBreakpoint(
		ClassType classType, Breakpoint breakpoint) {

		Method method = classType.concreteMethodByName(
			breakpoint.getMethodName(), breakpoint.getMethodSignature());

		if (method == null) {
			throw new RuntimeException(
				new ClassNotFoundException(breakpoint.getMethodName()));
		}
		BreakpointRequest breakpointRequest =
			_eventRequestManager.createBreakpointRequest(method.location());

		ObjectReference objectReference = breakpoint.getInstance();

		if (objectReference != null) {
			breakpointRequest.addInstanceFilter(objectReference);
		}

		breakpointRequest.setSuspendPolicy(
			EventRequest.SUSPEND_EVENT_THREAD);

		breakpointRequest.putProperty(RequestType.BREAKPOINT, breakpoint);

		System.out.println(_eventRequestManager.breakpointRequests());

		breakpoint.setRequest(breakpointRequest);

		return breakpointRequest;
	}

	private static ExceptionRequest submitException(
		ClassType classType, ExceptionNotice exceptionNotice) {

		ExceptionRequest exceptionRequest =
			_eventRequestManager.createExceptionRequest(classType, true, true);

		ObjectReference objectReference = exceptionNotice.getInstance();

		if (objectReference != null) {
			exceptionRequest.addInstanceFilter(objectReference);
		}
		exceptionNotice.setRequest(exceptionRequest);

		exceptionRequest.setSuspendPolicy(
			EventRequest.SUSPEND_EVENT_THREAD);

		exceptionRequest.addClassExclusionFilter("*.jdi.*");

		exceptionRequest.putProperty(RequestType.EXCEPTION, exceptionNotice);

		System.out.println(_eventRequestManager.exceptionRequests());


		return exceptionRequest;
	}

	private static MethodEntryRequest submitMethodEntry(
		ClassType classType, MethodEvent methodEntry) {

		MethodEntryRequest methodEntryRequest =
			_eventRequestManager.createMethodEntryRequest();

		ObjectReference objectReference = methodEntry.getInstance();

		if (objectReference != null) {
			methodEntryRequest.addInstanceFilter(objectReference);
		}

		methodEntryRequest.addClassFilter(classType);

		methodEntryRequest.setSuspendPolicy(
			EventRequest.SUSPEND_EVENT_THREAD);

		methodEntryRequest.putProperty(RequestType.METHOD_ENTRY, methodEntry);

		System.out.println(_eventRequestManager.methodEntryRequests());

		methodEntry.setRequest(methodEntryRequest);

		return methodEntryRequest;
	}

	private static MethodExitRequest submitMethodExit(
		ClassType classType, MethodEvent methodExit) {

		MethodExitRequest methodExitRequest =
			_eventRequestManager.createMethodExitRequest();

		ObjectReference objectReference = methodExit.getInstance();

		if (objectReference != null) {
			methodExitRequest.addInstanceFilter(objectReference);
		}

		methodExitRequest.addClassFilter(classType);

		methodExitRequest.setSuspendPolicy(
			EventRequest.SUSPEND_EVENT_THREAD);

		methodExitRequest.putProperty(RequestType.METHOD_EXIT, methodExit);

		System.out.println(_eventRequestManager.methodExitRequests());

		methodExit.setRequest(methodExitRequest);

		return methodExitRequest;
	}

	private static ModificationWatchpointRequest
		submitModificationWatchpointRequest(
			ClassType classType, FieldEvent fieldAccess) {

			Field field = classType.fieldByName(
				fieldAccess.getField().getName());

			if (field == null) {
				throw new RuntimeException(
					new NoSuchFieldException(fieldAccess.getField().getName()));
			}

			ModificationWatchpointRequest modificationWatchpointRequest =
				_eventRequestManager.createModificationWatchpointRequest(field);

			ObjectReference objectReference = fieldAccess.getInstance();

			if (objectReference != null) {
				modificationWatchpointRequest.addInstanceFilter(
					objectReference);
			}

			modificationWatchpointRequest.setSuspendPolicy(
				EventRequest.SUSPEND_EVENT_THREAD);

			modificationWatchpointRequest.putProperty(
				RequestType.MODIFICATION_WATCHPOINT, fieldAccess);

			System.out.println(
				_eventRequestManager.modificationWatchpointRequests());

			fieldAccess.setRequest(modificationWatchpointRequest);

			return modificationWatchpointRequest;
		}

	private static void submitThreadStartRequest(ThreadEvent threadEvent) {
		ThreadStartRequest threadStartRequest =
				_eventRequestManager.createThreadStartRequest();

		threadEvent.setRequest(threadStartRequest);

		threadStartRequest.setSuspendPolicy(
			EventRequest.SUSPEND_EVENT_THREAD);

		threadStartRequest.putProperty(
				RequestType.THREAD_START, threadEvent);

		System.out.println(_eventRequestManager.threadStartRequests());
	}

	private static void submitThreadDeathRequest(ThreadEvent threadEvent) {
		ThreadDeathRequest threadDeathRequest =
				_eventRequestManager.createThreadDeathRequest();

		threadEvent.setRequest(threadDeathRequest);

		threadDeathRequest.setSuspendPolicy(
			EventRequest.SUSPEND_EVENT_THREAD);

		threadDeathRequest.putProperty(
				RequestType.THREAD_DEATH, threadEvent);

		System.out.println(_eventRequestManager.threadDeathRequests());
	}

	private static final String _IS_OBJECT_REFERENCE = "breakpoint.object";
	private static EventRequestManager _eventRequestManager;
	private static final Map<String, ClassPrepareRequest> _nameToClass =
		new HashMap<>();
	private static Map<ClassVisableEvent, ObjectReference>
		_objectReferenceMap;
	private static VirtualMachine vm = null;
	private static final VirtualMachineManager vmMgr =
		Bootstrap.virtualMachineManager();

}
