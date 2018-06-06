package jana.gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class MainFrame extends JFrame
{
	static final String FRAME_TITLE = "Jana";
	private MainModel mainModel;
	private RepositoryContentView repositoryContent;
	
	public MainFrame(MainModel aMainModel)
	{
		super(FRAME_TITLE);
		
		this.mainModel = aMainModel;
	}
	
	public void openInWorld()
	{
		this.setBounds(0, 0, 800, 600);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel yellowLabel = new JLabel();
		yellowLabel.setBackground(Color.yellow);
		this.getContentPane().add(yellowLabel, BorderLayout.CENTER);
		
		this.repositoryContent = new RepositoryContentView(this.mainModel.getRepositoryContentModel());
		this.getContentPane().add(this.repositoryContent, BorderLayout.NORTH);
		
		this.repositoryContent.setVisible(true);
		this.setVisible(true);
	}
}
