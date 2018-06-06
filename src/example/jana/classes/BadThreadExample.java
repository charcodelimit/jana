package example.jana.classes;

public class BadThreadExample extends Thread

{
	int value = 0;
	
	BadThreadExample()
	{
		start();
		
		try
		{
			sleep(100);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.value = 42;
	}
	
	public void run()
	{
		System.out.println("Hello " + this.value);
	}
	
	public static void main(String[] args)
	{
		BadThreadExample bte = new BadThreadExample();
		
		System.out.println("Hi " + bte.value);
	}
}
