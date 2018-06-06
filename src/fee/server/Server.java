package fee.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

public abstract class Server extends ServerStatusModel implements ServerRemoteInterface
{
	private static final int PORT_RETRIES = 4;
	
	protected InetAddress serverAddress;
	protected int serverPort;

	protected volatile Boolean stopped = Boolean.FALSE;

	public Server() throws RemoteException
	{
		super();
	}
	
	/**
	 * Tests if the server can be bound to the server-address and port
	 */
	protected void testServerPort() throws Exception
	{
		ServerSocket socket;
		int retry = 0;
		
		while(retry < PORT_RETRIES)
		{
			try
			{
				socket = new ServerSocket(this.serverPort,0,this.serverAddress);
				socket.close();
				
				retry = PORT_RETRIES;
			}
			catch(IOException ioe)
			{
				retry++;
				
				if(retry == PORT_RETRIES)
				{
					setHadErrors();
					throw new Exception("Can't start a server on port " + 
							this.serverPort + " because of " + ioe.toString(), ioe);
				}
			}
		}
	}
	
	public void setPortNumber(String aPortNumber)
	{
		int p;
		
		if(aPortNumber != null)
		{
			try
			{
				p = Integer.parseInt(aPortNumber);
				if(p < 1024 || p > 49151)
				{
					logger.warn("Please use a port in the range [1024..49151].");
					throw new NumberFormatException("Please use a port in the range [1024..49151].");
				}
				
				this.serverPort = p;
			}
			catch(NumberFormatException nfe)
			{
				logger.warn(aPortNumber + " is no valid port number! " +
						"\nUsing default port " + serverPort);
			}
		}
	}
	
	protected InetAddress hostInetAddress(String aHostname) throws UnknownHostException
	{
		InetAddress address;
		
		if(aHostname == null)
		{
			address = InetAddress.getLocalHost();
		}
		else
		{
			address = InetAddress.getByName(aHostname);
		}
		
		return address;
	}
	
	public void setHostName(String aHostname) throws UnknownHostException
	{
		try
		{
			this.serverAddress = hostInetAddress(aHostname);
		}
		catch(UnknownHostException uhe)
		{
			logger.warn(aHostname + " is no valid hostname! " + "\nUsing localhost." );
			this.serverAddress = InetAddress.getLocalHost();
		}
	}

	public InetAddress getServerInetAdress() throws RemoteException
	{
		if(this.serverAddress == null)
			return this.serverAddress;
		
		try
		{
			return (InetAddress) InetAddress.getByAddress(this.serverAddress.getHostName(), this.serverAddress.getAddress());
		}
		catch(UnknownHostException uhe)
		{
			return null;
		}
	}
	
	public int getServerPort() throws RemoteException
	{
		return this.serverPort;
	}

	public void stopServer()
	{
		synchronized(this.stopped)
		{
			this.stopped = Boolean.TRUE;
		}
	}

}
