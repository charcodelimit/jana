package fee.server;

import jana.lang.java.JJavaSignature;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FeeStarDustServerRemoteInterface extends Remote
{
	/**
	 * returns the number of StarDust servers that have been registered
	 */
	public int generateStarDustRemoteServerID() throws RemoteException;
	
	/**
	 * adds a remote StarDust server
	 * 
	 * @param hostName
	 * @param portNumber
	 * @param registeredName
	 * @throws RemoteException
	 */
	public void registerStarDustRemoteServer(String hostName, int portNumber, String registeredName) throws RemoteException;
	
	/**
	 * removes a remote StarDust server
	 * 
	 * @param registeredName
	 * @throws RemoteException
	 */
	public void removeStarDustRemoteServer(String registeredName) throws RemoteException;
	
	/**
	 * Updates the view associated with the StarDust object that is
	 * bound to registeredName
	 * 
	 * @param registeredName
	 * @throws RemoteException
	 */
	public void updateStatusView(String registeredName) throws RemoteException;
	
	/**
	 * Adds a signature of an analyzed class,
	 * and the output file in which the analysis results are stored
	 * to the classname-map of the java-project managed by the server.
	 * 
	 * @param aSignature
	 * @param aRelativeFilename
	 * @throws RemoteException
	 */
	public void addClassnameEntry(JJavaSignature aSignature, String aRelativeFilename) throws RemoteException, IOException;
}
