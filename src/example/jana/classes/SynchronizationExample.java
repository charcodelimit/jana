package example.jana.classes;

public class SynchronizationExample implements Runnable
{
	private static int value = 0;
	
	public synchronized int synch1()
	{
		return value;
	}
	
	public void synch2()
	{
		synchronized(this)
		{
			value = value + 1;
		}
	}
	
	public void synch3()
	{
		synchronized(SynchronizationExample.class)
		{
			value = value + 1;
		}
	}
	
	/**
	 * Benign race (there is a possible race condition, but even if synch2() occurs in between it doesn't matter for the result)
	 */
	public void run()
	{
		int tmpValue;
		
		while((tmpValue = synch1()) < 4)
		{
			System.out.println(tmpValue);
			synch2();
			synch3();
		}
	}
	
	public void test()
	{
		SynchronizationExample e1,e2;
		
		e1 = new SynchronizationExample();
		e2 = new SynchronizationExample();
		
		(new Thread(e1)).start();
		(new Thread(e2)).start();
	}
	
	public static void main(String[] args)
	{
		SynchronizationExample se;
		
		se = new SynchronizationExample();
		se.test();
	}
}
