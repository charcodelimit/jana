package fee.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerStatusModelRemoteInterface extends Remote
{
	public String getStatus() throws RemoteException;
	
	public boolean hadErrors() throws RemoteException;
	
	public boolean isBusy() throws RemoteException;
	
	public void setCliMode(boolean cliMode) throws RemoteException;
}
