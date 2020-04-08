package execution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import bankruptcy_code.Claimer;
import bankruptcy_code.Coalition;
import bankruptcy_code.CoalitionWithRankingDifference;
import bankruptcy_code.RankCalculator;
import bankruptcy_code.RuleCalculator;

@SuppressWarnings("serial")
public class SimulationUIFrame extends JFrame
{
	private JTextField numberOfCreditors;
	
	private JLabel numberOfCreditorsLabel;
	
	private JLabel maximumDiffLabel;
	private JLabel propLabel;
	private JLabel ceaLabel;
	private JLabel celLabel;
	private JLabel apropLabel;
	private JLabel shapLabel;
	private JLabel talLabel;
	private JLabel moLabel;
	private JLabel cliLabel;
	
	private JButton runButton;
	private JButton stopButton;
	
	private JPanel canvasPanel;
	private JPanel inputsPanel;
	private JPanel buttonsPanel;
	private JPanel averagesPanel;
	
	private JPanel consolePanel;
	private JTextArea consoleOutput;
	
	private JLabel numIterations;
	private boolean stop = false;	
	
	public SimulationUIFrame()
	{
		this.setLayout(new GridLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBackground(Color.WHITE);
		this.setTitle("BANKRUPTCY GAME");
		
		canvasPanel = new JPanel(new BorderLayout());
		canvasPanel.setBackground(Color.WHITE);
		
		buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.setBackground(Color.WHITE);
		
		// run button action
		runButton = createSimpleButton("Calculate");
		runButton.setFont(new Font("Serif", Font.PLAIN, 21));
		runButton.addActionListener(new RunButtonActionListener());
		
		// stop button action
		stopButton = createSimpleButton("Stop");
		stopButton.setFont(new Font("Serif", Font.PLAIN, 21));
		stopButton.addActionListener(new StopButtonActionListener());
		
		buttonsPanel.add(runButton);
		buttonsPanel.add(stopButton);
		
		// inputs
		
		numberOfCreditorsLabel = new JLabel("Number of Creditors: ");
		numberOfCreditorsLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		numberOfCreditors = new JTextField(4);
		numberOfCreditors.setFont(new Font("Serif", Font.PLAIN, 21));
		
		inputsPanel = new JPanel();
		inputsPanel.setBackground(Color.WHITE);
		
		inputsPanel.add(numberOfCreditorsLabel);
		inputsPanel.add(numberOfCreditors);
		
		canvasPanel.add(inputsPanel, BorderLayout.NORTH);
		canvasPanel.add(buttonsPanel, BorderLayout.CENTER);
		
		// numIterations display:
		//
		numIterations = new JLabel("iterations: " + 0);
		numIterations.setFont(new Font("Serif", Font.BOLD, 21));
		numIterations.setHorizontalAlignment(JLabel.CENTER);
		buttonsPanel.add(numIterations);
		//	
		
		// averages display:
		//
		
		averagesPanel = new JPanel();
		averagesPanel.setBackground(Color.WHITE);
		averagesPanel.setLayout(new BoxLayout(averagesPanel, BoxLayout.Y_AXIS));
		averagesPanel.setBorder(new EmptyBorder(new Insets(0, 50, 100, 0)));
		
		maximumDiffLabel = new JLabel("Maximum: ");
		maximumDiffLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		propLabel = new JLabel("Average Proportional Rule SRD: ");
		propLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		ceaLabel = new JLabel("Average CEA Rule SRD: ");
		ceaLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		celLabel = new JLabel("Average CEL Rule SRD: ");
		celLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		apropLabel = new JLabel("Average Adjusted Proportional Rule SRD:");
		apropLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		shapLabel = new JLabel("Average Shapley Value SRD:");
		shapLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		talLabel = new JLabel("Average Talmud Rule SRD:");
		talLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		moLabel = new JLabel("Average Minimal Overlap Rule SRD:");
		moLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		cliLabel = new JLabel("Average Per Capita Nucleolous Rule SRD:");
		cliLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		averagesPanel.add(maximumDiffLabel);
		averagesPanel.add(propLabel);
		averagesPanel.add(ceaLabel);
		averagesPanel.add(celLabel);
		averagesPanel.add(apropLabel);
		averagesPanel.add(shapLabel);
		averagesPanel.add(talLabel);
		averagesPanel.add(moLabel);
		averagesPanel.add(cliLabel);
		
		canvasPanel.add(averagesPanel, BorderLayout.SOUTH);
		// console panel:
		//
		/*
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
		*/
		
		this.add(canvasPanel);
		
		this.setPreferredSize(new Dimension(700, 600)); // 525, 750
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	private class RunButtonActionListener implements ActionListener 
	{		
		@Override
		public void actionPerformed(ActionEvent event)
		{	
			if(!numberOfCreditors.getText().isEmpty())
			{
				disableAllButtons();
				Thread thread = new Thread(new Runnable()
			    {
			    	@Override
			    	public void run()
			    	{
			    		try
			    		{
			    			calculateAverageSRD();	    					    							    			
						}
			    		catch(Exception exception)  // TODO?
			    		{
							exception.printStackTrace();
						}
			    	}
			    });
			    
			    thread.start();
			}
			else
			{
				JOptionPane.showMessageDialog(new JFrame(), "Please input number of creditors", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
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
	
	private void calculateAverageSRD()
	{
		final int numClaimers = Integer.parseInt(numberOfCreditors.getText());
		double estate;
		int iterations = 1;
		
		List<CoalitionWithRankingDifference> ref;
		List<CoalitionWithRankingDifference> prop;
		List<CoalitionWithRankingDifference> cea;
		List<CoalitionWithRankingDifference> cel;
		List<CoalitionWithRankingDifference> aprop;
		List<CoalitionWithRankingDifference> shap;
		List<CoalitionWithRankingDifference> tal;
		List<CoalitionWithRankingDifference> mo;
		List<CoalitionWithRankingDifference> cli;
		
		Map<String, Double> averageSRD;		
		
		numIterations.setText("iterations: " + iterations);  // update iterations label in UI
		
		List<Claimer> claimers = new ArrayList<Claimer>(); // master list of claimers
		
		for(int i = 0; i < numClaimers; i++)
		{
			claimers.add(new Claimer((char)(97 + i), generateUniformRandom(0, 1000)));
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
		
		maximumDiffLabel.setText("Maximum: " + (coalitions.size() - 1)*((coalitions.size() - 1) / 2)); // TODO
		
		estate = generateUniformRandom(RuleCalculator.sum(claimers, "claims")/2, RuleCalculator.sum(claimers, "claims"));
		
		// System.out.println("estate: " + estate);
		
		calculateClaimerRuleAllocations(estate, claimers);
		RuleCalculator.calculateReference(estate, coalitions, claimers);
		RuleCalculator.calculateShapleyValues(claimers, coalitions);
		calculateCoalitionRuleAllocations(coalitions);	
		
		// print(claimers, coalitions);
		
		ref = RankCalculator.rankingBasedOnReference(coalitions);
		prop = RankCalculator.rankingBasedOnProportionalAllocation(coalitions);
		cea = RankCalculator.rankingBasedOnCEAAllocation(coalitions);
		cel = RankCalculator.rankingBasedOnCELAllocation(coalitions);
		aprop = RankCalculator.rankingBasedOnAdjustedProportionalAllocation(coalitions);
		shap = RankCalculator.rankingBasedOnShapleyAllocation(coalitions);
		tal = RankCalculator.rankingBasedOnTalmudAllocation(coalitions);
		mo = RankCalculator.rankingBasedOnMinimalOverlappingAllocation(coalitions);
		cli = RankCalculator.rankingBasedOnClightsAllocation(coalitions);

		
		RankCalculator.compareRanks(prop, ref);
		RankCalculator.compareRanks(cea, ref);
		RankCalculator.compareRanks(cel, ref);
		RankCalculator.compareRanks(aprop, ref);
		RankCalculator.compareRanks(shap, ref);
		RankCalculator.compareRanks(tal, ref);
		RankCalculator.compareRanks(mo, ref);
		RankCalculator.compareRanks(cli, ref);
		
		averageSRD = new HashMap<String, Double>();
		
		averageSRD.put("prop", sumRankingDifferences(prop));
		averageSRD.put("cea", sumRankingDifferences(cea));
		averageSRD.put("cel", sumRankingDifferences(cel));
		averageSRD.put("aprop", sumRankingDifferences(aprop));
		averageSRD.put("shap", sumRankingDifferences(shap));
		averageSRD.put("tal", sumRankingDifferences(tal));
		averageSRD.put("mo", sumRankingDifferences(mo));
		averageSRD.put("cli", sumRankingDifferences(cli));
		
		// System.out.println("loop start");
		
		while(!stop)
		{		
			for(int i = 0; i < numClaimers; i++)
			{
				claimers.get(i).setClaim(generateUniformRandom(0, 1000));
				// clearClaimer(claimers.get(i));
			}

			estate = generateUniformRandom(RuleCalculator.sum(claimers, "claims")/2.0, RuleCalculator.sum(claimers, "claims"));

			iterations = iterations + 1;			
			
			updateCoalitionClaimers(coalitions, claimers);
			
			calculateClaimerRuleAllocations(estate, claimers);
			RuleCalculator.calculateReference(estate, coalitions, claimers);
			RuleCalculator.calculateShapleyValues(claimers, coalitions);
			
			calculateCoalitionRuleAllocations(coalitions);	
			
			// print(claimers, coalitions);
			
			ref = RankCalculator.rankingBasedOnReference(coalitions);
			prop = RankCalculator.rankingBasedOnProportionalAllocation(coalitions);
			cea = RankCalculator.rankingBasedOnCEAAllocation(coalitions);
			cel = RankCalculator.rankingBasedOnCELAllocation(coalitions);
			aprop = RankCalculator.rankingBasedOnAdjustedProportionalAllocation(coalitions);
			shap = RankCalculator.rankingBasedOnShapleyAllocation(coalitions);
			tal = RankCalculator.rankingBasedOnTalmudAllocation(coalitions);
			mo = RankCalculator.rankingBasedOnMinimalOverlappingAllocation(coalitions);
			cli = RankCalculator.rankingBasedOnClightsAllocation(coalitions);

			
			RankCalculator.compareRanks(prop, ref);
			RankCalculator.compareRanks(cea, ref);
			RankCalculator.compareRanks(cel, ref);
			RankCalculator.compareRanks(aprop, ref);
			RankCalculator.compareRanks(shap, ref);
			RankCalculator.compareRanks(tal, ref);
			RankCalculator.compareRanks(mo, ref);
			RankCalculator.compareRanks(cli, ref);
			
			averageSRD.put("prop", averageSRD.get("prop").doubleValue() + (sumRankingDifferences(prop) - averageSRD.get("prop").doubleValue())/iterations);
			averageSRD.put("cea", averageSRD.get("cea").doubleValue() + (sumRankingDifferences(cea) - averageSRD.get("cea").doubleValue())/iterations);
			averageSRD.put("cel", averageSRD.get("cel").doubleValue() + (sumRankingDifferences(cel) - averageSRD.get("cel").doubleValue())/iterations);
			averageSRD.put("aprop", averageSRD.get("aprop").doubleValue() + (sumRankingDifferences(aprop) - averageSRD.get("aprop").doubleValue())/iterations);
			averageSRD.put("shap", averageSRD.get("shap").doubleValue() + (sumRankingDifferences(shap) - averageSRD.get("shap").doubleValue())/iterations);
			averageSRD.put("tal", averageSRD.get("tal").doubleValue() + (sumRankingDifferences(tal) - averageSRD.get("tal").doubleValue())/iterations);
			averageSRD.put("mo", averageSRD.get("mo").doubleValue() + (sumRankingDifferences(mo) - averageSRD.get("mo").doubleValue())/iterations);
			averageSRD.put("cli", averageSRD.get("cli").doubleValue() + (sumRankingDifferences(cli) - averageSRD.get("cli").doubleValue())/iterations);
			
			
			if(iterations % 10 == 0)
			{
				propLabel.setText("Average Proportional Rule SRD: " + averageSRD.get("prop"));
				// propLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				ceaLabel.setText("Average CEA Rule SRD: " + averageSRD.get("cea"));
				// ceaLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				celLabel.setText("Average CEL Rule SRD: " + averageSRD.get("cel"));
				// celLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				apropLabel.setText("Average Adjusted Proportional Rule SRD: " + averageSRD.get("aprop"));
				// apropLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				shapLabel.setText("Average Shapley Value SRD: " + averageSRD.get("shap"));
				// shapLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				talLabel.setText("Average Talmud Rule SRD: " + averageSRD.get("tal"));
				// talLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				moLabel.setText("Average Minimal Overlap Rule SRD: " + averageSRD.get("mo"));
				// moLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				cliLabel.setText("Average Per Capita Nucleolous Rule SRD: " + averageSRD.get("cli"));
				// cliLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				/*
				for(Map.Entry<String, Double> entry : averageSRD.entrySet())
				{
					// System.out.println(entry.getKey() + ": " + entry.getValue());
					
				}
				*/
			}
			/*
			try 
			{
				Thread.sleep(50);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			*/
			numIterations.setText("iterations: " + iterations);  // update iterations label in UI
		}
		
		// System.out.println("done.");
		enableAllButtons();
		stop = false;
	}
	
	private static void print(List<Claimer> list1, List<Coalition> list2)
	{	
		System.out.println("Claimers");
		for(Claimer entry : list1)
		{
			System.out.println(entry.getId() + " PROP: " + entry.getProportionalAllocation() + " CEA: " + entry.getCEAAllocation()
			+ " CEL: " + entry.getCELAllocation()  + " SHAP: " + entry.getShapleyValue()  + " TAL: " + entry.getTalmudAllocation()
			+ " MO: " + entry.getMinimalOverlappingAllocation() + " APROP: " + entry.getAdjustedProportionalAllocation()
			+ " CLI: " + entry.getClightsAllocation());
		}	
		System.out.println("Coalitions");
		for(Coalition entry : list2)
		{
			System.out.println(entry.getId() + " REF: " + entry.getReference() + " PROP: " + entry.getProportionalAllocation()
			+ " CEA: " + entry.getCEAAllocation()+ " CEL: " + entry.getCELAllocation() + " SHAP: " + entry.getShapleyValueAllocation()
			+ " TAL: " + entry.getTalmudAllocation() + " MO: " + entry.getMinimalOverlappingAllocation() + " APROP: " + entry.getAdjustedProportionalAllocation()
			+ " CLI: " + entry.getClightsAllocation());
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
	
	private static void calculateClaimerRuleAllocations(double estate, List<Claimer> claimers)
	{
		RuleCalculator.proportionalRuleAllocation(estate, claimers);
		RuleCalculator.CEARuleAllocation(estate, claimers);
		RuleCalculator.CELRuleAllocation(estate, claimers);
		RuleCalculator.adjustedProportionalAllocation(estate, claimers);
		RuleCalculator.talmudRuleAllocation(estate, claimers);
		RuleCalculator.clightsRuleAllocation(estate, claimers);
		RuleCalculator.minimalOverlappingRuleAllocation(estate, claimers);
	}
	
	private static void calculateCoalitionRuleAllocations(List<Coalition> coalitions)
	{
		RuleCalculator.calculateCoalitionProportionalAllocation(coalitions);
		RuleCalculator.calculateCoalitionCEAAllocation(coalitions);
		RuleCalculator.calculateCoalitionCELAllocation(coalitions);
		RuleCalculator.calculateCoalitionAdjustedProportionalAllocation(coalitions);
		RuleCalculator.calculateCoalitionShapleyAllocation(coalitions);
		RuleCalculator.calculateCoalitionTalmudAllocation(coalitions);
		RuleCalculator.calculateCoalitionMinimalOverlappingAllocation(coalitions);
		RuleCalculator.calculateCoalitionClightsAllocation(coalitions);
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
	
	
	private static double generateUniformRandom(double min, double max)
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
				// clearClaimer(claimer);
			}
		}
	}
	
	private void disableAllButtons()
	{
		numberOfCreditors.setEditable(false);
		runButton.setEnabled(false);		
	}
	
	private void enableAllButtons()
	{
		numberOfCreditors.setEditable(true);
		runButton.setEnabled(true);		
	}
	
	private static JButton createSimpleButton(String text) 
	{
		  JButton button = new JButton(text);
		  button.setForeground(Color.BLACK);
		  button.setBackground(Color.WHITE);
		  Border line = new LineBorder(Color.BLACK);
		  Border margin = new EmptyBorder(5, 15, 5, 15);
		  Border compound = new CompoundBorder(line, margin);
		  button.setBorder(compound);
		  // button.setOpaque(false);
		  return button;
	}
}
