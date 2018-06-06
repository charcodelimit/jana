package jana.tools;
import jana.java.JJavaClasspath;
import jana.java.JJavaDefaultClasspath;
import jana.util.logging.JLogLevel;
import jana.util.logging.JLogger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;

import soot.PackManager;
import soot.Scene;
import soot.options.Options;


/**
 * JINC is no compiler
 * 
 * TODO: - add command line options for providing a classpath
 * 
 * @author chr
 *
 */
public class JimpleCompiler
{
	protected static JLogger logger = JLogger.getLogger("JINC");
	
	public final static String JIMPLE_FILETYPE_DESIGNATOR = ".jimple";

	private ArrayList<String> classnames;
	
	public JimpleCompiler()
	{
		BasicConfigurator.configure();
		this.classnames = new ArrayList<String>(128);
	}
	
	public void collectClassnames(String aDirectoryName) throws IOException
	{
		collectClassnames(new File(aDirectoryName));
	}
	
	/**
	 * Collects the names of all classes by removing the filetype designator
	 * from the filenames found in aDirectory.
	 * 
	 * @param aDirectory
	 * @return
	 */
	private void collectClassnames(File aDirectory)
	{
		int count;
		String filename;
		File currentDirectory;
		ArrayList<File> directoryStack = new ArrayList<File>();
		
		this.classnames = new ArrayList<String>(128);
		
		// no directory was given, don't do anything
		if(!aDirectory.isDirectory())
			return;
		
		directoryStack.add(aDirectory);
		
		count = 0;
		
		while(!directoryStack.isEmpty())
		{
			currentDirectory = directoryStack.remove(directoryStack.size() - 1);
			
			for( File file : currentDirectory.listFiles() )
			{
				if( file.isDirectory() )
				{
					directoryStack.add(file);
				}
				
				if( file.isFile() )
				{
					filename = file.getName().trim(); 
					
					if( filename.toLowerCase().endsWith(JIMPLE_FILETYPE_DESIGNATOR) )
					{
						this.classnames.add( filename.substring(0, filename.lastIndexOf('.')) );
						count++;
					}
				}
			}
		}
		
		logger.info("Found " + count + " classes");
	}
	
	@SuppressWarnings("unchecked")
	public void compile(File aDirectory) throws Exception
	{
		JJavaClasspath javaClasspath;

		long overallTime1,overallTime2, loadTime1, loadTime2, analysisTime1, analysisTime2, outputTime1, outputTime2;
		
		overallTime1 = System.currentTimeMillis();
		
		// initialize classpath
		javaClasspath = new JJavaDefaultClasspath();
		javaClasspath.addClasspathElements(aDirectory.getCanonicalPath());
		javaClasspath.addClasspathElements(Scene.v().defaultClassPath());

		logger.debug("Using Classpath: " + javaClasspath.toString());
		
		// initialize Soot

		Scene.v().setSootClassPath(javaClasspath.toString());
		Options.v().set_output_format( Options.output_format_class );
		Options.v().set_src_prec( Options.src_prec_jimple );
		
		// Default options: Analysed Classes in 42.875 sec		
		// Default optimizations off: Analysed Classes in 11.874 sec 
		
		Options.v().set_output_jar(true);
		
		Options.v().set_output_dir("jinc-output.jar");
		
		//SHIMPLE optimizations (default - off)
		Options.v().setPhaseOption("sop","enabled:false"); // shimple constant propagator and folder
		
		// JIMPLE optimizations (default - off)
		Options.v().setPhaseOption("wjop", "enabled:false"); // whole program jimple optimizations (static method binder, static inliner)
		Options.v().setPhaseOption("jop", "enabled:false");  // jimple optimizations
		
		// JIMPLE Body Creation
		Options.v().setPhaseOption("jb.ls", "enabled:false");  // locals splitter
		Options.v().setPhaseOption("jb.a", "enabled:false");   // locals aggregator
		Options.v().setPhaseOption("jb.ule", "enabled:false"); // unused local eliminator
		Options.v().setPhaseOption("jb.tr", "enabled:false");  // type assigner
		Options.v().setPhaseOption("jb.ulp", "enabled:false"); // unsplit-orignals locals packer  (makes only sense in combination with splitter)
		Options.v().setPhaseOption("jb.lns", "enabled:false"); // local names standardizer (standardizes names that represent stack locations)
		Options.v().setPhaseOption("jb.cp", "enabled:false");  // copy propagator
		Options.v().setPhaseOption("jb.dae", "enabled:false"); // dead assignment eliminator
		Options.v().setPhaseOption("jb.cp-ule", "enabled:false"); // post-copy propagation unused local eliminator (removes locals that become unused through copy propagation)
		Options.v().setPhaseOption("jb.lp", "enabled:true");   // locals packer
		Options.v().setPhaseOption("jb.ne", "enabled:false");  // nop eliminator
		Options.v().setPhaseOption("jb.uce", "enabled:false"); // unreachable code eliminator
		
		// BAF Body Creation 
		Options.v().setPhaseOption("bb.lso", "enabled:true"); // load-store optimizer (needed for jgf-moldyn)
		Options.v().setPhaseOption("bb.pho", "enabled:false"); // peep-hole-optimizer
		Options.v().setPhaseOption("bb.ule", "enabled:false"); // unused-local eliminator
		Options.v().setPhaseOption("bb.lp", "enabled:true"); // locals packer
		
		// is usually inferred correctly
		//Options.v().set_main_class("Driver");

		logger.info("Adding Classes");

		for(String className : this.classnames)
		{
			logger.debug("Adding: " + className);
			Options.v().classes().add(className);
		}

		if(this.classnames == null || this.classnames.size() == 0)
			return; 

		logger.info("Added " + this.classnames.size() + " classes");
		
		logger.info("Loading Classes");
		
		loadTime1 = System.currentTimeMillis();
		
		for(String className : this.classnames)
		{
			logger.debug("Loading: " + className);
			Scene.v().loadClassAndSupport(className);
			System.out.print(".");
		}
		
	    Scene.v().loadNecessaryClasses();
		
		System.out.print(".");
		System.out.println();
		
		loadTime2 = System.currentTimeMillis();
		
		logger.info("Loaded Classes in " + ((loadTime2-loadTime1)/1000.0) + " sec");

		//Options.v().set_verbose(true);
		
		analysisTime1 = System.currentTimeMillis();
		
		logger.info("Analyzing");
		PackManager.v().runPacks();
		
		analysisTime2 = System.currentTimeMillis();
		
		logger.info("Analysed Classes in " + ((analysisTime2-analysisTime1)/1000.0) + " sec");
		
		outputTime1 = System.currentTimeMillis();
		
		logger.info("Writing Output");
		PackManager.v().writeOutput();
		
		outputTime2 = System.currentTimeMillis();
		
		logger.info("Wrote output in " + ((outputTime2-outputTime1)/1000.0) + " sec");
		
		overallTime2 = System.currentTimeMillis();
		
		logger.info("Compiled Files in Directory '" + aDirectory.getName() + "' in: " + ((overallTime2-overallTime1)/1000.0) + " sec");
	}

	public static void main(String[] args)
	{
		JimpleCompiler jinc;
		
		logger.setLevel(JLogLevel.DEBUG);
		
		if(args.length == 0)
		{
			System.out.println("Usage: TestJimpleCompiler <directory-name> <file-name>" + 
					"\n directory-name is the name of the directory that contains the .jimple files to be compiled." +
					"\n When no file-name is given, all files in the directory are compiled.");
			System.exit(0);
		}
		
		jinc = new JimpleCompiler();
		
		try
		{
			if(args.length > 1)
				for(int i=1; i < args.length; i++)
					jinc.classnames.add(args[i]);
			else
				jinc.collectClassnames(args[0]);
				
			jinc.compile(new File(args[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
}
