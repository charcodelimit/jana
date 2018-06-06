package example.jana.classes;

public class ObjectExample
{
	int x,y;
	static int sx,sy;
	
	public void fieldAccess()
	{
		int z;
		
		this.x = 2;
		this.y = this.x;
		this.x = 1;
		
		z = this.x + this.y;
		
		System.out.println(z);
		
		sx = this.x;
		
		z = sx + this.y;
		System.out.println(z);
		
		sy = z;
		z = sy + sx;
		System.out.println(z);
	}
	
	public void typecasts()
	{
		Number number;
		Integer integer;
		boolean isInstance;
		
		number = new Integer(12);
		
		System.out.println("int value from Number: " + number.intValue());
		
		integer = (Integer) number;
		
		System.out.println("Integer: " + integer.toString());
		
		isInstance = (integer instanceof Integer);
		
		if(isInstance)
			System.out.println("The variable integer is of type " + integer.getClass().getName() + " which is an instance of " + Integer.class.getName());
		else
			System.out.println("The variable integer is of type " + integer.getClass().getName() + " which is no instance of " + Integer.class.getName());
		
		isInstance = (number instanceof Number);
		
		if(isInstance)
			System.out.println("The variable number is of type " + number.getClass().getName() + " which is an instance of " + Number.class.getName());
		else
			System.out.println("The variable number is of type " + number.getClass().getName() + " which is no instance of " + Number.class.getName());
	}
	
	public void test()
	{
		typecasts();
		fieldAccess();
	}
	
	public static void main(String[] args)
	{
		ObjectExample oe = new ObjectExample();
		oe.test();
	}
}
