define bop as (int x, int y) where x > 0
define expr as int|bop

int f(expr e):
    if e ~= bop:
        return e.x + e.y
    else:
        return e // requires type difference

void System::main([string] args):
    int x = f(1)
    print str(x)
    x = f((x:4,y:10))   
    print str(x)