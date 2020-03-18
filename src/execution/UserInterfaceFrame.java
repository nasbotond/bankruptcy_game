package execution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
	private JButton stopButton;
	
	private JPanel canvasPanel;
	private JPanel inputsPanel;
	private JPanel buttonsPanel;
	
	private JPanel consolePanel;
	private JTextArea consoleOutput;
	
	private JLabel numIterations;
	
	private int iterations;
	private boolean stop = false;
	
	
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
		
		// stop button action
		stopButton = new JButton("stop");
		stopButton.addActionListener(new StopButtonActionListener());
		
		buttonsPanel.add(runButton);
		buttonsPanel.add(stopButton);
		
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
		
		// numIterations display:
		//
		numIterations = new JLabel("iterations: " + iterations);
		numIterations.setHorizontalAlignment(JLabel.CENTER);
		buttonsPanel.add(numIterations);
		//		
				
		
		// console panel:
		//
		/*
		consolePanel = new JPanel();
		consolePanel.setBackground(Color.WHITE);
						
		consoleOutput = new JTextArea(33, 65);
		consoleOutput.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(consoleOutput);		
						
		PrintStream printStream = new PrintStream(new CustomOutputStream(consoleOutput));
					
		// keeps reference of standard output stream
		// standardOut = System.out;
				         
		// re-assigns standard output stream and error output stream
		System.setOut(printStream);
		System.setErr(printStream);
						
		consolePanel.add(scrollPane);
		//
		
		canvasPanel.add(consolePanel, BorderLayout.SOUTH);
		
		*/
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
		    			
		    			int iterations = 1;
		    			double estateInput = Double.parseDouble(estate.getText());
		    			List<Claimer> claimers = new ArrayList<Claimer>();
		    			
		    			claimers.add(new Claimer('a', Double.parseDouble(agentA.getText())));
		    			claimers.add(new Claimer('b', Double.parseDouble(agentB.getText())));
		    			claimers.add(new Claimer('c', Double.parseDouble(agentC.getText())));
		    			claimers.add(new Claimer('d', Double.parseDouble(agentD.getText())));
		    			
		    			
		    			List<LinkedList<Claimer>> powerSet = new LinkedList<LinkedList<Claimer>>();
		    			
		    			for(int i = 1; i <= claimers.size(); i++)
		    			{
		    				powerSet.addAll((Collection<? extends LinkedList<Claimer>>) combination(claimers, i));
		    			}
		    			
		    			List<Coalition> coalitions = new ArrayList<Coalition>(); // master list of coalitions
		    			
		    			for(LinkedList<Claimer> entry : powerSet)
		    			{
		    				coalitions.add(new Coalition(entry));
		    			}
		    			
		    			proportionalRuleClaimers(estateInput, claimers);
		    			CEARuleClaimers(estateInput, claimers);
		    			CELRuleClaimers(estateInput, claimers);
		    			
		    			calculateReference(estateInput, coalitions, claimers);
		    			calculateProportionalVariance(coalitions, iterations);
		    			calculateCEAVariance(coalitions, iterations);
		    			calculateCELVariance(coalitions, iterations);
		    			double[] shap = calculateShapleyValues(claimers, coalitions);
		    			
		    			
		    			for(Coalition entry : coalitions)
		    			{
		    				System.out.println(entry.getId() + " ref: " + entry.getReference() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getConstrainedEAAllocation()+ " CEL: " + entry.getConstrainedELAllocation());
		    			}		    			
		    			
		    			System.out.println("Started...");
		    			System.out.println(shap.length);
		    			for(int j = 0; j < shap.length; j++)
		    			{
		    				System.out.println(shap[j]);
		    			}
		    			System.out.println("Ended...");
		    			
		    			// calculateAverageVariances();
		    							    			
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
	
	private class StopButtonActionListener implements ActionListener 
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
		    			stop = true;		    							    			
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
	
	private void calculateAverageVariances()
	{
		double minClaim = Double.parseDouble(agentA.getText());
		double maxClaim = Double.parseDouble(agentB.getText());
		int numClaimers = Integer.parseInt(agentC.getText());
		double estateInput = Double.parseDouble(estate.getText());
		iterations = 1;
		numIterations.setText("iterations: " + iterations);  // update iterations label in UI
		
		
		List<Claimer> claimers = new ArrayList<Claimer>(); // master list of claimers
		
		for(int i = 0; i < numClaimers; i++)
		{
			claimers.add(new Claimer((char)(97 + i), generateRandomClaim(minClaim, maxClaim)));
		}
		
		List<LinkedList<Claimer>> powerSet = new LinkedList<LinkedList<Claimer>>();
		
		for(int i = 1; i <= claimers.size(); i++)
		{
			powerSet.addAll((Collection<? extends LinkedList<Claimer>>) combination(claimers, i));
		}
		
		List<Coalition> coalitions = new ArrayList<Coalition>(); // master list of coalitions
		
		for(LinkedList<Claimer> entry : powerSet)
		{
			coalitions.add(new Coalition(entry));
		}
		
		proportionalRuleClaimers(estateInput, claimers);
		CEARuleClaimers(estateInput, claimers);
		CELRuleClaimers(estateInput, claimers);
		
		calculateReference(estateInput, coalitions, claimers);
		calculateProportionalVariance(coalitions, iterations);
		calculateCEAVariance(coalitions, iterations);
		calculateCELVariance(coalitions, iterations);	
		
		// print(claimers, coalitions);
		// printAverages(coalitions);
		// System.out.println(coalitions);	
		
		// int stop = 10;
		// while(stop > 0)
		while(stop == false)
		{		
			for(int i = 0; i < numClaimers; i++)
			{
				claimers.get(i).setClaim(generateRandomClaim(minClaim, maxClaim));
			}
			
			if(sum(claimers, "claims") > estateInput)
			{
				iterations++;
				numIterations.setText("iterations: " + iterations);  // update iterations label in UI
				
				updateCoalitionClaimers(coalitions, claimers);
				
				proportionalRuleClaimers(estateInput, claimers);
				CEARuleClaimers(estateInput, claimers);
				CELRuleClaimers(estateInput, claimers);
				
				
				calculateReference(estateInput, coalitions, claimers);
				calculateProportionalVariance(coalitions, iterations);
				calculateCEAVariance(coalitions, iterations);
				calculateCELVariance(coalitions, iterations);
				
				// System.out.println(stop);
				// print(claimers, coalitions);
				
				if(iterations % 10 == 0)
				{
					printAverages(coalitions, iterations);					
				}
				
				// stop--;
			}
		}	

		// print(claimers, coalitions);
	}
	
	private static void print(List<Claimer> list1, List<Coalition> list2)
	{	
		for(Claimer entry : list1)
		{
			System.out.println(entry.getId() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getConstrainedEAAllocation() + " CEL: " + entry.getConstrainedELAllocation());
		}		
		for(Coalition entry : list2)
		{
			System.out.println(entry.getId() + " ref: " + entry.getReference() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getConstrainedEAAllocation()+ " CEL: " + entry.getConstrainedELAllocation());
		}
	}
	
	private static void printAverages(List<Coalition> list2, int iter)
	{
		System.out.println("iteration: " + iter);
		for(Coalition entry : list2)
		{
			System.out.println(entry.getId() + " ref: " + entry.getReference() + " prop avg.: " + entry.getAveragePropVariation() + " CEA avg.: " + entry.getAverageCEAVariation() + " CEL avg.: " + entry.getAverageCELVariation());
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
	
	// generate all permutations of the input list
	private static <E> List<List<E>> generatePerm(List<E> original) 
	{
	     if (original.isEmpty()) 
	     {
	       List<List<E>> result = new ArrayList<>(); 
	       result.add(new ArrayList<>()); 
	       return result; 
	     }
	     E firstElement = original.remove(0);
	     List<List<E>> returnValue = new ArrayList<>();
	     List<List<E>> permutations = generatePerm(original);
	     for (List<E> smallerPermutated : permutations) 
	     {
	       for (int index=0; index <= smallerPermutated.size(); index++) 
	       {
	         List<E> temp = new ArrayList<>(smallerPermutated);
	         temp.add(index, firstElement);
	         returnValue.add(temp);
	       }
	     }
	     return returnValue;
	}
	
	// calculates the Shapley values of the claimers and puts them in a double array
	public static double[] calculateShapleyValues(List<Claimer> claimers, List<Coalition> coalitions)
	{
		List<Claimer> claimersClone = new ArrayList<Claimer>(claimers);
		List<List<Claimer>> permutations = generatePerm(claimersClone);
		
		double[] shapleys = new double[claimers.size()];
		List<Claimer> currentCoalition = new ArrayList<Claimer>();
		double previousReference = 0;
		
		for(List<Claimer> permutation : permutations)
		{
			for(int i = 0; i < claimers.size(); i++)			
			{		
				currentCoalition.add(permutation.get(i)); // needs to be sorted so we can match it with the list of coalitions
				Collections.sort(currentCoalition, new Comparator<Claimer>() {
				    public int compare(Claimer c1, Claimer c2) {
				        return c1.getId().compareTo(c2.getId());
				    }
				});
				
				for(Coalition coalition : coalitions)
				{
					if(coalition.getClaimers().equals(currentCoalition)) 
					{
						if(currentCoalition.size() == 1)
						{
							shapleys[claimers.indexOf(permutation.get(i))] += coalition.getReference();
						}
						else
						{
							shapleys[claimers.indexOf(permutation.get(i))] += (coalition.getReference() - previousReference);
						}
						previousReference = coalition.getReference();
					}
				}
			}
			currentCoalition.clear();
			previousReference = 0;
		}
		
		
		for(int j = 0; j < shapleys.length; j++)
		{
			shapleys[j] = shapleys[j]/permutations.size();
		}
		
		
		return shapleys;
	}
	
	// returns the amount obligated to a given agent based on parameters
	public static void proportionalRuleClaimers(double estate, List<Claimer> input)
	{
		double sumOfClaims = sum(input, "claims");
		
		for(Claimer claim : input)
		{
			claim.setProportionalAllocation((claim.getClaim()*estate)/sumOfClaims);
		}
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
	public static void CELRuleClaimers(double estate, List<Claimer> allClaimers)
	{
		double sumOfClaims = sum(allClaimers, "claims");
		
		double equalLoss = (sumOfClaims - estate)/allClaimers.size();
		
		for(Claimer claimer : allClaimers)
		{
			claimer.setConstrainedELAllocation(claimer.getClaim() - equalLoss);
		}
	}
	
	// v(s) function: used as reference point for SRD
	public static void calculateReference(double estate, List<Coalition> coalitions, List<Claimer> allClaimers)
	{
		double sumOfAllClaims = sum(allClaimers, "claims");
		
		for(Coalition entry : coalitions)
		{
			double sumCurrentCoalition = sum(entry.getClaimers(), "claims");			
			
			double maxUpperBound = estate - (sumOfAllClaims - sumCurrentCoalition);
			entry.setReference(Math.max(0, maxUpperBound));
		}
	}
	
	// calculateReference must be called before this method to ensure we have the correct reference values
	// calculates the profit (or surplus) each coalition earns with this rule
	public static void calculateProportionalVariance(List<Coalition> coalitions, int iteration)
	{
		for(Coalition entry : coalitions)
		{
			double sumPropClaimers = sum(entry.getClaimers(), "prop");
			
			double propProfit = sumPropClaimers - entry.getReference();
			
			entry.setProportionalAllocation(propProfit);
			
			entry.setAveragePropVariation(entry.getAveragePropVariation()+((propProfit-entry.getAveragePropVariation())/iteration));
		}
	}
	
	// calculates the profit (or surplus) each coalition earns with this rule
	public static void calculateCEAVariance(List<Coalition> coalitions, int iteration)
	{
		for(Coalition entry : coalitions)
		{
			double sumCEAClaimers = sum(entry.getClaimers(), "CEA");
			
			double CEAProfit = sumCEAClaimers - entry.getReference();
			if(CEAProfit < 0)
			{
				entry.setConstrainedEAAllocation(0);
				entry.setAverageCEAVariation(entry.getAverageCEAVariation()+((0-entry.getAverageCEAVariation())/iteration));
			}
			else
			{
				entry.setConstrainedEAAllocation(CEAProfit);
				entry.setAverageCEAVariation(entry.getAverageCEAVariation()+((CEAProfit-entry.getAverageCEAVariation())/iteration));
			}	
		}
	}
	
	public static void calculateCELVariance(List<Coalition> coalitions, int iteration)
	{
		for(Coalition entry : coalitions)
		{
			double sumCELClaimers = sum(entry.getClaimers(), "CEL");
			
			double CELProfit = sumCELClaimers - entry.getReference();
			if(CELProfit < 0)
			{
				entry.setConstrainedELAllocation(0);
				entry.setAverageCELVariation(entry.getAverageCELVariation()+((0-entry.getAverageCELVariation())/iteration));
			}
			else
			{
				entry.setConstrainedELAllocation(CELProfit);
				entry.setAverageCELVariation(entry.getAverageCELVariation()+((CELProfit-entry.getAverageCELVariation())/iteration));
			}	
		}
	}
	
	private static double generateRandomClaim(double min, double max)
	{
		if (min >= max)
		{
	        throw new IllegalArgumentException("max must be greater than min");
	    }
		
	    Random r = new Random();
	    
	    return min + (max - min) * r.nextDouble();
	}
	
	// finds the sum of the Claims in a given coalition
	private static double sum(List<Claimer> list, String identifier)
	{
		double sumOfElements = 0;
		
		switch(identifier)
		{
			case "claims":
				for(Claimer element : list)
				{
					sumOfElements += element.getClaim();
				}
				break;
			case "prop":
				for(Claimer element : list)
				{
					sumOfElements += element.getProportionalAllocation();
				}
				break;
			case "CEA":
				for(Claimer element : list)
				{
					sumOfElements += element.getConstrainedEAAllocation();
				}
				break;
			case "CEL":
				for(Claimer element : list)
				{
					sumOfElements += element.getConstrainedELAllocation();
				}
				break;		
		}
			
		return sumOfElements;
	}
	
	// update the claims of every claimer in the coalition lists
	private static void updateCoalitionClaimers(List<Coalition> coalitions, List<Claimer> claimers)
	{
		for(Coalition coalition : coalitions)
		{
			for(Claimer claimer : coalition.getClaimers())
			{
				claimer.setClaim(claimers.get(claimers.indexOf(claimer)).getClaim());
			}
		}
	}
}
 