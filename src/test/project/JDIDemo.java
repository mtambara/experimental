package test.project;

import java.util.List;
import java.util.Map;
import com.sun.jdi.Bootstrap;
import com.sun.jdi.Method;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventIterator;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.MethodEntryEvent;
import com.sun.jdi.event.MethodExitEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.MethodEntryRequest;
import com.sun.jdi.request.MethodExitRequest;
import com.sun.jdi.request.ThreadDeathRequest;

public class JDIDemo {

	public static void main(String[] args) throws Exception {
		int debugPort = 8000;
//		int lineNumber = 17;
//		String varName = "counter";

		VirtualMachineManager vmMgr = Bootstrap.virtualMachineManager();
		AttachingConnector socketConnector = null;
		List<AttachingConnector> attachingConnectors = vmMgr.attachingConnectors();
		for (AttachingConnector ac : attachingConnectors) {
			if (ac.transport().name().equals("dt_socket")) {
				socketConnector = ac;
				break;
			}
		}

		if (socketConnector != null) {
			Map paramsMap = socketConnector.defaultArguments();
			Connector.IntegerArgument portArg = (Connector.IntegerArgument) paramsMap.get("port");
			portArg.setValue(debugPort);
			VirtualMachine vm = socketConnector.attach(paramsMap);
			System.out.println("Attached to process '" + vm.name() + "'");

//			List<ReferenceType> referenceTypes = vm.classesByName("test.package.CounterTest");
//			System.out.println(referenceTypes);
			
			EventRequestManager evtReqMgr = vm.eventRequestManager();

			ClassPrepareRequest cReq = evtReqMgr.createClassPrepareRequest();
			cReq.addClassFilter("test.project.CounterIncRunnable");
			cReq.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
			cReq.enable();

			MethodEntryRequest mReqInc = evtReqMgr.createMethodEntryRequest();
			mReqInc.addClassFilter("test.project.CounterIncRunnable");
			mReqInc.setSuspendPolicy(EventRequest.SUSPEND_NONE);
			ThreadReference threadInc = null;
//			mReqInc.enable();

			MethodExitRequest mReqDec = evtReqMgr.createMethodExitRequest();
			mReqDec.addClassFilter("test.project.CounterDecRunnable");
			mReqDec.setSuspendPolicy(EventRequest.SUSPEND_NONE);
			ThreadReference threadDec = null;
			mReqDec.enable();
			
			ThreadDeathRequest tReq = null;
			BreakpointRequest bReq = null;
			
			ThreadReference threadBreak = null;

			EventQueue evtQueue = vm.eventQueue();
			while (true) {
				EventSet evtSet = evtQueue.remove();
				EventIterator evtIter = evtSet.eventIterator();
//					System.out.println(evtSet);
				while (evtIter.hasNext()) {
					try {
						Event evt = evtIter.next();
						System.out.println("");
						System.out.println(evt);
						EventRequest evtReq = evt.request();
						if (evtReq.equals(mReqInc)) {
							MethodEntryEvent methodEntryEvent = (MethodEntryEvent) evt;
//							if (methodEntryEvent.location().lineNumber() == 56) {
								threadInc = methodEntryEvent.thread();
								if (!threadInc.name().contains("main")) {
									System.out.println(threadInc.name());
									threadInc.suspend();
								}
//							}
						}
						else if (evtReq.equals(mReqDec)) {
							MethodExitEvent methodExitEvent = (MethodExitEvent) evt;
								System.out.println(methodExitEvent.thread().name());

//							if ((tReq == null) & !methodExitEvent.thread().name().contains("main")) {
//								System.out.println("Death set for: " + methodExitEvent.thread().name());
//								tReq = evtReqMgr.createThreadDeathRequest();
//								tReq.addThreadFilter(methodExitEvent.thread());
//							}

							threadBreak.name();
							if ((methodExitEvent.location().lineNumber() == 100) & (threadBreak != null)) {
								threadBreak.resume();
							}
						}
						else if (evtReq.equals(tReq)) {
							System.out.println("Dec thread dies");
							threadInc.resume();
						}
						else if (evtReq.equals(bReq)) {
							System.out.println("Breakpoint at " + bReq.location());
							BreakpointEvent bEvt = (BreakpointEvent) evt;
							threadBreak = bEvt.thread();
							threadBreak.suspend();
							System.out.println(threadBreak.name() + " suspended");
						}
						else if (evtReq.equals(cReq)) {
							List<ReferenceType> referenceTypes = vm.classesByName("test.project.CounterIncRunnable");
							
							ReferenceType referenceType = referenceTypes.get(0);

							List<Method> methods = referenceType.methodsByName("run");

							Method method = methods.get(0);
							
//							System.out.println(method.location());
							
							bReq = evtReqMgr.createBreakpointRequest(method.location());
							bReq.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
							bReq.enable();

							System.out.println(evtReqMgr.breakpointRequests());

//							List<ReferenceType> referenceTypes = vm.allClasses();
						}
						else if (evt instanceof VMDisconnectEvent) {
							System.out.println("VM disconnected");
							break;
						}
					} catch (VMDisconnectedException aie) {
						System.out.println("AbsentInformationException: did you compile your target application with -g option?");
					} catch (Exception exc) {
//							System.out.println(exc.getClass().getName() + ": " + exc.getMessage());
					} finally {
						evtSet.resume();
					}
				}
			}
		}
	}
}
