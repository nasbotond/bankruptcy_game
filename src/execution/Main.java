package execution;

import java.awt.EventQueue;

public class Main {

	public static void main(String[] args) 
	{
		
		EventQueue.invokeLater(() -> {
			UserInterfaceFrame frame = new UserInterfaceFrame();
			
			frame.setVisible(true);
		});		
	}	
}
