/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi.events;

/**
 *
 * @author Matthew Tambara
 */
public class ThreadEvent extends JVMEventImpl {

	public ThreadEvent(RequestType requestType) {
		if (!(requestType == RequestType.THREAD_START ||
			  requestType == RequestType.THREAD_DEATH)) {

			throw new RuntimeException("Request type must be thread start " +
				"or thread death");
		}

		_requestType = requestType;
	}
}
