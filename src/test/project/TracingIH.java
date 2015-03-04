/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.project;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author matthew
 */
public class TracingIH implements InvocationHandler {
		public static Object createProxy( Object obj) {
			return Proxy.newProxyInstance(
				obj.getClass().getClassLoader(), obj.getClass().getInterfaces(),
				new TracingIH(obj));
		}
		
		private Object target;
		
		private TracingIH( Object obj) {
			target = obj;
		}
		
		public Object invoke( Object proxy, Method method, Object[] args )
			throws Throwable {

			Object result = null;

			try {
				System.out.println( method.getName() + "(...) called" );
				result = method.invoke( target, args );
			} catch (InvocationTargetException e) {
				System.out.println(method.getName() + " throws " + e.getCause());
				throw e.getCause();
			}

			System.out.println( method.getName() + " returns" );
			
			return result;
			}
	}
