real f(real x) requires x > 0, ensures $ < 0:
    return -x

void System::main([string] args):
    print str(f(1.2))
    print str(f(0.00001))
    print str(f(5632))