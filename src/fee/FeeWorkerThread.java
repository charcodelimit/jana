package fee;

import java.rmi.RemoteException;

import fee.stardust.StarDust;
import fee.stardust.StarDustRemoteInterface;

public abstract class FeeWorkerThread implements Runnable
{
	protected final StarDustRemoteInterface starDust;
	protected volatile boolean isFinished;
	protected String[] classnames;
	protected volatile Object semaphore;
	
	public FeeWorkerThread(Object aSemaphore, StarDustRemoteInterface aStarDustServer)
	{
		this.starDust = aStarDustServer;
		this.semaphore = aSemaphore;
		this.isFinished = true;
	}
	
	public void initThread(String[] aClassnameArray)
	{
		synchronized(this)
		{
			this.isFinished = false;
		}
		
		this.classnames = aClassnameArray;
	}
	
	/**
	 * Does this need synchronization?
	 * 
	 * @return
	 */
	public boolean analysisIsDone()
	{
		return this.isFinished;
	}
	
	public void waitForServer()
	{
		boolean serverFailure = true;
		
		while(serverFailure)
		{	
			try
			{
				if(!starDust.getVersion().equals(StarDust.version()))
					serverFailure = true;
				else
					serverFailure = false;
			}
			catch(RemoteException re)
			{
				serverFailure = true;
			}
		}
	}
}
