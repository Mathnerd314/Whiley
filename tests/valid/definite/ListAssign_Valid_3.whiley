void f(int i):
    [int] arr1
    [int] arr2
    arr1 = [1,2,64]
    arr2 = arr1
    if i != |arr1|:
        arr2[2] = 3
    else:
        arr2[2] = i
    assert arr2[2] == |arr1|
    print str(arr1)
    print str(arr2)

void System::main([string] args):
    f(2)
    f(3)