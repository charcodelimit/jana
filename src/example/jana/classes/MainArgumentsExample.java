package example.jana.classes;

public class MainArgumentsExample
{
	public static void main(String argv[])
	{
		int num;

		System.out.println("argv.length: " + argv.length);

		if(argv.length != 0 ) {
			num = Integer.parseInt(argv[0]);
			System.out.println("Passed the integer: " + argv[0]);
		} else {
			System.out.println("No arguments were passed. Defaulting to 2.");
			System.out.println("  ");
			num = 2;
		}

		System.out.println("Num: " + num);
	}
}
