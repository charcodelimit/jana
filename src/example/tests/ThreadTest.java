package example.tests;

public class ThreadTest implements Runnable
{	
	public static void main(String[] args)
	{
		ThreadTest t = new ThreadTest();
		new Thread(t).start();
		new Thread(t).start();
	}

	public void run()
	{
		for(int i = 0; i < 50; i++)
		{
			synchronized(ThreadTest.class)
			{
				System.out.println(Thread.currentThread());
			}	
		
			try
			{
				Thread.sleep((int) (Math.random()*10.0));
			} catch (InterruptedException e)
			{
				return;
			}
		}
	}
}