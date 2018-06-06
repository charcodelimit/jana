package example.tests;

public class TestStatic3
{	
	static int x;
	
	static
	{
		x = 0;
		Thread t = new Thread(new TestStatic3a(x));
		t.start();
		try
		{	
			Thread.sleep(4096);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		x = 12;
		System.out.println("Finished: " + x);
	}
	
	public static void main(String[] args)
	{
		TestStatic3 ts;
	}
	
}

class TestStatic3a implements Runnable
{
	int value;
	
	public TestStatic3a(int aValue)
	{
		this.value = aValue;
	}
	
	public void run()
	{
		System.out.println(TestStatic3.x);
		System.out.println("Escaped!");
		TestStatic3.x = -1;
	}
}