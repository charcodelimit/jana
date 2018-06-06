package fee.server;


import jana.java.JJavaProject;
import jana.java.JJavaSystemLibraries;
import jana.lang.java.JJavaSignature;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fee.FeeAnalysisThread;
import fee.FeeCompilationThread;
import fee.FeeServerStatusFrame;
import fee.FeeWorkerThread;
import fee.stardust.StarDust;
import fee.stardust.StarDustRemoteInterface;
import fee.stardust.StarDustStatusView;
import fee.util.ActiveServerException;
import fee.util.NoServerException;

/**
 * The analysis interface offered by FeeClient is synchronous.
 * TODO eventually pull out timing important constants 
 *      (default thread pool size, expected analysis times, ack-sleep-time) 
 *      into a properties file. 
 * 
 * There must be only one instance of FeeStarDustServer active per repository !
 * Otherwise, working file-synchronization is required!
 * 
 * @author chr
 *
 */
public class FeeStarDustServer extends StarDustServer implements FeeStarDustServerRemoteInterface, Runnable
{
	public static final String DEFAULT_HOSTNAME = "localhost";
	public static final int DEFAULT_RMI_PORT = 13580;
	public static final String REGISTERED_NAME = "fee-jana-server-root";
	
	private static final int CHECK_FOR_NEW_SERVERS_INTERVALL = 10; // how many classes should be analyzed before we add a new server if it became available
	private static final long MAXIMUM_EXPECTED_ANALYSIS_TIME = 14;
	private static final TimeUnit MAXIMUM_EXPECTED_ANALYSIS_TIME_UNITS = TimeUnit.DAYS;
	public static final long MINIMUM_EXPECTED_ANALYSIS_TIME = 50;
	private static final long MINIMUM_ACK_SLEEP_TIME = 2500; // time until the RMI connection is tested
	
	protected final static int MAX_BATCH_SIZE = 1 << 9; // 512
	protected final static int MIN_BATCH_SIZE = 1 << 3; // 8
	private static final int MAX_RESOLUTION_LEVEL = 1 << 5; // 32
	
	private volatile Thread serverThread;
	private int registeredServers;
	
	private boolean analyzeReferencedClasses = true;
	private boolean analyzeSupertypeClasses = false;
	private Map<String,Object> classNamesAnalyzed;
	
	private final List<StarDustRemoteInterface> starDustRemoteServers;
	private final Map<String, Integer> starDustRemoteServerNames; 
	private final Map<String, StarDustStatusView> statusViews;
	
	private StarDust star;
	private volatile JJavaSystemLibraries javaSystemLibraries;
	private volatile int registeredStatusViews;
	
	private Registry rmiRegistry;
	private String projectName;
	private String projectFilename;
	private int minServers;
	
	private boolean fileSystemSupportsLocking;
		
	public FeeStarDustServer(boolean cliMode) throws RemoteException
	{	
		super();
		
		this.minServers = 0;
		this.registeredServers = 0;
		
		this.starDustRemoteServers = new ArrayList<StarDustRemoteInterface>(1);
		this.starDustRemoteServerNames = new HashMap<String, Integer>();
		this.statusViews = new HashMap<String, StarDustStatusView>();
		this.registeredStatusViews = this.statusViews.size();
		this.serverPort = DEFAULT_RMI_PORT;
		
		this.setCliMode(cliMode);
		this.star = new StarDust(this, cliMode);
	}
	
	private JJavaProject getJavaProject() throws RemoteException
	{
		return this.star.getProject();
	}
	
	public int generateStarDustRemoteServerID() throws RemoteException
	{
		int index;
		
		synchronized (this.starDustRemoteServers)
		{
			this.registeredServers++;
			index = this.registeredServers;
			return index;
		}
	}
	
	public void registerStarDust(StarDust aStar, String registeredName)
	{
		StarDustStatusView starStatus;
		
		if(!isCliMode())
		{
			starStatus = new StarDustStatusView(aStar, registeredName);
			starStatus.setLocalMode();
			
			try
			{
				// set compression
				aStar.setCompressionLevel(this.compressionLevel());
				
				// set project file
				if(this.projectName != null)
					aStar.setProjectName(this.projectName);
				if(this.projectFilename != null)
					aStar.setProjectFileName(this.projectFilename);
				
				// set cli mode
				aStar.setCliMode(this.isCliMode());
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			statusViews.put(registeredName, starStatus);
			this.registeredStatusViews = statusViews.size();
		}
	}

	public void registerStarDustRemoteServer(String canonicalHostName, int portNumber, String registeredName)
			throws RemoteException
	{
		String URL, version;
		StarDustRemoteInterface remoteInterface;
		
		try 
		{		
			// construct the URL
			URL = "//" + canonicalHostName + ":" + portNumber + "/" + registeredName;
			logger.debug("Looking-up: " + URL);
			// get the JanaFee object
			remoteInterface = (StarDustRemoteInterface) Naming.lookup(URL);
			
			version = remoteInterface.getVersion();
			if( ! version.equals(StarDust.version()) )
				logger.warn("Wrong StarDust Version: " + version + " expected: " + StarDust.version());
			version = remoteInterface.getJavaVersion();
			if( ! version.equals(System.getProperty("java.version")))
				logger.warn("The StarDustRemoteServer " + URL + " runs on a JavaVirtualMachine supporting Java Version: " + version 
						    + "\n Fee runs on a JVM supporting Java Version: " + System.getProperty("java.version"));
			
			// initialize the JanaFee object
			// set compression level
			remoteInterface.setCompressionLevel(this.compressionLevel());
			// set the project to be used
			if(this.projectName != null)
				remoteInterface.setProjectName(this.projectName);
			if(this.projectFilename != null)
				remoteInterface.setProjectFileName(this.projectFilename);
			// set cli mode
			remoteInterface.setCliMode(this.isCliMode());
			
			if(!this.isCliMode())
			{
				StarDustStatusView statusView;
			
				statusView = new StarDustStatusView(remoteInterface, registeredName);
				statusViews.put(registeredName, statusView);
				this.registeredStatusViews = statusViews.size();
			}
			
			synchronized(this.starDustRemoteServers)
			{
				this.starDustRemoteServers.add(remoteInterface);
				this.starDustRemoteServerNames.put(registeredName, new Integer(this.starDustRemoteServers.size() - 1));
				
				if(this.serverThread == null)
					this.startWatchdogThread();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	public void removeStarDustRemoteServer(String registeredName) throws RemoteException
	{
		Integer index;
		
		synchronized(this.starDustRemoteServers)
		{
			
			index = this.starDustRemoteServerNames.get(registeredName);
			
			if(index != null && index.intValue() >= 0)
			{
				this.starDustRemoteServers.remove(index.intValue());
				this.starDustRemoteServerNames.remove(registeredName);
				
				if(this.registeredStatusViews > 0)
				{
					StarDustStatusView sv = this.statusViews.get(registeredName);
					this.statusViews.remove(registeredName);
					this.registeredStatusViews = statusViews.size();
				
					if(sv != null)
						sv.dispose();
				}
			}
			
			if(this.starDustRemoteServers.size() == 0)
				this.stopWatchdogThread();
		}
	}

	public void updateStatusView(String registeredName) throws RemoteException
	{
		StarDustStatusView statusView; 
	
		if(registeredStatusViews > 0)
		{
			statusView = this.statusViews.get(registeredName);
		
			if(statusView != null)
				statusView.update();
		}
	}
	
	public void addClassnameEntry(JJavaSignature aSignature, String aRelativeFilename) throws RemoteException, IOException
	{
		getJavaProject().addClassnameEntry(aSignature, aRelativeFilename);
	}
	
	/**
	 * set the minimum number of remote stardust remote servers required before the analysis is started
	 * 
	 * @param minServers
	 * @throws NoServerException
	 */
	public void setMinStarDustRemoteServers(int minServers)
	{
		this.minServers = minServers;
	}
	
	/************ JanaFee REMOTE INTERFACE **************/ 
	
	/***
	 * Tests if the StarDust remote servers are still available.
	 * If a remote server fails to answer, the server is removed from 
	 * the list of active servers. 
	 * 
	 * chr: This doesn't scale for a large number of servers!
	 */
	public boolean testConnections()
	{
		String serverName;
		int index;
		boolean allConnectionsWork;
		
		do
		{
			allConnectionsWork = true;
			serverName = null;
			
			synchronized(this.starDustRemoteServers)
			{	
				for(StarDustRemoteInterface remoteInterface : this.starDustRemoteServers)
				{
					try
					{
						Thread.sleep((long) (Math.random() * MINIMUM_EXPECTED_ANALYSIS_TIME));
					}
					catch(InterruptedException ie)
					{
						return false; // err on the save side, if we have to stop here
					}
				
					try
					{
						allConnectionsWork = allConnectionsWork && remoteInterface.isAlive();
					}
					catch(RemoteException re)
					{
						logger.debug("Stale connection found!");
						allConnectionsWork = allConnectionsWork && false;
						index = this.starDustRemoteServers.indexOf(remoteInterface);
					
						for(String key : this.starDustRemoteServerNames.keySet())
						{
							if(this.starDustRemoteServerNames.get(key).intValue() == index)
							{
								logger.debug("Server" + key + " does not respond!");
								
								serverName = key;
								break;
							}
						}
					
						return false; // can't find the server name, better terminate
					}
				}
			}
		
			// this is separate, because we don't want to modify the collection that we iterate
			if( serverName != null )
			{
				logger.debug("Removing " + serverName);
			
				try	{ this.removeStarDustRemoteServer(serverName); } 
				catch(Exception e){ logger.warn("Error while removing key: " + serverName + e.toString()); }
			}
		}
		while(!allConnectionsWork); // terminates, because broken connections are removed from the list
			
		return allConnectionsWork; // true
	}
	
	private void waitForServersToBecomeAvailable()
	{
		boolean serversAvailable;
		
		serversAvailable = this.starDustRemoteServers.size() >= this.minServers;
		
		if(!serversAvailable)
			logger.info("Waiting for " + this.minServers + " StarDust remote servers to become available.");
		
		while(!serversAvailable)
		{	
			try {
				Thread.sleep(MINIMUM_EXPECTED_ANALYSIS_TIME + (long) (Math.random() * MINIMUM_EXPECTED_ANALYSIS_TIME));
				serversAvailable = this.starDustRemoteServers.size() >= this.minServers;
			}
			catch(InterruptedException ie)
			{
				serversAvailable = true;
			}
		}
	}
	
	/************** ANALYSIS *************/
	
	/**
	 * Public Analysis Interface
	 * 
	 * chr: Assumes a single client!
	 * 
	 * @param aProjectFilename
	 */
	public void analyzeProjectFile(String aProjectFilename) throws IOException
	{
		this.setProjectFileName(aProjectFilename);
		this.analyzeProject();
	}
	
	/**
	 * Public Analysis Interface
	 * 
	 * chr: Assumes a single client!
	 * 
	 * @param aProjectName
	 * @throws IOException
	 */
	public void analyzeProject(String aProjectName) throws IOException
	{
		this.setProjectName(aProjectName);		
		this.analyzeProject();
	}
	
	private void analyzeProject() throws IOException
	{
		this.javaSystemLibraries = new JJavaSystemLibraries(this.star.getRepositoryDirectoryName());
		waitForServersToBecomeAvailable();
		analyzeProjectWithStarDust();
	}
	 
	private void analyzeProjectWithStarDust() throws RemoteException, IOException
	{
		List<JJavaSignature> projectClasses;
		List<JJavaSignature> projectAspects;
		JJavaProject project;
		long end,start;
		int level = 0;	
		int numProjectClassFiles;
			
		start = System.currentTimeMillis();
			
		project = getJavaProject();
			
		projectClasses = project.getProjectClasses();
		projectAspects = project.getProjectAspects();
		
		numProjectClassFiles = projectAspects.size() + projectClasses.size();
		
		this.classNamesAnalyzed = new HashMap<String,Object>(numProjectClassFiles);
		
		if(isCliMode())
			System.out.println("\nAnalyzing " + numProjectClassFiles + " classes at resolution-level: " + level);
		
		this.analyze(projectAspects);
		
		for(JJavaSignature signature : projectAspects)
			this.classNamesAnalyzed.put(signature.qualifiedName(), null);
		projectAspects = null;
		
		this.analyze(projectClasses);
		
		for(JJavaSignature signature : projectClasses)
			this.classNamesAnalyzed.put(signature.qualifiedName(), null);	
		projectClasses = null;
		
		if(this.analyzeReferencedClasses)
			resolveAllObjectTypeReferences();
		else 
			if(this.analyzeSupertypeClasses)
				resolveAllSuperTypes();
			
		
		end = System.currentTimeMillis();
			
		if(isCliMode())
			System.out.println();
			
		logger.info("Finished analysis of project " + project.getProjectName() + " in " + ((end - start) / 1000.0) + " sec.");
	}

	/**
	 * returns T if the class has not been already loaded, 
	 * and cannot be found in the system libraries
	 * 
	 * @param aQualifiedClassname
	 * @return
	 */
	private boolean classHasBeenAnalyzed(String aQualifiedClassname)
	{
		Map<String, Object> systemLibraryClassnames = null;
		
		if(this.javaSystemLibraries != null)
			systemLibraryClassnames = this.javaSystemLibraries.getJavaSystemLibraryClassnames();
		
		if(systemLibraryClassnames == null)
			systemLibraryClassnames = new HashMap<String,Object>();
		
		return this.classNamesAnalyzed.containsKey(aQualifiedClassname) || 
			   systemLibraryClassnames.containsKey(aQualifiedClassname);
	}
	
	private void resolveAllSuperTypes() throws IOException
	{
		List<String> classNamesSuperTypes;
		Set<String> superTypes;
		String[] qualifiedNames;
		int level = 0;
		
		classNamesSuperTypes = new ArrayList<String>();
		
		do
		{
			if(classNamesSuperTypes.size() > 0)
			{
				// create an array with the qualified names of external classes
				qualifiedNames = new String[0];
				qualifiedNames = classNamesSuperTypes.toArray(qualifiedNames);
		
				// add names of external classes to analyzed classes and free memory
				for(String className : classNamesSuperTypes)
					classNamesAnalyzed.put(className, null);
				classNamesSuperTypes = null;
				
				// analyze external classes
				this.analyze(qualifiedNames);
			}
			
			superTypes = this.getSuperTypes();
			classNamesSuperTypes = new ArrayList<String>();
		
			for(String qualifiedName : superTypes)
			{				
				if( !classHasBeenAnalyzed(qualifiedName) )
					classNamesSuperTypes.add(qualifiedName);
			}
			
			level++;
			
			if(isCliMode() && classNamesSuperTypes.size() > 0)
				System.out.println("\nAnalyzing " + classNamesSuperTypes.size() + " out of " + superTypes.size() + " supertype-classes at resolution-level: " + level);
			
			superTypes = null;
		}
		while(classNamesSuperTypes.size() > 0 && level < MAX_RESOLUTION_LEVEL);
		
		if(level >= MAX_RESOLUTION_LEVEL)
			logger.warn("Maximum resolution level was reached!" + level);
	}	
	
	private void resolveAllObjectTypeReferences() throws IOException
	{
		List<String> classNamesExternal;
		Set<String> typeNames;
		String[] qualifiedNames;
		int level = 0;
		
		classNamesExternal = new ArrayList<String>();
		
		do
		{
			if(classNamesExternal.size() > 0)
			{
				// create an array with the qualified names of external classes
				qualifiedNames = new String[0];
				qualifiedNames = classNamesExternal.toArray(qualifiedNames);
				
				// add names of external classes to analyzed classes and free memory
				for(String className : classNamesExternal)
					classNamesAnalyzed.put(className, null);
				classNamesExternal = null;
				
				// analyze external classes
				this.analyze(qualifiedNames);
			}
			
			typeNames = this.getReferencedObjectTypes();
			classNamesExternal = new ArrayList<String>();
		
			for(String qualifiedName : typeNames)
			{
				if( !classHasBeenAnalyzed(qualifiedName) )
					classNamesExternal.add(qualifiedName);
			}
			
			level++;
			
			if(isCliMode() && classNamesExternal.size() > 0)
				System.out.println("\nAnalyzing " + classNamesExternal.size() + " out of " + typeNames.size() + " referenced classes at resolution-level: " + level);
			
			typeNames = null;
		}
		while(classNamesExternal.size() > 0 && level < MAX_RESOLUTION_LEVEL);
		
		if(level >= MAX_RESOLUTION_LEVEL)
			logger.warn("Maximum resolution level was reached!" + level);
	}
	
	/**
	 * <b><i>Important Assumption</i></b>: 
	 * All object-types referenced by the analyzed classes
	 * are only known when the analysis is complete.
	 * Therefore, the remote analysis must block until all
	 * servers have finished their analysis.
	 * 
	 * @return
	 */
	private Set<String> getSuperTypes()
	{
		StarDustRemoteInterface server;
		Set<String> superTypes = new HashSet<String>();
		
		try
		{
			superTypes = new HashSet<String>(this.star.getSuperTypes());
		}
		catch(RemoteException re)
		{
			re.printStackTrace();
			logger.error(re.toString());
		}
		
		// check all servers
		synchronized(this.starDustRemoteServers)
		{	
			for( int idx = 0; idx < this.starDustRemoteServers.size(); idx++)
			{
				server = this.starDustRemoteServers.get(idx);
				
				try
				{
					superTypes.addAll(server.getSuperTypes());
				}
				catch(RemoteException re)
				{
					re.printStackTrace();
					logger.error(re.toString());
				}
			}
		}
		
		return superTypes;
	}
	
	/**
	 * <b><i>Important Assumption</i></b>: 
	 * All object-types referenced by the analyzed classes
	 * are only known when the analysis is complete.
	 * Therefore, the remote analysis must block until all
	 * servers have finished their analysis.
	 * 
	 * @return
	 */
	private Set<String> getReferencedObjectTypes()
	{
		StarDustRemoteInterface server;
		Set<String> objectTypes = new HashSet<String>();
		
		try
		{
			objectTypes = new HashSet<String>(this.star.getReferencedObjectTypes());
		}
		catch(RemoteException re)
		{
			re.printStackTrace();
			logger.error(re.toString());
		}
		
		// check all servers
		synchronized(this.starDustRemoteServers)
		{	
			for( int idx = 0; idx < this.starDustRemoteServers.size(); idx++)
			{
				server = this.starDustRemoteServers.get(idx);
				
				try
				{
					objectTypes.addAll(server.getReferencedObjectTypes());
				}
				catch(RemoteException re)
				{
					re.printStackTrace();
					logger.error(re.toString());
				}
			}
		}
		
		return objectTypes;
	}
	
	/**
	 * Public Analysis interface
	 * 
	 * chr: Assumes a single client!
	 * 
	 * @param aListOfSignatures
	 * @throws RemoteException
	 * @throws IOException
	 */
	public void analyze(List<JJavaSignature> aListOfSignatures) throws RemoteException, IOException
	{
		String[] classNames = new String[aListOfSignatures.size()];
		
		int i = 0;
		for( JJavaSignature signature : aListOfSignatures )
		{
			classNames[i++] = signature.qualifiedName();
		}
		
		aListOfSignatures = null;
		
		this.analyze(classNames);
	}
	
	/***
	 * Public Analysis interface
	 * 
	 * Blocks until all threads are finished.
	 * 
	 * chr: Assumes a single client!
	 * 
	 * @param aClassNameArray
	 */
	public void analyze(String[] aClassNameArray) throws IOException
	{
		this.delegateAnalysisToServer(aClassNameArray);
	}
	
	/***
	 * Public Analysis interface
	 *
	 * Picks a random FeeStarDustServer and
	 * blocks until the analysis is finished.
	 * This method avoids the overhead of creating an
	 * analysis thread.
	 * 
	 * chr: Assumes a single client!
	 * 
	 * @param aClassName
	 */
	public void analyze(String aClassName) throws IOException
	{
		delegateAnalysisToServer(aClassName);
	}
	
	/**
	 * Analysis Entry Point
	 * 
	 * explicitly anlyzes the class remotely if a
	 * remote server is connected, otherwise it is analyzed locally
	 * @param aClassNameArray
	 */
	private void delegateAnalysisToServer(String[] aClassNameArray) throws IOException
	{
		boolean analyzeWithStar;
		JJavaProject project;
		
		project = getJavaProject();
		if(this.fileSystemSupportsLocking)
			project.lockProject();
		project.loadClassnameMap();
		
		waitForServersToBecomeAvailable();
		
		analyzeWithStar = false;
		synchronized(this.starDustRemoteServers)
		{
			if(this.starDustRemoteServers.size() == 0)
				analyzeWithStar = true;
		}
		
		if(analyzeWithStar)
		{
			this.analyzeWithStar(aClassNameArray);
		}
		else
		{
			this.analyzeWithStarDustRemoteServer(aClassNameArray);
		}
		
		project.saveClassnameMap();
		if(this.fileSystemSupportsLocking)
			project.unlockProject();
	}
	
	
	/***
	 * explicitly analyzes the classes given by aClassNameArray locally 
	 * @param aClassNameArray
	 */
	private void analyzeWithStar(String[] aClassNameArray)
	{
		String classname;
		
		for( int i = 0; i < aClassNameArray.length; i++)
		{
			classname = aClassNameArray[i];

			this.analyzeWithStar(classname);
		}
	}
	
	/***
	 * Uses all StarDust servers to analyze a set 
	 * of classes.
	 * Blocks until the analysis is finished.
	 * 
	 * @param aClassName
	 */
	private void analyzeWithStarDustRemoteServer(String[] aClassNameArray)
	{
		analyzeInParallel(aClassNameArray);
		//analyzeInSeries(aClassNameArray);
	}
	
	/**
	 * Blocks until a server is finished.
	 * Analyze in parallel is the preferred way on multi processor machines.
	 * chr: Assumes a single client for the remote servers! 
	 * 
	 * @param aClassNameArray
	 */
	@SuppressWarnings("unused")
	private void analyzeInSeries(String[] aClassNameArray) throws IOException
	{
		for(int i = 0; i < aClassNameArray.length; i++)
			this.analyze(aClassNameArray[i]);
	}
	
	/***
	 * Blocks until all threads are finished.
	 * chr: Assumes a single client for the remote servers! 
	 * 
	 * @param aClassNameArray
	 */
	@SuppressWarnings("unused")
	private void analyzeInParallel(String[] aClassNameArray)
	{
		ExecutorService executor = Executors.newFixedThreadPool(1);
		FeeWorkerThread[] workerThreads = new FeeAnalysisThread[0];
		String classnames[] = new String[0];
		
		boolean done = false;
		int maxThreads;
		int maxRemoteServers;
		int previouslyAnalyzedClasses;
		int analyzedClasses;
		int classesToAnalyze;
		
		int batchSize;
		
		analyzedClasses = 0;
		maxRemoteServers = 0;
		maxThreads = 1;
		batchSize = MIN_BATCH_SIZE;
		
		while(!done)
		{
			if(analyzedClasses == 0 || analyzedClasses % CHECK_FOR_NEW_SERVERS_INTERVALL == 0)
			{
				synchronized(this.starDustRemoteServers)
				{
					maxRemoteServers = this.starDustRemoteServers.size();
					maxThreads = maxRemoteServers + 1;
				}	
				
				if(maxRemoteServers > 0)
				{
					batchSize = Math.max(MIN_BATCH_SIZE, aClassNameArray.length / (maxRemoteServers << 2));
					batchSize = Math.min(MAX_BATCH_SIZE, batchSize);
				}
				else
				{
					batchSize = MIN_BATCH_SIZE; 
				}
				
			}
			
			if(maxThreads > workerThreads.length)
			{
				executor = Executors.newFixedThreadPool(maxThreads);
				workerThreads = new FeeAnalysisThread[maxThreads];
					
				workerThreads[0] = new FeeAnalysisThread(this,this.star);
				
				for(int i = 0; i < maxRemoteServers; i++)
				{
					//analysisThreads[i] = new FeeAnalysisThread(this.tournamentBarrier, this.starDustRemoteServers, this.freeStarDustRemoteServers);
					workerThreads[i+1] = new FeeAnalysisThread(this,this.starDustRemoteServers.get(i));
				}
			}
			
			if(aClassNameArray.length > 0)
			{	
				previouslyAnalyzedClasses = analyzedClasses;
				
				// try to schedule analysis jobs
				for( int count = 0; count < maxThreads; count++ )
				{
					// only schedule the thread if the analysis is finished
					if(analyzedClasses < aClassNameArray.length && workerThreads[count].analysisIsDone()) 
					{
						classesToAnalyze = Math.min(aClassNameArray.length - analyzedClasses, batchSize);
						classnames = new String[classesToAnalyze];
						System.arraycopy(aClassNameArray, analyzedClasses, classnames, 0, classesToAnalyze);
							
						workerThreads[count].initThread(classnames);
						analyzedClasses+=classesToAnalyze;
						executor.execute(workerThreads[count]);
					}
				}
				
				// check if jobs could be scheduled
				if( analyzedClasses == previouslyAnalyzedClasses ) // all threads are still busy
				{
					try
					{
						synchronized(this)
						{
							this.wait();
						}
					}
					catch(InterruptedException ie)
					{
						return;
					}
				}
			}
			
			done = analyzedClasses >= aClassNameArray.length;
		}
		
		executor.shutdown();
		
		try
		{
			executor.awaitTermination(MAXIMUM_EXPECTED_ANALYSIS_TIME, MAXIMUM_EXPECTED_ANALYSIS_TIME_UNITS);
		} 
		catch (InterruptedException e)
		{
			return;
		}
		
		// chr: disc caches may lead to wrong status information! 
		logger.debug(this.star.getProjectInfo());
	}
	
	/**
	 * anlyze the class remotely if a remote server is connected, 
	 * or analyze locally
	 * @param aClassName
	 */
	private void delegateAnalysisToServer(String aClassName) throws IOException
	{
		boolean analyzeWithStar;
		JJavaProject project;
		
		project = getJavaProject();
		
		if(this.fileSystemSupportsLocking)
			project.lockProject();	
		project.loadClassnameMap();
	
		waitForServersToBecomeAvailable();
		
		analyzeWithStar = false;
		synchronized(this.starDustRemoteServers)
		{
			if(this.starDustRemoteServers.size() == 0)
				analyzeWithStar = true;
			else // distribute the load if remote servers are available
				if( Math.random() * 10 < 5)
					analyzeWithStar = true;
				else
					analyzeWithStar = false;
		}
		
		if(analyzeWithStar)
		{
			analyzeWithStar(aClassName);
		}
		else
		{
			analyzeWithStarDustRemoteServer(aClassName);
		}
		
		project.saveClassnameMap();
		if(this.fileSystemSupportsLocking)
			project.unlockProject();
	}
	
	/***
	 * explicitly anlyzes the class given by aClassName locally 
	 * @param aClassName
	 */
	private void analyzeWithStar(String aClassName)
	{
		try
		{	
			synchronized(this.star)
			{
				this.star.analyze(aClassName);
			}
		}
		catch(RemoteException re)
		{
			re.printStackTrace();
			logger.error(re.toString());
		}
	}
	
	/***
	 * Picks a random FeeStarDustServer and
	 * blocks until the analysis is finished.
	 * This method avoids the overhead of creating an
	 * analysis thread.
	 * 
	 * No synchronization between calls to analyze!
	 * Assumes that all servers are free!
	 * 
	 * @param aClassName
	 */
	private void analyzeWithStarDustRemoteServer(String aClassName)
	{
		StarDustRemoteInterface server;
		
		int index;
		
		// distribute the load -- pick a server randomly
		synchronized(this.starDustRemoteServers)
		{	
			index = (int) (this.starDustRemoteServers.size() * Math.random());
			server = this.starDustRemoteServers.get(index);
		}
		
		try
		{
			server.analyze(aClassName);
		}
		catch(RemoteException re)
		{
			re.printStackTrace();
			logger.error(re.toString());
		}
	}
	
	/************** COMPILATION *************/
	
	/**
	 * Public Compiler Interface
	 * 
	 * chr: Assumes a single client!
	 * 
	 * @param aProjectFilename
	 */
	public void compileProjectFile(String aProjectFilename) throws IOException
	{
		this.setProjectFileName(aProjectFilename);
		this.compileProject();
	}
	
	public void compileProject(String aProjectName) throws IOException
	{
		this.setProjectName(aProjectName);
		this.compileProject();
	}
	
	private void compileProject() throws IOException
	{
		waitForServersToBecomeAvailable();
		//TODO: cleanCompilationOutputDirectory();
		compileProjectWithStarDust();
		createFinalProjectJarFile();
	}
	
	public void compileProjectWithStarDust()
	{
		List<JJavaSignature> projectTransformedClasses;
		JJavaProject project;
		long end,start;

		try
		{	
			System.out.println("\nCompiling ");
			
			start = System.currentTimeMillis();
			
			project = this.star.getProject();
			
			if(!project.transformationOutputIsAvailable())
				return;
			
			projectTransformedClasses = project.getProjectTransformedClasses();
			
			this.compile(projectTransformedClasses);
			
			end = System.currentTimeMillis();
			
			if(isCliMode())
				System.out.println();
			
			logger.info("Finished compilation of project " + project.getProjectName() + " in " + ((end - start) / 1000.0) + " sec.");
		}
		catch(RemoteException re)
		{
			re.printStackTrace();
			logger.error(re.toString());
		}
	}
	
	public void compile(List<JJavaSignature> aListOfSignatures) throws RemoteException
	{
		String[] classNames = new String[aListOfSignatures.size()];
		
		int i = 0;
		for( JJavaSignature signature : aListOfSignatures )
		{
			classNames[i++] = signature.qualifiedName();
		}
		
		this.compile(classNames);
	}
	
	/***
	 * Blocks until all threads are finished.
	 * chr: Assumes a single client!
	 * 
	 * @param aClassNameArray
	 */
	public void compile(String[] aClassNameArray) throws RemoteException
	{
		JJavaProject project = this.star.getProject();
		
		if(!project.transformationOutputIsAvailable())
			return;
		
		compileWithStarDust(aClassNameArray);
	}
	
	/***
	 * explicitly compiles the class given by aClassName locally 
	 * @param aClassNameArray
	 */
	private void compileWithStar(String[] aClassNameArray)
	{
		String classname;
		
		for( int i = 0; i < aClassNameArray.length; i++)
		{
			classname = aClassNameArray[i];

			this.compileWithStar(classname);
		}
	}
	
	/**
	 * explicitly anlyzes the class remotely if a
	 * remote server is connected, otherwise it is compiled locally
	 * @param aClassNameArray
	 */
	public void compileWithStarDust(String[] aClassNameArray) throws RemoteException
	{
		boolean compileWithStar;
		JJavaProject project;
		
		project = this.star.getProject();
		
		if(!project.transformationOutputIsAvailable())
			return;
		
		waitForServersToBecomeAvailable();
		
		compileWithStar = false;
		synchronized(this.starDustRemoteServers)
		{
			if(this.starDustRemoteServers.size() == 0)
				compileWithStar = true;
		}
		
		if(compileWithStar)
		{
			compileWithStar(aClassNameArray);
		}
		else
		{
			compileWithStarDustRemoteServer(aClassNameArray);
		}
	}
	
	private void compileWithStarDustRemoteServer(String[] aClassNameArray)
	{
		compileInParallel(aClassNameArray);
		//compileInSeries(aClassNameArray);
	}
	
	/**
	 * Blocks until a server is finished.
	 * Analyze in parallel is the preferred way on multi processor machines.
	 * chr: Assumes a single client for the remote servers! 
	 * 
	 * @param aClassNameArray
	 */
	@SuppressWarnings("unused")
	private void compileInSeries(String[] aClassNameArray) throws RemoteException
	{
		for(int i = 0; i < aClassNameArray.length; i++)
			this.compile(aClassNameArray[i]);
	}
	
	/***
	 * Blocks until all threads are finished.
	 * chr: Assumes a single client for the remote servers! 
	 * 
	 * @param aClassNameArray
	 */
	@SuppressWarnings("unused")
	private void compileInParallel(String[] aClassNameArray)
	{
		ExecutorService executor = Executors.newFixedThreadPool(1);
		FeeWorkerThread[] workerThreads = new FeeCompilationThread[0];
		String classnames[] = new String[0];
		
		boolean done = false;
		int maxThreads;
		int maxRemoteServers;
		int previouslyAnalyzedClasses;
		int compiledClasses;
		int classesToAnalyze;
		
		int batchSize;
		
		compiledClasses = 0;
		maxRemoteServers = 0;
		maxThreads = 1;
		batchSize = MIN_BATCH_SIZE;
		
		while(!done)
		{
			if(compiledClasses == 0 || compiledClasses % CHECK_FOR_NEW_SERVERS_INTERVALL == 0)
			{
				synchronized(this.starDustRemoteServers)
				{
					maxRemoteServers = this.starDustRemoteServers.size();
					maxThreads = maxRemoteServers + 1;
				}	
				
				if(maxRemoteServers > 0)
				{
					batchSize = Math.max(MIN_BATCH_SIZE, aClassNameArray.length / (maxRemoteServers << 2));
					batchSize = Math.min(MAX_BATCH_SIZE, batchSize);
				}
				else
				{
					batchSize = MIN_BATCH_SIZE;
				}
				
			}
			
			if(maxThreads > workerThreads.length)
			{
				executor = Executors.newFixedThreadPool(maxThreads);
				workerThreads = new FeeCompilationThread[maxThreads];
					
				workerThreads[0] = new FeeCompilationThread(this,this.star);
				
				for(int i = 0; i < maxRemoteServers; i++)
				{
					workerThreads[i+1] = new FeeCompilationThread(this,this.starDustRemoteServers.get(i));
				}
			}
			
			if(aClassNameArray.length > 0)
			{	
				previouslyAnalyzedClasses = compiledClasses;
				
				// try to schedule analysis jobs
				for( int count = 0; count < maxThreads; count++ )
				{
					// only schedule the thread if the analysis is finished
					if(compiledClasses < aClassNameArray.length && workerThreads[count].analysisIsDone()) 
					{
						classesToAnalyze = Math.min(aClassNameArray.length - compiledClasses, batchSize);
						classnames = new String[classesToAnalyze];
						System.arraycopy(aClassNameArray, compiledClasses, classnames, 0, classesToAnalyze);
							
						workerThreads[count].initThread(classnames);
						compiledClasses+=classesToAnalyze;
						executor.execute(workerThreads[count]);
					}
				}
				
				// check if jobs could be scheduled
				if( compiledClasses == previouslyAnalyzedClasses ) // all threads are still busy
				{
					try
					{
						synchronized(this)
						{
							this.wait();
						}
					}
					catch(InterruptedException ie)
					{
						return;
					}
				}
			}
			
			done = compiledClasses >= aClassNameArray.length;
		}
		
		executor.shutdown();
		
		try
		{
			executor.awaitTermination(MAXIMUM_EXPECTED_ANALYSIS_TIME, MAXIMUM_EXPECTED_ANALYSIS_TIME_UNITS);
		} 
		catch (InterruptedException e)
		{
			return;
		}
		
		// chr: disc caches may lead to wrong status information! 
		logger.debug(this.star.getProjectInfo());
	}
	
	/***
	 * Picks a random FeeStarDustServer and
	 * blocks until the compilation is finished.
	 * This method avoids the overhead of creating an
	 * compilation thread.
	 * 
	 * @param aClassName
	 */
	public void compile(String aClassName) throws RemoteException
	{
		JJavaProject project = this.star.getProject();
		
		if(!project.transformationOutputIsAvailable())
			return;
		
		compileWithStarDust(aClassName);
	}
	
	/***
	 * compiles the class given by aClassName locally 
	 * @param aClassName
	 */
	private void compileWithStar(String aClassName)
	{
		try
		{	
			synchronized(this.star)
			{
				this.star.compile(aClassName);
			}
		}
		catch(RemoteException re)
		{
			re.printStackTrace();
			logger.error(re.toString());
		}
	}
	
	/**
	 * compile the class remotely if a remote server is connected, 
	 * or compile locally
	 * @param aClassName
	 */
	public void compileWithStarDust(String aClassName) throws RemoteException
	{
		boolean compileWithStar;
		JJavaProject project;
		
		project = this.star.getProject();
		
		if(!project.transformationOutputIsAvailable())
			return;
		
		waitForServersToBecomeAvailable();
		
		compileWithStar = false;
		synchronized(this.starDustRemoteServers)
		{
			if(this.starDustRemoteServers.size() == 0)
				compileWithStar = true;
			else // distribute the load if remote servers are available
				if( Math.random() * 10 < 5)
					compileWithStar = true;
				else
					compileWithStar = false;
		}
		
		if(compileWithStar)
		{
			compileWithStar(aClassName);
		}
		else
		{
			compileWithStarDustRemoteServer(aClassName);
		}
	}
	
	/***
	 * Picks a random FeeStarDustServer and
	 * blocks until the compilation is finished.
	 * This method avoids the overhead of creating a
	 * compilation thread.
	 * 
	 * No synchronization between calls to compile!
	 * Assumes that all servers are free!
	 * 
	 * @param aClassName
	 */
	private void compileWithStarDustRemoteServer(String aClassName)
	{
		StarDustRemoteInterface server;
		
		int index;
		
		// distribute the load -- pick a server randomly
		synchronized(this.starDustRemoteServers)
		{	
			index = (int) (this.starDustRemoteServers.size() * Math.random());
			server = this.starDustRemoteServers.get(index);
		}
		
		try
		{
			server.compile(aClassName);
		}
		catch(RemoteException re)
		{
			re.printStackTrace();
			logger.error(re.toString());
		}
	}
	
	public void createFinalProjectJarFile() throws IOException
	{
		long start, end;
		
		try
		{
		
			start = System.currentTimeMillis();
			this.star.createFinalProjectJarFile(this.registeredServers);
			end = System.currentTimeMillis();
			
			verboseLogger.info("Creating the final .jar file took: " + ((end - start) / 1000.0) + " sec");
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	/**
	 * Should ask all Servers too!
	 * @param aClassName
	 */
	private void which(String aClassName)
	{
		this.star.which(aClassName);
	}
	
	public void which(String[] aClassNameArray) throws RemoteException
	{
		for(int i = 0; i < aClassNameArray.length; i++)
			this.which(aClassNameArray[i]);
	}
	
	public void whichProjectFiles(String[] aClassNameArray, String aProjectName) throws RemoteException, IOException
	{
		this.setProjectName(aProjectName);
		this.which(aClassNameArray);
	}
	
	public void whichProject(String aProjectName) throws RemoteException, IOException
	{
		this.setProjectName(aProjectName);
		this.whichProject();
	}
	
	private void whichProject() throws RemoteException, IOException
	{
		JJavaProject project;
		String[] classNameArray;
		
		project = this.star.getProject();
		
		classNameArray = new String[project.getProjectClasses().size()];
		
		int index = 0;
		for(JJavaSignature signature : project.getProjectClasses())
			classNameArray[index++] = signature.qualifiedName();
		
		this.which(classNameArray);
	}
		
	/**
	 * set the name of the repository directory
	 * The repository directory is the directory where 
	 * the analysis results are stored.
	 * 
	 * @param aFilename
	 */
	public void setRepositoryDirectory(String aFilename) throws IOException
	{	
		this.star.setRepositoryDirectory(aFilename);
	}
	
	/**
	 * Add elements in classpathString to the classpath of the local
	 * StarDust instance. The classpath of 
	 * remote StarDustRemoteServer instances has to be set locally on 
	 * these machines.
	 * @param classpathString
	 */
	public void addClasspath(String aClasspathString) throws IOException
	{	
		this.star.addClasspath(aClasspathString);
	}
	
	/**
	 * set the name of the project
	 * 
	 * @param aString
	 */
	public void setProjectName(String aName) throws IOException
	{
		this.projectName = aName;
	
		if(this.projectName == null)
		{
			logger.error(aName + " is no valid name for a project!");
			return;
		}
		
		if( ! testConnections() )
		{
			logger.warn("Some servers were not reachable!");
		}
		
		synchronized(this.starDustRemoteServers)
		{
			try	{
				this.star.setProjectName(aName);
				
				for(StarDustRemoteInterface server : this.starDustRemoteServers)
					server.setProjectName(aName);
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
				logger.error(ioe.toString());
			}
		}
		
		this.fileSystemSupportsLocking = getJavaProject().filesystemSupportsLocking();
	}
	
	/**
	 * set the name of the file where the project information
	 * is stored.
	 * 
	 * @param aString
	 */
	public void setProjectFileName(String aFilename)
	{
		this.projectFilename = aFilename;
	
		if(this.projectFilename == null)
		{
			logger.error(aFilename + " is no valid project filename!");
			return;
		}
		
		if( ! testConnections() )
		{
			logger.warn("Some servers were not reachable!");
		}
		
		synchronized(this.starDustRemoteServers)
		{
			try	{
				this.star.setProjectFileName(aFilename);
				
				for(StarDustRemoteInterface server : this.starDustRemoteServers)
					server.setProjectFileName(aFilename);
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
				logger.error(ioe.toString());
			}
		}
	}
	
	public void setCompressionLevel(int compressionLevel) throws RemoteException
	{
		super.setCompressionLevel( compressionLevel );
		
		if( ! testConnections() )
		{
			logger.warn("Some servers were not reachable!");
		}
		
		synchronized(this.starDustRemoteServers)
		{
			try	{
				this.star.setCompressionLevel(compressionLevel);
				
				for(StarDustRemoteInterface server : this.starDustRemoteServers)
					server.setCompressionLevel(compressionLevel);
			}
			catch(RemoteException re) {
				re.printStackTrace();
				logger.error(re.toString());
			}
		}
	}
	
	/**
	 * set if the referenced classes should be analyzed too
	 * (default value if not set is true)
	 */
	public void setAnalyzeReferencedClasses(boolean aBooleanValue)
	{
		this.analyzeReferencedClasses = aBooleanValue;
		
		if( ! testConnections() )
		{
			logger.warn("Some servers were not reachable!");
		}
		
		synchronized(this.starDustRemoteServers)
		{
			try	{
				this.star.setRecordReferencedObjectTypes(aBooleanValue);
				
				for(StarDustRemoteInterface server : this.starDustRemoteServers)
					server.setRecordReferencedObjectTypes(aBooleanValue);
			}
			catch(RemoteException re) {
				re.printStackTrace();
				logger.error(re.toString());
			}
		}
	}


	/**
	 * set if the supertype classes should be analyzed too
	 * (default value if not set is false)
	 */
	public void setAnalyzeSupertypeClasses(boolean aBooleanValue)
	{
		this.analyzeSupertypeClasses = aBooleanValue;
		
		if( ! testConnections() )
		{
			logger.warn("Some servers were not reachable!");
		}
		
		synchronized(this.starDustRemoteServers)
		{
			try	{
				this.star.setRecordSupertypes(aBooleanValue);
				
				for(StarDustRemoteInterface server : this.starDustRemoteServers)
					server.setRecordSupertypes(aBooleanValue);
			}
			catch(RemoteException re) {
				re.printStackTrace();
				logger.error(re.toString());
			}
		}
	}

	
	public void startFeeStarDustServer() throws Exception
	{
		startFeeStarDustServer(DEFAULT_HOSTNAME, new Integer(DEFAULT_RMI_PORT).toString());
	}
	
	public void startFeeStarDustServer(String aPortNumber) throws Exception
	{	
		startFeeStarDustServer(DEFAULT_HOSTNAME, aPortNumber);
	}
	
	public void startFeeStarDustServer(String aHostname, String aPortnumber) throws Exception
	{
		int registrySize;
		
		if(aHostname != null)
			setHostName(aHostname);
		else
			setHostName(DEFAULT_HOSTNAME);
		
		if(aPortnumber != null)
			setPortNumber(aPortnumber);
		else
			setPortNumber(new Integer(DEFAULT_RMI_PORT).toString());
		
		try
		{	
			this.rmiRegistry = LocateRegistry.getRegistry(this.serverAddress.getCanonicalHostName(), this.serverPort);
			
			try
			{
				registrySize = this.rmiRegistry.list().length;
				logger.debug("Found Registry with " + registrySize + " entries.");
			}
			catch(Exception e)
			{
				logger.info("No RMI registry found running on: //" + this.serverAddress.getCanonicalHostName() +
						    ":" + this.serverPort + " !\n" + "Creating new Registry.");
				this.rmiRegistry = LocateRegistry.createRegistry(this.serverPort);
			}
			
			FeeStarDustServerRemoteInterface stub = (FeeStarDustServerRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
			this.rmiRegistry.bind(REGISTERED_NAME, stub);	
		
			logger.info(this.getClass().getName() + " " + REGISTERED_NAME + " bound!");
		}
		catch(AlreadyBoundException abe)
		{
			throw new ActiveServerException("A " + this.getClass().getName()  +" instance has been found running!");
		}
		catch(Exception ex)
		{
			// print a notification and re-throw
			logger.error("Failed to bind the JanaFeeServer " + REGISTERED_NAME + " to port " + this.serverPort + 
					     " on host " + this.serverAddress.getCanonicalHostName()); // + ".\n" + "Because of: " + ex.toString());
			throw ex;
		}
		
		if(!isCliMode())
			FeeServerStatusFrame.getInstance().update();
	}
	
	private void startWatchdogThread()
	{
		super.stopped = false;
		
		// Start the RMI connection Watchdog
		this.serverThread = new Thread(this);
		this.serverThread.start();
	}
	
	private void stopWatchdogThread()
	{
		super.stopped = true;
		this.serverThread.interrupt();
		this.serverThread = null;
	}
	
	/**
	 * Checks periodically if the servers are still alive.
	 * If a connection fails, the server is removed from the list.
	 */
	public void run()
	{
		boolean running = true;
		
		while(running)
		{	
			synchronized(this.starDustRemoteServers)
			{	
				testConnections();
			}
			
			try {
				Thread.sleep(MINIMUM_ACK_SLEEP_TIME + (long) (Math.random() * MINIMUM_ACK_SLEEP_TIME));
			}
			catch(InterruptedException ie)
			{
			}
			
			synchronized(this.stopped)
			{
				if(this.stopped)
				{
					logger.debug("Stopping FeeStarDustServer ...");
					running = false;
				}
			}
		}
		
		logger.debug("FeeStarDustServer stopped!");
	}
	
	public void stopServer()
	{
		this.stopWatchdogThread();
		
		try
		{
			UnicastRemoteObject.unexportObject(this.rmiRegistry, true);
		}
		catch(NoSuchObjectException nsoe)
		{
			logger.warn(nsoe);
		}
	}
}
