package fee.server;

import java.net.InetAddress;
import java.rmi.RemoteException;

public interface ServerRemoteInterface extends ServerStatusModelRemoteInterface
{
	public int getServerPort() throws RemoteException;
	public InetAddress getServerInetAdress() throws RemoteException;
}
