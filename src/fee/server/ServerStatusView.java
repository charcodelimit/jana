package fee.server;

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

import fee.server.ServerRemoteInterface;

public class ServerStatusView
{
	private ServerRemoteInterface remoteInterface;
	
	private JTextField serverAddressField;
	private JTextField serverPortField;
	private JTextField statusField;
	private JLayeredPane statusPane;
	
	private String frameTitle;

	public ServerStatusView(ServerRemoteInterface aServerRemoteInterface)
	{
		this(aServerRemoteInterface, "");
	}
	
	public ServerStatusView(ServerRemoteInterface aServerRemoteInterface, String aFrameTitle)
	{
		this.remoteInterface = aServerRemoteInterface;
		this.frameTitle = aFrameTitle;
			
		initWindows();
	}
	
	/************ Initialization ****************/
	protected void initWindows()
	{	
		this.statusPane = new JLayeredPane();
	    this.statusPane.setPreferredSize(new Dimension(300, 150));
	    this.statusPane.setBorder(BorderFactory.createTitledBorder(this.frameTitle));
	    this.statusPane.setLayout(new BoxLayout(this.statusPane, BoxLayout.Y_AXIS));		
		
	    initStatusPane();
	}
	
	protected void initStatusPane()
	{
		this.serverPortField = new JTextField("Server Port not set!");
		this.serverPortField.setEditable(false);
		
		this.serverAddressField = new JTextField("Unknown Server IP-Address!");
		this.serverAddressField.setEditable(false);

		this.statusField = new JTextField();
		this.statusField.setEditable(false);
		
		this.statusPane.add(this.serverPortField);
		this.statusPane.add(this.serverAddressField);
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
		
		synchronized (this.remoteInterface)
		{
			status = new String( this.remoteInterface.getStatus() );
		}
		
		return status;
	}

	private boolean hadErrors() throws RemoteException
	{
		synchronized (this.remoteInterface)
		{
			return this.remoteInterface.hadErrors();
		}
	}

	private boolean isBusy() throws RemoteException
	{
		synchronized (this.remoteInterface)
		{
			return this.remoteInterface.isBusy();
		}
	}

	/************ Displaying ***************/
	public void update()
	{
		try
		{
			updateServerConnectionStatus();
			updateServerStatus();
		}
		catch(RemoteException re)
		{
			this.statusField.setText(re.toString());
			this.statusField.setBackground(Color.RED);
		}
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
}
