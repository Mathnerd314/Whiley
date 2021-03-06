define Expr as real | Var | BinOp
define BinOp as { Expr lhs, Expr rhs } 
define Var as { string id }

define SyntaxError as { string err }
define SExpr as SyntaxError | Expr

Expr build(int i):    
    if i > 10:
        return { id: "var" }
    else if i > 0:
        return i
    else:
        return { lhs:build(i+10), rhs:build(i+1) } 

SExpr sbuild(int i):
    if i > 20:
        return { err: "error" }
    else:
        return build(i)

// Main method
public void System::main([string] args):
    i = -5
    while i < 10:
        e = sbuild(i)
        if e is SyntaxError:
            out.println("syntax error: " + e.err)
        else:
            out.println(str(e))
        i = i + 1
