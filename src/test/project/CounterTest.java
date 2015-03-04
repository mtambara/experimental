/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.project;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 *
 * @author matthew
 */
public class CounterTest {
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
		String jvmName = runtimeBean.getName();
		System.out.println("JVM Name = " + jvmName);
		long pid = Long.valueOf(jvmName.split("@")[0]);
		System.out.println("JVM PID  = " + pid);
		try {
			Counter.test();
		}
		catch (Exception e) {
		}
		System.out.println("Runtime: " + (System.currentTimeMillis()-startTime) + " ms");

	}
}
