package example.jana.classes;

public class InnerClassExample 
{
	int x;
	
	public class InnerClass2
	{
		Integer z;
		
		public class InnerInnerClass
		{
			Float a;
		
			public InnerInnerClass(float aNumber)
			{
				a = new Float(aNumber);
			}
			
			public float getFloatValue()
			{
				return a.floatValue();
			}
		}
		
		public InnerClass2(int aNumber)
		{
			z = new Integer(aNumber);
		}
		
		InnerInnerClass getInnermost()
		{
			return new InnerInnerClass(0.1f);
		}
		
		public int getIntValue()
		{
			return z + Math.round( 10.0f * this.getInnermost().getFloatValue() );  
		}
	}
	
	public InnerClassExample(int aNumber)
	{
		x = aNumber;
	}
	
	public int getIntValue()
	{
		InnerClass2 ic2;
		
		ic2 = new InnerClass2(x);
		return ic2.getIntValue();
	}
	
	public void anonymousInnerClass()
	{
		Object o;
		
		o = new Object() 
		{
			public boolean equals(Object o)
			{
				return false;
			}
		};
		
		if( o.equals(o) ) 
			System.out.println("Equal");
		else
			System.out.println("Unequal");
	}

	public static void main(String[] args)
	{
		InnerClassExample ice;
		InnerClass2 ic2;
		InnerClassExample.InnerClass2.InnerInnerClass iic;
		
		ice = new InnerClassExample(10);
		System.out.println("Value: " + ice.getIntValue());
				
		ic2 = ice.new InnerClass2(14);
		iic = ic2.new InnerInnerClass(0.21f);
		System.out.println("Value: " + iic.getFloatValue());
		
		ice.anonymousInnerClass();
		
	}
	
}
