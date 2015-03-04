/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.project;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 *
 * @author matthew
 */
public class Test {
	public static void main(String[] args) {
		HelloWorld prox = (HelloWorld)Proxy.newProxyInstance(HelloWorld.class.getClassLoader(), new Class<?>[]{HelloWorld2.class}, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.out.println(method.getDeclaringClass());
				Class<?> ret = method.getReturnType();
				System.out.println(ret.getCanonicalName());
				return method.invoke(new HellowWorldImpl(), args);
				
			}
		
	});
		
		prox.hellowWorld();
		prox.toString();
	}
}
