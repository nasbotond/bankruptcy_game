package execution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import calculations.CustomMathOperations;
import calculations.RankCalculator;
import calculations.RuleCalculator;
import data_elements.Claimer;
import data_elements.Coalition;
import data_elements.CoalitionWithRankingDifference;
import exceptions.InvalidEstateException;

@SuppressWarnings("serial")
public class ExactCalculationPanel extends CalculationPanel 
{
	private JTextField estate;
	
	private JLabel estateLabel;
	private JLabel comboLabel;
	private JLabel referenceComboLabel;
	
	private JButton runButton;
	
	private JPanel canvasPanel;
	private JPanel creditorsComboPanel;
	private JPanel referenceComboPanel;
	private JPanel comboPanel;
	private JPanel inputsPanel;
	private JPanel buttonsPanel;
	
	private JComboBox<Integer> numCreditorsCombo;
	private JComboBox<String> referenceSelectionCombo;
	
	private List<JTextField> listOfCreditorInputs;
	
	private JPanel consolePanel;
	private JTextArea consoleOutput;
	
	private boolean isVersionB = false;
	private boolean isVersionC = false;
	
	public ExactCalculationPanel(boolean isVersionB, boolean isVersionC)
	{		
		this.isVersionB = isVersionB;
		this.isVersionC = isVersionC;
		this.setLayout(new GridLayout());

		comboPanel = new JPanel(new BorderLayout());
		comboPanel.setBackground(Color.WHITE);
		
		creditorsComboPanel = new JPanel();
		creditorsComboPanel.setBackground(Color.WHITE);
		Integer[] creditorNumber = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};
		numCreditorsCombo = new JComboBox<Integer>(creditorNumber);
		numCreditorsCombo.setFont(new Font("Serif", Font.PLAIN, 18));
		numCreditorsCombo.setBackground(Color.WHITE);
		numCreditorsCombo.setSelectedIndex(0);
		numCreditorsCombo.setBounds(50, 100,90,20);	
		
		comboLabel = new JLabel("Number of creditors: ");
		comboLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		creditorsComboPanel.add(comboLabel);
		creditorsComboPanel.add(numCreditorsCombo);
		
		listOfCreditorInputs = new ArrayList<JTextField>();
		
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
		
		comboPanel.add(referenceComboPanel, BorderLayout.NORTH);
		comboPanel.add(creditorsComboPanel, BorderLayout.CENTER);
		
		canvasPanel = new JPanel(new BorderLayout());
		
		buttonsPanel = new JPanel(new FlowLayout());
		buttonsPanel.setBackground(Color.WHITE);
		
		// run button action
		runButton = createSimpleButton("calculate");
		runButton.addActionListener(new RunButtonActionListener());
		runButton.setFont(new Font("Serif", Font.PLAIN, 18));
		
		buttonsPanel.add(runButton);
		
		// inputs
		inputsPanel = new JPanel();
		inputsPanel.setBackground(Color.WHITE);		
		
		numCreditorsCombo.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e)
			{
				listOfCreditorInputs.removeAll(listOfCreditorInputs);
				inputsPanel.removeAll();
				estateLabel = new JLabel("Estate/Endowment:");
				estateLabel.setFont(new Font("Serif", Font.PLAIN, 21));
				estate = new JTextField(4);
				estate.setFont(new Font("Serif", Font.PLAIN, 21));
				inputsPanel.add(estateLabel);
				inputsPanel.add(estate);
				
				for(int i = 0; i < numCreditorsCombo.getItemAt(numCreditorsCombo.getSelectedIndex()); i++)
				{
					JTextField textField = new JTextField(4);
					textField.setFont(new Font("Serif", Font.PLAIN, 21));
					listOfCreditorInputs.add(textField);
					JLabel label = new JLabel((char)(97 + i) + ":");
					label.setFont(new Font("Serif", Font.PLAIN, 21));
					inputsPanel.add(label);
					inputsPanel.add(listOfCreditorInputs.get(i));
				}
				
				inputsPanel.revalidate();
				inputsPanel.repaint();
			}
		});

		canvasPanel.add(comboPanel, BorderLayout.NORTH);
		canvasPanel.add(inputsPanel, BorderLayout.CENTER);		
		
		// console panel:
		//
		
		consolePanel = new JPanel(new BorderLayout());
		consolePanel.setBackground(Color.WHITE);						
		consoleOutput = new JTextArea(24, 60); //29, 60
		consoleOutput.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(consoleOutput);	
		/*
		PrintStream out = null;
		try 
		{
			out = new PrintStream(new FileOutputStream("output.txt", false), false);
		} 
		catch (FileNotFoundException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.setOut(out);
		System.setErr(out);			
		*/
		
		/*
		PrintStream printStream = new PrintStream(new CustomOutputStream(consoleOutput));
					
		// keeps reference of standard output stream
		// standardOut = System.out;
				         
		// re-assigns standard output stream and error output stream
		System.setOut(printStream);
		System.setErr(printStream);
		*/
						
		consolePanel.add(buttonsPanel, BorderLayout.NORTH);
		consolePanel.add(scrollPane, BorderLayout.CENTER);
		//
		
		canvasPanel.add(consolePanel, BorderLayout.SOUTH);		
		
		this.add(canvasPanel);
		
		this.setPreferredSize(new Dimension(700, 675)); // 525, 750
	}
	
	private class RunButtonActionListener implements ActionListener 
	{
		@Override
		public void actionPerformed(ActionEvent event)
		{	
			if(!listOfCreditorInputs.isEmpty())
			{
				double sumOfClaims = 0.0;
				for(JTextField input : listOfCreditorInputs)
				{
					sumOfClaims += Double.parseDouble(input.getText());					
				}
				if(sumOfClaims >= Double.parseDouble(estate.getText()))
				{
					Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								// PrintWriter pw = new PrintWriter("output.txt");
								// pw.close();
								if(isVersionC)
								{
									calculateEqualCoalitionExactSRD();
								}
								else
								{
									calculateExactSRD(isVersionB);
								}
								
								// FileReader reader = new FileReader("output.txt");
								// consoleOutput.read(reader, "output.txt"); // Object of JTextArea
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
					JLabel label = new JLabel("Sum of claims must be greater than or equal to the estate");
					label.setFont(new Font("Serif", Font.PLAIN, 21));
					JOptionPane.showMessageDialog(new JFrame(), label, "ERROR", JOptionPane.ERROR_MESSAGE);
				}										
			}
			else
			{
				JLabel label = new JLabel("Number of creditors must be greater than 0");
				label.setFont(new Font("Serif", Font.PLAIN, 21));
				JOptionPane.showMessageDialog(new JFrame(), label, "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void calculateEqualCoalitionExactSRD() throws InvalidEstateException
	{
		double estateInput = Double.parseDouble(estate.getText());
		List<Claimer> claimers = new ArrayList<Claimer>();
		double sumOfClaims = 0.0;
		
		for(JTextField input : listOfCreditorInputs)
		{
			sumOfClaims += Double.parseDouble(input.getText());
			claimers.add(new Claimer((char)(97 + listOfCreditorInputs.indexOf(input)), Double.parseDouble(input.getText())));
		}
		
		if(estateInput <= sumOfClaims)
		{
			List<Coalition> coalitions = getAllCoalitions(claimers);
			// List<Coalition> coalitions = getIndependentCoalitions(claimers.size(), claimers);
			
			calculateClaimerRuleAllocations(estateInput, claimers);
			// RuleCalculator.calculateCharacteristicFunction(estateInput, coalitions, claimers, isVersionB);
			long startTime = System.nanoTime();
			// RuleCalculator.calculateShapleyValues(estateInput, claimers, coalitions, isVersionB); // also calculates the characteristic function
			RuleCalculator.shap(estateInput, claimers, coalitions, false);
			
			RuleCalculator.calculateCharacteristicFunction(estateInput, coalitions, claimers, false);
			
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startTime;
			// System.out.println(elapsedTime);
			System.out.println("shap total time: " + ((double) elapsedTime/1_000_000_000) + " seconds");
			
			calculateCoalitionRuleAllocations(coalitions, false);
			RuleCalculator.calculateReference(referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()), coalitions);
			// coalitions.remove(coalitions.size() - 1); // TODO
			// RuleCalculator.calculateReferenceOLD(estateInput, coalitions, claimers, isVersionB);
			long startTimeRank = System.nanoTime();
			System.out.println("Starting ranking...");
			FileWriter fw;
			try
			{
				fw = new FileWriter("exact_versionC_ref-" + referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()) 
				+ "_numAgents-" + listOfCreditorInputs.size() + "_estate-" + Double.parseDouble(estate.getText()) + ".txt");
				
				writeToConsoleAndFile(consoleOutput, fw, "Version C\nreference: "
						+ referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()) 
						+ "\nNumber of Creditors: " + listOfCreditorInputs.size() + "\nEstate: " + Double.parseDouble(estate.getText()) + "\n");		

				writeToConsoleAndFile(consoleOutput, fw, "\nClaimers\n");

				for(Claimer entry : claimers)
				{
					writeToConsoleAndFile(consoleOutput, fw, entry.getId() + " Claim: " + entry.getClaim() + " PROP: " + entry.getProportionalAllocation() + " CEA: " + entry.getCEAAllocation()
					+ " CEL: " + entry.getCELAllocation()  + " SHAP: " + entry.getShapleyValue()  + " TAL: " + entry.getTalmudAllocation()
					+ " MO: " + entry.getMinimalOverlappingAllocation() + " APROP: " + entry.getAdjustedProportionalAllocation()
					+ " CLI: " + entry.getClightsAllocation() + " EQ: " + entry.getEqualAllocation() + " UNIRAND: " + entry.getUniformRandomAllocation() + "\n");
				}
				
				List<CoalitionWithRankingDifference> ref = null;
				List<CoalitionWithRankingDifference> prop = null;
				List<CoalitionWithRankingDifference> cea = null;
				List<CoalitionWithRankingDifference> cel = null;
				List<CoalitionWithRankingDifference> aprop = null;
				List<CoalitionWithRankingDifference> shap = null;
				List<CoalitionWithRankingDifference> tal = null;
				List<CoalitionWithRankingDifference> mo = null;
				List<CoalitionWithRankingDifference> cli = null;
				List<CoalitionWithRankingDifference> eq = null;
				List<CoalitionWithRankingDifference> unirand = null;
				
				List<Coalition> equalSizedCoalitions;
				
				// double[] eqCoalitionSRDs = new double[10];
				
				for(int i = 1; i <= claimers.size()/2; i++)
				{
					equalSizedCoalitions = new ArrayList<Coalition>();
					int cap = claimers.size()-2;
					while(cap > 0)
					{
						int index = (int)(Math.random()*coalitions.size());
						if(coalitions.get(index).getClaimers().size() == (claimers.size() - i))
						{
							if(!equalSizedCoalitions.contains(coalitions.get(index)))
							{
								equalSizedCoalitions.add(coalitions.get(index));
								cap--;
							}							
						}
					}
					
					ref = RankCalculator.rankingBasedOnReference(equalSizedCoalitions);
					prop = RankCalculator.rankingBasedOnProportionalAllocation(equalSizedCoalitions);
					cea = RankCalculator.rankingBasedOnCEAAllocation(equalSizedCoalitions);
					cel = RankCalculator.rankingBasedOnCELAllocation(equalSizedCoalitions);
					aprop = RankCalculator.rankingBasedOnAdjustedProportionalAllocation(equalSizedCoalitions);
					shap = RankCalculator.rankingBasedOnShapleyAllocation(equalSizedCoalitions);
					tal = RankCalculator.rankingBasedOnTalmudAllocation(equalSizedCoalitions);
					mo = RankCalculator.rankingBasedOnMinimalOverlappingAllocation(equalSizedCoalitions);
					cli = RankCalculator.rankingBasedOnClightsAllocation(equalSizedCoalitions);
					eq = RankCalculator.rankingBasedOnEqualAllocation(equalSizedCoalitions);
					unirand = RankCalculator.rankingBasedOnUniformRandomAllocation(equalSizedCoalitions);
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
					
					/*
					eqCoalitionSRDs[0] = eqCoalitionSRDs[0] + sumRankingDifferences(prop);
					eqCoalitionSRDs[1] = eqCoalitionSRDs[1] + sumRankingDifferences(cea);
					eqCoalitionSRDs[2] = eqCoalitionSRDs[2] + sumRankingDifferences(cel);
					eqCoalitionSRDs[3] = eqCoalitionSRDs[3] + sumRankingDifferences(aprop);
					eqCoalitionSRDs[4] = eqCoalitionSRDs[4] + sumRankingDifferences(shap);
					eqCoalitionSRDs[5] = eqCoalitionSRDs[5] + sumRankingDifferences(tal);
					eqCoalitionSRDs[6] = eqCoalitionSRDs[6] + sumRankingDifferences(mo);
					eqCoalitionSRDs[7] = eqCoalitionSRDs[7] + sumRankingDifferences(cli);
					eqCoalitionSRDs[8] = eqCoalitionSRDs[8] + sumRankingDifferences(eq);
					eqCoalitionSRDs[9] = eqCoalitionSRDs[9] + sumRankingDifferences(unirand);
					*/
					writeToConsoleAndFile(consoleOutput, fw, "\n" + (claimers.size() - i) + " member coalition\n");
					writeToConsoleAndFile(consoleOutput, fw, "\nCoalitions\n");
					for(Coalition entry : equalSizedCoalitions)
					{
						writeToConsoleAndFile(consoleOutput, fw, entry.getId() + " Claim: " + entry.coalitionClaim() + " REF: " + entry.getReference() + " PROP: " + entry.getProportionalAllocation()
						+ " CEA: " + entry.getCEAAllocation()+ " CEL: " + entry.getCELAllocation() + " SHAP: " + entry.getShapleyValueAllocation()
						+ " TAL: " + entry.getTalmudAllocation() + " MO: " + entry.getMinimalOverlappingAllocation() + " APROP: " + entry.getAdjustedProportionalAllocation()
						+ " CLI: " + entry.getClightsAllocation() + " EQ: " + entry.getEqualAllocation() + " UNIRAND: " + entry.getUniformRandomAllocation() + "\n");
					}

					writeToConsoleAndFile(consoleOutput, fw, "\n");
					for(CoalitionWithRankingDifference entry : ref)
					{
						writeToConsoleAndFile(consoleOutput, fw, "REF   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + "\n");
					}

					writeToConsoleAndFile(consoleOutput, fw, "\n");
					for(CoalitionWithRankingDifference entry : prop)
					{
						writeToConsoleAndFile(consoleOutput, fw, "PROP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of PROP ranking differences : " + sumRankingDifferences(prop) + "\n");

					writeToConsoleAndFile(consoleOutput, fw, "\n");
					for(CoalitionWithRankingDifference entry : cea)
					{
						writeToConsoleAndFile(consoleOutput, fw, "CEA   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of CEA ranking differences : " + sumRankingDifferences(cea)+ "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : cel)
					{
						writeToConsoleAndFile(consoleOutput, fw, "CEL   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of CEL ranking differences : " + sumRankingDifferences(cel)+ "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : aprop)
					{
						writeToConsoleAndFile(consoleOutput, fw, "APROP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of APROP ranking differences : " + sumRankingDifferences(aprop) + "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : shap)
					{
						writeToConsoleAndFile(consoleOutput, fw, "SHAP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}	
					writeToConsoleAndFile(consoleOutput, fw, "sum of SHAP ranking differences : " + sumRankingDifferences(shap) + "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : tal)
					{
						writeToConsoleAndFile(consoleOutput, fw, "TAL   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of TAL ranking differences : " + sumRankingDifferences(tal) + "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : mo)
					{
						writeToConsoleAndFile(consoleOutput, fw, "MO   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of MO ranking differences : " + sumRankingDifferences(mo) + "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : cli)
					{
						writeToConsoleAndFile(consoleOutput, fw, "CLI   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of CLI ranking differences : " + sumRankingDifferences(cli) + "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : eq)
					{
						writeToConsoleAndFile(consoleOutput, fw, "EQ   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of EQ ranking differences : " + sumRankingDifferences(eq) + "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					for(CoalitionWithRankingDifference entry : unirand)
					{
						writeToConsoleAndFile(consoleOutput, fw, "UNIRAND   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
					}
					writeToConsoleAndFile(consoleOutput, fw, "sum of UNIRAND ranking differences : " + sumRankingDifferences(unirand) + "\n");
					writeToConsoleAndFile(consoleOutput, fw, "\n");

					
				}
				fw.close();
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}		

			long endTimeRank = System.nanoTime();
			long elapsedTimeRank = endTimeRank - startTimeRank;
			// System.out.println(elapsedTimeRank);
			System.out.println("ranking time: " + ((double) elapsedTimeRank/1_000_000_000) + " seconds");			
		}
		else
		{
			throw new InvalidEstateException("Estate is greater than the sum");
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void calculateExactSRD(boolean isVersionB) throws InvalidEstateException
	{
		double estateInput = Double.parseDouble(estate.getText());
		List<Claimer> claimers = new ArrayList<Claimer>();
		double sumOfClaims = 0.0;
		
		for(JTextField input : listOfCreditorInputs)
		{
			sumOfClaims += Double.parseDouble(input.getText());
			claimers.add(new Claimer((char)(97 + listOfCreditorInputs.indexOf(input)), Double.parseDouble(input.getText())));
		}
		
		if(estateInput <= sumOfClaims)
		{
			List<Coalition> allCoalitions = getAllCoalitions(claimers);
			List<Coalition> coalitions = getIndependentCoalitions(claimers.size(), claimers);
			
			calculateClaimerRuleAllocations(estateInput, claimers);
			// RuleCalculator.calculateCharacteristicFunction(estateInput, coalitions, claimers, isVersionB);
			long startTime = System.nanoTime();
			// RuleCalculator.calculateShapleyValues(estateInput, claimers, coalitions, isVersionB); // also calculates the characteristic function
			RuleCalculator.shap(estateInput, claimers, allCoalitions, isVersionB);
			
			RuleCalculator.calculateCharacteristicFunction(estateInput, coalitions, claimers, isVersionB);
			
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startTime;
			// System.out.println(elapsedTime);
			System.out.println("shap total time: " + ((double) elapsedTime/1_000_000_000) + " seconds");
			
			calculateCoalitionRuleAllocations(coalitions, isVersionB);
			RuleCalculator.calculateReference(referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()), coalitions);
			// coalitions.remove(coalitions.size() - 1); // TODO
			// RuleCalculator.calculateReferenceOLD(estateInput, coalitions, claimers, isVersionB);
			long startTimeRank = System.nanoTime();
			System.out.println("Starting ranking...");
			List<CoalitionWithRankingDifference> ref = RankCalculator.rankingBasedOnReference(coalitions);
			List<CoalitionWithRankingDifference> prop = RankCalculator.rankingBasedOnProportionalAllocation(coalitions);
			List<CoalitionWithRankingDifference> cea = RankCalculator.rankingBasedOnCEAAllocation(coalitions);
			List<CoalitionWithRankingDifference> cel = RankCalculator.rankingBasedOnCELAllocation(coalitions);
			List<CoalitionWithRankingDifference> aprop = RankCalculator.rankingBasedOnAdjustedProportionalAllocation(coalitions);
			List<CoalitionWithRankingDifference> shap = RankCalculator.rankingBasedOnShapleyAllocation(coalitions);
			List<CoalitionWithRankingDifference> tal = RankCalculator.rankingBasedOnTalmudAllocation(coalitions);
			List<CoalitionWithRankingDifference> mo = RankCalculator.rankingBasedOnMinimalOverlappingAllocation(coalitions);
			List<CoalitionWithRankingDifference> cli = RankCalculator.rankingBasedOnClightsAllocation(coalitions);
			List<CoalitionWithRankingDifference> eq = RankCalculator.rankingBasedOnEqualAllocation(coalitions);
			List<CoalitionWithRankingDifference> unirand = RankCalculator.rankingBasedOnUniformRandomAllocation(coalitions);

			
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

			long endTimeRank = System.nanoTime();
			long elapsedTimeRank = endTimeRank - startTimeRank;
			// System.out.println(elapsedTimeRank);
			System.out.println("ranking time: " + ((double) elapsedTimeRank/1_000_000_000) + " seconds");
			FileWriter fw;
			try
			{
				if(!isVersionB)
				{
					fw = new FileWriter("exact_versionA_ref-" + referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()) 
					+ "_numAgents-" + listOfCreditorInputs.size() + "_estate-" + Double.parseDouble(estate.getText()) + ".txt");	
					
					writeToConsoleAndFile(consoleOutput, fw, "Version A\nreference: "
							+ referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()) + "\nNumber of Creditors: "
							+ listOfCreditorInputs.size() + "\nEstate: " + Double.parseDouble(estate.getText()) + "\n");
				}
				else
				{
					fw = new FileWriter("exact_versionB_ref-" + referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()) 
					+ "_numAgents-" + listOfCreditorInputs.size() + "_estate-" + Double.parseDouble(estate.getText()) + ".txt");
					
					writeToConsoleAndFile(consoleOutput, fw, "Version B\nreference: "
							+ referenceSelectionCombo.getItemAt(referenceSelectionCombo.getSelectedIndex()) 
							+ "\nNumber of Creditors: " + listOfCreditorInputs.size() + "\nEstate: " + Double.parseDouble(estate.getText()) + "\n");
				}		

				writeToConsoleAndFile(consoleOutput, fw, "\nClaimers\n");

				for(Claimer entry : claimers)
				{
					writeToConsoleAndFile(consoleOutput, fw, entry.getId() + " Claim: " + entry.getClaim() + " PROP: " + entry.getProportionalAllocation() + " CEA: " + entry.getCEAAllocation()
					+ " CEL: " + entry.getCELAllocation()  + " SHAP: " + entry.getShapleyValue()  + " TAL: " + entry.getTalmudAllocation()
					+ " MO: " + entry.getMinimalOverlappingAllocation() + " APROP: " + entry.getAdjustedProportionalAllocation()
					+ " CLI: " + entry.getClightsAllocation() + " EQ: " + entry.getEqualAllocation() + " UNIRAND: " + entry.getUniformRandomAllocation() + "\n");
				}	

				writeToConsoleAndFile(consoleOutput, fw, "\nCoalitions\n");
				for(Coalition entry : coalitions)
				{
					writeToConsoleAndFile(consoleOutput, fw, entry.getId() + " Claim: " + entry.coalitionClaim() + " REF: " + entry.getReference() + " PROP: " + entry.getProportionalAllocation()
					+ " CEA: " + entry.getCEAAllocation()+ " CEL: " + entry.getCELAllocation() + " SHAP: " + entry.getShapleyValueAllocation()
					+ " TAL: " + entry.getTalmudAllocation() + " MO: " + entry.getMinimalOverlappingAllocation() + " APROP: " + entry.getAdjustedProportionalAllocation()
					+ " CLI: " + entry.getClightsAllocation() + " EQ: " + entry.getEqualAllocation() + " UNIRAND: " + entry.getUniformRandomAllocation() + "\n");
				}

				writeToConsoleAndFile(consoleOutput, fw, "\n");
				for(CoalitionWithRankingDifference entry : ref)
				{
					writeToConsoleAndFile(consoleOutput, fw, "REF   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + "\n");
				}

				writeToConsoleAndFile(consoleOutput, fw, "\n");
				for(CoalitionWithRankingDifference entry : prop)
				{
					writeToConsoleAndFile(consoleOutput, fw, "PROP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of PROP ranking differences : " + sumRankingDifferences(prop) + "\n");

				writeToConsoleAndFile(consoleOutput, fw, "\n");
				for(CoalitionWithRankingDifference entry : cea)
				{
					writeToConsoleAndFile(consoleOutput, fw, "CEA   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of CEA ranking differences : " + sumRankingDifferences(cea)+ "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : cel)
				{
					writeToConsoleAndFile(consoleOutput, fw, "CEL   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of CEL ranking differences : " + sumRankingDifferences(cel)+ "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : aprop)
				{
					writeToConsoleAndFile(consoleOutput, fw, "APROP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of APROP ranking differences : " + sumRankingDifferences(aprop) + "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : shap)
				{
					writeToConsoleAndFile(consoleOutput, fw, "SHAP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}	
				writeToConsoleAndFile(consoleOutput, fw, "sum of SHAP ranking differences : " + sumRankingDifferences(shap) + "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : tal)
				{
					writeToConsoleAndFile(consoleOutput, fw, "TAL   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of TAL ranking differences : " + sumRankingDifferences(tal) + "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : mo)
				{
					writeToConsoleAndFile(consoleOutput, fw, "MO   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of MO ranking differences : " + sumRankingDifferences(mo) + "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : cli)
				{
					writeToConsoleAndFile(consoleOutput, fw, "CLI   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of CLI ranking differences : " + sumRankingDifferences(cli) + "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : eq)
				{
					writeToConsoleAndFile(consoleOutput, fw, "EQ   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of EQ ranking differences : " + sumRankingDifferences(eq) + "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				for(CoalitionWithRankingDifference entry : unirand)
				{
					writeToConsoleAndFile(consoleOutput, fw, "UNIRAND   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference() + "\n");
				}
				writeToConsoleAndFile(consoleOutput, fw, "sum of UNIRAND ranking differences : " + sumRankingDifferences(unirand) + "\n");
				writeToConsoleAndFile(consoleOutput, fw, "\n");

				fw.close();				
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			throw new InvalidEstateException("Estate is greater than the sum");
		}		
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
			+ " CLI: " + entry.getClightsAllocation() + " EQ: " + entry.getEqualAllocation() + " UNIRAND: " + entry.getUniformRandomAllocation());
		}	
		System.out.println("Coalitions");
		for(Coalition entry : list2)
		{
			System.out.println(entry.getId() + " REF: " + entry.getReference() + " PROP: " + entry.getProportionalAllocation()
			+ " CEA: " + entry.getCEAAllocation()+ " CEL: " + entry.getCELAllocation() + " SHAP: " + entry.getShapleyValueAllocation()
			+ " TAL: " + entry.getTalmudAllocation() + " MO: " + entry.getMinimalOverlappingAllocation() + " APROP: " + entry.getAdjustedProportionalAllocation()
			+ " CLI: " + entry.getClightsAllocation() + " EQ: " + entry.getEqualAllocation() + " UNIRAND: " + entry.getUniformRandomAllocation());
		}
		System.out.println();
	}
	
	private static void writeToConsoleAndFile(JTextArea area, FileWriter fw, String string) throws IOException
	{
		fw.write(string);
		area.append(string);
	}
}
 