package example.jana.classes;

public enum Month implements Runnable
{	
	JAN, FEB, MAR, APR, MAY, JUN, JUL;

	private int x;
	private int y;
	
	private Month()
	{
		foo();
	}
	
	void foo()
	{
		this.x = 0;
		this.y = 1;
		
		System.out.println("x+y=" + (this.x+this.y));
	}
	
	public enum MonthGER
	{
		JANUAR, FEBRUAR, MAERZ, APRIL, MAI, JUNI, JULI; // The Ä in MÄRZ will make Soot crash
		
		MonthGER()
		{
			
		}
	}
	
	public enum MonthUK
	{
		JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY;
	}

	public void run()
	{
		System.out.println("BOO!");
	}
}
