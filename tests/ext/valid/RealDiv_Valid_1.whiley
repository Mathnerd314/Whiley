real g(int x):
     return x / 3.0

string f(int x, int y) requires x>=0 && y>0:
    return str(g(x))

void System::main([string] args):
     out<->println(f(1,2))
