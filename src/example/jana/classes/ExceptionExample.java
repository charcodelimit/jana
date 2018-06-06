package example.jana.classes;

import java.io.IOException;

public class ExceptionExample extends Object
{
	public ExceptionExample()
	{
	}
	
	public void simple() throws Exception
	{
		throw new Exception("Whoops! simple()");
	}
	
	public void nullExample() throws Exception
	{
		throw null;
	}
	
	public void multipleBlockExample()
	{
		int i;
		
		i = 12;
		
		try
		{
			if( i == 0)
			{
				throw new Exception("Whoops! multipleBlockExample()");
			}
			else
			{
				throw new IOException("Whoops! multipleBlockExample");
			}
		}
		catch(IOException ioe)
		{
			System.out.println("IOException Caught " + ioe.toString());
			ioe.printStackTrace();
		}
		catch(Exception e)
		{
			System.out.println("Exception Caught " + e.toString());
			e.printStackTrace();
		}
	}
	
	public void finalExample() 
	{
		try
		{
			throw new Exception("Whoops! finalExample()");
		}
		catch(Exception e)
		{
			System.out.println("Handling " + e.toString());
		}
		finally
		{
			System.out.println("Finalizing");
		}
	}
	
	public void test()
	{
		try
		{
			simple();
		}
		catch(Exception e)
		{
			System.out.println("Caught " + e.toString());
			e.printStackTrace();
		}
		
		try
		{
			nullExample();
		}
		catch(NullPointerException npe)
		{
			System.out.println("NullPointerException Caught " + npe.toString());
			npe.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Caught " + e.toString());
		}
		
		multipleBlockExample();
		
		finalExample();
	}
	
	public static void main(String[] args)
	{
		ExceptionExample ee;
		
		ee = new ExceptionExample();
		
		ee.test();
	}
}
