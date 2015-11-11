using
System;
using
System.Threading;
class
MutexTest
{
public
static
Mutex[]
m;
public
static
void
ThreadMethod_A()
{
Console.WriteLine("[Thread A] - Started.....");
for
(int
i=0;i<10;i++)
{
Console.WriteLine("[Thread A] - Trying to lock mutex "+i+"...");
m[i].WaitOne();
Console.WriteLine("[Thread A] - m["+i+"] Locked!");
Console.WriteLine("[Thread A] - Now using  mutex ["+i+"]");
Thread.Sleep(2000);
m[i].ReleaseMutex();
Console.WriteLine("[Thread A] - Unlocked the mutex ["+i+"]");
}
Console.WriteLine("[Thread A] - exiting.....");
}
public
static
void
ThreadMethod_B()
{
Console.WriteLine("[Thread B] - Started.....");
for
(int
h=0;h<10;h++)
{
int
i=5;
Console.WriteLine("[Thread B] - Trying to lock mutex "+i+" for "+h+" time...");
m[i].WaitOne();
Console.WriteLine("[Thread B] - m["+i+"] Locked recursively ["+h+"] times!");
Thread.Sleep(4500);
}
for
(int
h=0;h<10;h++)
{
int
i=5;
m[i].ReleaseMutex();
Console.WriteLine("[Thread B] - Unlocked the mutex ["+i+"] for ["+h+"] times");
}
Console.WriteLine("[Thread B] - Finished.....");
}
public
static
void
Main()
{
m
=
new
Mutex[10];
for
(int
i
=
0
;
i<10
;
i++
)
m[i]
=
new
Mutex();
Console.WriteLine("[  Main  ] - Creating first thread..");
ThreadStart
Thread_1
=
new
ThreadStart(ThreadMethod_A);
Console.WriteLine("[  Main  ] - Creating second thread..");
ThreadStart
Thread_2
=
new
ThreadStart(ThreadMethod_B);
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
A.Start();
B.Start();
Thread.Sleep(500);
Console.WriteLine("[  Main  ] - Test Ended");
}
}