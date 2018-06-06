package fee.server;

import java.io.IOException;
import java.net.SocketException;
import java.rmi.RemoteException;

import com.richhickey.foil.RuntimeServer;

public class FeeFoilServer extends Server implements Runnable
{
	// number of times to wait for the client to send before 
	// asking for acknowledgment that the connection is still alive
	private final static int ACKNOWLEDGE_TIMEOUT = 1024;
	// default server port
	private static final int DEFAULT_FOIL_RUNTIME_SERVER_PORT = 13579;
	private static final String DEFAULT_FOIL_RUNTIME_SERVER_HOST = "localhost";
	
	private RuntimeServer foilRuntimeServer;
	
	private Thread serverThread;
	
	public FeeFoilServer(boolean cliMode) throws Exception
	{
		super();
		this.serverPort = DEFAULT_FOIL_RUNTIME_SERVER_PORT;
		this.setCliMode(cliMode);
	}
	
	public void startServer(String portNumber) throws Exception, RemoteException
	{
		startServer(portNumber,null);
	}
	
	public void startServer(String portNumber, String hostname) throws Exception, RemoteException
	{
		if(portNumber != null)
			setPortNumber(portNumber);
		else
			setPortNumber(new Integer(DEFAULT_FOIL_RUNTIME_SERVER_PORT).toString());
		
		if(hostname != null)
			setHostName(hostname);
		else
			setHostName(DEFAULT_FOIL_RUNTIME_SERVER_HOST);
		
		startServer();
	}
	
	/**
	 * Restarts the Foil run-time server.
	 * 
	 * @throws Exception
	 */
	protected void restartServer() throws Exception
	{
		synchronized(this)
		{
			setStatus("Restarting Server");
			logger.info("Restarting the Server");
			this.foilRuntimeServer.stopServer();
			this.foilRuntimeServer = null;
		
			resetHadErrors();
			resetBusy();
		}
		
		System.gc(); 
	}
	
	/***
	 * starts the server thread
	 * 
	 * @throws Exception
	 */
	private void startServer()
	{
		this.serverThread = new Thread(this);
		this.serverThread.start();
	}

	public void run()
	{
		boolean isRunning;
		
		isRunning = true;
		
		// while the server has not been stopped, 
		// it restarts when an recoverable error occurs
		while(isRunning)
		{
			try	{
				// first test if the server port can be bound
				testServerPort();
			}
			catch(Exception ex)	{
				System.err.println("Error while binding the Foil-server to its designated port!");
				System.err.println("Critical Failure " + ex.toString() + "\nExiting!");
				System.exit(1); // Failures during server startup are critical
			}
		
			this.foilRuntimeServer = RuntimeServer.produceServer(logger);
			this.foilRuntimeServer.setTimeout(ACKNOWLEDGE_TIMEOUT);
			
			setBusy();
			setStatus("Listening");
	
			try	{
				this.foilRuntimeServer.processMessagesOnSocket(this.serverPort);
			}
			catch(IOException ioe)
			{
				logger.debug("Stopping FoilServer ...");
				isRunning = false;
				
				setHadErrors();
			
				if(ioe instanceof SocketException)
				{
					logger.warn("Server Connection Failed: " + ioe.toString());
					
					try {
						restartServer();
					}
					catch(Exception e)
					{
						throw new RuntimeException("Error while restarting the Foil-server!", e);
					}
				}
				else
				{
					throw new RuntimeException("Foil-Server error while processing messages!", ioe);
				}
			}
			
			synchronized(this.stopped)
			{
				logger.debug("Stopping FoilServer ...");
				if(this.stopped)
					isRunning = false;
			}
		}
		
		System.err.println("FoilServer stopped!");
		logger.info("Stopped Server");
	}
	
	public void stopServer()
	{
		super.stopServer();
		serverThread.interrupt();
	}
}
