package jana.tools;

import jana.tools.sde.SDEA;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Level;

/**
 * The SootDecompilerExtension
 * decompiles class files to JIMPLE code.
*/
public class SDE extends SDEA
{
	String sdeCLIName = "SDE";
	
	public SDE() throws Exception
	{
		super();
	}
	
	private Options createOptions()
	{
		Option help = new Option("help", "print this message");
		Option debug = new Option("debug", "print debugging information.");
		
		OptionBuilder.withArgName( "classnames" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "Exclude classes from analysis.");
    	OptionBuilder.withValueSeparator(File.pathSeparatorChar);
		Option exclude = OptionBuilder.create( "exclude" );
		
		OptionBuilder.withArgName( "files" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "Add files to classpath.");
    	OptionBuilder.withValueSeparator(File.pathSeparatorChar);
		Option classPath = OptionBuilder.create( "classpath" );
		
		OptionBuilder.withArgName( "files" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "find classes in given JAR-files. JAR-file names have to be separated by " + 
    			File.pathSeparatorChar + "." );
    	OptionBuilder.withValueSeparator(File.pathSeparatorChar);
		Option jarFiles = OptionBuilder.create( "jarfiles" );
		
		OptionBuilder.withArgName( "directories" );
    	OptionBuilder.hasArg();
    	OptionBuilder.withDescription(  "find classes in the given directories. Directory names have to be separated by " +
    			File.pathSeparatorChar + "." );
    	OptionBuilder.withValueSeparator(File.pathSeparatorChar);
    	Option directories = OptionBuilder.create( "directories" );
		
		Options options = new Options();
		
		options.addOption(help);
		options.addOption(debug);
		options.addOption(classPath);
		options.addOption(directories);
		options.addOption(jarFiles);
		options.addOption(exclude);
		
		return options;
	}
	
	public void processCommandLine(String[] args) throws Exception
	{
		HelpFormatter formatter;
		Options options;
		String[] remainingArgs;
		String helpstring;
		
		helpstring = sdeCLIName + " [options] [classnames]";
		
		options = createOptions();
		
		formatter = new HelpFormatter();
		
		try
		{
			CommandLineParser parser = new PosixParser();
			CommandLine cmd = parser.parse( options, args);
			
			if( cmd.hasOption("help") )
			{
				formatter.printHelp( helpstring, options );
				System.exit(0);
			}
			
			logger.setLevel(Level.INFO);
			
			if( cmd.hasOption("debug") )
				logger.setLevel(Level.DEBUG);
			
			if( cmd.hasOption("jarfiles") )
			{
				this.jarFiles( cmd.getOptionValue("jarfiles") );
			}
			
			if( cmd.hasOption("classpath") )
			{
				this.classPath( cmd.getOptionValue("classpath") );
			}
			
			if( cmd.hasOption("directories") )
				this.directories( cmd.getOptionValue("directories") );
		
			if( cmd.hasOption("exclude") )
				this.excludeClasses( cmd.getOptionValue("exclude") );
			
			remainingArgs = cmd.getArgs();
			
			long t1 = System.currentTimeMillis();
			this.analyzeClasses(remainingArgs);
			long t2 = System.currentTimeMillis();
			
			logger.info("Decompiling took " + (t2 - t1) + "ms");
		}
		catch(ParseException pe)
		{	
			formatter.printHelp( helpstring, options );
		}
	}
	
	public static void main(String[] args)
	{	
		SDE sde;
		
		try
		{
			sde = new SDE();
			sde.processCommandLine(args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
		}
	}
}
