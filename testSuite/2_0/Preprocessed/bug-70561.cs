using
System;
using
System.Threading;
class
T
{
static
void
DoStuff
()
{
Console.WriteLine
("DoStuff ()");
}
static
void
Main
(string
[]
args)
{
try
{
Thread.CurrentThread.Abort
();
}
finally
{
try
{
DoStuff
();
}
catch
(Exception)
{}
Thread.CurrentThread.Abort
();
}
}
}