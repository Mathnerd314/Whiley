define expr as {int op, expr lhs} | {string err}

int f(expr e):
    if e is {string err}:
        return |e.err|
    else:
        return -1
    
void System::main([string] args):
    x = f({err:"Hello World"})
    out.println(str(x))
    x = f({op:1,lhs:{err:"Gotcha"}})
    out.println(str(x))
