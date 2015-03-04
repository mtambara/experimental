/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.project;

/**
 *
 * @author matthew
 */
public class Counter {

	static int _max = 100000;

    public static void test() throws InterruptedException {
		long startTime = System.currentTimeMillis();
        MyCounter counter = new MyCounter();

		Thread incThread = makeIncThread(counter);
		Thread decThread = makeDecThread(counter);
        
        incThread.setName("inc thread");

        decThread.setName("dec thread");

        decThread.start();
        incThread.start();

        decThread.join();
        incThread.join();

        System.out.println(counter.value());
		System.out.println("Runtime: " + (System.currentTimeMillis()-startTime) + " ms");
    }
	
	public static Thread makeIncThread(MyCounter counter) {
		return new Thread(new CounterIncRunnable(counter, _max));
	}

	public static Thread makeDecThread(MyCounter counter) {
		return new Thread(new CounterDecRunnable(counter, _max));
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
		System.out.println("Inc: "+Thread.currentThread().getName());
        for ( int i=0; i<100000; i++ ) {
            _counter.increment();
			if (i==(_max/4)) {
				System.out.println("Inc 25%");	
			}
			else if (i==(_max/2)) {
				System.out.println("Inc 50%");	
			}
			else if (i==(3*(_max/4))) {
				System.out.println("Inc 75%");	
			}
			else if (i==(_max-1)) {
				System.out.println("Inc 100%");	
			}
        }
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
		System.out.println("Dec: "+Thread.currentThread().getName());
        for ( int i=0; i<100000; i++ ) {
            _counter.decrement();
			if (i==(_max/4)) {
				System.out.println("Dec 25%");	
			}
			else if (i==(_max/2)) {
				System.out.println("Dec 50%");	
			}
			else if (i==(3*(_max/4))) {
				System.out.println("Dec 75%");	
			}
			else if (i==(_max-1)) {
				System.out.println("Dec 100%");	
			}
        }
    }
}


class MyCounter {
    private int c = 0;

    public void increment() {
        c++;
    }

    public void decrement() {
        c--;
    }

    public  int value() {
        return c;
    }    
}
