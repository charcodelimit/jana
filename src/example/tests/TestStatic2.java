package example.tests;


public class TestStatic2 extends TestStatic1
{
	public static void changeState()
	{
		state = 2;
	}
	
	public static void main(String[] args)
	{
		TestStatic1.changeState();
		System.out.println("TestStatic1.state: " + TestStatic1.state);
		System.out.println("TestStatic2.state: " + TestStatic2.state);
		
		TestStatic2.changeState();
		System.out.println("TestStatic1.state: " + TestStatic1.state);
		System.out.println("TestStatic2.state: " + TestStatic2.state);

		TestStatic1.printState();
	}
}
