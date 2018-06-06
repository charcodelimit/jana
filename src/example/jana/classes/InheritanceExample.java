package example.jana.classes;

public class InheritanceExample extends SuperClassExample implements InnerInterfaceTest
{
	int v;
	static int w;
	
	public void foo()
	{
		super.u = 12;
		this.v = 4;
		z = (float) Math.PI;
		w = 33;
		
		System.out.println("x: " + x);
		System.out.println("y: " + y);
		System.out.println("z: " + z);
		System.out.println("u: " + u);
		System.out.println("v: " + v);
		System.out.println("w: " + w);
		
		System.out.println("--------");
		
		SuperClassExample.z = u;
		InheritanceExample.w = v;
		
		System.out.println("z: " + z);
		System.out.println("u: " + u);
		System.out.println("v: " + v);
		System.out.println("w: " + w);
	}
	
	public void bar()
	{
		System.out.println("InheritanceExample's bar");
		super.bar();
	}
	
	public static void main(String[] args)
	{
		InnerInterfaceTest iit;
		SuperClassExample sce;
		InheritanceExample ie;
		
		iit = new InheritanceExample();
		
		iit.foo();
		System.out.println("accessing ");
		ie = (InheritanceExample) iit;
		ie.bar();
		ie.flupp();
		System.out.println("accessing ");
		sce = (SuperClassExample) iit; 
		sce.bar();
		
		InheritanceExample.baz();
	}

}
