define scf8nat as int where $ > 0
define scf8tup as (scf8nat f, int g) where g > f 

void System::main([string] args):
    [scf8tup] x = [(f:1,g:2),(f:4,g:8)]
    x[0].f = 2
    