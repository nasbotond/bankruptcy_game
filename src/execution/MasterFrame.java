package execution;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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

        addComponentToPane(this.getContentPane());
        
		this.pack();
		this.setLocationRelativeTo(null);
    }
    
    public void addComponentToPane(Container pane) 
    {
    	pane.setBackground(Color.WHITE);
    	JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Serif", Font.PLAIN, 15));
    	tabbedPane.setBackground(Color.WHITE);
    	tabbedPane.setForeground(Color.BLACK);
         
    	//Create the "cards".
        JPanel card1 = new JPanel();        
        card1.setBackground(Color.WHITE);
        card1.add(new ExactCalculationPanel());
         
        JPanel card2 = new JPanel();
        card2.setBackground(Color.WHITE);
        card2.add(new SimulationPanel());
         
        tabbedPane.addTab("EXACT", card1);
        tabbedPane.addTab("SIMULATION", card2);
         
        pane.add(tabbedPane, BorderLayout.CENTER);
    }    
}
