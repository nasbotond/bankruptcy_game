package execution;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import bankruptcy_code.RuleCalculator;

public class Main 
{

	public static void main(String[] args) 
	{
		
		EventQueue.invokeLater(() -> {
			// ExactUIFrame frame = new ExactUIFrame();
			// SimulationUIFrame frame1 = new SimulationUIFrame();
			MasterFrame frame1 = new MasterFrame();
			// frame.setVisible(true);
			frame1.setVisible(true);
		});
		
		
	}
}
