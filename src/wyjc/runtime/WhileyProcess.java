// Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//    * Redistributions of source code must retain the above copyright
//      notice, this list of conditions and the following disclaimer.
//    * Redistributions in binary form must reproduce the above copyright
//      notice, this list of conditions and the following disclaimer in the
//      documentation and/or other materials provided with the distribution.
//    * Neither the name of the <organization> nor the
//      names of its contributors may be used to endorse or promote products
//      derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
// ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package wyjc.runtime;

import java.util.*;
import java.lang.reflect.*;
import java.util.concurrent.ArrayBlockingQueue;

public final class WhileyProcess extends Thread {
	private Object state;
	private ArrayBlockingQueue<Message> queue = new ArrayBlockingQueue<Message>(10); 
	
	public WhileyProcess(Object c) {
		state = c;
	}

	public Object state() {
		return state;
	}		
	
	public WhileyProcess clone() {
		return new WhileyProcess(this.state);
	}

	/**
	 * Send a message asynchronously to this actor. If the mailbox is full, then
	 * this will in fact block.
	 * 
	 * @param m
	 * @param arguments
	 */
	public void asyncSend(Method method, Object... arguments) {
		queue.add(new Message(method,arguments));
	}
	
	public void run() {
		// this is where the action happens
		while(1==1) {
			try {
				Message m = queue.take();
				m.method.invoke(null, m.arguments);
			} catch(InterruptedException e) {
				// do nothing I guess
			} catch(IllegalAccessException e) {
				// do nothing I guess
			} catch(InvocationTargetException ex) {
				// not sure what to do!
				Throwable e = ex.getCause();
				if(e instanceof RuntimeException) {
					RuntimeException re = (RuntimeException) e;
					throw re;
				}
				// do nothing I guess
			}
		}
	}
	
	public String toString() {
		return state + "@" + System.identityHashCode(this);
	}
	
	public static WhileyProcess systemProcess() {
		// Not sure what the default value should be yet!!!
		HashMap<String,Object> fields = new HashMap<String,Object>();
		fields.put("out",new WhileyProcess(null));
		return new WhileyProcess(new WhileyRecord(fields));
	}
	
	private final static class Message {
		public final Method method;
		public final Object[] arguments;
		
		public Message(Method method, Object[] arguments) {
			this.method = method;
			this.arguments = arguments;
		}				
	}
}
