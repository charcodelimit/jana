package fee.stardust;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import fee.server.FeeStarDustServer;
import fee.server.FeeStarDustServerRemoteInterface;
import fee.server.StarDustServer;

public abstract class StarDustRemoteServer extends StarDustServer implements StarDustRemoteServerInterface, Runnable
{	
	private static final String DEFAULT_NAME = "stardust";
	
	private static final int MIN_SLEEP_TIME = 32768; // wake up every 32 seconds and update the views 
	
	private static final int DEFAULT_PORT = 13580;
	private static final String DEFAULT_HOST = "localhost";
	
	// the hostname and port for the RMI connection to the FeeStarDustServer
	protected InetAddress feeStarDustServerRMIInetAddress;
	protected int feeStarDustServerRMIPort;
	protected FeeStarDustServerRemoteInterface feeStarDustServerInterface;
	// the name under which this object can be found in the RMI registry
	protected String registeredName;
	protected Registry rmiRegistry;
	
	protected File repositoryDirectory;
	
	private long lastUpdate;
	
	public StarDustRemoteServer() throws RemoteException
	{
		super();
	
		this.registeredName = this.getClass().getName();
		this.serverPort = DEFAULT_PORT;
		this.feeStarDustServerRMIInetAddress = null;
		this.feeStarDustServerRMIPort = FeeStarDustServer.DEFAULT_RMI_PORT;
	}
	
	public StarDustRemoteServer(FeeStarDustServerRemoteInterface aFeeStarDustServer) throws RemoteException
	{
		this();
		
		this.feeStarDustServerInterface = aFeeStarDustServer;
	}
	
	/*********** STATE *****************/
	public String getRepositoryDirectoryName() throws RemoteException, IOException
	{
		return this.repositoryDirectory.getCanonicalPath();
	}
	
	/*********** RMI *******************/
	
	public void setFeeStarDustServerHostname(String aHostname) throws UnknownHostException
	{
		try
		{
			this.feeStarDustServerRMIInetAddress = hostInetAddress(aHostname);
		}
		catch(UnknownHostException uhe)
		{
			logger.warn(aHostname + " is no valid hostname! " + "\nUsing localhost." );
			this.serverAddress = InetAddress.getLocalHost();
		}
	}
	
	public void connectToFeeStarDustServer() 
	{
		String URL;
		int index;
		int registrySize;
		
		URL = "";
		index = 0;
		
		try
		{
			if(this.feeStarDustServerRMIInetAddress == null)
				setFeeStarDustServerHostname(FeeStarDustServer.DEFAULT_HOSTNAME);
		
			// construct the URL
			URL = "//" + this.feeStarDustServerRMIInetAddress.getCanonicalHostName() + 
			       ":" + this.feeStarDustServerRMIPort + "/" + FeeStarDustServer.REGISTERED_NAME;
		
			verboseLogger.info("Trying to look-up: " + URL);
		
			// get the server object
			this.feeStarDustServerInterface = (FeeStarDustServerRemoteInterface) Naming.lookup(URL);
		
			index = this.feeStarDustServerInterface.generateStarDustRemoteServerID();
		}
		catch(Exception ex)
		{
			logger.error("Failed to connect to " + URL + " because of: " + ex.toString());
			ex.printStackTrace();
			return;
		}
			
		this.registeredName = DEFAULT_NAME + "-" + index;
		verboseLogger.info("Registering as: " + registeredName);
		
		try
		{	
			if(this.getServerInetAdress() == null)
				this.setHostName(DEFAULT_HOST);
			
			this.rmiRegistry = LocateRegistry.getRegistry(this.serverAddress.getCanonicalHostName(), this.serverPort);
			
			try
			{
				registrySize = this.rmiRegistry.list().length;
				logger.debug("Found Registry with " + registrySize + " entries.");
			}
			catch(Exception e)
			{
				logger.warn("No RMI registry found running on: //" + this.serverAddress.getCanonicalHostName() +
						    ":" + this.serverPort + " !\n" + "Creating new Registry.");
				this.rmiRegistry = LocateRegistry.createRegistry(this.serverPort);
			}
			
			StarDustRemoteInterface stub = (StarDustRemoteInterface) UnicastRemoteObject.exportObject(this, 0);
			this.rmiRegistry.rebind(this.registeredName, stub);	
			
			logger.info(this.getClass().getName() + " bound to: " + this.registeredName);
			
			this.feeStarDustServerInterface.registerStarDustRemoteServer(this.getServerInetAdress().getCanonicalHostName(), this.getServerPort(), this.registeredName);
			
			setStatus("Ready");
		} 
		catch (Exception ex) 
		{
			logger.error(ex.toString());
			ex.printStackTrace();
			return;
		}
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run()
	{
		boolean isRunning;
		
		isRunning = true;
		
		while(isRunning)
		{
			try
			{
				Thread.sleep(MIN_SLEEP_TIME + (long) (Math.random() * MIN_SLEEP_TIME));
			}
			catch(InterruptedException ie)
			{
				isRunning = false;
			}
			
			//synchronized(feeStarDustServerInterface)
			//{
			try
			{
				// only test if no update has been made recently
				if((System.currentTimeMillis() - lastUpdate) > MIN_SLEEP_TIME) 
					this.feeStarDustServerInterface.updateStatusView(this.registeredName);
			}
			catch(RemoteException re)
			{
				while(this.isBusy()) // the server is gone -- still try to finish analysis first
				{
					try
					{
						Thread.sleep(MIN_SLEEP_TIME + (long) (Math.random() * MIN_SLEEP_TIME));
					}
					catch(InterruptedException ie)
					{
						stopServer(); // try to exit gracefully
					}
				}
				
				logger.warn("Can't reach server.");
				stopServer();
			}
			//}
						
			//synchronized(this)
			//{
			isRunning = !this.stopped.booleanValue();
			//}
		}
	}
	
	public void stopServer()
	{
		logger.info("Shutting down!");
		
		super.stopServer();
		
		try
		{
			feeStarDustServerInterface.removeStarDustRemoteServer(this.registeredName);
		}
		catch(Exception e)
		{
			verboseLogger.debug(e.toString());
		}
		finally
		{
			try
			{
				this.rmiRegistry.unbind(this.registeredName);
			}
			catch(Exception e)
			{
				verboseLogger.debug(e.toString());
				// Registry no longer exists --> exit
				System.exit(1);
			}
		}
	}
	
	/**** GUI *****************/

	protected void updateViews()
	{
		//synchronized(this)
		//{
		if(this.feeStarDustServerInterface == null || this.isCliMode())
			return;
		//}
		
		try
		{
			//synchronized(feeStarDustServerInterface)
			//{
				this.feeStarDustServerInterface.updateStatusView(this.registeredName);
				lastUpdate = System.currentTimeMillis();
			//}
		}
		catch(RemoteException re)
		{
			logger.warn("Failed to update views! Because of: " + re.toString());
		}
	}
}
