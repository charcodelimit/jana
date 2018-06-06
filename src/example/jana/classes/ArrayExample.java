package example.jana.classes;

public class ArrayExample
{
	private Integer intTestArray[];
	private Number numTestArray[][];
	private Object objTestArray[][][];
	
	public void localVarTest()
	{
		int basicIntTestArray[][];
		int[][] dynamicIntTestArray;
		
		dynamicIntTestArray = new int[12][12];
		
		basicIntTestArray = new int[12][dynamicIntTestArray.length];
		
		for(int i = 0; i < 12; i++)
		{
			for(int j = 0; j < 12; j++)
				basicIntTestArray[i][j] = (i *12) + j;
		}
		
		for(int i = 0; i < 12; i++)
		{
			for(int j = 0; j < 12; j++)
				System.out.print(basicIntTestArray[i][j] + " ");
			System.out.println();
		}
		
		if(basicIntTestArray instanceof int[][])
		{
			System.out.println("basicIntTestArray has type int[][]");
		}
		
		System.out.println("length of basicIntTestArray: " + basicIntTestArray.length);
	}
	
	public void instVarTest()
	{
		intTestArray = new Integer[12];
		
		for(int i = 0; i < intTestArray.length; i++)
		{
			intTestArray[i] = new Integer(intTestArray.length - i);
		}
		
		numTestArray = new Number[1][intTestArray.length];
		
		for(int i = 0; i < intTestArray.length; i++)
		{
			this.numTestArray[0][i] = (Number) intTestArray[i];	
		}
		
		System.out.println("numTestArray.length: " + numTestArray.length);
		System.out.println("numTestArray[0].length: " + numTestArray[0].length);
		System.out.println("intTestArray.length: " + intTestArray.length);
		objTestArray = new Object[1][numTestArray.length][intTestArray.length];
		
		
		for(int j = 0; j < this.numTestArray.length; j++)
		{
			for(int i = 0; i < numTestArray[j].length; i++)
			{
				this.objTestArray[0][j][i] = (Object) numTestArray[j][i];
				System.out.println(this.objTestArray[0][j][i]);
			}
		}	
		
		if(intTestArray instanceof Integer[])
		{
			System.out.println("Integer[]");
		}
	}
	
	public void test()
	{
		instVarTest();
		localVarTest();
	}
	
	public static void main(String[] args)
	{
		ArrayExample ae = new ArrayExample();
		
		ae.test();
	}
}
