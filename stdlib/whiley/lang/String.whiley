// This file is part of the Whiley Compiler
//
// The Whiley Compiler is free software; you can redistribute it 
// and/or modify it under the terms of the GNU General Public 
// License as published by the Free Software Foundation; either 
// version 2 of the License, or (at your option) any later version.
//
// The Whiley Compiler is distributed in the hope
// that it will be useful, but WITHOUT ANY WARRANTY; without 
// even the implied warranty of MERCHANTABILITY or FITNESS FOR 
// A PARTICULAR PURPOSE.  See the GNU General Public License 
// for more details.
//
// You should have received a copy of the GNU General Public 
// License along with the Java Compiler Kit; if not, 
// write to the Free Software Foundation, Inc., 59 Temple Place, 
// Suite 330, Boston, MA  02111-1307  USA
//
// (C) David James Pearce, 2009. 

package whiley.lang

define string as [char]

public string str(* item):
    if item ~= null:
        return "null"
    extern jvm:
        aload 0
        invokevirtual java/lang/Object.toString:()Ljava/lang/String;
        invokestatic wyil/jvm/rt/Util.fromString:(Ljava/lang/String;)Ljava/util/ArrayList;
        areturn
    return "DUMMY" // dead code

// Convert an integer into a hex string
public string hexStr(int item):    
    r = []
    while item > 0:
        v = item / 16
        w = item - (v*16)
        if w <= 9:                
            r = ['0' + w] + r
        else:
            w = w - 10
            r = ['a' + w] + r
        item = v
    return r
