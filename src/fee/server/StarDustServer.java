package fee.server;

import java.rmi.RemoteException;

public abstract class StarDustServer extends Server
{
	// fastest possible compression 
	protected static final int DEFAULT_COMPRESSION_LEVEL = 1;
	
	public StarDustServer() throws RemoteException
	{
		super();
	}

	protected int compressionLevel = 1; // default - use lowest compression
	protected boolean useDictionary = false; // default - don't use dictionary
	
	public int compressionLevel()
	{
		return this.compressionLevel;
	}
	
	public void setCompressionLevel(int compressionLevel) throws RemoteException
	{
		if(compressionLevel < 0 || compressionLevel > 9)
			this.compressionLevel = DEFAULT_COMPRESSION_LEVEL;
		else
			this.compressionLevel = compressionLevel;
	}
}
