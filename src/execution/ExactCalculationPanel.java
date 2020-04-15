package execution;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
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
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import bankruptcy_code.Claimer;
import bankruptcy_code.Coalition;
import bankruptcy_code.CoalitionWithRankingDifference;
import bankruptcy_code.CustomMathOperations;
import bankruptcy_code.RankCalculator;
import bankruptcy_code.RuleCalculator;
import exceptions.InvalidEstateException;

@SuppressWarnings("serial")
public class ExactCalculationPanel extends JPanel 
{
	private JTextField estate;
	
	private JLabel estateLabel;
	private JLabel comboLabel;
	
	private JButton runButton;
	
	private JPanel canvasPanel;
	private JPanel comboPanel;
	private JPanel inputsPanel;
	private JPanel buttonsPanel;
	
	private JComboBox<Integer> numCreditorsCombo;
	
	private List<JTextField> listOfCreditorInputs;
	
	private JPanel consolePanel;
	private JTextArea consoleOutput;
	
	public ExactCalculationPanel()
	{
		this.setLayout(new GridLayout());

		comboPanel = new JPanel();
		comboPanel.setBackground(Color.WHITE);
		Integer[] creditorNumber = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		numCreditorsCombo = new JComboBox<Integer>(creditorNumber);
		numCreditorsCombo.setFont(new Font("Serif", Font.PLAIN, 18));
		numCreditorsCombo.setBackground(Color.WHITE);
		numCreditorsCombo.setSelectedIndex(0);
		numCreditorsCombo.setBounds(50, 100,90,20);	
		
		comboLabel = new JLabel("Number of creditors: ");
		comboLabel.setFont(new Font("Serif", Font.PLAIN, 21));
		
		comboPanel.add(comboLabel);
		comboPanel.add(numCreditorsCombo);
		
		listOfCreditorInputs = new ArrayList<JTextField>();
		
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
		consoleOutput = new JTextArea(30, 60);
		consoleOutput.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(consoleOutput);		
						
		PrintStream printStream = new PrintStream(new CustomOutputStream(consoleOutput));
					
		// keeps reference of standard output stream
		// standardOut = System.out;
				         
		// re-assigns standard output stream and error output stream
		System.setOut(printStream);
		System.setErr(printStream);
						
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
				Thread thread = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							calculateExactSRD();							
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
				JOptionPane.showMessageDialog(new JFrame(), "Number of creditors must be greater than 0", "ERROR", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void calculateExactSRD() throws InvalidEstateException
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
			List<LinkedList<Claimer>> powerSet = new LinkedList<LinkedList<Claimer>>();
			
			for(int i = 1; i <= claimers.size(); i++)
			{
				powerSet.addAll((Collection<? extends LinkedList<Claimer>>) CustomMathOperations.combination(claimers, i));
			}
			
			List<Coalition> coalitions = new ArrayList<Coalition>(); // master list of coalitions
			
			for(LinkedList<Claimer> entry : powerSet)
			{
				coalitions.add(new Coalition(entry));
			}
			
			calculateClaimerRuleAllocations(estateInput, claimers);
			RuleCalculator.calculateReference(estateInput, coalitions, claimers);		
			RuleCalculator.calculateShapleyValues(claimers, coalitions); // needs to be after calculateReference() because it needs the reference values for calculation	
			
			calculateCoalitionRuleAllocations(coalitions);
			
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
			
			for(CoalitionWithRankingDifference entry : aprop)
			{
				System.out.println("APROP   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
			}
			System.out.println("sum of APROP ranking differences : " + sumRankingDifferences(aprop));
			
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
			
			for(CoalitionWithRankingDifference entry : cli)
			{
				System.out.println("CLI   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
			}
			System.out.println("sum of CLI ranking differences : " + sumRankingDifferences(cli));
			
			for(CoalitionWithRankingDifference entry : eq)
			{
				System.out.println("EQ   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
			}
			System.out.println("sum of EQ ranking differences : " + sumRankingDifferences(eq));
			
			for(CoalitionWithRankingDifference entry : unirand)
			{
				System.out.println("UNIRAND   coalition: " + entry.getCoalition().getId() + " rank: " + entry.getRank() + " diff: " + entry.getRankingDifference());
			}
			System.out.println("sum of UNIRAND ranking differences : " + sumRankingDifferences(unirand));
		}
		else
		{
			throw new InvalidEstateException("Estate is greater than the sum");
		}		
	}
	
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
		RuleCalculator.equalAllocation(estate, claimers);
		RuleCalculator.uniformRandomAllocation(estate, claimers);
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
		RuleCalculator.calculateCoalitionEqualAllocation(coalitions);
		RuleCalculator.calculateCoalitionUniformRandomAllocation(coalitions);
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
		  return button;
	}
}
 