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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import bankruptcy_code.Claimer;
import bankruptcy_code.Coalition;
import bankruptcy_code.CoalitionWithRankingDifference;
import bankruptcy_code.CustomMathOperations;
import bankruptcy_code.RankCalculator;
import bankruptcy_code.RuleCalculator;

@SuppressWarnings("serial")
public class SimulationPanel extends CalculationPanel
{
	private JTextField numberOfCreditors;
	
	private JLabel numberOfCreditorsLabel;
	
	private JLabel referenceComboLabel;
	
	private JLabel maximumDiffLabel;
	private JLabel propLabel;
	private JLabel ceaLabel;
	private JLabel celLabel;
	private JLabel apropLabel;
	private JLabel shapLabel;
	private JLabel talLabel;
	private JLabel moLabel;
	private JLabel cliLabel;
	private JLabel eqLabel;
	private JLabel unirandLabel;
	
	private JLabel estateLabel;
	private JLabel estateFunctionLabel1;
	private JLabel estateFunctionLabel2;
	
	private JTextField estateFunctionMin;
	private JTextField estateFunctionMax;
	
	private JButton runButton;
	private JButton stopButton;
	
	private JPanel canvasPanel;
	private JPanel numCreditorsPanel;
	private JPanel referenceComboPanel;
	private JPanel estatePanel;
	private JPanel inputsPanel;
	private JPanel buttonsPanel;
	private JPanel averagesPanel;
	
	private JComboBox<String> referenceSelectionCombo;
	
	private JLabel numIterations;
	private boolean stop = false;
	private boolean isVersionB = false;
	
	public SimulationPanel(boolean isVersionB)
	{
		this.isVersionB = isVersionB;
		this.setLayout(new GridLayout());
		this.setBackground(Color.WHITE);
		
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
		inputsPanel = new JPanel(new BorderLayout());
		inputsPanel.setBackground(Color.WHITE);
		
		referenceComboPanel = new JPanel();
		referenceComboPanel.setBackground(Color.WHITE);
		String[] referenceOptions = {"characteristic function", "average of rules", "proportional rule", "CEA rule", "CEL rule", "Talmud rule",
				"adjusted proportional rule", "minimal overlapping rule", "per capita nucleolous rule", "Shapley values", "equal allocation",
				"uniform random allocation"};
		referenceSelectionCombo = new JComboBox<String>(referenceOptions);
		referenceSelectionCombo.setFont(new Font("Serif", Font.PLAIN, 18));
		referenceSelectionCombo.setBackground(Color.WHITE);
		referenceSelectionCombo.setSelectedIndex(0);
		referenceSelectionCombo.setBounds(50, 100,90,20);	
		
		referenceComboLabel = new JLabel("Reference: ");
		referenceComboLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		referenceComboPanel.add(referenceComboLabel);
		referenceComboPanel.add(referenceSelectionCombo);
		
		numberOfCreditorsLabel = new JLabel("Number of Creditors: ");
		numberOfCreditorsLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		numberOfCreditors = new JTextField(4);
		numberOfCreditors.setFont(new Font("Serif", Font.PLAIN, 21));
		
		numCreditorsPanel = new JPanel();
		numCreditorsPanel.setBackground(Color.WHITE);
		
		numCreditorsPanel.add(numberOfCreditorsLabel);
		numCreditorsPanel.add(numberOfCreditors);
		
		estateLabel = new JLabel("Estate: ");
		estateLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		estateFunctionMin = new JTextField(4);
		estateFunctionMin.setFont(new Font("Serif", Font.PLAIN, 21));
		
		estateFunctionLabel1 = new JLabel("*D <= estate <=");
		estateFunctionLabel1.setFont(new Font("Serif", Font.PLAIN, 21));
		
		estateFunctionMax = new JTextField(4);
		estateFunctionMax.setFont(new Font("Serif", Font.PLAIN, 21));
		
		estateFunctionLabel2 = new JLabel("*D, where D denotes the sum of claims");
		estateFunctionLabel2.setFont(new Font("Serif", Font.PLAIN, 21));
		
		estatePanel = new JPanel();
		estatePanel.setBackground(Color.WHITE);
		estatePanel.setPreferredSize(new Dimension(700, 50));
		estatePanel.add(estateLabel);
		estatePanel.add(estateFunctionMin);
		estatePanel.add(estateFunctionLabel1);
		estatePanel.add(estateFunctionMax);
		estatePanel.add(estateFunctionLabel2);
		
		inputsPanel.add(referenceComboPanel, BorderLayout.NORTH);
		inputsPanel.add(numCreditorsPanel, BorderLayout.CENTER);
		inputsPanel.add(estatePanel, BorderLayout.SOUTH);
		
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
		propLabel = new JLabel("Relative Average Proportional Rule SRD: ");
		propLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		ceaLabel = new JLabel("Relative Average CEA Rule SRD: ");
		ceaLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		celLabel = new JLabel("Relative Average CEL Rule SRD: ");
		celLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		apropLabel = new JLabel("Relative Average Adjusted Proportional Rule SRD:");
		apropLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		shapLabel = new JLabel("Relative Average Shapley Value SRD:");
		shapLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		talLabel = new JLabel("Relative Average Talmud Rule SRD:");
		talLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		moLabel = new JLabel("Relative Average Minimal Overlap Rule SRD:");
		moLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		cliLabel = new JLabel("Relative Average Per Capita Nucleolous Rule SRD:");
		cliLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		eqLabel = new JLabel("Control 1: Relative Average Equal Allocation SRD:");
		eqLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		unirandLabel = new JLabel("Control 2: Relative Average Uniform Random Allocation SRD:");
		unirandLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		averagesPanel.add(maximumDiffLabel);
		averagesPanel.add(propLabel);
		averagesPanel.add(ceaLabel);
		averagesPanel.add(celLabel);
		averagesPanel.add(apropLabel);
		averagesPanel.add(shapLabel);
		averagesPanel.add(talLabel);
		averagesPanel.add(moLabel);
		averagesPanel.add(cliLabel);
		averagesPanel.add(eqLabel);
		averagesPanel.add(unirandLabel);
		
		canvasPanel.add(averagesPanel, BorderLayout.SOUTH);
		
		this.add(canvasPanel);
		
		this.setPreferredSize(new Dimension(700, 600)); // 525, 750
	}
	
	private class RunButtonActionListener implements ActionListener 
	{		
		@Override
		public void actionPerformed(ActionEvent event)
		{	
			if(!numberOfCreditors.getText().isEmpty() && !estateFunctionMax.getText().isEmpty() && !estateFunctionMin.getText().isEmpty())
			{
				if(Double.parseDouble(estateFunctionMax.getText()) >= Double.parseDouble(estateFunctionMin.getText()))
				{
					disableAllButtons();
					Thread thread = new Thread(new Runnable()
				    {
				    	@Override
				    	public void run()
				    	{
				    		try
				    		{
				    			calculateAverageSRD(isVersionB);	    					    							    			
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
					JLabel label = new JLabel("Minimum must be less than or equal to maximum");
					label.setFont(new Font("Serif", Font.PLAIN, 21));
					JOptionPane.showMessageDialog(new JFrame(), label, "ERROR", JOptionPane.ERROR_MESSAGE);
				}
				
			}
			else if(numberOfCreditors.getText().isEmpty())
			{
				JLabel label = new JLabel("Please input number of creditors");
				label.setFont(new Font("Serif", Font.PLAIN, 21));
				JOptionPane.showMessageDialog(new JFrame(), label, "ERROR", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				JLabel label = new JLabel("Please input valid estate bounds");
				label.setFont(new Font("Serif", Font.PLAIN, 21));
				JOptionPane.showMessageDialog(new JFrame(), label, "ERROR", JOptionPane.ERROR_MESSAGE);
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

	private void calculateAverageSRD(boolean isVersionB)
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
		List<CoalitionWithRankingDifference> eq;
		List<CoalitionWithRankingDifference> unirand;
		
		Map<String, Double> averageSRD;		
		
		numIterations.setText("iterations: " + iterations);  // update iterations label in UI
		
		List<Claimer> claimers = new ArrayList<Claimer>(); // master list of claimers
		
		for(int i = 0; i < numClaimers; i++)
		{
			claimers.add(new Claimer((char)(97 + i), CustomMathOperations.generateUniformRandom(0, 1000)));
		}

		List<Coalition> allCoalitions = getAllCoalitions(claimers);
		List<Coalition> coalitions = getIndependentCoalitions(numClaimers, claimers);
		
		// double maximumSRD = (((coalitions.size()-1)*(coalitions.size() - 1)) / 2);
		double maximumSRD = (((coalitions.size())*(coalitions.size())) / 2);
		maximumDiffLabel.setText("Maximum: 1.0");
		
		estate = CustomMathOperations.generateUniformRandom((RuleCalculator.sum(claimers, "claims") * Double.parseDouble(estateFunctionMin.getText())),
							(RuleCalculator.sum(claimers, "claims") * Double.parseDouble(estateFunctionMax.getText())));
		
		calculateClaimerRuleAllocations(estate, claimers);
		RuleCalculator.shap(estate, claimers, allCoalitions, isVersionB);
		
		RuleCalculator.calculateCharacteristicFunction(estate, coalitions, claimers, isVersionB); // needs to be called after shapley (because shapley calls same method as version A every time)
		
		calculateCoalitionRuleAllocations(coalitions, isVersionB);	
		RuleCalculator.calculateReference(referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()), coalitions);
		// print(claimers, coalitions);
		// long startTime = System.nanoTime();
		ref = RankCalculator.rankingBasedOnReference(coalitions);
		prop = RankCalculator.rankingBasedOnProportionalAllocation(coalitions);
		cea = RankCalculator.rankingBasedOnCEAAllocation(coalitions);
		cel = RankCalculator.rankingBasedOnCELAllocation(coalitions);
		aprop = RankCalculator.rankingBasedOnAdjustedProportionalAllocation(coalitions);
		shap = RankCalculator.rankingBasedOnShapleyAllocation(coalitions);
		tal = RankCalculator.rankingBasedOnTalmudAllocation(coalitions);
		mo = RankCalculator.rankingBasedOnMinimalOverlappingAllocation(coalitions);
		cli = RankCalculator.rankingBasedOnClightsAllocation(coalitions);
		eq = RankCalculator.rankingBasedOnEqualAllocation(coalitions);
		unirand = RankCalculator.rankingBasedOnUniformRandomAllocation(coalitions);
		/*
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		System.out.println(elapsedTime);
		System.out.println("ranking time: " + ((double) elapsedTime/1_000_000_000) + " seconds");
		*/
		
		RankCalculator.compareRanks(prop, ref);
		RankCalculator.compareRanks(cea, ref);
		RankCalculator.compareRanks(cel, ref);
		RankCalculator.compareRanks(aprop, ref);
		RankCalculator.compareRanks(shap, ref);
		RankCalculator.compareRanks(tal, ref);
		RankCalculator.compareRanks(mo, ref);
		RankCalculator.compareRanks(cli, ref);
		RankCalculator.compareRanks(eq, ref);
		RankCalculator.compareRanks(unirand, ref);
		
		averageSRD = new HashMap<String, Double>();
		
		averageSRD.put("prop", sumRankingDifferences(prop));
		averageSRD.put("cea", sumRankingDifferences(cea));
		averageSRD.put("cel", sumRankingDifferences(cel));
		averageSRD.put("aprop", sumRankingDifferences(aprop));
		averageSRD.put("shap", sumRankingDifferences(shap));
		averageSRD.put("tal", sumRankingDifferences(tal));
		averageSRD.put("mo", sumRankingDifferences(mo));
		averageSRD.put("cli", sumRankingDifferences(cli));
		averageSRD.put("eq", sumRankingDifferences(eq));
		averageSRD.put("unirand", sumRankingDifferences(unirand));
		
		while(!stop)
		{		
			for(int i = 0; i < numClaimers; i++)
			{
				claimers.get(i).setClaim(CustomMathOperations.generateUniformRandom(0, 1000));
			}

			// estate = generateUniformRandom(RuleCalculator.sum(claimers, "claims")/2.0, RuleCalculator.sum(claimers, "claims"));
			estate = CustomMathOperations.generateUniformRandom((RuleCalculator.sum(claimers, "claims") * Double.parseDouble(estateFunctionMin.getText())),
					(RuleCalculator.sum(claimers, "claims") * Double.parseDouble(estateFunctionMax.getText())));

			iterations = iterations + 1;			
			
			updateCoalitionClaimers(allCoalitions, claimers); // update all coalitions with the new claims
			
			coalitions = getIndependentCoalitions(numClaimers, claimers);
			
			calculateClaimerRuleAllocations(estate, claimers);
			// RuleCalculator.calculateCharacteristicFunction(estate, coalitions, claimers, isVersionB);
			// RuleCalculator.calculateShapleyValues(estate, claimers, coalitions, isVersionB);
			RuleCalculator.shap(estate, claimers, allCoalitions, isVersionB);
			RuleCalculator.calculateCharacteristicFunction(estate, coalitions, claimers, isVersionB);
			calculateCoalitionRuleAllocations(coalitions, isVersionB);	
			RuleCalculator.calculateReference(referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()), coalitions);
			
			ref = RankCalculator.rankingBasedOnReference(coalitions);
			prop = RankCalculator.rankingBasedOnProportionalAllocation(coalitions);
			cea = RankCalculator.rankingBasedOnCEAAllocation(coalitions);
			cel = RankCalculator.rankingBasedOnCELAllocation(coalitions);
			aprop = RankCalculator.rankingBasedOnAdjustedProportionalAllocation(coalitions);
			shap = RankCalculator.rankingBasedOnShapleyAllocation(coalitions);
			tal = RankCalculator.rankingBasedOnTalmudAllocation(coalitions);
			mo = RankCalculator.rankingBasedOnMinimalOverlappingAllocation(coalitions);
			cli = RankCalculator.rankingBasedOnClightsAllocation(coalitions);
			eq = RankCalculator.rankingBasedOnEqualAllocation(coalitions);
			unirand = RankCalculator.rankingBasedOnUniformRandomAllocation(coalitions);

			
			RankCalculator.compareRanks(prop, ref);
			RankCalculator.compareRanks(cea, ref);
			RankCalculator.compareRanks(cel, ref);
			RankCalculator.compareRanks(aprop, ref);
			RankCalculator.compareRanks(shap, ref);
			RankCalculator.compareRanks(tal, ref);
			RankCalculator.compareRanks(mo, ref);
			RankCalculator.compareRanks(cli, ref);
			RankCalculator.compareRanks(eq, ref);
			RankCalculator.compareRanks(unirand, ref);
			
			averageSRD.put("prop", averageSRD.get("prop").doubleValue() + (sumRankingDifferences(prop) - averageSRD.get("prop").doubleValue())/iterations);
			averageSRD.put("cea", averageSRD.get("cea").doubleValue() + (sumRankingDifferences(cea) - averageSRD.get("cea").doubleValue())/iterations);
			averageSRD.put("cel", averageSRD.get("cel").doubleValue() + (sumRankingDifferences(cel) - averageSRD.get("cel").doubleValue())/iterations);
			averageSRD.put("aprop", averageSRD.get("aprop").doubleValue() + (sumRankingDifferences(aprop) - averageSRD.get("aprop").doubleValue())/iterations);
			averageSRD.put("shap", averageSRD.get("shap").doubleValue() + (sumRankingDifferences(shap) - averageSRD.get("shap").doubleValue())/iterations);
			averageSRD.put("tal", averageSRD.get("tal").doubleValue() + (sumRankingDifferences(tal) - averageSRD.get("tal").doubleValue())/iterations);
			averageSRD.put("mo", averageSRD.get("mo").doubleValue() + (sumRankingDifferences(mo) - averageSRD.get("mo").doubleValue())/iterations);
			averageSRD.put("cli", averageSRD.get("cli").doubleValue() + (sumRankingDifferences(cli) - averageSRD.get("cli").doubleValue())/iterations);
			averageSRD.put("eq", averageSRD.get("eq").doubleValue() + (sumRankingDifferences(eq) - averageSRD.get("eq").doubleValue())/iterations);
			averageSRD.put("unirand", averageSRD.get("unirand").doubleValue() + (sumRankingDifferences(unirand) - averageSRD.get("unirand").doubleValue())/iterations);
			
			
			if(iterations % 10 == 0)
			{
				propLabel.setText("Relative Average Proportional Rule SRD: " + averageSRD.get("prop")/maximumSRD);
				ceaLabel.setText("Relative Average CEA Rule SRD: " + averageSRD.get("cea")/maximumSRD);
				celLabel.setText("Relative Average CEL Rule SRD: " + averageSRD.get("cel")/maximumSRD);
				apropLabel.setText("Relative Average Adjusted Proportional Rule SRD: " + averageSRD.get("aprop")/maximumSRD);
				shapLabel.setText("Relative Average Shapley Value SRD: " + averageSRD.get("shap")/maximumSRD);
				talLabel.setText("Relative Average Talmud Rule SRD: " + averageSRD.get("tal")/maximumSRD);
				moLabel.setText("Relative Average Minimal Overlap Rule SRD: " + averageSRD.get("mo")/maximumSRD);
				cliLabel.setText("Relative Average Per Capita Nucleolous Rule SRD: " + averageSRD.get("cli")/maximumSRD);				
				eqLabel.setText("Control 1: Relative Average Equal Allocation SRD: " + averageSRD.get("eq")/maximumSRD);
				unirandLabel.setText("Control 2: Relative Average Uniform Random Allocation SRD: " + averageSRD.get("unirand")/maximumSRD);
			}
			
			numIterations.setText("iterations: " + iterations);  // update iterations label in UI
		}
		try 
		{
			saveToFile(isVersionB, referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()), Integer.parseInt(numberOfCreditors.getText()), 
					Double.parseDouble(estateFunctionMin.getText()), Double.parseDouble(estateFunctionMax.getText()),  iterations);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		enableAllButtons();
		stop = false;
	}
	
	@SuppressWarnings("unused")
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
	
	@SuppressWarnings("unused")
	private static void printAverages(List<Coalition> list2, int iter)
	{
		System.out.println("iteration: " + iter);
		for(Coalition entry : list2)
		{
			System.out.println(entry.getId() + " ref: " + entry.getReference() + " prop avg.: " + entry.getAveragePropVariation() + " CEA avg.: " + entry.getAverageCEAVariation() + " CEL avg.: " + entry.getAverageCELVariation()+ " Shap avg.: " + entry.getAverageShapleyValueVariation());
		}
	}
	
	// update the claims of every claimer in the coalition lists
	/**
	 * Updates the claims of every claimer in each coalition's claimer list.
	 * @param List of coalitions that need to be updated.
	 * @param List of claimers with new claim values.
	 */
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
	
	/**
	 * Disable all editable JTextFields and JButtons.
	 */
	private void disableAllButtons()
	{
		referenceSelectionCombo.setEnabled(false);
		numberOfCreditors.setEditable(false);
		estateFunctionMin.setEditable(false);
		estateFunctionMax.setEditable(false);
		runButton.setEnabled(false);	
	}
	
	/**
	 * Enable all editable JTextFields and JButtons.
	 */
	private void enableAllButtons()
	{
		referenceSelectionCombo.setEnabled(true);
		numberOfCreditors.setEditable(true);
		estateFunctionMin.setEditable(true);
		estateFunctionMax.setEditable(true);
		runButton.setEnabled(true);		
	}
	
	private void saveToFile(boolean versionB, String reference, int numAgents, double min, double max, int iterations) throws IOException
	{
		FileWriter fw;
		if(!versionB)
		{
			fw = new FileWriter("sim_versionA_ref-" + reference + "_numAgents-" + numAgents + "_percent-range-" + min + "-"
					+ max + "_iter-" + iterations + ".txt");	
			fw.write("Version A\nreference: " + reference + "\nNumber of Creditors: " + numAgents + "\nRange min.: " + min
					+ ", max.: " + max + "\niterations: " + iterations + "\n");
		}
		else
		{
			fw = new FileWriter("sim_versionB_ref-" + reference + "_numAgents-" + numAgents + "_percent-range-" + min + "-"
					+ max + "_iter-" + iterations + ".txt");
			fw.write("Version B\nreference: " + reference + "\nNumber of Creditors: " + numAgents + "\nRange min.: " + min
					+ ", max.: " + max + "\niterations: " + iterations + "\n");
		}		 
		
		fw.write(propLabel.getText() + "\n");
		fw.write(ceaLabel.getText() + "\n");
		fw.write(celLabel.getText() + "\n");
		fw.write(apropLabel.getText() + "\n");
		fw.write(shapLabel.getText() + "\n");
		fw.write(talLabel.getText() + "\n");
		fw.write(moLabel.getText() + "\n");
		fw.write(cliLabel.getText() + "\n");
		fw.write(eqLabel.getText() + "\n");
		fw.write(unirandLabel.getText() + "\n");
	 
		fw.close();
	}
}
