package example.jana.classes;

public class AliasExample
{
	public void aliases()
	{
		Object o1,a1,a2;
		
		o1 = new Object();
		
		a1 = o1;
		a2 = o1;
		
		if( a1 == a2 )
			System.out.println("a1 and b2 are the same!");
		
		if( a1 == o1 && a2 == o1 )
			System.out.println("a1 and b2 are aliases of o1!");
		
		if( a1 != o1 )
			System.out.println("a1 is no alias of o1!");

		if( a2 != o1 )
			System.out.println("a2 is no alias of o1!");
	}
	
	public static void main(String[] args)
	{
		AliasExample ae = new AliasExample();
		ae.aliases();
	}
}
