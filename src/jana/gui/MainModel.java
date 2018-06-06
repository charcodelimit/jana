package jana.gui;

import java.io.File;

public class MainModel extends Object
{
	File repositoryDirectory;
	RepositoryContentModel repositoryContent;
	
	public MainModel(String aRepositoryDirectoryName)
	{
		this.repositoryDirectory = new File(aRepositoryDirectoryName);
		
		initialize();
	}
	
	private void initialize()
	{
		this.repositoryContent = new RepositoryContentModel(this);
	}
	
	public File getRepositoryDirectory()
	{
		return repositoryDirectory;
	}
	
	public RepositoryContentModel getRepositoryContentModel()
	{
		return this.repositoryContent;
	}
}
