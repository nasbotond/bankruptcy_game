package execution;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import bankruptcy_code.Claimer;
import bankruptcy_code.Coalition;
import bankruptcy_code.CoalitionWithRankingDifference;
import bankruptcy_code.RuleCalculator;

@SuppressWarnings("serial")
public abstract class CalculationPanel extends JPanel
{
	public static double sumRankingDifferences(List<CoalitionWithRankingDifference> input)
	{
		double sum = 0.0;
		for(CoalitionWithRankingDifference coalition : input)
		{
			sum += coalition.getRankingDifference();
		}
		return sum;
	}
	
	/**
	 * Calculate every rule allocation for each claimer.
	 * @param Estate.
	 * @param List of claimers.
	 */
	public static void calculateClaimerRuleAllocations(double estate, List<Claimer> claimers)
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
	
	/**
	 * Calculate every rule allocation for each coalition.
	 * @param List of coalitions.
	 */
	public static void calculateCoalitionRuleAllocations(List<Coalition> coalitions, boolean isVersionB)
	{
		RuleCalculator.calculateCoalitionProportionalAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionCEAAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionCELAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionAdjustedProportionalAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionShapleyAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionTalmudAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionMinimalOverlappingAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionClightsAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionEqualAllocation(coalitions, isVersionB);
		RuleCalculator.calculateCoalitionUniformRandomAllocation(coalitions, isVersionB);
	}
	
	public static List<Coalition> orderCoalitions(List<Coalition> coalitions)
	{
		HashMap<Coalition, Integer> coalitionInt = new HashMap<Coalition, Integer>();
		
		for(Coalition coalition : coalitions)
		{
			int sum = 0;
			for(Claimer claimer : coalition.getClaimers())
			{
				sum += (int) Math.pow(2, ((claimer.getId())-97));
			}
			coalitionInt.put(coalition, sum);
		}
		Map<Coalition, Integer> sorted = sortByValue(coalitionInt);
		List<Coalition> sortedList = new ArrayList<Coalition>();
		for(Map.Entry<Coalition, Integer> entry : sorted.entrySet())
		{
			sortedList.add(entry.getKey());
		}
		return sortedList;
	}
	
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) 
    {
        List<Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
    
    public static JButton createSimpleButton(String text) 
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
