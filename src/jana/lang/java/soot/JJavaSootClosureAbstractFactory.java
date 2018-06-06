package jana.lang.java.soot;

import jana.java.JJavaClasspath;
import jana.java.JJavaRepository;
import jana.lang.java.JJavaClosureAbstractFactory;
import jana.lang.java.JJavaClosureFactory;
import jana.lang.java.JJavaSignature;
import jana.lang.java.bcel.typesystem.JJavaBcelType;
import jana.lang.java.soot.typesystem.JJavaSootType;
import jana.util.logging.JLogger;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

public class JJavaSootClosureAbstractFactory extends JJavaClosureAbstractFactory 
{
	private final static int MAX_GC_TRIES = 128; // how hard to try garbage collecting 
	
	private final long memorySavingThresholdHigh, memorySavingThresholdLow;
	private long maxFree = 0L;
	
	public JJavaSootClosureAbstractFactory(long memorySavingThresholdHigh, long memorySavingThresholdLow)
	{
		this.memorySavingThresholdHigh = memorySavingThresholdHigh;
		this.memorySavingThresholdLow = memorySavingThresholdLow;
	}
	
	public JJavaClosureFactory newInstance(String qualifiedClassName, JJavaClasspath classpath) 
	{	
		initSoot();
		
		return new JJavaSootClosureFactory(qualifiedClassName, classpath);
	}
	
	/**
	 * Initializes the Jimple Analysis Options for Soot
	 */
	private void initSoot()
	{
		// preserve line number information
		Options.v().set_keep_line_number(true);
		
		// JIMPLE optimizations (default - off)
		Options.v().setPhaseOption("wjop", "enabled:false"); // whole program jimple optimizations (static method binder, static inliner)
		Options.v().setPhaseOption("jop", "enabled:false");  // jimple optimizations
		
		// JIMPLE Body Creation
		Options.v().setPhaseOption("jb.tt", "enabled:false"); // trap tightener
		Options.v().setPhaseOption("jb.ls", "enabled:true");  // locals splitter
		Options.v().setPhaseOption("jb.a", "enabled:true");   // locals aggregator
		Options.v().setPhaseOption("jb.ule", "enabled:true"); // unused local eliminator
		Options.v().setPhaseOption("jb.tr", "enabled:true");  // type assigner
		Options.v().setPhaseOption("jb.tr", "use-older-type-assigner:false"); // use old type assigner (chr: which is more deterministic)
		Options.v().setPhaseOption("jb.ulp", "enabled:false"); // unsplit-orignals locals packer  (makes only sense in combination with splitter)
		Options.v().setPhaseOption("jb.lns", "enabled:true"); // local names standardizer (standardizes names that represent stack locations)
		Options.v().setPhaseOption("jb.cp", "enabled:false");  // copy propagator
		Options.v().setPhaseOption("jb.dae", "enabled:true"); // dead assignment eliminator
		Options.v().setPhaseOption("jb.cp-ule", "enabled:false"); // post-copy propagation unused local eliminator (removes locals that become unused through copy propagation)
		Options.v().setPhaseOption("jb.lp", "enabled:false");   // locals packer (implementation-wise the same as jb.ulp)
		Options.v().setPhaseOption("jb.ne", "enabled:false");  // nop eliminator
		Options.v().setPhaseOption("jb.uce", "enabled:false"); // unreachable code eliminator
	}
	
	public void resetCachesLazily()
	{
		long free;
		
		free = Runtime.getRuntime().freeMemory();
		
		JLogger.getLogger(JJavaRepository.VERBOSE_LOGGER).debug((free >> 20) + " MB [" + (memorySavingThresholdHigh >> 20) + " MB]" );
		
		if(this.maxFree < Runtime.getRuntime().freeMemory())
			this.maxFree = Runtime.getRuntime().freeMemory();
		
		if( free < memorySavingThresholdHigh)
		{
			PackManager.v().releaseAllBodies();
			System.out.print("r");
		}
		
		if( free < memorySavingThresholdLow )
		{
			this.resetCaches();
		}
	}
	
	public void resetCaches()
	{	
		StringBuffer memoryInfo = new StringBuffer();
		
		System.out.print("s");

		memoryInfo.append("\n-----------------");
		memoryInfo.append("\nMemoization Cache");
		memoryInfo.append("\n-----------------");
		memoryInfo.append("\nSignatures: " + JJavaSignature.numSignatures());
		memoryInfo.append("\nSoot Types: " + JJavaSootType.numTypes());
		memoryInfo.append("\nBCEL Types: " + JJavaBcelType.numTypes());
		memoryInfo.append("\n------------------");
		memoryInfo.append("\nTotal Memory: " + (Runtime.getRuntime().totalMemory() >> 20L) + " MBytes");
		memoryInfo.append("\nFree Memory: " + (Runtime.getRuntime().freeMemory() >> 20L) + " MBytes");
		memoryInfo.append("\nMaximum Free Memory Since Last Reset: " + (this.maxFree >> 20L) + " MBytes");	
		
		JLogger.getLogger(JJavaRepository.VERBOSE_LOGGER).info(memoryInfo.toString());

		this.maxFree = 0;
		
		this.reset();

		int count = 0;

		// wait until memory has been successfully reclaimed before continuing
		do
		{
			System.gc();
			try
			{
				Thread.sleep(10);
			} 
			catch (InterruptedException e)
			{
				return;
			}
			count++;
		}
		while( count < MAX_GC_TRIES && Runtime.getRuntime().freeMemory() < this.memorySavingThresholdLow ); 
	}
	
	public void reset()
	{	
		Scene.v().resetAll();
	}
}
