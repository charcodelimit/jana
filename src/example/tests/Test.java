package example.tests;
import javax.swing.JOptionPane;


public class Test
{
	
	int next;
	
	Test()
	{
		//this(next++);
		initialize();
	}
	
	Test(int id) {
		
	}
	
	private static synchronized void initialize()
	{
		
	}

	
	@SuppressWarnings("unchecked")
	void test() throws Exception
	{
		int $test_0_variable_1_name = 42;
		
		java.lang.Class c = this.getClass();
		Thread t = java.lang.Thread.currentThread();
		JOptionPane.showMessageDialog(null, 
				                      "Hi " + c.getName() + "@" + t.toString(), 
									  "Title " + $test_0_variable_1_name, 1);
		
		try
		{
			throw new Exception("Test Exception");
		}
		catch(Exception e)
		{
			System.out.println("Rethrowing: " + e.toString());
			throw e;
		}
	}
	
	public static void main(String[] args)
	{
		Test t = new Test();
		
		try
		{
			try
			{
				synchronized(Test.class)
				{
					t.test();
				}
			}
			catch(Exception e)
			{
				throw e;
			}
			
			System.out.println("No exception!");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
