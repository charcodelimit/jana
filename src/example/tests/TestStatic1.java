package example.tests;

public class TestStatic1
{
	static int state = 0;
	
	public static void changeState()
	{
		state = 1;
	}
	
	public static int getState()
	{
		return state;
	}
	
	public static void printState()
	{
		System.out.println("TestStatic1.printState(): " + TestStatic1.getState());
	}
	
	
}
