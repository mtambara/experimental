/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi;

import sun.security.util.KeyUtil;

/**
 *
 * @author matthew
 */
public class Counter {

	static int _max;

	public static void main(String[] args) {
		try {
			test();
		}
		catch (Exception e) {
		}
	}

	public static void test() throws InterruptedException {
		long startTime = System.currentTimeMillis();

		MyCounter counter = new MyCounter();

		Thread incThread = makeIncThread(counter);
		Thread decThread = makeDecThread(counter);

		incThread.setName("inc thread");

		decThread.setName("dec thread");

		incThread.start();
		decThread.start();

		incThread.join();
		decThread.join();

		System.out.println(counter.value());
		System.out.println("Runtime: " + (System.currentTimeMillis() - startTime) + " ms");
	}

	public static Thread makeIncThread(MyCounter counter) {
		return new Thread(new CounterIncRunnable(counter, _max));
	}

	public static Thread makeDecThread(MyCounter counter) {
		return new Thread(new CounterDecRunnable(counter, _max));
	}

	public static void setCounter(int max) {
		_max = max;
	}

}

class CounterIncRunnable implements Runnable {

	private MyCounter _counter;
	private int _max;

	public CounterIncRunnable(MyCounter counter, int max) {
		_counter = counter;
		_max = max;
	}

	@Override
	public void run() {
		System.out.println("\n" + Thread.currentThread().getName() + " start");
		for (int i = 0; i < _max; i++) {
			_counter.increment();
			if (i == (_max / 4)) {
				System.out.println("Inc 25%");
			}
			else if (i == (_max / 2)) {
				System.out.println("Inc 50%");
			}
			else if (i == (3 * (_max / 4))) {
				System.out.println("Inc 75%");
			}
			else if (i == (_max - 1)) {
				System.out.println("Inc 100%");
			}
		}
		System.out.println("Inc thread done");
	}

}

class CounterIncValueRunnable implements Runnable {

	private MyCounter _counter;
	private int _max;

	public CounterIncValueRunnable(MyCounter counter, int max) {
		_counter = counter;
		_max = max;
	}

	@Override
	public void run() {
		System.out.println("\n" + Thread.currentThread().getName() + " start");
		for (int i = 0; i < _max; i++) {
			_counter.increment();
			if (i == (_max / 4)) {
				System.out.println("Inc 25%");
			}
			else if (i == (_max / 2)) {
				System.out.println("Inc 50%");
			}
			else if (i == (3 * (_max / 4))) {
				System.out.println("Inc 75%");
			}
			else if (i == (_max - 1)) {
				System.out.println("Inc 100%");
			}
		}
		System.out.println("Inc Value: " + _counter.value());
		System.out.println("Inc thread done");
	}

}

class CounterDecRunnable implements Runnable {

	private MyCounter _counter;
	private int _max;

	public CounterDecRunnable(MyCounter counter, int max) {
		_counter = counter;
		_max = max;
	}

	@Override
	public void run() {
		System.out.println("\n" + Thread.currentThread().getName() + " start");
		for (int i = 0; i < _max; i++) {
			_counter.decrement();
			if (i == (_max / 4)) {
				System.out.println("Dec 25%");
			}
			else if (i == (_max / 2)) {
				System.out.println("Dec 50%");
			}
			else if (i == (3 * (_max / 4))) {
				System.out.println("Dec 75%");
			}
			else if (i == (_max - 1)) {
				System.out.println("Dec 100%");
			}
		}
		end(true, (byte) 0, 'n', (short) 0, 0, 0, 0, 0, "l", new String[][]{});
	}

	public String[] end(boolean Z, byte B, char C, short S, int I, long J, float F, double D, String L, String[][] i) {
		System.out.println("Dec thread done");
		return null;
	}

	public void end() {
		System.out.println("Dec thread done");
	}

}

class CounterDecValueRunnable implements Runnable {

	private MyCounter _counter;
	private int _max;

	public CounterDecValueRunnable(MyCounter counter, int max) {
		_counter = counter;
		_max = max;
	}

	@Override
	public void run() {
		System.out.println("\n" + Thread.currentThread().getName() + " start");
		for (int i = 0; i < _max; i++) {
			_counter.decrement();
			if (i == (_max / 4)) {
				System.out.println("Dec 25%");
			}
			else if (i == (_max / 2)) {
				System.out.println("Dec 50%");
			}
			else if (i == (3 * (_max / 4))) {
				System.out.println("Dec 75%");
			}
			else if (i == (_max - 1)) {
				System.out.println("Dec 100%");
			}
		}
		System.out.println("Dec Value: " + _counter.value());
		System.out.println("Dec thread done");
	}

}

class MyCounter {

	private int c = 0;

	public void increment() {
		c++;
		int a = c;
	}

	public void decrement() {
		c--;
	}

	public int value() {
		return c;
	}

}
