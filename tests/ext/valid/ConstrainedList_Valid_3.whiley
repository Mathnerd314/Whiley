define posintlist as [int] where no { x in $ | x < 0 }

int sum(posintlist ls, int i) requires i >=0 && i <= |ls|, ensures $ >= 0:
    if(i == |ls|):
        return 0
    else:
        return ls[i] + sum(ls,i+1)

int sum(posintlist ls) ensures $ >= 0:
    return sum(ls,0)

void System::main([string] args):
    c = sum([1,2,3,4,5,6,7])
    out<->println(str(c))
    
