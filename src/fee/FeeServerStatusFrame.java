package fee;


import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import fee.server.Server;
import fee.server.ServerStatusView;
import fee.server.TopLevelServerView;
import fee.stardust.StarDust;
import fee.stardust.StarDustStatusView;


public class FeeServerStatusFrame implements WindowListener, TopLevelServerView, Serializable
{
	private static final long serialVersionUID = -5722340916391064130L;

	private static class StatusFrameLock {};
	private final static StatusFrameLock statusFrameLock = new StatusFrameLock();
	
	private static FeeServerStatusFrame singletonInstance = null;
	
	private final List<StarDustStatusView> starDustStatusViews;
	private final List<ServerStatusView> serverStatusViews;
	
	private JFrame mainFrame;
	private JSplitPane backPlane;
	private JPanel serverPane;
	private JPanel starDustPane; 
	
	private FeeServerStatusFrame()
	{
		 this.starDustStatusViews = new ArrayList<StarDustStatusView>();
		 this.serverStatusViews = new ArrayList<ServerStatusView>();
		
		 initWindows();
		 
		 setVisible(false);
	}
	
	public static boolean hasInstance()
	{
		return singletonInstance != null;
	}
	
	public static void disposeFrame()
	{
		synchronized(statusFrameLock)
		{
			if(hasInstance())
			{
				getInstance().dispose();
				singletonInstance = null;
			}
		}
	}
	
	public static FeeServerStatusFrame getInstance()
	{
		synchronized(statusFrameLock)
		{	
			if(singletonInstance == null)
				singletonInstance = new FeeServerStatusFrame();
			
			return singletonInstance;
		}
	}
		
	/************* Initialization *****************/
	
	public void addServerStatusModel(Server aServer, String aFrameTitle)
	{
		synchronized(statusFrameLock)
		{
			ServerStatusView ssv = new ServerStatusView(aServer, aFrameTitle);
			this.serverStatusViews.add(ssv);
			this.serverPane.add(ssv.getStatusPane());
			aServer.addView(this);
		}
	}
		
	public void addStatusView(StarDustStatusView aStatusView)
	{
		synchronized(statusFrameLock)
		{
			if(!this.starDustStatusViews.contains(aStatusView))
			{
				this.starDustStatusViews.add(aStatusView);
			
				this.starDustPane.add(aStatusView.getStatusPane(), this.starDustPane);
				this.mainFrame.pack();
			}
		}
	}
	
	public void removeStatusView(StarDustStatusView aStatusView)
	{
		synchronized(statusFrameLock)
		{
			if(this.starDustStatusViews.contains(aStatusView))
			{
				this.starDustStatusViews.remove(aStatusView);
			
				this.starDustPane.remove(aStatusView.getStatusPane());
				this.mainFrame.pack();
			}
		}
	}

	/**************** Displaying **************************/
	
	private void initWindows()
	{
		this.mainFrame = new JFrame(Fee.APPLICATION_NAME + " running " + StarDust.version());
		this.mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.mainFrame.addWindowListener(this);
		this.mainFrame.setAlwaysOnTop(true);
		
		this.backPlane = new JSplitPane();
		
		this.serverPane = new JPanel();
		this.serverPane.setBorder(null);
        this.serverPane.setLayout(new BoxLayout(this.serverPane, BoxLayout.X_AXIS));
		
		this.starDustPane = new JPanel();
		this.starDustPane.setBorder(null);
        this.starDustPane.setLayout(new BoxLayout(this.starDustPane, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPane = new JScrollPane(this.starDustPane);
		
		this.backPlane.setLeftComponent(this.serverPane);
		this.backPlane.setRightComponent(scrollPane);
		this.mainFrame.getContentPane().add(this.backPlane);
	}
	
	public void update()
	{
		//synchronized(statusFrameLock)
		//{
			for( ServerStatusView ssv : this.serverStatusViews )
			{
				ssv.update();
			}

			this.mainFrame.invalidate();
		//}
	}
	
	public void setVisible(boolean aBooleanValue)
	{
		//synchronized(statusFrameLock)
		//{
			this.mainFrame.setVisible(aBooleanValue);
			this.mainFrame.pack();
		//}
	}
	
	/*************** Finalization ****************/
	
	public void dispose()
	{	
		synchronized(statusFrameLock)
		{
			// be careful in case of multiple dispose messages
			if(this.mainFrame != null) 
			{
				this.mainFrame.dispose();
				this.mainFrame = null;
			}
		}
	}

	/************* Event Handling *****************/

	public void windowClosing(WindowEvent e) {
		JOptionPane.showMessageDialog(null, "Please Stop the Server by Pressing CTR+C at the command line!");
    }
	
	public void windowClosed(WindowEvent e) {
		System.exit(1);
	}
	
	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}
}