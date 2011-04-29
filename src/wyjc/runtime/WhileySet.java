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

public final class WhileySet extends HashSet {
	public WhileySet() {
		super();
	}
	
	public WhileySet(Collection c) {
		super(c);
	}
	
	public WhileySet clone() {
		return Util.set_clone(this);		
	}
	
	public String toString() {
		String r = "{";
		boolean firstTime=true;
		ArrayList<Comparable> ss = new ArrayList<Comparable>(this);		
		Collections.sort(ss);

		for(Object o : ss) {
			if(!firstTime) {
				r = r + ", ";
			}
			firstTime=false;
			r = r + o.toString();
		}
		return r + "}";
	}		
	
	public boolean equals(WhileySet ws) {
		// FIXME: optimisation opportunity here
		return super.equals(ws);
	}
	
	public boolean notEquals(WhileySet ws) {
		return !super.equals(ws);
	}
	
	public boolean subset(WhileySet ws) {
		return ws.containsAll(this) && ws.size() > size();
	}
	
	public boolean subsetEq(WhileySet ws) {
		return ws.containsAll(this);
	}
	
	public WhileySet union(WhileySet rset) {
		WhileySet set = new WhileySet(this);
		set.addAll(rset);
		return set;
	}
	
	public WhileySet difference(WhileySet rset) {
		WhileySet set = new WhileySet(this);
		set.removeAll(rset);
		return set;
	}
	
	public WhileySet intersect(WhileySet rset) {
		WhileySet set = new WhileySet(); 		
		for(Object o : this) {
			if(rset.contains(o)) {
				set.add(o);
			}
		}
		return set;
	}
}