package jana.gui;

import jana.java.JJavaExistingProject;
import jana.java.JJavaProject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class RepositoryContentModel extends AbstractListModel implements ListModel
{
	private MainModel main;
	private List<String> javaProjectNames;
	private Map<String,JJavaProject> javaProjects;
	private final LispFilenameFilter lispFilter = new LispFilenameFilter();
	
	class LispFilenameFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name)
		{
			return name.endsWith(".lisp") || name.endsWith(".lsp") || name.endsWith(".cl");
		}
	}
	
	RepositoryContentModel(MainModel aMainModel)
	{
		this.main = aMainModel;
		
		initialize();
	}
	
	private void initialize()
	{
		try
		{
			this.getRepositoryContents();
		}
		catch(IOException ioe)
		{
			throw new RuntimeException(ioe);
		}
	}
	
	public void getRepositoryContents() throws IOException
	{
		File[] projectFiles;
		File baseDirectory;
		JJavaProject currentJavaProject;
		
		baseDirectory = this.main.getRepositoryDirectory();
		
		projectFiles = baseDirectory.listFiles(lispFilter);
		
		this.javaProjects = new HashMap<String,JJavaProject>(projectFiles.length);
		
		for(int i = 0; i < projectFiles.length; i++)
		{
			currentJavaProject = new JJavaExistingProject(projectFiles[i], baseDirectory);
			this.javaProjects.put(currentJavaProject.getProjectName(), currentJavaProject);
		}
		
		this.javaProjectNames = new ArrayList<String>(javaProjects.keySet());
		
		this.fireContentsChanged(this, 0, projectFiles.length);
	}
	
	public JJavaProject getJavaProject(String aProjectName)
	{
		return this.javaProjects.get(aProjectName);
	}
	
	public List<String> getJavaProjectNames()
	{
		return this.javaProjectNames;
	}

	public Object getElementAt(int index)
	{
		return this.javaProjectNames.get(index);
	}

	public int getSize()
	{
		return this.javaProjectNames.size();
	}
}
