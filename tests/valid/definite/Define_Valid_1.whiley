define point as (int x,int y)
define listint as [int]
define setint as {int}

void System::main([string] args):
     point p
     setint si
     listint li
     int x
     
     si = {1,2,3}
     li = [1,2,3]
     
     p = (x:1,y:2)
     x = p.x
     
     print str(x)
     print str(|si|)
     print str(li[0])