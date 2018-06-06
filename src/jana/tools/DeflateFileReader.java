package jana.tools;

import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;
import jana.util.logging.JSimpleLineLayout;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;


public class DeflateFileReader
{
	public static final String APPLICATION_NAME = "DeflateFileReader";
	public static final String DEFAULT_LOGGER = "deflate-tool";
	public static int INPUT_BUFFER_SIZE = 1 << 17; // 128k Input Buffer Size
	
	private final static JLogger logger = JLogger.getLogger(DEFAULT_LOGGER); 
		
	private DeflateFileReader()
	{
		super();
	}
	
	private Options createOptions()
	{
		Option help = new Option("h", "help", false, "print this message");
		help.setRequired(false);
		
		Option debug = new Option("v", "verbose", false, "print debugging information.");
		debug.setRequired(false);
		
		Option verbose = new Option("D", "debug", false, "print debugging information.");
		debug.setRequired(false);
		
		Option compress;
		OptionBuilder.withArgName( "compression-level" );
		OptionBuilder.withDescription("use GZip to compress the analysis output.");
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
		OptionBuilder.withLongOpt( "compress" );
		compress = OptionBuilder.create();
			
		OptionBuilder.withArgName( "filename" );
		OptionBuilder.hasArg();
		OptionBuilder.isRequired(false);
    	OptionBuilder.withDescription(  "the pathname relative to the repository directory of a project file that should be analyzed." );
		OptionBuilder.withLongOpt( "outputfile" );
    	Option outputFile = OptionBuilder.create();
				
		Options options = new Options();
		
		options.addOption(help);
		options.addOption(debug);
		options.addOption(verbose);
		options.addOption(compress);
		options.addOption(outputFile);
		
		return options;
	}
	
	public void processCommandLine(String[] args) throws Exception
	{
		HelpFormatter formatter;
		Options options;
		String[] remainingArgs;
		String helpstring;
		String inputFilename;
		
		helpstring = APPLICATION_NAME + " [options] Filename";
		
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
			
			if( cmd.hasOption("debug") )
			{
				BasicConfigurator.configure();
				
				logger.setLevel(JLogLevel.DEBUG);
			}
			else if( cmd.hasOption("verbose") )
			{
				int count = 0;
				for(String argument : args)
				{
					if(argument.equals("-verbose") || argument.equals("-v") ||
					   argument.equals("--verbose") || argument.equals("--v"))
						count++;
				}
				
				if(count > 1)
				{
					BasicConfigurator.configure(new ConsoleAppender(new PatternLayout()));
					
					logger.setLevel(JLogLevel.VERBOSE);
				}
				else
				{
					BasicConfigurator.configure(new ConsoleAppender(new JSimpleLineLayout()));
					
					logger.setLevel(JLogLevel.INFO);
				}
			}
			else
			{
				BasicConfigurator.configure(new ConsoleAppender(new JSimpleLineLayout(),"System.err"));	
				
				logger.setLevel(JLogLevel.WARN);
			}
						
			remainingArgs = cmd.getArgs();
			
			inputFilename = "";
			
			if(remainingArgs != null && remainingArgs.length > 0)
			{
				inputFilename = remainingArgs[0];
			}
			else
			{
				formatter.printHelp( helpstring, options );
				System.exit(0);				
			}
			
			if( cmd.hasOption("compress") )
			{	
				if( cmd.hasOption("outputfile") )
					logger.error("Unsupported Operation!");
				else
					deflateFileToSystemOut(inputFilename);
			}
			else
			{
				if( cmd.hasOption("outputfile") )
					logger.error("Unsupported Operation!");
				else
					inflateFileToSystemOut(inputFilename);
			}
		}
		catch(ParseException pe)
		{	
			System.err.println("Wrong options!");
			formatter.printHelp( helpstring, options );
			System.exit(0);
		}
		catch(IOException ioe)
		{
			System.err.println(ioe.toString());
			System.exit(1);
		}
	}
	
	private void deflateFileToSystemOut(String aFileName) throws IOException
	{
		this.deflateFileToSystemOut(aFileName,1);
	}
	
	private void deflateFileToSystemOut(String aFileName, int compressionLevel) throws IOException
	{
		Deflater def;
		File inputFile;
		BufferedInputStream bis;
		DataOutputStream daos;
		DeflaterOutputStream dos;
		byte[] buffer;
		int bytesRead;
		
		inputFile = new File(aFileName);
		
		if(!inputFile.exists())
			throw new FileNotFoundException("Can't find file: " + aFileName);
		if(!inputFile.canRead())
			throw new IOException("Can't read file: " + aFileName + " . \n" + 
								  "Please check the file permissions!");
		
		buffer = new byte[INPUT_BUFFER_SIZE];
		bis = new BufferedInputStream(new FileInputStream(inputFile), INPUT_BUFFER_SIZE);
		daos = new DataOutputStream(System.out);
		def = new Deflater(compressionLevel);
		dos = new DeflaterOutputStream(daos, def, (int) inputFile.length());
		
		try
		{
			do
			{
				bytesRead = bis.read(buffer);
				
				if(bytesRead > 0)
					dos.write(buffer,0,bytesRead);
			}	
			while(bytesRead > 0);
			
			dos.finish();
			daos.writeInt(def.getTotalIn());
		}
		finally
		{
			dos.close();
			bis.close();
		}
	}
	
	private void inflateFileToSystemOut(String aFileName) throws IOException
	{
		File inputFile;
		InflaterInputStream iis;
		byte[] buffer;
		int bytesRead;
		
		inputFile = new File(aFileName);
		
		if(!inputFile.exists())
			throw new FileNotFoundException("Can't find file: " + aFileName);
		if(!inputFile.canRead())
			throw new IOException("Can't read file: " + aFileName + " . \n" + 
								  "Please check the file permissions!");
		
		buffer = new byte[INPUT_BUFFER_SIZE];
		iis = new InflaterInputStream(new FileInputStream(inputFile), new Inflater(), (int) inputFile.length());
		
		try
		{
			do
			{
				bytesRead = iis.read(buffer);
				
				if(bytesRead > 0)
					System.out.write(buffer, 0, bytesRead);
			}	
			while(bytesRead > 0);
		}
		finally
		{
			iis.close();
		}
	}
	
	public static void main(String[] args)
	{	
		DeflateFileReader reader = new DeflateFileReader();
		
		try
		{
			reader.processCommandLine(args);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.err.println(e.toString());
			System.exit(1);
		}
	}
}
