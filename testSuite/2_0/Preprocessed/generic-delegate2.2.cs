using
System;
using
System.Reflection;
public
class
main
{
public
static
int
work
()
{
Gen<string>
gs
=
new
Gen<string>
();
Gen<object>
go
=
new
Gen<object>
();
MethodInfo
miObj
=
typeof
(Gen<object>).GetMethod
("newArr",
BindingFlags.Public
|
BindingFlags.Instance);
MethodInfo
miStr
=
typeof
(Gen<string>).GetMethod
("newArr",
BindingFlags.Public
|
BindingFlags.Instance);
if
(miObj
==
miStr)
{
Console.WriteLine
("methods equal");
return
1;
}
ObjArrDel
oad
=
go.newObjDel
();
StrArrDel
sad
=
gs.newStrDel
();
StrArrDel
sad2
=
(StrArrDel)Delegate.CreateDelegate
(typeof
(StrArrDel),
null,
miStr);
if
(oad.Method
!=
miObj)
{
Console.WriteLine
("wrong object method");
if
(oad.Method
==
miStr)
Console.WriteLine
("object method is string");
return
1;
}
if
(sad.Method
!=
miStr)
{
Console.WriteLine
("wrong string method");
if
(sad.Method
==
miObj)
Console.WriteLine
("string method is object");
else
return
1;
}
else
{
Console.WriteLine
("right string method");
}
if
(sad2.Method
!=
miStr)
{
Console.WriteLine
("wrong string2 method");
if
(sad2.Method
==
miObj)
Console.WriteLine
("string2 method is object");
return
1;
}
Console.WriteLine
("calling object del");
if
(oad
(go,
3).GetType
()
!=
typeof
(object
[]))
{
Console.WriteLine
("not object array");
return
1;
}
Console.WriteLine
("calling string del");
if
(sad
(gs,
3).GetType
()
!=
typeof
(string
[]))
{
Console.WriteLine
("not string array");
return
1;
}
Console.WriteLine
("calling string del2");
if
(sad2
(gs,
3).GetType
()
!=
typeof
(string
[]))
{
Console.WriteLine
("not string2 array");
return
1;
}
try
{
StrArrDel
sad3
=
(StrArrDel)Delegate.CreateDelegate
(typeof
(StrArrDel),
null,
miObj);
Console.WriteLine
("object method for string delegate");
return
1;
}
catch
(ArgumentException)
{
}
Console.WriteLine
("done");
return
0;
}
public
static
int
Main
()
{
Gen<string>
gs
=
new
Gen<string>
();
Gen<object>
go
=
new
Gen<object>
();
gs.newArr
(3);
go.newArr
(3);
return
work
();
}
}