package execution;

import java.awt.GridLayout;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class UserInterfaceFrame extends JFrame 
{
	public UserInterfaceFrame()
	{
		this.setLayout(new GridLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("BANKRUPTCY GAME");
	}
}
