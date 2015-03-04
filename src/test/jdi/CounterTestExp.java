/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi;

import test.jdi.events.MethodEvent;
import java.lang.reflect.Method;
import test.jdi.events.ExceptionNotice;

/**
 *
 * @author matthew
 */
public class CounterTestExp {
	public static void main(String[] args) {
		System.out.println("Entered Main");

		Counter.setCounter(10000);

		try {
			JDIUtil.attach();

Thread.sleep(1000);
//			ts.attach("127.0.0.1:8000", 0, 0);


//#############################
//			Method method =
//				CounterDecRunnable.class.getDeclaredMethod(
//					"end", new Class<?>[]{boolean.class, byte.class, char.class,
//					short.class, int.class, long.class, float.class,
//					double.class, String.class, String[][].class});
//
//			MethodEvent methodExitEvent = JDIUtil.addMethodExit(CounterDecRunnable.class);
//			MethodEvent methEntryEvent = JDIUtil.addMethodEntry(CounterIncRunnable.class);
//
//			Field field = MyCounter.class.getDeclaredField("c");
//
//			System.out.println("field: " + field);

//			JDIUtil.addModificationWatchpoint(field);
//			FieldEvent accessFieldEvent = JDIUtil.addAccessWatchpoint(field);
//			accessFieldEvent.enable();
//
//			ThreadEvent threadEvent = JDIUtil.addThreadStart();
//			ThreadEvent threadDeathEvent = JDIUtil.addThreadDeath();
//			threadEvent.enable();
//			threadDeathEvent.enable();
//			test1();
//##############################
//			System.out.println("Exit Trace: " + methodExitEvent.getTrace());
//			System.out.println("Entry Trace: " + methEntryEvent.getTrace());
//
//			System.out.println("Test: " + methodEvent.getMethod());

//			Breakpoint breakpoint = JDIUtil.addBreakpoint(method);

//			test2(breakpoint);

//############################
//			Class clazz = MyCounter.class;
//
//			Method method = clazz.getDeclaredMethod("value");
//
//			test3(method);

//############################
			Class.forName("java.lang.NoSuchMethodException");
			ExceptionNotice exceptionNotice = JDIUtil.addExceptionNotice(NoSuchMethodException.class);
			exceptionNotice.enable();
//			exceptionNotice.addClassExclusionFilter("*.ExceptionRunnable");
			test4();

//			JDIUtil.disconnect();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void test1() {
		long startTime = System.currentTimeMillis();

		MyCounter myCounter = new MyCounter();

		Thread incThread = Counter.makeIncThread(myCounter);
		incThread.setName("inc thread");

		Thread decThread = Counter.makeDecThread(myCounter);
		decThread.setName("dec thread");

		try {
			decThread.start();
//Thread.sleep(100);
			incThread.start();


			decThread.join();
			incThread.join();

			System.out.println(myCounter.value());
			System.out.println("Runtime: " + (System.currentTimeMillis()-startTime) + " ms");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void test2(MethodEvent breakpoint) {
		breakpoint.enable();

		long startTime = System.currentTimeMillis();

		MyCounter myCounter = new MyCounter();

		Thread incThread = Counter.makeIncThread(myCounter);
		incThread.setName("inc thread");

		Thread decThread = Counter.makeDecThread(myCounter);
		decThread.setName("dec thread");

		try {
			decThread.start();

			breakpoint.waitForHit();

			incThread.start();

			decThread.join();
			incThread.join();

			System.out.println(myCounter.value());
			System.out.println("Runtime: " + (System.currentTimeMillis()-startTime) + " ms");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void test3(Method method) {
		long startTime = System.currentTimeMillis();

		MyCounter counterInc = new MyCounter();
		MyCounter counterDec = new MyCounter();

		MethodEvent breakpoint = JDIUtil.addBreakpoint(method, counterDec);

		breakpoint.enable();

		breakpoint.suspendOnHit(true);

		int max = 10000000;
		Thread decThread = new Thread(new CounterDecValueRunnable(counterDec, 10000));
		decThread.setName("dec thread");

		Thread incThread = new Thread(new CounterIncValueRunnable(counterInc, 10000000));
		incThread.setName("inc thread");

		try {
			decThread.start();
			incThread.start();

			incThread.join();

			if (breakpoint.isHit() & breakpoint.isThreadSuspended()) {
				breakpoint.resume();
			}


			decThread.join();

//			System.out.println(myCounter.value());
			System.out.println("Runtime: " + (System.currentTimeMillis()-startTime) + " ms");
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void test4() {

			Thread thread = new Thread(new ExceptionRunnable());
			thread.setName("test4Thread");
			thread.start();
	}
}



class ExceptionRunnable implements Runnable {

	@Override
	public void run() {
		try {
			throw new NoSuchMethodException();
		}
		catch (Exception e) {
		}
	}
}




