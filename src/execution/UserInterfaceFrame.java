package execution;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import bankruptcy_code.Claimer;
import bankruptcy_code.Coalition;

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
	
	private JPanel canvasPanel;
	private JPanel inputsPanel;
	private JPanel buttonsPanel;
	
	
	public UserInterfaceFrame()
	{
		this.setLayout(new GridLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("BANKRUPTCY GAME");
		
		canvasPanel = new JPanel(new BorderLayout());
		
		buttonsPanel = new JPanel(new FlowLayout());
		
		// run button action
		runButton = new JButton("calculate");
		runButton.addActionListener(new RunButtonActionListener());
		
		buttonsPanel.add(runButton);
		
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
		
		canvasPanel.add(inputsPanel, BorderLayout.NORTH);
		canvasPanel.add(buttonsPanel, BorderLayout.CENTER);
		
		
		this.add(canvasPanel);
		
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
		    			List<Claimer> input = new ArrayList<Claimer>();
		    			
		    			input.add(new Claimer('a', Double.parseDouble(agentA.getText())));
		    			input.add(new Claimer('b', Double.parseDouble(agentB.getText())));
		    			input.add(new Claimer('c', Double.parseDouble(agentC.getText())));
		    			input.add(new Claimer('d', Double.parseDouble(agentD.getText())));
		    			
		    			List<LinkedList<Claimer>> powerSet = new LinkedList<LinkedList<Claimer>>();
		    			
		    			for(int i = 1; i <= input.size(); i++)
		    			{
		    				powerSet.addAll((Collection<? extends LinkedList<Claimer>>) combination(input, i));
		    			}
		    			
		    			List<Coalition> coalitions = new ArrayList<Coalition>();
		    			
		    			for(LinkedList<Claimer> entry : powerSet)
		    			{
		    				coalitions.add(new Coalition(entry));
		    			}
		    			
		    			System.out.println(coalitions);
		    			
		    			// proportional rule (claimers)
		    			double estateInput = Double.parseDouble(estate.getText());
		    			double sum = 0.0;
		    			
		    			for(Claimer entry : input)
		    			{
		    				sum += entry.getClaim();
		    			}
		    			
		    			for(Claimer entry : input)
		    			{
		    				entry.setProportionalAllocation(proportionalRuleClaimers(estateInput, sum, entry.getClaim()));
		    			}
		    			
		    			CEARuleClaimers(estateInput, input);
		    			CELRuleClaimers(estateInput, sum, input);
		    			
		    			for(Claimer entry : input)
		    			{
		    				System.out.println(entry.getId() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getConstrainedEAAllocation() + " CEL: " + entry.getConstrainedELAllocation());
		    			}
		    			
		    			calculateReference(estateInput, coalitions, sum);
		    			calculateProportionalProfit(coalitions);
		    			calculateCEAProfit(coalitions);
		    			calculateCELProfit(coalitions);
		    			
		    			for(Coalition entry : coalitions)
		    			{
		    				System.out.println(entry.getId() + " ref: " + entry.getReference() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getConstrainedEAAllocation()+ " CEL: " + entry.getConstrainedELAllocation());
		    			}
		    							    			
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
	
	public static <T> List<List<T>> combination(List<T> values, int size)
	{
		if(size == 0)
		{
			return Collections.singletonList(Collections.<T> emptyList());
		}
		
		if(values.isEmpty())
		{
			return Collections.emptyList();
		}
		
		List<List<T>> combination = new LinkedList<List<T>>();
		
		T actual = values.iterator().next();
		
		List<T> subSet = new LinkedList<T>(values);		
		subSet.remove(actual);
		
		List<List<T>> subSetCombination = combination(subSet, size - 1);
		
		for(List<T> set : subSetCombination)
		{
			List<T> newSet = new LinkedList<T>(set);
			newSet.add(0, actual);
			combination.add(newSet);
		}
		
		combination.addAll(combination(subSet, size));
		
		return combination;
	}
	
	// returns the amount obligated to a given agent based on parameters
	public static double proportionalRuleClaimers(double estate, double sumOfClaims, double claim)
	{
		return (claim*estate)/sumOfClaims;
	}
	
	// updates the CEAAllocation value for each agent
	public static void CEARuleClaimers(double estate, List<Claimer> input)
	{
		double equalAmount = estate/input.size();
		double remainingAmount = estate;
		int iterator = input.size();
		
		for(Claimer claimer : input)
		{
			remainingAmount -= equalAmount;
			iterator--;
			
			if(claimer.getClaim() < equalAmount)
			{
				claimer.setConstrainedEAAllocation(claimer.getClaim());
				remainingAmount += (equalAmount-claimer.getClaim());
			}
			
			if (claimer.getClaim() >= equalAmount)
			{
				claimer.setConstrainedEAAllocation(equalAmount);
			}
			
			equalAmount = remainingAmount/iterator;
		}
	}
	
	// updates the CELAllocation value for each agent
	public static void CELRuleClaimers(double estate, double sum, List<Claimer> input)
	{
		double equalLoss = (sum - estate)/input.size();
		
		for(Claimer claimer : input)
		{
			claimer.setConstrainedELAllocation(claimer.getClaim() - equalLoss);
		}
	}
	
	// v(s) function: used as reference point for SRD
	public static void calculateReference(double estate, List<Coalition> coalitions, double sum)
	{
		for(Coalition entry : coalitions)
		{
			double sumCurrentCoalition = 0;
			for(Claimer claimer : entry.getClaimers())
			{
				sumCurrentCoalition += claimer.getClaim();
			}
			
			double maxUpperBound = estate - (sum - sumCurrentCoalition);
			entry.setReference(Math.max(0, maxUpperBound));
		}
	}
	
	// calculateReference must be called before this method to ensure we have the correct reference values
	// calculates the profit (or surplus) each coalition earns with this rule
	public static void calculateProportionalProfit(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumPropClaimers = 0;
			for(Claimer claimer : entry.getClaimers())
			{
				sumPropClaimers += claimer.getProportionalAllocation();
			}
			
			entry.setProportionalAllocation(sumPropClaimers - entry.getReference());		
		}
	}
	
	// calculates the profit (or surplus) each coalition earns with this rule
	public static void calculateCEAProfit(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumCEAClaimers = 0;
			for(Claimer claimer : entry.getClaimers())
			{
				sumCEAClaimers += claimer.getConstrainedEAAllocation();
			}
			
			double CEAProfit = sumCEAClaimers - entry.getReference();
			if(CEAProfit < 0)
			{
				entry.setConstrainedEAAllocation(0);
			}
			else
			{
				entry.setConstrainedEAAllocation(CEAProfit);
			}	
		}
	}
	
	public static void calculateCELProfit(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumCELClaimers = 0;
			for(Claimer claimer : entry.getClaimers())
			{
				sumCELClaimers += claimer.getConstrainedELAllocation();
			}
			
			double CELProfit = sumCELClaimers - entry.getReference();
			if(CELProfit < 0)
			{
				entry.setConstrainedELAllocation(0);
			}
			else
			{
				entry.setConstrainedELAllocation(CELProfit);
			}	
		}
	}
}
