//
// load-exceptions.cs: Tests for loading missing types/methods/fields from IL
//

using System;
using System.IO;

class Miss1 : Missing.Foo1 {
}

public class Tests : LoadMissing {

	public delegate void TestDel ();

	internal static int check_type_load (TestDel d) {
		try {
			d ();
		}
		catch (TypeLoadException ex) {
			//Console.WriteLine (ex.TypeName);
			//Console.WriteLine (ex);
			return 0;
		}

		return 1;
	}

	internal static int check_missing_method (TestDel d) {
		try {
			d ();
		}
		catch (MissingMethodException ex) {
			//Console.WriteLine (ex);
			return 0;
		}

		return 1;
	}

	internal static int check_file_not_found (TestDel d){
		try {
			d ();
		} catch (FileNotFoundException ex){
			return 0;
		}
		return 1;
	}
	
	internal static int check_missing_field (TestDel d) {
		try {
			d ();
		}
		catch (MissingFieldException ex) {
			//Console.WriteLine (ex);
			return 0;
		}

		return 1;
	}


	//
	// Base instructions
	//

	public static int test_0_call () {
		return check_missing_method (new TestDel (missing_call));
	}

	public static int test_0_jmp () {
		return check_missing_method (new TestDel (missing_jmp));
	}

	public static int test_0_ldftn () {
		return check_missing_method (new TestDel (missing_ldftn));
	}

	//
	// Object model instructions
	//

	public static int test_0_box () {
		// Thrown earlier
		return 0;
	}

	public static int test_0_callvirt () {
		return check_missing_method (new TestDel (missing_callvirt));
	}

	public static int test_0_castclass () {
		return check_type_load (new TestDel (missing_castclass));
	}

	public static int test_0_cpobj () {
		return check_type_load (new TestDel (missing_cpobj));
	}

        public static int test_0_missing_type_on_parameter () {
		return check_type_load (new TestDel (missing_external_type_reference_on_parameter));
        } 
	public static int test_0_initobj () {
		return check_type_load (new TestDel (missing_initobj));
	}

	public static int test_0_isinst () {
		return check_type_load (new TestDel (missing_isinst));
	}

	public static int test_0_ldelem () {
		// Thrown earlier
		return 0;
	}

	public static int test_0_ldelema () {
		// Thrown earlier
		return 0;
	}

	public static int test_0_ldfld () {
		return check_missing_field (new TestDel (missing_ldfld));
	}		

	public static int test_0_ldflda () {
		return check_missing_field (new TestDel (missing_ldflda));
	}		

	public static int test_0_ldobj () {
		// Thrown earlier
		return 0;
	}

	public static int test_0_ldsfld () {
		return check_missing_field (new TestDel (missing_ldsfld));
	}		

	public static int test_0_ldsflda () {
		return check_missing_field (new TestDel (missing_ldsflda));
	}		

	public static int test_0_ldtoken_type () {
		return check_type_load (new TestDel (missing_ldtoken_type));
	}

	public static int test_0_ldtoken_method () {
		return check_missing_method (new TestDel (missing_ldtoken_method));
	}

	public static int test_0_ldtoken_field () {
		return check_missing_field (new TestDel (missing_ldtoken_field));
	}

	public static int test_0_ldvirtftn () {
		return check_missing_method (new TestDel (missing_ldvirtftn));
	}

	public static int test_0_mkrefany () {
		// Thrown earlier
		return 0;
	}

	public static int test_0_newarr () {
		return check_type_load (new TestDel (missing_newarr));
	}		

	public static int test_0_newobj () {
		return check_missing_method (new TestDel (missing_newobj));
	}		

	public static int test_0_refanyval () {
		return check_type_load (new TestDel (missing_refanyval));
	}

	public static int test_0_sizeof () {
		return check_type_load (new TestDel (missing_sizeof));
	}

	public static int test_0_stelem () {
		// Thrown earlier
		return 0;
	}

	public static int test_0_stfld () {
		return check_missing_field (new TestDel (missing_stfld));
	}

	public static int test_0_stobj () {
		// Thrown earlier
		return 0;
	}

	public static int test_0_stsfld () {
		return check_missing_field (new TestDel (missing_stsfld));
	}

	public static int test_0_unbox () {
		return check_type_load (new TestDel (missing_unbox));
	}

	public static int test_0_unbox_any () {
		return check_type_load (new TestDel (missing_unbox_any));
	}


	// Bummer: we regressed!   I should have put this before
	public static int test_0_missing_assembly_in_fieldref () {
		return check_file_not_found (new TestDel (missing_assembly_in_fieldref));
	}


	// FIXME: the corrent exception here is FileNotFoundException
	public static int test_0_missing_assembly_in_call () {
		return check_type_load (new TestDel (missing_assembly_in_call));
	}

	public static int test_0_missing_assembly_in_newobj () {
		return check_type_load (new TestDel (missing_assembly_in_newobj));
	}
	
	//
	// Missing classes referenced from metadata
	//

	// FIXME: These do not work yet

	public static int test_0_missing_local () {
		try {
			missing_local ();
		}
		catch (TypeLoadException ex) {
		}

		/* MS.NET doesn't throw an exception if a local is not found */
		return 0;
	}

	public static void missing_parent () {
		new Miss1 ();
	}

	public static int test_0_missing_parent () {
		return check_type_load (new TestDel (missing_parent));
	}


	public static int Main () {
		return TestDriver.RunTests (typeof (Tests));
	}
}