define nat as int where $ >= 0
define expr as nat | (int op, expr left, expr right)

void System::main([string] args):
    expr e = 14897
    print str(e)