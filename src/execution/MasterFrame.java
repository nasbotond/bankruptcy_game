package execution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial")
public class MasterFrame extends JFrame
{
    final static String EXACT = "Exact Calculation";
    final static String SIM = "Simulation";
    final static int extraWindowWidth = 100;
     
    public MasterFrame()
    {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBackground(Color.WHITE);
		this.setTitle("BANKRUPTCY GAME");

        addVersionComponentToPane(this.getContentPane());
        
		this.pack();
		this.setLocationRelativeTo(null);
    }
    
    public void addVersionComponentToPane(Container pane)
    {
    	pane.setBackground(Color.WHITE);
    	JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Serif", Font.PLAIN, 15));
    	tabbedPane.setBackground(Color.WHITE);
    	tabbedPane.setForeground(Color.BLACK);
         
    	//Create the "cards".
        JPanel card1 = new JPanel();        
        card1.setBackground(Color.WHITE);
        card1.add(tabbedCalculationPanel(false));
         
        JPanel card2 = new JPanel();
        card2.setBackground(Color.WHITE);
        card2.add(tabbedCalculationPanel(true));
         
        tabbedPane.addTab("VERSION A", card1);
        tabbedPane.addTab("VERSION B", card2);
         
        pane.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JComponent tabbedCalculationPanel(boolean isVersionB)
    {
    	JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Serif", Font.PLAIN, 15));
    	tabbedPane.setBackground(Color.WHITE);
    	tabbedPane.setForeground(Color.BLACK);
         
    	//Create the "cards".
        JPanel card1 = new JPanel();        
        card1.setBackground(Color.WHITE);
        card1.add(new ExactCalculationPanel(isVersionB));
         
        JPanel card2 = new JPanel();
        card2.setBackground(Color.WHITE);
        card2.add(new SimulationPanel(isVersionB));
         
        tabbedPane.addTab("EXACT", card1);
        tabbedPane.addTab("SIMULATION", card2);
        
        return tabbedPane;
    }
    
    private void addComponentToPane(Container pane) 
    {
    	pane.setBackground(Color.WHITE);
    	JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Serif", Font.PLAIN, 15));
    	tabbedPane.setBackground(Color.WHITE);
    	tabbedPane.setForeground(Color.BLACK);
         
    	//Create the "cards".
        JPanel card1 = new JPanel();        
        card1.setBackground(Color.WHITE);
        card1.add(new ExactCalculationPanel(false));
         
        JPanel card2 = new JPanel();
        card2.setBackground(Color.WHITE);
        card2.add(new SimulationPanel(false));
         
        tabbedPane.addTab("EXACT", card1);
        tabbedPane.addTab("SIMULATION", card2);
         
        pane.add(tabbedPane, BorderLayout.CENTER);
    }    
}
