package fee;

import jana.java.JJavaRepository;
import jana.util.logging.JLogger;

import java.io.IOException;
import java.rmi.RemoteException;

import fee.server.FeeFoilServer;
import fee.server.FeeStarDustServer;
import fee.util.NoServerException;
import fee.util.ActiveServerException;

/**
 * The Java interface to the Fee system
 * 
 * @author chr
 *
 */
public class FeeServer
{
	private int instances = 0;
	
	private static final int CHECK_SERVER_SHOULD_STOP = 1000; // time until checking if server should be stopped
	private boolean stopServer = false;
	
	private FeeFoilServer feeFoilServer;
	private FeeStarDustServer feeStarDustServer;
	
	private boolean cliMode = false;
	
	public FeeServer(boolean cliMode)
	{
		if(instances > 0)
			throw new RuntimeException("There is already a FeeServer instance active in your JVM!");
		
		this.feeFoilServer = null;
		this.feeStarDustServer = null;
		this.cliMode = cliMode;
	}
		
	/******** SERVER MODE ***********/
	public void enterServerMode() throws Exception
	{	
		while(!this.stopServer)
		{
			Thread.sleep(CHECK_SERVER_SHOULD_STOP);
		}
		
		try
		{
			this.finalize();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			throw new Exception(t.toString(), t);
		}
	}
	
	public void startFoilServer(String foilServerHostName, String foilServerPort) throws Exception
	{
		if(this.feeFoilServer != null)
			throw new ActiveServerException("A " + Fee.APPLICATION_NAME + " server is already running on your system!");
		
		this.feeFoilServer = new FeeFoilServer(this.cliMode);
		
		if(!this.cliMode)
			FeeServerStatusFrame.getInstance().addServerStatusModel(this.feeFoilServer, "Foil Server");
		
		this.feeFoilServer.startServer(foilServerHostName,foilServerPort);
	}
	
	public void startStarDustServer(String feeStarDustServerHostName, String feeStarDustServerPort) throws Exception
	{
		if(this.feeStarDustServer != null)
			throw new ActiveServerException("A " + Fee.APPLICATION_NAME + " server is already running on your system!");
		
		this.feeStarDustServer = new FeeStarDustServer(this.cliMode);
		
		if(!this.cliMode)
			FeeServerStatusFrame.getInstance().addServerStatusModel(this.feeStarDustServer, "StarDust Server");
		
		this.feeStarDustServer.startFeeStarDustServer(feeStarDustServerHostName,feeStarDustServerPort);
	}
	
	public void openUI()
	{
		FeeServerStatusFrame.getInstance().setVisible(true);
	}
	
	/**** PROXY METHODS ***/
	
	/***
	 * Picks a random FeeStarDustServer and
	 * blocks until the analysis is finished.
	 * This method avoids the overhead of creating an
	 * analysis thread.
	 * 
	 * @param aClassName
	 * @throws NoServerException Thrown when no server has been started.
	 */
	public void analyze(String aClassName) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.analyze(aClassName);
	}
	
	/***
	 * Blocks until all threads are finished.
	 * chr: Assumes a single client!
	 * 
	 * @param aClassName
	 * @throws NoServerException Thrown when no server has been started.
	 */
	public void analyze(String[] aClassNameArray) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
	
		if(aClassNameArray != null)
			this.feeStarDustServer.analyze(aClassNameArray);
		else
			System.err.println("The value of the class name array passed for analysis is null!"); // chr: kludge!
			
	}
	
	public void analyzeProject(String aProjectName) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.analyzeProject(aProjectName);	
	}
	
	public void analyzeProjectFile(String aProjectFilename) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.analyzeProjectFile(aProjectFilename);	
	}
	
	/***
	 * Picks a random FeeStarDustServer and
	 * blocks until the analysis is finished.
	 * This method avoids the overhead of creating an
	 * analysis thread.
	 * 
	 * @param aClassName
	 * @throws NoServerException Thrown when no server has been started.
	 */
	public void compile(String aClassName) throws NoServerException, RemoteException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.compile(aClassName);
	}
	
	/***
	 * Blocks until all threads are finished.
	 * chr: Assumes a single client!
	 * 
	 * @param aClassName
	 * @throws NoServerException Thrown when no server has been started.
	 */
	public void compile(String[] aClassNameArray) throws NoServerException, RemoteException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
	
		if(aClassNameArray != null)
			this.feeStarDustServer.compile(aClassNameArray);
		else
			System.err.println("The value of the class name array passed for analysis is null!"); // chr: kludge!			
	}
	
	public void compileProject(String aProjectName) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.compileProject(aProjectName);	
	}
	
	public void compileProjectFile(String aProjectFilename) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.compileProjectFile(aProjectFilename);	
	}
	
	public void which(String[] aClassnameArray) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.which(aClassnameArray);
	}
	
	public void whichProjectFiles(String[] aClassnameArray, String aProjectName) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.whichProjectFiles(aClassnameArray, aProjectName);
	}
	
	public void whichProject(String aProjectFilename) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.whichProject(aProjectFilename);
	}
	
	/**
	 * set if the referenced classes should be analyzed too
	 * (default value if not set is true)
	 */
	public void setAnalyzeReferencedClasses(boolean aBooleanValue)
	{
		this.feeStarDustServer.setAnalyzeReferencedClasses( aBooleanValue );
	}
	
	/**
	 * set if supertype classes should be analyzed too
	 * (default value if not set is true)
	 */
	public void setAnalyzeSupertypeClasses(boolean aBooleanValue)
	{
		this.feeStarDustServer.setAnalyzeSupertypeClasses( aBooleanValue );
	}
	
	/**
	 * set the name of the repository directory
	 * The repository directory is the directory where 
	 * the analysis results are stored.
	 * 
	 * @param aFilename
	 */
	public void setRepositoryDirectory(String aFilename) throws IOException, NoServerException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.setRepositoryDirectory(aFilename);
	}
	
	/***
	 * set the file in which the project information is stored
	 * 
	 * @param aFilename
	 * @throws NoServerException Thrown when no server has been started.
	 */
	public void setProjectFileName(String aFilename) throws NoServerException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.setProjectFileName(aFilename);
	}
	
	/**
	 * Sets the repository to terse output mode.
	 * This means that ZLIB deflate compression is used to compress the output.
	 * @throws NoServerException Thrown when no server has been started.
	 *
	 * @param compressionLevel - the compression level of the ZLIB compression (0-9)
	 */
	public void setCompressionLevel(int compressionLevel) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.setCompressionLevel(compressionLevel);
	}

	/**
	 * Adds the classpath elements found in classpathString to the classpath
	 * used for the analysis of Java .class files.
	 * @param classpathString
	 */
	public void addClasspath(String classpathString) throws NoServerException, IOException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.addClasspath(classpathString);
	}
	
	/**
	 * set the minimum number of remote stardust remote servers required before the analysis is started
	 * 
	 * @param minServers
	 * @throws NoServerException
	 */
	public void setMinStarDustComputeServers(int minServers) throws NoServerException
	{
		if(this.feeStarDustServer == null)
			throw new NoServerException("Please start a StarDust server first!");
		
		this.feeStarDustServer.setMinStarDustRemoteServers(minServers);
	}

	public void setCliMode(boolean aBooleanValue)
	{
		this.cliMode = aBooleanValue;
	}
	
	@Override
	/**
	 * Causes the VM to Exit, as there is no other way to stop the RMI Server!
	 */
	public void finalize() throws Throwable
	{
		JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).debug("FeeServer Finalizing ...");
		
		super.finalize();
		
		this.stopServer = true;
		
		if(this.feeFoilServer != null)
		{
			this.feeFoilServer.stopServer();
			this.feeFoilServer = null;
		}
		
		if(this.feeStarDustServer != null)
		{
			this.feeStarDustServer.stopServer();
			this.feeStarDustServer = null;
		}
		
		System.err.println("FeeServer Finalized!");
		
		Thread.yield();
		
		System.exit(0); // chr: Kludge, as we cannot stop the RMI server otherwise from running
	}
}
