package example.jana.classes;

public class ArithmeticExample
{
	public void integerArithmetic()
	{
		int value;
		
		value = 2;
		value = -value;
		System.out.println("Value: " + value);
		
		short s;
		byte b;
		char c;
		long l;
		
		b = 127;
		s = 16384;
		c = 'a';
		l = 65537L;
		
		l = s + b + c;
		
		System.out.println("Values (byte, short, char, long): " + b + "," + s + "," + c + "," + l);
	}
	
	public void floatingPointArithmetic()
	{
		float f1;
		double d1;
		
		f1 = 0.0f;
		
		for(int i = 0; i < 10; i++)
		{
			f1 = f1 + ((float) i/2.0f);
			System.out.print(f1 + " ");
		}
		
		System.out.println();
		
		d1 = 0.0;
		
		for(int i = 1; i < 11; i++)
		{
			d1 = d1 + ((double) 1.0 / i);
			System.out.print(d1 + " ");
		}
		
		System.out.println();
		d1 = 0.00001E-12;
		System.out.println(d1);
	}
	
	public void operators()
	{
		int i = 0;
		double f,f1,f2 = 0;
		
		// increment
		i++;
		// decrement
		i--;
		// increment again
		i+=12;
		// decrement again
		i-=6;
		// modulo
		f1 = 2.0;
		f2 = 0.1;
		f = f1 % f2;
		
		System.out.println();
		System.out.println("i: " + i + " f: " + f);
	}
	
	public void comparisons()
	{
		double d1, d2;
		long l1, l2;
		int i1, i2;
		
		System.out.println();
		
		d1 = 0.1; d2 = 0.5;
		
		if(d1 < d2){ System.out.println("d1 < d2"); }
		if(d1 > d2){ System.out.println("d1 > d2"); }
		if(d1 == d2){ System.out.println("d1 == d2"); }
		if(d1 <= d2){ System.out.println("d1 <= d2"); }
		if(d1 >= d2){ System.out.println("d1 >= d2"); }
		
		l1 = 514144L; l2 = 128536L;
		
		if(l1 < l2){ System.out.println("l1 < l2"); }
		if(l1 > l2){ System.out.println("l1 > l2"); }
		if(l1 == l2){ System.out.println("l1 == l2"); }
		if(l1 <= l2){ System.out.println("l1 <= l2"); }
		if(l1 >= l2){ System.out.println("l1 >= l2"); }
		
		i1 = 32; i2 = 16384;
		
		if(i1 < i2){ System.out.println("i1 < i2"); }
		if(i1 > i2){ System.out.println("i1 > i2"); }
		if(i1 == i2){ System.out.println("i1 == i2"); }
		if(i1 <= i2){ System.out.println("i1 <= i2"); }
		if(i1 >= i2){ System.out.println("i1 >= i2"); }
	}
	
	public void test()
	{
		integerArithmetic();
		floatingPointArithmetic();
		operators();
		comparisons();
	}
	
	public static void main(String[] args)
	{
		ArithmeticExample ae;
		
		ae = new ArithmeticExample();
		
		ae.test();
	}
}
