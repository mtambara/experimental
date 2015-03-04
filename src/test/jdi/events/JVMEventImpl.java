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
public class JVMEventImpl implements JVMEvent {

	@Override
	public void addCountfilter(int count) {
		if (_eventRequest.isEnabled()) {
			_eventRequest.disable();
			_eventRequest.addCountFilter(count);
			_eventRequest.enable();
		} else {
			_eventRequest.addCountFilter(count);
		}
	}

	@Override
	public void disable() {
		_eventRequest.disable();
	}

	@Override
	public void enable() {
		_eventRequest.enable();
	}

	@Override
	public EventRequest getRequest() {
		return _eventRequest;
	}

	@Override
	public RequestType getRequestType() {
		return _requestType;
	}

	@Override
	public ThreadReference getThread() {
		return _thread;
	}

	public boolean isEnabled() {
		return _eventRequest.isEnabled();
	}

	@Override
	public boolean isHit() {
		return _hit;
	}

	@Override
	public boolean isThreadSuspended() {
		if (_thread == null) {
			return false;
		}

		return _thread.isSuspended();
	}

	@Override
	public void resume() {
		if (_thread.isSuspended()) {
			System.out.println("#Resuming thread");
			_thread.resume();
		}
	}

	@Override
	public void setHit(boolean hit, ThreadReference thread) {
		_hit = hit;

		_thread = thread;

		if (_suspendOnHit) {
			suspend();
		}
	}

	@Override
	public void setRequest(EventRequest eventRequest) {
		System.out.println("Setting event");
		_eventRequest = eventRequest;
	}

	@Override
	public void setSuspendPolicy(int policy) {
		if (_eventRequest.isEnabled()) {
			_eventRequest.disable();
			
			System.out.println("here");
			
			_eventRequest.setSuspendPolicy(policy);
			
			_eventRequest.enable();
		} else {
			_eventRequest.setSuspendPolicy(policy);
		}
	}

	@Override
	public void suspend() {
		_thread.suspend();
	}

	@Override
	public void suspendOnHit(boolean suspend) {
		_suspendOnHit = suspend;
	}

	@Override
	public int suspendPolicy() {
		return _eventRequest.suspendPolicy();
	}

	@Override
	public synchronized void waitForHit() throws InterruptedException {
		this.wait();
	}

	@Override
	public synchronized void waitForHit(long timeout) throws InterruptedException {
		this.wait(timeout);
	}

	protected EventRequest _eventRequest;
	protected boolean _hit;
	protected RequestType _requestType;
	protected boolean _suspendOnHit;
	protected ThreadReference _thread;
}
