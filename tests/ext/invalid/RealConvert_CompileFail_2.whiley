int f(int x) requires x>=0 && y>0:
    return 0.0

void System::main([string] args):
    f(1)
