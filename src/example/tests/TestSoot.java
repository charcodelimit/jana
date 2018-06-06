package example.tests;

import jana.java.JJavaDefaultClasspath;

import java.util.Iterator;

import soot.Body;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.JimpleBody;


public class TestSoot
{
	public static void main(String[] args)
	{
		SootClass sc;
		
		String className = "java.lang.Object";
		
		if(args.length == 1)
			className = args[0];
		else
		{
			System.out.println("Usage: TestSoot <classname>");
			System.exit(0);
		}
		
		if(args != null)
			if(args.length > 0 )
				className = args[0];
		
		System.out.println(Scene.v().defaultClassPath());
		
		try
		{
			Scene.v().setSootClassPath((new JJavaDefaultClasspath()).toString());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		
		sc = Scene.v().loadClassAndSupport(className);
		sc.setApplicationClass();
		
		System.out.println( className + " contains " + sc.getMethodCount() + " methods" );
		System.out.println( "\n" );
		
		for( Iterator<SootMethod> i = sc.getMethods().iterator(); i.hasNext(); )
		{
			SootMethod sm = (SootMethod) i.next();
			Body b = sm.retrieveActiveBody();
			/* there is no guarantee that future versions of soot will also return a jimple body, so first check.*/
			if(! (b instanceof JimpleBody) )
			{
				System.out.println("Whoops, didn't get a Jimple Body");
				System.exit(1);
			}
			
			JimpleBody jb = (JimpleBody) b;
			PatchingChain<Unit> uc = jb.getUnits();
			
			Unit[] units = new Unit[uc.size()];
			
			units = uc.toArray(units); 

			System.out.println( sm.toString() );
			System.out.println( jb.toString() );
		}
	}
}
