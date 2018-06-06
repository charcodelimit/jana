package example.jana.classes;

public class CastExample
{
	public void method(A anAObject)
	{
		System.out.println(anAObject.getValue());
	}
		
	public void castTest()
	{
		A a = new A(0);
		B b = new B(0);
		
		method(a);

		// conversion to the more general type should not create casts 
		method(b);
	 
		method((A) b);
		
		a = b;
		
		method(a);
	}
	
	public void primitiveCastTest()
	{
		int i = 128; 
		char c; byte b; short s; 
		long l; double d; float f;
		
		c = (char) i;
		b = (byte) i;
		s = (short) i;
		l = i;
		l = (long) i;
		d = i;
		d = (double) i;
		f = i;
		f = (float) i;
		
		System.out.println("c: " + c + " b: " + b + " s: " + s + " l: " + l + " d: " + d + " f: " + f );
	}
	
	public void arrayCastTest()
	{
		int i[] = new int[1024];
		short s[];
		long l[];
		Object o;
		
		i[10] = 10;
		
		o = (Object) i;
		try
		{
			l = (long[]) o;
		}
		catch(ClassCastException ce)
		{
			System.out.println("O.K. - Exception was thrown!" + " " + ce.toString());
		}
		
		try
		{
			s = (short[]) o;
		}
		catch(ClassCastException ce)
		{
			System.out.println("O.K. - Exception was thrown!" + " " + ce.toString());
		}
		
		i = (int[]) o;
		
		System.out.println("Object: " + o);
		System.out.println("Int: " + i[10]);
	}
	
	public static void main(String[] args)
	{
		CastExample ce = new CastExample();
		
		ce.castTest();
		ce.primitiveCastTest();
		ce.arrayCastTest();
	}
}
