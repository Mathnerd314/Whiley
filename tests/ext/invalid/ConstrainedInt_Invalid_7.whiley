// this is a comment!
define num as {1,2,3,4}
define bignum as {1,2,3,4,5,6,7}

void f(num x):
    y = x
    print str(y)

void g({bignum} zs, int z) requires z in {x | x in zs, x < 6}:
    f(z)

void System::main([string] args):
    g({1,2,3,5},5)
