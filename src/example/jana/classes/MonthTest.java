package example.jana.classes;

public class MonthTest
{
	Month.MonthGER aMonth1;
	Month aMonth2;
	
	MonthTest()
	{
		
	}
	
	void test()
	{
		this.aMonth1 = Month.MonthGER.APRIL;
		this.aMonth2 = Month.APR;
		
		System.out.println(aMonth1);
		System.out.println(aMonth2);
		
		this.aMonth2.foo();
		this.aMonth2.run();
	}
	
	public static void main(String[] args)
	{
		MonthTest mt = new MonthTest();
		
		mt.test();
	}
}
