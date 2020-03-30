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
import bankruptcy_code.CoalitionWithRankingDifference;
import bankruptcy_code.RankCalculator;
import bankruptcy_code.RuleCalculator;

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
		
		/*
		estateLabel = new JLabel("Estate/Endowment: ");
		agentALabel = new JLabel("Minimum Claim: ");
		agentBLabel = new JLabel("Maximum Claim: ");
		agentCLabel = new JLabel("Number of Agents: ");
		// agentDLabel = new JLabel("Agent D: ");
		*/
		
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
		
		consolePanel = new JPanel();
		consolePanel.setBackground(Color.WHITE);
						
		consoleOutput = new JTextArea(33, 60);
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
		    			calculateExactVariance();
		    			
		    			// disableAllButtons();
		    			// System.out.println("Averages will be displayed after stop is clicked...");
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
		
		RuleCalculator.proportionalRuleAllocation(estateInput, claimers);
		RuleCalculator.CEARuleAllocation(estateInput, claimers);
		RuleCalculator.CELRuleAllocation(estateInput, claimers);
		
		
		RuleCalculator.calculateReference(estateInput, coalitions, claimers);
		RuleCalculator.calculateProportionalVariance(coalitions, iterations);
		RuleCalculator.calculateCEAVariance(coalitions, iterations);
		RuleCalculator.calculateCELVariance(coalitions, iterations);
		RuleCalculator.calculateShapleyVariance(claimers, coalitions, iterations);
		
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
			
			if(RuleCalculator.sum(claimers, "claims") > estateInput)
			{
				iterations++;
				numIterations.setText("iterations: " + iterations);  // update iterations label in UI
				
				updateCoalitionClaimers(coalitions, claimers);
				
				RuleCalculator.proportionalRuleAllocation(estateInput, claimers);
				RuleCalculator.CEARuleAllocation(estateInput, claimers);
				RuleCalculator.CELRuleAllocation(estateInput, claimers);
				
				
				RuleCalculator.calculateReference(estateInput, coalitions, claimers);
				RuleCalculator.calculateProportionalVariance(coalitions, iterations);
				RuleCalculator.calculateCEAVariance(coalitions, iterations);
				RuleCalculator.calculateCELVariance(coalitions, iterations);
				RuleCalculator.calculateShapleyVariance(claimers, coalitions, iterations);
				
				// System.out.println(stop);
				// print(claimers, coalitions);
				/*
				if(iterations % 10 == 0)
				{
					printAverages(coalitions, iterations);					
				}
				*/
				// stop--;
			}
		}
		printAverages(coalitions, iterations);
		enableAllButtons();
		stop = false;
		// print(claimers, coalitions);
	}
	
	private void calculateExactVariance()
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
		
		RuleCalculator.proportionalRuleAllocation(estateInput, claimers);
		RuleCalculator.CEARuleAllocation(estateInput, claimers);
		RuleCalculator.CELRuleAllocation(estateInput, claimers);
		RuleCalculator.TalmudRuleAllocation(estateInput, claimers);
		RuleCalculator.calculateReference(estateInput, coalitions, claimers);
		
		RuleCalculator.calculateShapleyValues(claimers, coalitions); // needs to be after calculateReference() because it needs the reference values for calculation
		RuleCalculator.minimalOverlappingRuleAllocation(estateInput, claimers);
		/*
		RuleCalculator.calculateProportionalVariance(coalitions, iterations);
		RuleCalculator.calculateCEAVariance(coalitions, iterations);
		RuleCalculator.calculateCELVariance(coalitions, iterations);
		RuleCalculator.calculateShapleyVariance(claimers, coalitions, iterations);
		*/
		
		RuleCalculator.calculateCoalitionProportionalAllocation(coalitions);
		RuleCalculator.calculateCoalitionCEAAllocation(coalitions);
		RuleCalculator.calculateCoalitionCELAllocation(coalitions);
		RuleCalculator.calculateCoalitionShapleyAllocation(coalitions);
		RuleCalculator.calculateCoalitionTalmudAllocation(coalitions);
		RuleCalculator.calculateCoalitionMinimalOverlappingAllocation(coalitions);
		
		List<CoalitionWithRankingDifference> ref = RankCalculator.rankingBasedOnReference(coalitions);
		List<CoalitionWithRankingDifference> prop = RankCalculator.rankingBasedOnProportionalAllocation(coalitions);
		List<CoalitionWithRankingDifference> cea = RankCalculator.rankingBasedOnCEAAllocation(coalitions);
		List<CoalitionWithRankingDifference> cel = RankCalculator.rankingBasedOnCELAllocation(coalitions);
		List<CoalitionWithRankingDifference> shap = RankCalculator.rankingBasedOnShapleyAllocation(coalitions);
		List<CoalitionWithRankingDifference> tal = RankCalculator.rankingBasedOnTalmudAllocation(coalitions);
		List<CoalitionWithRankingDifference> mo = RankCalculator.rankingBasedOnMinimalOverlappingAllocation(coalitions);
		
		RankCalculator.compareRanks(prop, ref);
		RankCalculator.compareRanks(cea, ref);
		RankCalculator.compareRanks(cel, ref);
		RankCalculator.compareRanks(shap, ref);
		RankCalculator.compareRanks(tal, ref);
		RankCalculator.compareRanks(mo, ref);
		
		/*
		for(Claimer entry : claimers)
		{
			System.out.println(entry.getId() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getCEAAllocation() + " CEL: "
					+ entry.getCELAllocation() + " shap: " + entry.getShapleyValue());
		}
		
		for(Coalition entry : coalitions)
		{
			System.out.println("coalition: " + entry.getId() + ", ref: " + entry.getReference() + ", prop. profit: " + 
					entry.getProportionalAllocation() + ", CEA profit: " + entry.getCEAAllocation() 
					+ ", CEL profit: " + entry.getCELAllocation() + ", Shapley profit: " + entry.getShapleyValueAllocation());
		}
		
		for(Claimer cl : claimers)
		{
			System.out.println("MO  " + cl.getId() + " " + cl.getMinimalOverlappingAllocation());
			
			System.out.println("TALMUD  " + cl.getId() + " " + cl.getTalmudAllocation());
			System.out.println("CEA  " + cl.getId() + " " + cl.getCEAAllocation());
			System.out.println("CEL  " + cl.getId() + " " + cl.getCELAllocation());
			
		}
		*/
		print(claimers, coalitions);
		for(CoalitionWithRankingDifference entry : ref)
		{
			System.out.println("REF   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank());
		}
		System.out.println("sum of REF ranking differences : " + sumRankingDifferences(ref));
		
		for(CoalitionWithRankingDifference entry : prop)
		{
			System.out.println("PROP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
		}
		System.out.println("sum of PROP ranking differences : " + sumRankingDifferences(prop));
		
		for(CoalitionWithRankingDifference entry : cea)
		{
			System.out.println("CEA   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
		}
		System.out.println("sum of CEA ranking differences : " + sumRankingDifferences(cea));
		
		for(CoalitionWithRankingDifference entry : cel)
		{
			System.out.println("CEL   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
		}
		System.out.println("sum of CEL ranking differences : " + sumRankingDifferences(cel));
		
		for(CoalitionWithRankingDifference entry : shap)
		{
			System.out.println("SHAP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
		}	
		System.out.println("sum of SHAP ranking differences : " + sumRankingDifferences(shap));
		
		for(CoalitionWithRankingDifference entry : tal)
		{
			System.out.println("TAL   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
		}
		System.out.println("sum of TAL ranking differences : " + sumRankingDifferences(tal));
		
		for(CoalitionWithRankingDifference entry : mo)
		{
			System.out.println("MO   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
		}
		System.out.println("sum of MO ranking differences : " + sumRankingDifferences(mo));
	}
	
	private static void print(List<Claimer> list1, List<Coalition> list2)
	{	
		System.out.println("Claimers");
		for(Claimer entry : list1)
		{
			System.out.println(entry.getId() + " PROP: " + entry.getProportionalAllocation() + " CEA: " + entry.getCEAAllocation()
			+ " CEL: " + entry.getCELAllocation()  + " SHAP: " + entry.getShapleyValue()  + " TAL: " + entry.getTalmudAllocation()
			+ " MO: " + entry.getMinimalOverlappingAllocation());
		}	
		System.out.println("Coalitions");
		for(Coalition entry : list2)
		{
			System.out.println(entry.getId() + " REF: " + entry.getReference() + " PROP: " + entry.getProportionalAllocation()
			+ " CEA: " + entry.getCEAAllocation()+ " CEL: " + entry.getCELAllocation() + " SHAP: " + entry.getShapleyValueAllocation()
			+ " TAL: " + entry.getTalmudAllocation() + " MO: " + entry.getMinimalOverlappingAllocation());
		}
	}
	
	private static void printAverages(List<Coalition> list2, int iter)
	{
		System.out.println("iteration: " + iter);
		for(Coalition entry : list2)
		{
			System.out.println(entry.getId() + " ref: " + entry.getReference() + " prop avg.: " + entry.getAveragePropVariation() + " CEA avg.: " + entry.getAverageCEAVariation() + " CEL avg.: " + entry.getAverageCELVariation()+ " Shap avg.: " + entry.getAverageShapleyValueVariation());
		}
	}
	
	private static double sumRankingDifferences(List<CoalitionWithRankingDifference> input)
	{
		double sum = 0.0;
		for(CoalitionWithRankingDifference coalition : input)
		{
			sum += coalition.getRankingDifference();
		}
		return sum;
	}
	
	private static <T> List<List<T>> combination(List<T> values, int size)
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
	
	
	private static double generateRandomClaim(double min, double max)
	{
		if (min >= max)
		{
	        throw new IllegalArgumentException("max must be greater than min");
	    }
		
	    Random r = new Random();
	    
	    return min + (max - min) * r.nextDouble();
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
	
	private void disableAllButtons()
	{
		estate.setEditable(false);
		agentA.setEditable(false);
		agentB.setEditable(false);
		agentC.setEditable(false);
		runButton.setEnabled(false);		
	}
	
	private void enableAllButtons()
	{
		estate.setEditable(true);
		agentA.setEditable(true);
		agentB.setEditable(true);
		agentC.setEditable(true);
		runButton.setEnabled(true);		
	}
}
 