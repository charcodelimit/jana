package example.jana.classes;

public class IfExample extends Object
{
	public IfExample()
	{
	}
	
	public void foo()
	{
		int i;
		
		i = 0;
		
		i++;
		
		if( i == 1 )
		{
			System.out.println("Hallo");
			i++;
		}
		
		if( i == 2 )
		{
			System.out.println("Hier");
		}
		else
		{
			System.out.println("Nirgendwo");
		}
		
	}
	
	public static void main(String[] args)
	{
		IfExample ie;
		
		ie = new IfExample();
		
		ie.foo();
	}
}
