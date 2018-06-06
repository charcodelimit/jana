package example.jana.classes;

import java.util.ArrayList;
import java.util.Collection;


/**
 * Examples for field accesses before the constructor is called
 * 
 * @author chr
 *
 */
public class FieldAccess extends SuperFieldAccess
{
	static int root;
	TestList fieldAccessList;

	FieldAccess()
	{ 
		super(root = 10);
	}
	
	private void test() {
	    System.out.println("  Testing");
	    
	    final SuperFieldAccess field = new SuperFieldAccess(10);
    
	    fieldAccessList = new TestList() {
	    @SuppressWarnings("unchecked")
		public Collection fields() {
	        ArrayList r = new ArrayList(1);
	        r.add(field);
	        return r;
	      }
	    };
	    
	    System.out.println( "Size: " + fieldAccessList.fields().size() );
	}
	
	public static void main(String[] args)
	{
		FieldAccess fa = new FieldAccess();
		fa.test();
		System.out.println("Done.");
	}
}
