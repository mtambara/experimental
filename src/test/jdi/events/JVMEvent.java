/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.jdi.events;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.request.EventRequest;

/**
 *
 * @author Matthew Tambara
 */
public interface JVMEvent {

	public void addCountfilter(int count);

	public void disable();

	public void enable();

	public EventRequest getRequest();

	public RequestType getRequestType();

	public ThreadReference getThread();

	public boolean isHit();

	public boolean isThreadSuspended();

	public void resume();

	public void setHit(boolean hit, ThreadReference threadReference);

	public void setRequest(EventRequest eventRequest);

	public void setSuspendPolicy(int policy);

	public void suspend();

	public void suspendOnHit(boolean suspend);
	
	public int suspendPolicy();

	public void waitForHit() throws InterruptedException;

	public void waitForHit(long timeout) throws InterruptedException;

}
