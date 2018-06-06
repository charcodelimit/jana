package fee.server;

import jana.java.JJavaRepository;
import jana.util.logging.JLogger;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;

public abstract class ServerStatusModel implements ServerStatusModelRemoteInterface
{
	protected final static JLogger logger = JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER + ".server");
	protected final static JLogger verboseLogger = JLogger.getLogger(JJavaRepository.VERBOSE_LOGGER + ".server");
	
	private final List<ServerView> views = new ArrayList<ServerView>();
	protected volatile int registeredViews; 
	
	// use setters!
	private volatile boolean cliMode;
	private boolean isBusy;
	private boolean hadErrors;
	private String statusString;
	
	public ServerStatusModel() throws RemoteException
	{
		this.statusString = "";
		
		this.hadErrors = false;
		this.isBusy = false;
	}

	/********** GUI **********************/
	
	public boolean isCliMode()
	{
		return cliMode;
	}

	public void setCliMode(boolean cliMode) throws RemoteException
	{
		this.cliMode = cliMode;
	}
	
	public void addView(ServerView aView)
	{
		if(!cliMode)
		{
			synchronized(views)
			{
				this.views.add(aView);
				this.registeredViews = this.views.size();
			}
		}
	}
	
	public void removeView(ServerView aView)
	{
		synchronized(views)
		{
			if( this.views.contains(aView) )
				this.views.remove(aView);
			
			this.registeredViews = this.views.size();
		}
	}
	
	protected void updateViews()
	{
		if(!cliMode && this.registeredViews > 0)
		{
			synchronized(this.views)
			{
				for(ServerView view : this.views)
					view.update();
			}
		}
	}
	
	/************* STATUS *******************/
	
	/**
	 * Subclasses may change the status
	 * 
	 * @param aStatusMessage
	 */
	protected void setStatus(String aStatusMessage)
	{
		this.statusString = aStatusMessage;
		
		if(cliMode)
			logger.debug(this.statusString);
		
		updateViews();
	}
	
	/**
	 * Everyone is allowed to observe the status
	 * 
	 * @return
	 */
	public String getStatus()
	{
		return this.statusString;
	}
	
	protected void setHadErrors()
	{
		this.hadErrors = true;
	}
	
	protected void resetHadErrors()
	{
		this.hadErrors = false;
	}
	
	public boolean hadErrors()
	{
		return this.hadErrors;
	}
	
	protected  void setBusy()
	{
		this.isBusy = true;
	}
	
	protected void resetBusy()
	{
		this.isBusy = false;
	}
	
	public boolean isBusy()
	{
		return this.isBusy;
	}
	
	public static void setLogLevel(Level aLogLevel)
	{
		Level logLevel;
		
		if(aLogLevel == null)
			logLevel = Level.OFF;
		else
			logLevel = aLogLevel;
		
		synchronized(logger)
		{
			logger.setLevel(logLevel);
			verboseLogger.setLevel(logLevel);
			
			JLogger.getLogger(JJavaRepository.DEFAULT_LOGGER).setLevel(logLevel);
		}
	}
}
