using System;

namespace Missing {


public class Foo1 {
}


public class Foo2 {

	public Foo2 () {
	}


	public Foo2 (int i) {
	}

	public void missing () {
	}

	public static void static_missing () {
	}

}

public class Foo3 {

	public static int i;

}

public class Foo4 {

	public int i;

}

public class Foo5 {

	public virtual void missing_virtual () {
	}

}

public struct Foo6 {
}

}
