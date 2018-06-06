package fee;

import fee.stardust.StarDustRemoteInterface;

/***
 * The analysis fails, if no server with the same version as the compiled one is found,
 * or when the RMI connections to all remaining servers fail.
 * 
 * @author chr
 *
 */
public class FeeAnalysisThread extends FeeWorkerThread
{
	public FeeAnalysisThread(Object aSemaphore, StarDustRemoteInterface aStarDustServer)
	{
		super(aSemaphore, aStarDustServer);
	}
	
	public void run()
	{
		String[] qualifiedNames = new String[0];

		waitForServer();
		
		try 
		{				
			qualifiedNames = new String[this.classnames.length];
			System.arraycopy(this.classnames, 0, qualifiedNames, 0, qualifiedNames.length);
			
			this.starDust.analyze(qualifiedNames);
			
			synchronized(this)
			{
				this.isFinished = true;
				
				synchronized(this.semaphore)
				{
					this.semaphore.notify();
				}
			}
		}
		catch(Exception e) 
		{
			throw new RuntimeException("Failed to analyze: " + qualifiedNames, e); 
		}
	}
}
