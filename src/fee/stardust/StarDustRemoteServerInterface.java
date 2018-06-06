package fee.stardust;

import jana.java.JJavaProject;

import java.io.IOException;
import java.rmi.RemoteException;

import fee.server.ServerRemoteInterface;

public interface StarDustRemoteServerInterface extends ServerRemoteInterface
{
	public String getRepositoryDirectoryName() throws RemoteException, IOException;
	public JJavaProject getProject() throws RemoteException;
}
