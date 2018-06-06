package example.jana.classes;

/**
 * Methods from "The JavaTM Virtual Machine Specification", Section 7.10 "Compiling Switches"
 * 
 * @author chr
 *
 */
public class SwitchStatementExample
{
	enum Weather
	{
		cloudy, sunny, snowing, rainy;
	}
	
	public int niceWeather(Weather e)
	{
		switch(e) {
			case cloudy: return 0;
			case rainy: return -1;
			case snowing: return 1;
			case sunny: return 2;
			default: return 0;
		}
	}
	
	public int chooseNear(int i) {
	    switch (i) {
	        case 0:  return 0;
	        case 1:  return 1;
	        case 2:  return 2;
	        default: return -1;
	    }
	}
	
	public int chooseFar(int i) {
	    switch (i) {
	        case -100: return -1;
	        case 0:	   return 0;
	        case 100:  return 1;
	        default:   return -1;
	    }
	}
	
	public void test()
	{
		System.out.println("Choosing chooseNear(2): " + chooseNear(2));
		System.out.println("Choosing chooseFar(100): " + chooseFar(100));
		System.out.println("Choosing chooseNear(1024): " + chooseNear(1024));
		System.out.println("Choosing chooseFar(1024): " + chooseFar(1024));
	}
	
	public static void main(String[] args)
	{
		SwitchStatementExample sse;
		
		sse = new SwitchStatementExample();
		sse.test();
	}
}
