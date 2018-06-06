package jana;

import jana.gui.MainFrame;
import jana.gui.MainModel;


public class Main
{	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length == 0)
			System.err.println("Please provide the path to the repository directory!");
		
		MainFrame main = new MainFrame(new MainModel(args[0]));
		
		main.openInWorld();
	}

}
