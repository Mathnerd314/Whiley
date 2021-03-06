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

package wyjc.attributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import wyjvm.io.BinaryOutputStream;
import wyjvm.lang.BytecodeAttribute;
import wyjvm.lang.Constant;

/**
 * The WhileyVersion attribute is simply a marker used to indicate that a class
 * file was generated from a whiley source file. This is useful in
 * multi-platform scenarios where we might have multiple source languages.
 * 
 * @author djp
 * 
 */
public class WhileyVersion implements BytecodeAttribute {
	private int minor;
	private int major;
	
	public WhileyVersion(int major, int minor) {
		this.major = major;
		this.minor = minor;
	}
	public String name() {
		return "WhileyVersion";
	}
	
	public void write(BinaryOutputStream writer,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {		
		writer.write_u2(constantPool.get(new Constant.Utf8("WhileyVersion")));
		writer.write_u4(2);
		writer.write_u1(major);
		writer.write_u1(minor);
	}	
	
	public void addPoolItems(Set<Constant.Info> constantPool, ClassLoader loader) {		
		Constant.addPoolItem(new Constant.Utf8("WhileyVersion"), constantPool);		
	}
	
	public void print(PrintWriter output,
			Map<Constant.Info, Integer> constantPool, ClassLoader loader)
			throws IOException {
		output.println("  WhileyVersion: " + major + "." + minor);
	}
}
