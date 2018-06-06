package fee.stardust;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;

import fee.util.NoServerException;

public interface StarDustRemoteInterface extends StarDustRemoteServerInterface
{
	/**
	 * Used to test if a StarDustRemoteServer is still alive
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public boolean isAlive() throws RemoteException;
	
	/**
	 * Returns the version of the StarDustRemoteServer
	 * @return
	 * @throws RemoteException
	 */
	public String getVersion() throws RemoteException;
	
	/**
	 * Returns the version of the JavaVirtualMachine 
	 * on which the StarDustRemoteServer runs
	 * @return
	 * @throws RemoteException
	 */
	public String getJavaVersion() throws RemoteException;
	
	public void analyze(String[] aClassNameArray) throws RemoteException;
	public void analyze(String aClassName) throws RemoteException;
	
	public void compile(String[] aClassNameArray) throws RemoteException;
	public void compile(String aClassName) throws RemoteException;
	
	/**
	 * Set the compression level that GZip uses when it compresses the
	 * analysis output files. A level of 0 means, that files are stored in raw format.
	 * @throws NoServerException Thrown when no server has been started.
	 *
	 * @param compressionLevel - the compression level of the ZLIB compression (0-9)
	 */
	public void setCompressionLevel(int compressionLevel) throws RemoteException;
		
	/**
	 * The project has to reside inside the repository directory !
	 * @param aProjectName the name of the project
	 * @throws IOException
	 * @throws RemoteException
	 */
	public void setProjectName(String aProjectName) throws IOException, RemoteException;
	
	/**
	 * The project filename has to be a relative filename, 
	 * which denotes a file inside the repository directory!
	 * @param aProjectFile the filename of the project-file
	 * @throws IOException
	 * @throws RemoteException
	 */
	public void setProjectFileName(String aProjectFileName) throws IOException, RemoteException;
	
	/**
	 * Used to turn super type recording on and off
	 * @param aValue
	 * @throws RemoteException
	 */
	public void setRecordSupertypes(boolean aValue) throws RemoteException;
	
	/**
	 * Used to turn referenced object-type recording on and off
	 * @param aValue
	 * @throws RemoteException
	 */
	public void setRecordReferencedObjectTypes(boolean aValue) throws RemoteException;
	
	/**
	 * Returns the signatures of all object types that have been referenced in the analyzed classes.
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getReferencedObjectTypes() throws RemoteException;
	
	/**
	 * Returns the signatures of all supertypes of the analyzed classes.
	 * @return
	 * @throws RemoteException
	 */
	public List<String> getSuperTypes() throws RemoteException;
}
