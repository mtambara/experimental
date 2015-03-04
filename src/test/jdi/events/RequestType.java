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
public enum RequestType {

	BREAKPOINT, METHOD_ENTRY, METHOD_EXIT, ACCESS_WATCHPOINT, 
	MODIFICATION_WATCHPOINT, EXCEPTION, THREAD_START, THREAD_DEATH;

}
