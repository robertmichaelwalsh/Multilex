class
pop_test
{
public
static
int
t
=
4;
static
int
f
()
{
t
=
0;
return
1;
}
static
int
Main
()
{
if
(t
!=
4)
return
1;
f
();
if
(t
!=
0)
return
1;
return
0;
}
}