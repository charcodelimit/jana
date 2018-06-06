package example.tests;

public class TestMethodArgs
{
	int x = 0;
	
	void foo(int arg)
	{
		arg = 10;
		System.out.println("arg0 in foo(): " + arg);
		System.out.println("this.x in foo(): " + this.x);
	}
	
	void bar()
	{
		this.x = 1;
		System.out.println("this.x before calling foo(): " + this.x);
		foo(this.x);
		System.out.println("this.x after calling foo(): " + this.x);
	}
	
	public static void main(String[] args)
	{
		TestMethodArgs tst = new TestMethodArgs();
		System.out.println("tst.x before calling bar(): " + tst.x);
		tst.bar();
		System.out.println("tst.x after calling bar(): " + tst.x);
	}
}
