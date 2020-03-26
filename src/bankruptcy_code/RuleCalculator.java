package bankruptcy_code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RuleCalculator
{
	public RuleCalculator()
	{
		
	}
	
	// calculates the Shapley values of the claimers and puts them in a double array
	public static double[] calculateShapleyValues(List<Claimer> claimers, List<Coalition> coalitions)
	{
		List<Claimer> claimersClone = new ArrayList<Claimer>(claimers);
		List<List<Claimer>> permutations = generatePermutation(claimersClone);
		
		double[] shapleys = new double[claimers.size()];
		List<Claimer> currentCoalition = new ArrayList<Claimer>();
		double previousReference = 0;
		
		for(List<Claimer> permutation : permutations)
		{
			for(int i = 0; i < claimers.size(); i++)			
			{		
				currentCoalition.add(permutation.get(i)); // needs to be sorted so we can match it with the list of coalitions
				Collections.sort(currentCoalition, new Comparator<Claimer>() 
				{
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
			claimers.get(j).setShapleyValue(shapleys[j]/permutations.size()); // and update the Shapley value field for each claimer in the input list
			shapleys[j] = shapleys[j]/permutations.size();
		}
		
		return shapleys;
	}
	
	// returns the amount obligated to a given agent based on parameters
	public static void proportionalRuleAllocation(double estate, List<Claimer> input)
	{
		double sumOfClaims = sum(input, "claims");
		
		for(Claimer claim : input)
		{
			claim.setProportionalAllocation((claim.getClaim()*estate)/sumOfClaims);
		}
	}
	
	// updates the CEAAllocation value for each agent
	public static void CEARuleAllocation(double estate, List<Claimer> input)
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
				claimer.setCEAAllocation(claimer.getClaim());
				remainingAmount += (equalAmount-claimer.getClaim());
			}
			
			if (claimer.getClaim() >= equalAmount)
			{
				claimer.setCEAAllocation(equalAmount);
			}
			
			equalAmount = remainingAmount/iterator;
		}
	}
	
	// updates the CELAllocation value for each agent
	public static void CELRuleAllocation(double estate, List<Claimer> allClaimers)
	{
		double sumOfClaims = sum(allClaimers, "claims");
		double remainingLoss = sumOfClaims - estate;
		double equalLoss = (sumOfClaims - estate)/allClaimers.size();
		int iterator = allClaimers.size();
		
		for(Claimer claimer : allClaimers)
		{
			iterator--;
			
			if(claimer.getClaim() < equalLoss)
			{
				claimer.setCELAllocation(0.0);
				remainingLoss -= claimer.getClaim();
			}
			else
			{
				claimer.setCELAllocation(claimer.getClaim() - equalLoss);
				remainingLoss -= equalLoss;
			}
			
			equalLoss = remainingLoss/iterator;
		}
	}
	
	// calculate talmud allocation
	public static void TalmudRuleAllocation(double estate, List<Claimer> allClaimers)
	{
		double halvesSum = 0.0;
		List<Claimer> halvedClaimClaimers = new ArrayList<Claimer>(); // deep copy of allClaimers with adjusted claims (halved)
		for(Claimer claimer : allClaimers)
		{
			halvesSum += claimer.getClaim() * 0.5;
			halvedClaimClaimers.add(new Claimer(claimer.getId(), claimer.getClaim()*0.5));
		}

		if(estate <= halvesSum)
		{
			CEARuleAllocation(estate, halvedClaimClaimers);
			for(Claimer claimer : allClaimers)
			{
				// assumes that the lists are in the same order (should be in this context)
				claimer.setTalmudAllocation(halvedClaimClaimers.get(allClaimers.lastIndexOf(claimer)).getCEAAllocation());
			}
		}
		else if(estate >= halvesSum)
		{
			CEARuleAllocation(sum(allClaimers, "claims") - estate, halvedClaimClaimers);
			for(Claimer claimer : allClaimers)
			{
				// assumes that the lists are in the same order (should be in this context)
				claimer.setTalmudAllocation(claimer.getClaim() - halvedClaimClaimers.get(allClaimers.lastIndexOf(claimer)).getCEAAllocation());
			}
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
	
	public static void calculateCoalitionProportionalAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "prop");
			
			entry.setProportionalAllocation(sumClaimers);
		}
	}
	
	public static void calculateCoalitionCEAAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "CEA");
			
			entry.setCEAAllocation(sumClaimers);
		}
	}
	
	public static void calculateCoalitionCELAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "CEL");
			
			entry.setCELAllocation(sumClaimers);
		}
	}
	
	public static void calculateCoalitionShapleyAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "shap");
			
			entry.setShapleyValueAllocation(sumClaimers);
		}
	}
	
	public static void calculateCoalitionTalmudAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "talmud");
			
			entry.setTalmudAllocation(sumClaimers);
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
				entry.setAverageCEAVariation(entry.getAverageCEAVariation()+((0-entry.getAverageCEAVariation())/iteration));
			}
			else
			{
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
				entry.setAverageCELVariation(entry.getAverageCELVariation()+((0-entry.getAverageCELVariation())/iteration));
			}
			else
			{
				entry.setAverageCELVariation(entry.getAverageCELVariation()+((CELProfit-entry.getAverageCELVariation())/iteration));
			}	
		}
	}
	
	public static void calculateShapleyVariance(List<Claimer> claimers, List<Coalition> coalitions, int iteration)
	{
		calculateShapleyValues(claimers, coalitions); // first calculate the shapley values for each claimer
		
		// update the shapley value variance for each coalition
		for(Coalition entry : coalitions)
		{
			double sumShapleyClaimers = sum(entry.getClaimers(), "shap");
			
			double ShapleyProfit = sumShapleyClaimers - entry.getReference();
			if(ShapleyProfit < 0)
			{
				entry.setAverageShapleyValueVariation(entry.getAverageShapleyValueVariation()+((0-entry.getAverageShapleyValueVariation())/iteration));
			}
			else
			{
				entry.setAverageShapleyValueVariation(entry.getAverageShapleyValueVariation()+((ShapleyProfit-entry.getAverageShapleyValueVariation())/iteration));
			}	
		}
	}
	
	// finds the sum of the Claims in a given coalition
	public static double sum(List<Claimer> list, String identifier)
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
					sumOfElements += element.getCEAAllocation();
				}
				break;
			case "CEL":
				for(Claimer element : list)
				{
					sumOfElements += element.getCELAllocation();
				}
				break;	
			case "shap":
				for(Claimer element : list)
				{
					sumOfElements += element.getShapleyValue();
				}
			case "talmud":
				for(Claimer element : list)
				{
					sumOfElements += element.getTalmudAllocation();
				}
		}
				
		return sumOfElements;
	}
	
	
	// generate all permutations of the input list
	private static <E> List<List<E>> generatePermutation(List<E> original) 
	{
	     if (original.isEmpty()) 
	     {
	       List<List<E>> result = new ArrayList<>(); 
	       result.add(new ArrayList<>()); 
	       return result; 
	     }
	     E firstElement = original.remove(0);
	     List<List<E>> returnValue = new ArrayList<>();
	     List<List<E>> permutations = generatePermutation(original);
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
		
}
