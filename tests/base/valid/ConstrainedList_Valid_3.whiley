define posintlist as [int]

int sum(posintlist ls, int i):
    if(i == |ls|):
        return 0
    else:
        return ls[i] + sum(ls,i+1)

int sum(posintlist ls):
    return sum(ls,0)

void System::main([string] args):
    c = sum([1,2,3,4,5,6,7])
    out.println(str(c))
    
