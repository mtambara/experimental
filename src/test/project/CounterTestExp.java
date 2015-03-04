/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.project;

import com.sun.jdi.VirtualMachine;

/**
 *
 * @author matthew
 */
public class CounterTestExp {
	public static void main(String[] args) {
		Thread JDIThread = new Thread(new JDIRunnable());

		JDIThread.setName("JDIThread");
		System.out.println();
		try {
			VirtualMachine vm = JDIDemoExp.attach();
			JDIDemoExp.prepare("test.project.CounterIncRunnable","run");
			JDIThread.start();
			Counter.test();

			vm.exit(0);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void test1() {
		
		Thread counterThread = new Thread(new CounterRunnable());

		counterThread.setName("CounterThread");

		try {
			counterThread.start();
			counterThread.join();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
	
	class JDIRunnable implements Runnable {

		@Override
		public void run() {
			try {
				System.out.println("Begin Debug");
				JDIDemoExp.debug();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	class CounterRunnable implements Runnable {

		@Override
		public void run() {
			try {
				System.out.println("Begin Test");
				Counter.test();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

