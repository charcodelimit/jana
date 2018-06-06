package fee.stardust;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JTextField;

import fee.FeeServerStatusFrame;

public class StarDustStatusView
{	
	private FeeServerStatusFrame displayingFrame;
	
	private JTextField repositoryDirectoryField;
	private JTextField serverAddressField;
	private JTextField serverPortField;
	private JTextField statusField;
	private JLayeredPane statusPane;
	
	private String frameTitle;
	
	private StarDustRemoteServerInterface remoteInterface;
	
	public StarDustStatusView(StarDustRemoteServerInterface aRemoteInterface)
	{
		this(aRemoteInterface, StarDust.class.getName());
	}
	
	public StarDustStatusView(StarDustRemoteServerInterface aRemoteInterface, String aFrameTitle)
	{	
		this.remoteInterface = aRemoteInterface;
		this.frameTitle = aFrameTitle;
		this.initWindows();
	}

	/************ Initialization ****************/
	protected void initWindows()
	{	
		this.statusPane = new JLayeredPane();
	    this.statusPane.setPreferredSize(new Dimension(300, 150));
	    this.statusPane.setBorder(BorderFactory.createTitledBorder(this.frameTitle));
	    this.statusPane.setLayout(new BoxLayout(this.statusPane, BoxLayout.Y_AXIS));		
		
	    initStatusPane();
	    
	    this.displayingFrame = FeeServerStatusFrame.getInstance();
		this.displayingFrame.addStatusView(this);
	}
	
	protected void initStatusPane()
	{
		this.repositoryDirectoryField = new JTextField("Repository Directory not set!");
		this.repositoryDirectoryField.setEditable(false);
	
		this.serverPortField = new JTextField("Server Port not set!");
		this.serverPortField.setEditable(false);
		
		this.serverAddressField = new JTextField("Unknown Server IP-Address!");
		this.serverAddressField.setEditable(false);

		this.statusField = new JTextField();
		this.statusField.setEditable(false);
		
		this.statusPane.add(this.serverPortField);
		this.statusPane.add(this.serverAddressField);
		this.statusPane.add(this.repositoryDirectoryField);
		this.statusPane.add(this.statusField);
	}

	public Component getStatusPane()
	{
		return this.statusPane;
	}

	/*********** Proxy / Synchronization Wrapper ************/
	private String getStatus() throws RemoteException
	{
		String status;
		
		//synchronized (this.remoteInterface)
		//{
		status = new String( this.remoteInterface.getStatus() );
		//}
		
		return status;
	}

	private boolean hadErrors() throws RemoteException
	{
		//synchronized (this.remoteInterface)
		//{
		return this.remoteInterface.hadErrors();
		//}
	}

	private boolean isBusy() throws RemoteException
	{
		//synchronized (this.remoteInterface)
		//{
			return this.remoteInterface.isBusy();
		//}
	}

	private String repositoryDirectoryName() throws RemoteException
	{
		String directory;
		StarDustRemoteServerInterface starDustRemoteInterface;
		
		//synchronized (this.remoteInterface)
		//{
			try
			{
				 starDustRemoteInterface = (StarDustRemoteServerInterface) this.remoteInterface; 
				 directory = starDustRemoteInterface.getRepositoryDirectoryName();
			}
			catch(IOException ioe)
			{
				directory = ioe.toString();
			}
		//}
		
		return directory;
	}
	
	/************ Displaying ***************/
	public void setLocalMode()
	{
		this.serverAddressField.setVisible(false);
		this.serverPortField.setVisible(false);
	}
	
	public void update()
	{	
		try
		{
			updateServerConnectionStatus();
			
			this.repositoryDirectoryField.setText(repositoryDirectoryName());
			
			updateServerStatus();
		}
		catch(RemoteException re)
		{
			this.statusField.setText(re.toString());
			this.statusField.setBackground(Color.RED);
		}
		
		this.displayingFrame.update();
	}	
	
	private void updateServerStatus() throws RemoteException
	{
		this.statusField.setText(getStatus());
		
		if(hadErrors())
			this.statusField.setBackground(Color.RED);
		else 
			if(isBusy())
				this.statusField.setBackground(Color.YELLOW);
		else
			this.statusField.setBackground(Color.GREEN);
	}
	
	private void updateServerConnectionStatus() throws RemoteException
	{
		InetAddress serverAddress;
		
		this.serverPortField.setText("Listening on Port: " + this.remoteInterface.getServerPort());
		
		serverAddress = this.remoteInterface.getServerInetAdress();
	
		if(serverAddress == null) {
			this.serverAddressField.setText("No Server IP-Address set!");
		}
		else {
			this.serverAddressField.setText("Server Address: " + serverAddress);
		
			if(!serverAddress.isLoopbackAddress())
				this.serverAddressField.setBackground(Color.ORANGE);
			else
				this.serverAddressField.setBackground(Color.GREEN);
		
			try {
				if(!serverAddress.isReachable(100))
					this.serverAddressField.setBackground(Color.RED); 
			}
			catch(IOException ioe){}
		}
	}
	
	public void dispose()
	{
		//synchronized(this.remoteInterface)
		//{
			this.displayingFrame.removeStatusView(this);
			
			this.repositoryDirectoryField = null;
			this.serverAddressField = null;
			this.serverPortField = null;
			this.statusField = null;
			this.statusPane = null;
		//}
		
		this.remoteInterface = null;
		System.gc();
	}
}
