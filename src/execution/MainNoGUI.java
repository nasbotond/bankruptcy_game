package execution;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import calculations.CustomMathOperations;
import calculations.RankCalculator;
import calculations.RuleCalculator;
import data_elements.Claimer;
import data_elements.Coalition;
import data_elements.CoalitionWithRankingDifference;

@SuppressWarnings("serial")
public class MainNoGUI extends CalculationPanel
{
	static String propData;
	static String ceaData;
	static String celData;
	static String apropData;
	static String shapData;
	static String talData;
	static String moData;
	static String cliData;
	static String eqData;
	static String unirandData;

	public static void main(String[] args) 
	{
		long startTime = System.nanoTime();
		
		final int numClaimers = Integer.parseInt(args[0]);
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
		
		List<Claimer> claimers = new ArrayList<Claimer>(); // master list of claimers
		
		for(int i = 0; i < numClaimers; i++)
		{
			claimers.add(new Claimer((char)(97 + i), CustomMathOperations.generateUniformRandom(0, 1000)));
		}
		
		List<Coalition> allCoalitions = getAllCoalitions(claimers);
		List<Coalition> coalitions = getIndependentCoalitions(numClaimers, claimers);
		
		// double maximumSRD = (((coalitions.size()-1)*(coalitions.size() - 1)) / 2);
		double maximumSRD = (((coalitions.size())*(coalitions.size())) / 2);
		
		estate = CustomMathOperations.generateUniformRandom((RuleCalculator.sum(claimers, "claims") * Double.parseDouble(args[1])),
							(RuleCalculator.sum(claimers, "claims") * Double.parseDouble(args[2])));
		
		calculateClaimerRuleAllocations(estate, claimers);
		RuleCalculator.shap(estate, claimers, allCoalitions, true);
		
		RuleCalculator.calculateCharacteristicFunction(estate, coalitions, claimers, true); // needs to be called after shapley (because shapley calls same method as version A every time)
		
		calculateCoalitionRuleAllocations(coalitions, true);	
		RuleCalculator.calculateReference("characteristic function", coalitions);
		
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

		while(iterations < Integer.parseInt(args[3]))
		{		
			for(int i = 0; i < numClaimers; i++)
			{
				claimers.get(i).setClaim(CustomMathOperations.generateUniformRandom(0, 1000));
			}

			// estate = generateUniformRandom(RuleCalculator.sum(claimers, "claims")/2.0, RuleCalculator.sum(claimers, "claims"));
			estate = CustomMathOperations.generateUniformRandom((RuleCalculator.sum(claimers, "claims") * Double.parseDouble(args[1])),
					(RuleCalculator.sum(claimers, "claims") * Double.parseDouble(args[2])));

			iterations = iterations + 1;			
			
			updateCoalitionClaimers(allCoalitions, claimers); // update all coalitions with the new claims
			
			coalitions = getIndependentCoalitions(numClaimers, claimers);
			
			calculateClaimerRuleAllocations(estate, claimers);
			RuleCalculator.shap(estate, claimers, allCoalitions, true);
			
			RuleCalculator.calculateCharacteristicFunction(estate, coalitions, claimers, true); // needs to be called after shapley (because shapley calls same method as version A every time)
			
			calculateCoalitionRuleAllocations(coalitions, true);	
			RuleCalculator.calculateReference("characteristic function", coalitions);
			
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
				propData = "Relative Average Proportional Rule SRD: " + averageSRD.get("prop")/maximumSRD;
				ceaData = "Relative Average CEA Rule SRD: " + averageSRD.get("cea")/maximumSRD;
				celData = "Relative Average CEL Rule SRD: " + averageSRD.get("cel")/maximumSRD;
				apropData = "Relative Average Adjusted Proportional Rule SRD: " + averageSRD.get("aprop")/maximumSRD;
				shapData = "Relative Average Shapley Value SRD: " + averageSRD.get("shap")/maximumSRD;
				talData = "Relative Average Talmud Rule SRD: " + averageSRD.get("tal")/maximumSRD;
				moData = "Relative Average Minimal Overlap Rule SRD: " + averageSRD.get("mo")/maximumSRD;
				cliData = "Relative Average Per Capita Nucleolous Rule SRD: " + averageSRD.get("cli")/maximumSRD;				
				eqData = "Control 1: Relative Average Equal Allocation SRD: " + averageSRD.get("eq")/maximumSRD;
				unirandData = "Control 2: Relative Average Uniform Random Allocation SRD: " + averageSRD.get("unirand")/maximumSRD;
			}
		}
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		// System.out.println(elapsedTime);
		System.out.println("running time: " + ((double) elapsedTime/1_000_000_000) + " seconds");
		try 
		{
			saveToFile(true, "characteristic function", Integer.parseInt(args[0]), 
					Double.parseDouble(args[1]), Double.parseDouble(args[2]),  iterations);
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		// stop = false;

	}
	
	/**
	 * Calculate the sum of ranking differences for each CoalitionWithRankingDifference.
	 * @param List of CoalitionWithRankingDifference.
	 * @return Sum as a double.
	 */
	/*
	private static double sumRankingDifferences(List<CoalitionWithRankingDifference> input)
	{
		double sum = 0.0;
		for(CoalitionWithRankingDifference coalition : input)
		{
			sum += coalition.getRankingDifference();
		}
		return sum;
	}
	*/
	/**
	 * Calculate every rule allocation for each claimer.
	 * @param Estate.
	 * @param List of claimers.
	 */
	/*
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
	*/
	/**
	 * Calculate every rule allocation for each coalition.
	 * @param List of coalitions.
	 */
	/*
	private static void calculateCoalitionRuleAllocations(List<Coalition> coalitions, boolean isVersionB)
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
	*/
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
	
	private static void saveToFile(boolean versionB, String reference, int numAgents, double min, double max, int iterations) throws IOException
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
		
		fw.write(propData + "\n");
		fw.write(ceaData + "\n");
		fw.write(celData + "\n");
		fw.write(apropData + "\n");
		fw.write(shapData + "\n");
		fw.write(talData + "\n");
		fw.write(moData + "\n");
		fw.write(cliData + "\n");
		fw.write(eqData + "\n");
		fw.write(unirandData + "\n");
	 
		fw.close();
	}
	/*
	private static List<Coalition> orderCoalitions(List<Coalition> coalitions)
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
    */
}
