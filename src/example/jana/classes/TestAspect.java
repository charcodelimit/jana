package example.jana.classes;

import open.weaver.annotations.Aspect;
import open.weaver.annotations.BeforeAdvice;
import open.weaver.annotations.PointcutDesignator;

@Aspect(name="Test") public class TestAspect
{
	@PointcutDesignator(expression="(and (method-signature \"foo\") (return-type (void-type)))") 
	Pointcut fooPointcut;
	
	@BeforeAdvice public void doThingsBeforeFoo()
	{
		System.out.println("Hello Foo!");
	}
}