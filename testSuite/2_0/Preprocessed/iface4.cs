using
System;
public
interface
IVehicle
{
int
Start
();
int
Stop
();
int
Turn
();
}
public
interface
IWalker
{
int
Walk
();
}
public
class
Base
:
IVehicle
{
int
IVehicle.Start
()
{
return
1;
}
public
int
Stop
()
{
return
2;
}
public
virtual
int
Turn
()
{
return
3;
}
public
int
Walk
()
{
return
1;
}
}
public
class
Derived1
:
Base
{
public
override
int
Turn
()
{
return
4;
}
}
public
class
Derived2
:
Base,
IVehicle
{
public
new
int
Stop
()
{
return
6;
}
int
IVehicle.Start
()
{
return
5;
}
int
IVehicle.Turn
()
{
return
7;
}
public
override
int
Turn
()
{
return
8;
}
}
public
class
Derived3
:
Derived1,
IWalker
{
}
public
class
Test
{
static
int
Main
()
{
Derived1
d1
=
new
Derived1
();
Derived2
d2
=
new
Derived2
();
Derived3
d3
=
new
Derived3
();
Base
b1
=
d1;
Base
b2
=
d2;
Base
rb
=
new
Base
();
if
(d1.Turn
()
!=
4)
return
1;
if
(((IVehicle)d1).Turn
()
!=
4)
return
2;
if
(((IVehicle)d2).Turn
()
!=
7)
return
3;
if
(b2.Turn
()
!=
8)
return
4;
if
(((IVehicle)b2).Turn
()
!=
7)
return
5;
if
(((IVehicle)rb).Stop
()
!=
2)
return
6;
if
(((IVehicle)d1).Stop
()
!=
2)
return
7;
if
(((IVehicle)d2).Stop
()
!=
6)
return
8;
if
(d3.Walk
()
!=
1)
return
9;
return
0;
}
}