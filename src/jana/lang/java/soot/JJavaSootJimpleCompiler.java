package jana.lang.java.soot;

import jana.java.JJavaClasspath;
import jana.java.JJavaDebugInformation;
import jana.java.JJavaIntermediateRepresentationCompiler;
import jana.java.JJavaRepository;
import jana.util.logging.JLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;

import soot.Body;
import soot.G;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.options.Options;
import soot.tagkit.LineNumberTag;
import soot.tagkit.Tag;

public class JJavaSootJimpleCompiler extends JJavaIntermediateRepresentationCompiler
{	
	private final static boolean DEBUG_SOOT = false;
	private final static boolean DEBUG_SOOT_RESOLVER = false;
	
	private final static JLogger logger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER);
	private final static JLogger verboseLogger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER + ".verbose");

	private JJavaClasspath classpath;
	private String outputDirectory;
	private JJavaDebugInformation debugInformation;
	
	private ByteArrayOutputStream sootOutputBuffer;
	private PrintStream sootOutputStream;
	private SootClass sootClass;
	
	public JJavaSootJimpleCompiler()
	{
		this.sootOutputBuffer = new ByteArrayOutputStream();
		this.sootOutputStream = new PrintStream(this.sootOutputBuffer);
	}
	
	/**
	 * Set Soot Classpath
	 * 
	 * @param classPath
	 * @param qualifiedClassName
	 */
	private void initSootClasspath(String classPath)
	{
		Scene sc;
		String cp;
		
		if (classPath == null || classPath.trim().length() == 0)
		{
			cp = Scene.v().defaultClassPath();
		}
		else
		{
			cp = classPath;
		}
		
		sc = Scene.v();
		
		if(!this.classpath.classpathElementsAreInList(soot.SourceLocator.v().classPath()))
		{
			logger.debug("Setting Classpath!");
			sc.setSootClassPath(cp);
		}
	}
	
	/**
	 * Default options: Analysed Classes in 42.875 sec		
	 * Default optimizations off: Analysed Classes in 11.874 sec 
	 */
	private void initCompilerOptimizations()
	{	
		//SHIMPLE optimizations (default - off)
		Options.v().setPhaseOption("sop","enabled:false"); // shimple constant propagator and folder
		
		// JIMPLE optimizations (default - off)
		Options.v().setPhaseOption("wjop", "enabled:false"); // whole program jimple optimizations (static method binder, static inliner)
		Options.v().setPhaseOption("jop", "enabled:false");  // jimple optimizations
		
		// JIMPLE Body Creation (off - we already have a jimple body)
		Options.v().setPhaseOption("jb.tt", "enabled:false"); // trap tightener
		Options.v().setPhaseOption("jb.ls", "enabled:false");  // locals splitter
		Options.v().setPhaseOption("jb.a", "enabled:false");   // locals aggregator
		Options.v().setPhaseOption("jb.ule", "enabled:false"); // unused local eliminator
		Options.v().setPhaseOption("jb.tr", "enabled:false");  // type assigner
		Options.v().setPhaseOption("jb.ulp", "enabled:false"); // unsplit-orignals locals packer  (makes only sense in combination with splitter) 
		Options.v().setPhaseOption("jb.lns", "enabled:false"); // local names standardizer (standardizes names that represent stack locations)
		Options.v().setPhaseOption("jb.cp", "enabled:false");  // copy propagator
		Options.v().setPhaseOption("jb.dae", "enabled:false"); // dead assignment eliminator
		Options.v().setPhaseOption("jb.cp-ule", "enabled:false"); // post-copy propagation unused local eliminator (removes locals that become unused through copy propagation)
		Options.v().setPhaseOption("jb.lp", "enabled:false");   // locals packer
		Options.v().setPhaseOption("jb.ne", "enabled:false");  // nop eliminator
		Options.v().setPhaseOption("jb.uce", "enabled:false"); // unreachable code eliminator
		
		// BAF Body Creation 
		Options.v().setPhaseOption("bb.lso", "enabled:true"); // load-store optimizer (needed for jgf-moldyn to run fast)
		Options.v().setPhaseOption("bb.pho", "enabled:false"); // peep-hole-optimizer
		Options.v().setPhaseOption("bb.ule", "enabled:true"); // unused-local eliminator
		Options.v().setPhaseOption("bb.lp", "enabled:true"); // locals packer (needed to avoid coad bloat, e.g. for mpegaudio) 51556
	}
	
	/**
	 * Initialize Soot 
	 */
	public void initializeCompiler(JJavaClasspath aClasspath, File outputDirectory, JJavaDebugInformation debugInformation) throws IOException
	{	
		this.classpath = aClasspath;
		this.outputDirectory = outputDirectory.getCanonicalPath();
		this.debugInformation = debugInformation;
		
		initCompilerSettings();
	}
	
	private void initCompilerSettings()
	{
		initSootClasspath(this.classpath.toString());
		initCompilerOptimizations();
		
		if(Level.DEBUG.isGreaterOrEqual(logger.getLevel()))
			logger.debug("Compiler uses classpath: " + this.classpath.toString());
		
		Options.v().set_debug(DEBUG_SOOT);
		Options.v().set_debug_resolver(DEBUG_SOOT_RESOLVER);
		
		// preserve line number information
		Options.v().set_keep_line_number(true);
		
		// sources are .jimple files 
		Options.v().set_src_prec( Options.src_prec_jimple );
		
		// Output Format
		Options.v().set_output_format( Options.output_format_class );
		Options.v().set_output_jar(false);
		
		// Output Directory
		Options.v().set_output_dir(this.outputDirectory);
	}
		
	private void addDebugInformationToClass(String qualifiedClassName)
	{
		String sourceFileName;
		String methodSignature;
		Map<String, int[]> sourcePositions;
		int[] lineNumbers;
		
		sourceFileName = this.debugInformation.getFilenames().get(qualifiedClassName);
		
		if(sourceFileName != null)
			this.sootClass.addTag(new soot.tagkit.SourceFileTag(sourceFileName));
		else
			logger.warn("No Debug Information! Source-file attribute for class " + qualifiedClassName + " are not available.");
		
		
		sourcePositions = this.debugInformation.getSourcePositions().get(qualifiedClassName);
		
		if(sourcePositions != null)
		{
			for(SootMethod sootMethod : this.sootClass.getMethods())
			{
				if(sootMethod.isConcrete())
				{
					methodSignature = methodSignature(qualifiedClassName, sootMethod);
					lineNumbers = sourcePositions.get(methodSignature);
				
					if(lineNumbers != null)
					{
						addLineNumbersToMethod(sootMethod, lineNumbers);
					}
					else
						logger.warn("No Debug Information! No Line-Number attributes are available for method " + methodSignature + ".");
				}
			}
		}
		else
			logger.warn("No Debug Information! No Line-Number attributes are available for class " + qualifiedClassName + ".");
	}
	
	private void addLineNumbersToMethod(SootMethod sootMethod, int[] lineNumbers)
	{
		boolean wasAnalyzedBefore;
		Body body;
		
		// load method bodies
		wasAnalyzedBefore = sootMethod.hasActiveBody();
		body = sootMethod.retrieveActiveBody();
		PatchingChain<Unit> units = body.getUnits();
		
		// check if we have already added the line numbers
		if(wasAnalyzedBefore)
		{
			for(Unit unit : units)
			{
				for(Tag tag : unit.getTags())
					if(tag instanceof LineNumberTag)
						return;
			}
		}

		// no line numbers have been added so far -- lets add them now
		if(units.size() > lineNumbers.length)
		{
			logger.error("Can't add line numbers to method " + sootMethod.getSignature() + " because the number of instructions differs!");
			return;
		}
		
		int i = 0;
		for(Unit unit : units)
		{
			if(lineNumbers[i] >= 0)
				unit.addTag(new LineNumberTag(lineNumbers[i]));
			
			i++;
		}
	}

	@SuppressWarnings("unchecked")
	private String methodSignature(String qualifiedClassName, SootMethod sootMethod)
	{
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(sootMethod.getReturnType());
		buffer.append(' ');		
        buffer.append(qualifiedClassName);
        buffer.append('.');
        buffer.append(Scene.v().quotedNameOf(sootMethod.getName()));

        buffer.append('(');

        // parameters
        Iterator typeIt = sootMethod.getParameterTypes().iterator();
        //int count = 0;
        while (typeIt.hasNext()) {
            Type t = (Type) typeIt.next();

            buffer.append(t);

            if (typeIt.hasNext())
                buffer.append(", ");

        }

        buffer.append(')');
        
        return buffer.toString();
	}

	/**
	 * Loads a class from its Jimple intermediate representation
	 * 
	 * @param qualifiedClassName
	 */
	@SuppressWarnings("unchecked")
	private void loadSootClass(String qualifiedClassName)
	{
		Scene sc;
		
		sc = Scene.v();
		
		// make all application classes we compiled so far library classes
		List<SootClass> applicationClasses = new ArrayList<SootClass>(sc.getApplicationClasses().size());
		
		for(SootClass sootClass : sc.getApplicationClasses())
			applicationClasses.add(sootClass);
		
		for(SootClass sootClass : applicationClasses)
			sootClass.setLibraryClass();
		
		// remove the class if we have already compiled it and re-add
		if(sc.containsClass(qualifiedClassName))
		{
			this.sootClass = sc.getSootClass(qualifiedClassName);
			
			sc.removeClass(this.sootClass);
			sc.addClass(this.sootClass);
			
			System.out.print(".");
		}
				
		Options.v().classes().add(qualifiedClassName);
		
		// Required for Soot Version dev-3307: Options.v().set_whole_program(true);
		this.sootClass = sc.loadClassAndSupport(qualifiedClassName);
		this.sootClass.setApplicationClass();
		
		// this is where we load the method bodies
		addDebugInformationToClass(qualifiedClassName);
	}


	
	@SuppressWarnings("unchecked")
	public void compileIntermediateRepresentation(String aClassName)
	{
		long loadTime1, loadTime2, analysisTime1, analysisTime2, outputTime1, outputTime2;
		PrintStream defaultOut;

		initCompilerSettings();
		
		loadTime1 = System.currentTimeMillis();
		loadSootClass(aClassName);
		loadTime2 = System.currentTimeMillis();
		verboseLogger.verbose("Loading & Analyzing " + aClassName + " took: " + (loadTime2-loadTime1) + " ms");
		
		if(!DEBUG_SOOT)
		{
			defaultOut = G.v().out;
			G.v().out = this.sootOutputStream; // silence please
		}
		
		try
		{
			// optimize
			analysisTime1 = System.currentTimeMillis();
			PackManager.v().runPacks();
			analysisTime2 = System.currentTimeMillis();
			verboseLogger.verbose("Optimized Class in " + ((analysisTime2-analysisTime1)/1000.0) + " sec");

			outputTime1 = System.currentTimeMillis();
			// compilation takes place here
			PackManager.v().writeOutput();
			outputTime2 = System.currentTimeMillis();
			verboseLogger.verbose("Compiled & Wrote output in " + ((outputTime2-outputTime1)/1000.0) + " sec");					
		}
		finally
		{
			if(!DEBUG_SOOT)
				G.v().out = defaultOut;
			this.sootOutputBuffer.reset();
		}
	}
}
