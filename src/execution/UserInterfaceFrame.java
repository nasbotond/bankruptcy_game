package execution;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class UserInterfaceFrame extends JFrame 
{
	private JTextField estate;
	private JTextField agentA;
	private JTextField agentB;
	private JTextField agentC;
	private JTextField agentD;
	
	private JLabel estateLabel;
	private JLabel agentALabel;
	private JLabel agentBLabel;
	private JLabel agentCLabel;
	private JLabel agentDLabel;
	
	private JButton runButton;
	
	private JPanel inputsPanel;
	
	
	public UserInterfaceFrame()
	{
		this.setLayout(new GridLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("BANKRUPTCY GAME");
		
		// run button action
		runButton = new JButton("calculate");
		runButton.addActionListener(new RunButtonActionListener());
		
		// inputs
		
		estateLabel = new JLabel("Estate/Endowment: ");
		agentALabel = new JLabel("Agent A: ");
		agentBLabel = new JLabel("Agent B: ");
		agentCLabel = new JLabel("Agent C: ");
		agentDLabel = new JLabel("Agent D: ");
		
		estate = new JTextField(4);
		agentA = new JTextField(4);
		agentB = new JTextField(4);
		agentC = new JTextField(4);
		agentD = new JTextField(4);
		
		inputsPanel = new JPanel();
		
		inputsPanel.add(estateLabel);
		inputsPanel.add(estate);
		inputsPanel.add(agentALabel);
		inputsPanel.add(agentA);
		inputsPanel.add(agentBLabel);
		inputsPanel.add(agentB);
		inputsPanel.add(agentCLabel);
		inputsPanel.add(agentC);
		inputsPanel.add(agentDLabel);
		inputsPanel.add(agentD);
		
		this.add(inputsPanel);
		
		this.setPreferredSize(new Dimension(700, 675)); // 525, 750
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	private class RunButtonActionListener implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{	
			Thread thread = new Thread(new Runnable()
		    {
		    	@Override
		    	public void run()
		    	{
		    		try
		    		{
		    			
		    							    			
					}
		    		catch(Exception exception)  // TODO?
		    		{
						exception.printStackTrace();
					}
		    	}
		    });
		    
		    thread.start();		
		}
	}
}
