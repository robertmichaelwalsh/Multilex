using
System;
using
System.Threading;
class
EventTest
{
public
static
ManualResetEvent
e;
public
static
void
ThreadMethod_waiter_1()
{
Console.WriteLine("[Thread A] - Started.....");
Console.WriteLine("[Thread A] - I'm before wait for event .....");
e.WaitOne();
Console.WriteLine("[Thread A] - I'm after wait for event.");
}
public
static
void
ThreadMethod_waiter_2()
{
Console.WriteLine("[Thread B] - Started.....");
Console.WriteLine("[Thread B] - I'm before wait for event .....");
e.WaitOne();
Console.WriteLine("[Thread B] - I'm after wait for event.");
}
public
static
void
ThreadMethod_blocker()
{
Console.WriteLine("[Thread C] - Started.....");
Console.WriteLine("[Thread C] - Sleeping for 5000ms....");
Thread.Sleep(5000);
Console.WriteLine("[Thread C] - Setting the event....");
e.Set();
Console.WriteLine("[Thread C] - Finished.....");
}
public
static
void
Main()
{
e
=
new
ManualResetEvent(false);
Console.WriteLine("[  Main  ] - Creating first thread..");
ThreadStart
Thread_1
=
new
ThreadStart(ThreadMethod_waiter_1);
ThreadStart
Thread_2
=
new
ThreadStart(ThreadMethod_waiter_2);
Console.WriteLine("[  Main  ] - Creating second thread..");
ThreadStart
Thread_3
=
new
ThreadStart(ThreadMethod_blocker);
Thread
A
=
new
Thread(Thread_1);
Thread
B
=
new
Thread(Thread_2);
Thread
C
=
new
Thread(Thread_3);
A.Start();
B.Start();
C.Start();
Thread.Sleep(500);
Console.WriteLine("[  Main  ] - Finish...");
}
}