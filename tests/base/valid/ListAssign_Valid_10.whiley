define nint as null|int

[[nint]] move(int from, int to, [[nint]] list):
    tmp = list[from][from+1]
    list[from][from+1] = null
    list[to][to+1] = tmp
    return list

void System::main([string] args):
    ls = [[1,2,3],[4,5,6],[7,8,9]]
    ls = move(0,1,ls)
    out.println(str(ls))
