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

package wyil.lang;

import java.math.BigInteger;
import java.util.*;
import wyil.util.Pair;
import wyjc.runtime.BigRational;

public abstract class Value extends CExpr implements Comparable<Value> {	

	public static final Null V_NULL = new Null();
	
	public static Bool V_BOOL(boolean value) {
		return get(new Bool(value));
	}
	
	public static Int V_INT(BigInteger value) {
		return get(new Int(value));
	}

	public static Real V_REAL(BigRational value) {
		return get(new Real(value));
	}	

	public static Set V_SET(Collection<Value> values) {
		return get(new Set(values));
	}

	public static List V_LIST(Collection<Value> values) {
		return get(new List(values));
	}
	
	public static Record V_RECORD(Map<String,Value> values) {
		return get(new Record(values));
	}

	public static Dictionary V_DICTIONARY(
			java.util.Set<Pair<Value, Value>> values) {
		return get(new Dictionary(values));
	}

	public static TypeConst V_TYPE(Type type) {
		return get(new TypeConst(type));
	}
	
	public static FunConst V_FUN(NameID name, Type.Fun type) {
		return get(new FunConst(name,type));
	}
	
	/**
	 * Evaluate the given operation on the given values. If the evaluation is
	 * impossible, then return null.
	 * 
	 * @param op
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static Value evaluate(CExpr.BOP op, Value lhs, Value rhs) {
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());		
		lhs = convert(lub,lhs);
		rhs = convert(lub,rhs);
		if(lhs == null || rhs == null) {
			throw new IllegalArgumentException("Invalid arguments supplied to evaluate(BOP,Value,Value)");
		} else if(lub instanceof Type.Int || lub instanceof Type.Real) {
			return evaluateArith(op,lhs,rhs);
		} else if(lub instanceof Type.Set) {
			return evaluateSet(op,(Value.Set) lhs, (Value.Set) rhs);
		} else if(lub instanceof Type.List) {
			return evaluateList(op,(Value.List) lhs, (Value.List) rhs);
		}
		throw new IllegalArgumentException("Missing cases in evaluate(BOP,Value,Value)");
	}
	
	private static Value evaluateArith(CExpr.BOP op, Value lhs, Value rhs) {
		if(lhs instanceof Int){
			Int lv = (Int) lhs;
			Int rv = (Int) rhs;
			switch(op) {
			case ADD:
				return V_INT(lv.value.add(rv.value));
			case SUB:
				return V_INT(lv.value.subtract(rv.value));
			case MUL:
				return V_INT(lv.value.multiply(rv.value));
			case DIV:
				return V_INT(lv.value.divide(rv.value));
			}
		} else if(lhs instanceof Real) {
			Real lv = (Real) lhs;
			Real rv = (Real) rhs;
			switch(op) {
			case ADD:
				return V_REAL(lv.value.add(rv.value));
			case SUB:
				return V_REAL(lv.value.subtract(rv.value));
			case MUL:
				return V_REAL(lv.value.multiply(rv.value));
			case DIV:
				return V_REAL(lv.value.divide(rv.value));
			}
		}
		return null;
	}
	
	private static Value evaluateSet(CExpr.BOP op, Value.Set lhs, Value.Set rhs) {		
		switch(op) {
		case UNION:
		{			
			HashSet<Value> r = new HashSet<Value>(lhs.values);
			r.addAll(rhs.values);
			return V_SET(r);
		}
		case DIFFERENCE:
		{			
			HashSet<Value> r = new HashSet<Value>();
			for(Value v : lhs.values) {
				if(!(rhs.values.contains(v))) {
					r.add(v);
				}
			}
			return V_SET(r);
		}
		case INTERSECT:
			HashSet<Value> r = new HashSet<Value>();
			for(Value v : lhs.values) {
				if(rhs.values.contains(v)) {
					r.add(v);
				}
			}
			return V_SET(r);
		}
		return null;
	}
	
	private static Value evaluateList(CExpr.BOP op, Value.List lhs, Value.List rhs) {		
		switch(op) {
		case APPEND:
		{			
			ArrayList<Value> r = new ArrayList<Value>(lhs.values);
			r.addAll(rhs.values);
			return V_LIST(r);
		}		
		}
		return null;
	}
	
	public static CExpr evaluate(CExpr.NOP op, java.util.List<Value> args) {
		if(op == CExpr.NOP.LISTGEN) {
			return Value.V_LIST(args); 
		} else if(op == CExpr.NOP.SETGEN) {
			return Value.V_SET(args);
		} else if(op == CExpr.NOP.SUBLIST) {
			Value src = args.get(0);
			Value start = args.get(1);
			Value end = args.get(2);
			if (src instanceof List && start instanceof Int
					&& end instanceof Int) {
				List l = (List) src;
				Int s = (Int) start;
				Int e = (Int) end;
				// FIXME: potential bug here
				int si = s.value.intValue();
				int ei = e.value.intValue();
				if (si >= 0 && ei >= 0 && si < l.values.size()
						&& ei <= l.values.size()) {
					java.util.List nl = l.values.subList(si, ei);
					return V_LIST(nl);
				} else {
					return CExpr.NARYOP(op, l,s,e);
				}
			}
		} 		
		throw new IllegalArgumentException("Invalid operands to Value.evaluate(NOP,Value...)");		
	}
	
	public static CExpr evaluate(CExpr.UOP op, Value mhs) {
		switch(op) {
		case NEG:	
			if(mhs instanceof Int) {
				Int i = (Int) mhs;
				return V_INT(i.value.negate());
			} else if(mhs instanceof Real) {
				Real i = (Real) mhs;
				return V_REAL(i.value.negate());
			}
			break;
		case LENGTHOF:		
			if(mhs instanceof List) {
				List l = (List) mhs;
				return V_INT(BigInteger.valueOf(l.values.size()));
			} else if(mhs instanceof Set) {
				Set l = (Set) mhs;
				return V_INT(BigInteger.valueOf(l.values.size()));				
			}
			break;
		case PROCESSSPAWN:
		case PROCESSACCESS:
			return CExpr.UNOP(op,mhs);		
		}
		throw new IllegalArgumentException("Invalid operands to Value.evaluate(UOP,Value)");			
	}
	
	public static Boolean evaluate(Code.COP op, Value lhs, Value rhs) {
		Type lhs_t = lhs.type();
		Type rhs_t = rhs.type();
		Type lub = Type.leastUpperBound(lhs_t,rhs_t);
		
		if(lub instanceof Type.Int || lub instanceof Type.Real) {
			return evaluateArith(op,lhs,rhs);
		} else if(op == Code.COP.EQ) {
			return lhs.equals(rhs);
		} else if(op == Code.COP.NEQ) {
			return !lhs.equals(rhs);
		} else if (op == Code.COP.ELEMOF && rhs instanceof Value.Set) {
			Value.Set set = (Value.Set) rhs;
			return set.values.contains(lhs);
		} else if (op == Code.COP.ELEMOF && rhs instanceof Value.List) {
			Value.List list = (Value.List) rhs;
			return list.values.contains(lhs);
		} else if (op == Code.COP.SUBSET || op == Code.COP.SUBSETEQ) {
			return evaluateSet(op, lhs, rhs);
		} else if (op == Code.COP.SUBTYPEEQ) {
			TypeConst tc = (TypeConst) rhs;
			return Type.isSubtype(tc.type, lhs_t);
		} else if(rhs instanceof TypeConst) {
			TypeConst tc = (TypeConst) rhs;
			return !Type.isSubtype(tc.type,lhs_t);					
		} else {
			throw new IllegalArgumentException("Invalid operands to Value.evaluate(COP,Value,Value)");
		}
	}
	
	public static Boolean evaluateSet(Code.COP op, Value lhs, Value rhs) {
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());		
		Value.Set lv = (Value.Set) convert(lub,lhs); 
		Value.Set rv = (Value.Set) convert(lub,rhs);
		
		if(op == Code.COP.SUBSETEQ){			
			return rv.values.containsAll(lv.values);
		} else {
			return rv.values.containsAll(lv.values)
					&& rv.values.size() != lv.values.size();
		}
	}
	
	public static Boolean evaluateArith(Code.COP op, Value lhs, Value rhs) {		
		Type lub = Type.leastUpperBound(lhs.type(),rhs.type());		
		lhs = convert(lub,lhs);
		rhs = convert(lub,rhs);
		
		Comparable lv;
		Comparable rv;
		
		if(lub instanceof Type.Int) {
			lv = ((Int)lhs).value;
			rv = ((Int)rhs).value;			
		} else {
			lv = ((Real)lhs).value;
			rv = ((Real)rhs).value;			
		}		
		
		switch(op) {
		case LT:
			return lv.compareTo(rv) < 0;
		case LTEQ:
			return lv.compareTo(rv) <= 0;
		case GT:
			return lv.compareTo(rv) > 0;
		case GTEQ:
			return lv.compareTo(rv) >= 0;
		case EQ:
			return lv.equals(rv);
		case NEQ:
			return !lv.equals(rv);
		}
		
		throw new IllegalArgumentException("Invalid operands to Value.evaluateArith(COP,Value,Value)");
	}
	
	public static Value convert(Type t, Value val) {
		if (val.type().equals(t)) {
			return val;
		} else if (t instanceof Type.Real && val instanceof Int) {
			Int i = (Int) val;
			return new Real(new BigRational(i.value));
		} else if(t instanceof Type.Set) {
			Type.Set st = (Type.Set) t;
			if(val instanceof List) {
				List l = (List) val;
				ArrayList<Value> vs = new ArrayList<Value>();
				for(Value v : l.values) {
					vs.add(convert(st.element(),v));
				}
				return V_SET(vs);
			} else if(val instanceof Set) {
				Set s = (Set) val;
				ArrayList<Value> vs = new ArrayList<Value>();
				for(Value v : s.values) {
					vs.add(convert(st.element(),v));
				}
				return V_SET(vs);
			}
		} else if(t instanceof Type.List && val instanceof List) {
			Type.List st = (Type.List) t;			
			List l = (List) val;
			ArrayList<Value> vs = new ArrayList<Value>();
			for(Value v : l.values) {
				vs.add(convert(st.element(),v));
			}
			return V_LIST(vs);			
		}
		return null;
	}

	public static final class Null extends Value {				
		public Type type() {
			return Type.T_NULL;
		}
		public int hashCode() {
			return 0;
		}
		public boolean equals(Object o) {			
			return o instanceof Null;
		}
		public String toString() {
			return "null";
		}
		public int compareTo(Value v) {
			if(v instanceof Null) {
				return 0;
			} else {
				return 1; // everything is above null
			}
		}
	}
	
	public static final class Bool extends Value {
		public final boolean value;
		private Bool(boolean value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_BOOL;
		}
		public int hashCode() {
			return value ? 1 : 0;
		}
		public boolean equals(Object o) {
			if(o instanceof Bool) {
				Bool i = (Bool) o;
				return value == i.value;
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Bool) {
				Bool b = (Bool) v;
				if(value == b.value) {
					return 0;
				} else if(value) {
					return 1;
				} 
			} else if(v instanceof Null) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			if(value) { return "true"; }
			else {
				return "false";
			}
		}		
	}
	public static final class Int extends Value {
		public final BigInteger value;
		private Int(BigInteger value) {
			this.value = value;
		}
		public Type type() {
			return Type.T_INT;
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Int) {
				Int i = (Int) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Int) {
				Int i = (Int) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Bool) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
	}
	
	public static final class Real extends Value {
		public final BigRational value;
		private Real(BigRational value) {
			this.value = value;
		}
		public Type type() {
			if(value.isInteger()) {
				return Type.T_INT;
			} else {
				return Type.T_REAL;
			}
		}
		public int hashCode() {
			return value.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Real) {
				Real i = (Real) o;
				return value.equals(i.value);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Real) {
				Real i = (Real) v;
				return value.compareTo(i.value); 
			} else if(v instanceof Null || v instanceof Bool || v instanceof Int) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			return value.toString();
		}
	}
	
	public static class List extends Value {
		public final ArrayList<Value> values;
		private List(Collection<Value> value) {
			this.values = new ArrayList<Value>(value);
		}
		public Type type() {
			Type t = Type.T_VOID;
			for(Value arg : values) {
				t = Type.leastUpperBound(t,arg.type());
			}
			return Type.T_LIST(t);			
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof List) {
				List i = (List) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof List) {
				List l = (List) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					for(int i=0;i!=values.size();++i) {
						Value v1 = values.get(i);
						Value v2 = l.values.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Int || v instanceof Real) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "[";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "]";
		}
	}
	
	public static class Set extends Value {
		public final HashSet<Value> values;
		private Set(Collection<Value> value) {
			this.values = new HashSet<Value>(value);
		}
		public Type type() {
			Type t = Type.T_VOID;
			for(Value arg : values) {
				t = Type.leastUpperBound(t,arg.type());
			}
			return Type.T_SET(t);	
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Set) {
				Set i = (Set) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Set) {
				Set l = (Set) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					// this case is slightly awkward, since we can't rely on the
					// iteration order for HashSet.
					ArrayList<Value> vs1 = new ArrayList<Value>(values);
					ArrayList<Value> vs2 = new ArrayList<Value>(l.values);
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Value v1 = vs1.get(i);
						Value v2 = vs2.get(i);
						int c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Int || v instanceof Real
					|| v instanceof List) {
				return 1;
			}
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			for(Value v : values) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += v;
			}
			return r + "}";
		}
	}
	
	public static class Record extends Value {
		public final HashMap<String,Value> values;
		private Record(Map<String,Value> value) {
			this.values = new HashMap<String,Value>(value);
		}

		public Type type() {
			HashMap<String, Type> types = new HashMap<String, Type>();
			for (Map.Entry<String, Value> e : values.entrySet()) {
				types.put(e.getKey(), e.getValue().type());
			}
			return Type.T_RECORD(types);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Record) {
				Record i = (Record) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Record) {
				Record l = (Record) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<String> vs1 = new ArrayList<String>(values.keySet());
					ArrayList<String> vs2 = new ArrayList<String>(l.values.keySet());
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						String s1 = vs1.get(i);
						String s2 = vs2.get(i);
						int c = s1.compareTo(s2);
						if(c != 0) { return c; }
						Value v1 = values.get(s1);
						Value v2 = l.values.get(s1);
						c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Int || v instanceof Real
					|| v instanceof Set || v instanceof List) {
				return 1; 
			} 
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			ArrayList<String> keys = new ArrayList<String>(values.keySet());
			Collections.sort(keys);
			for(String key : keys) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				r += key + ":=" + values.get(key);
			}
			return r + "}";
		}
	}
	
	public static class Dictionary extends Value {
		public final HashMap<Value,Value> values;
		private Dictionary(Map<Value,Value> value) {
			this.values = new HashMap<Value,Value>(value);
		}
		private Dictionary(java.util.Set<Pair<Value,Value>> values) {
			this.values = new HashMap<Value,Value>();
			for(Pair<Value,Value> p : values) {
				this.values.put(p.first(), p.second());
			}
		}
		public Type type() {
			Type key = Type.T_VOID;
			Type value = Type.T_VOID;
			for (Map.Entry<Value, Value> e : values.entrySet()) {
				key = Type.leastUpperBound(key,e.getKey().type());
				value = Type.leastUpperBound(value,e.getKey().type());
			}
			return Type.T_DICTIONARY(key,value);
		}
		public int hashCode() {
			return values.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof Dictionary) {
				Dictionary i = (Dictionary) o;
				return values.equals(i.values);
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof Dictionary) {
				Dictionary l = (Dictionary) v;
				if(values.size() < l.values.size()) {
					return -1;
				} else if(values.size() > l.values.size()) {
					return 1;
				} else {
					ArrayList<Value> vs1 = new ArrayList<Value>(values.keySet());
					ArrayList<Value> vs2 = new ArrayList<Value>(l.values.keySet());
					Collections.sort(vs1);
					Collections.sort(vs2);
					for(int i=0;i!=values.size();++i) {
						Value k1 = vs1.get(i);
						Value k2 = vs2.get(i);
						int c = k1.compareTo(k2);
						if(c != 0) { return c; }
						Value v1 = values.get(k1);
						Value v2 = l.values.get(k1);
						c = v1.compareTo(v2);
						if(c != 0) { return c; }
					}
					return 0;
				}
			} else if (v instanceof Null || v instanceof Bool
					|| v instanceof Int || v instanceof Real
					|| v instanceof Set || v instanceof List
					|| v instanceof Record) {
				return 1;
			}
			return -1;			
		}
		public String toString() {
			String r = "{";
			boolean firstTime=true;
			ArrayList<String> keystr = new ArrayList<String>();
			HashMap<String,Value> keymap = new HashMap<String,Value>();
			for(Value key : values.keySet()) {
				keystr.add(key.toString());
				keymap.put(key.toString(), key);
			}
			Collections.sort(keystr);
			for(String key : keystr) {
				if(!firstTime) {
					r += ",";
				}
				firstTime=false;
				Value k = keymap.get(key); 
				r += k + "->" + values.get(k);
			}
			return r + "}";
		}
	}
	
	public static final class TypeConst extends Value {
		public final Type type;
		private TypeConst(Type type) {
			this.type = type;
		}
		public Type type() {
			return Type.T_META;
		}
		public int hashCode() {
			return type.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof TypeConst) {
				TypeConst i = (TypeConst) o;
				return type == i.type;
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof TypeConst) {
				TypeConst t = (TypeConst) v;
				// FIXME: following is an ugly hack!
				return type.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}					
		}
		public String toString() {
			return type.toString();
		}
	}
	
	public static final class FunConst extends Value {
		public final NameID name;
		public final Type.Fun type;
		
		private FunConst(NameID name, Type.Fun type) {
			this.name = name;
			this.type = type;
		}
		public Type type() {
			return type;
		}
		public int hashCode() {
			return type.hashCode();
		}
		public boolean equals(Object o) {
			if(o instanceof FunConst) {
				FunConst i = (FunConst) o;
				return name.equals(i.name) && type == i.type;
			}
			return false;
		}
		public int compareTo(Value v) {
			if(v instanceof FunConst) {
				FunConst t = (FunConst) v;
				// FIXME: following is an ugly hack!
				return type.toString().compareTo(t.toString());
			} else {
				return 1; // everything is above a type constant
			}					
		}
		public String toString() {
			return "&" + name.toString() + ":" + type.toString();
		}
	}
	private static final ArrayList<Value> values = new ArrayList<Value>();
	private static final HashMap<Value,Integer> cache = new HashMap<Value,Integer>();
	
	private static <T extends Value> T get(T type) {
		Integer idx = cache.get(type);
		if(idx != null) {
			return (T) values.get(idx);
		} else {					
			cache.put(type, values.size());
			values.add(type);
			return type;
		}
	}
}