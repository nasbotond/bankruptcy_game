package execution;

import java.awt.EventQueue;

public class Main 
{

	public static void main(String[] args) 
	{
		EventQueue.invokeLater(() -> {
			MasterFrame frame = new MasterFrame();
			frame.setVisible(true);
		});		
	}
}
