package bankruptcy_code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
	
	/*
	// updates the CEAAllocation value for each agent
	public static void CEARuleAllocation(double estate, List<Claimer> input)
	{
		double equalAmount = estate/input.size();
		double remainingAmount = estate;
		int iterator = input.size();
		
		for(Claimer claimer : input)
		{
			// remainingAmount -= equalAmount;
			iterator--;
			
			if(claimer.getClaim() < equalAmount)
			{
				claimer.setCEAAllocation(claimer.getClaim());
				// remainingAmount += (equalAmount-claimer.getClaim());
				remainingAmount -= claimer.getClaim();
			}
			
			if (claimer.getClaim() >= equalAmount)
			{
				claimer.setCEAAllocation(equalAmount);
				remainingAmount -= equalAmount;
			}
			
			equalAmount = remainingAmount/iterator;
		}
	}
	*/
	
	public static void CEARuleAllocation(double estate, List<Claimer> input)
	{
		List<Claimer> doneClaimers = new ArrayList<Claimer>(); // deep copy of input
		for(Claimer claimer : input)
		{
			doneClaimers.add(new Claimer(claimer.getId(), claimer.getClaim()));
		}
		
		double surplus = estate;
		double equal = surplus/doneClaimers.size();
		while(surplus > 0)
		{
			for(Iterator<Claimer> iterator = doneClaimers.iterator(); iterator.hasNext();)
			{
				Claimer claimer = iterator.next();
				if(claimer.getCEAAllocation() + equal >= claimer.getClaim()) // needed to dynamically remove from the list
				{
					surplus -= claimer.getClaim() - claimer.getCEAAllocation();
					// entry.setValue(false);
					for(Claimer inputClaimer : input)
					{
						if(inputClaimer.getId() == claimer.getId())
						{
							inputClaimer.setCEAAllocation(claimer.getClaim());
						}
					}
					iterator.remove();
				}
				else
				{
					claimer.setCEAAllocation(claimer.getCEAAllocation() + equal);
					for(Claimer inputClaimer : input)
					{
						if(inputClaimer.getId() == claimer.getId())
						{
							inputClaimer.setCEAAllocation(claimer.getCEAAllocation());
						}
					}
					surplus -= equal;
				}				
			}
			equal = surplus/doneClaimers.size();
		}
	}
	
	/*
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
	*/
	
	
	public static void CELRuleAllocation(double estate, List<Claimer> input)
	{
		List<Claimer> doneClaimers = new ArrayList<Claimer>(); // deep copy of input
		for(Claimer claimer : input)
		{
			doneClaimers.add(new Claimer(claimer.getId(), claimer.getClaim()));
		}
		
		for(Claimer claimer : doneClaimers)
		{
			claimer.setCELAllocation(claimer.getClaim());
		}
		
		double sumOfClaims = sum(doneClaimers, "claims");
		double deficit = sumOfClaims - estate;
		double equal = deficit/doneClaimers.size();
		while(deficit > 0.00001)
		{
			for(Iterator<Claimer> iterator = doneClaimers.iterator(); iterator.hasNext();)
			{
				Claimer claimer = iterator.next();
				if(claimer.getCELAllocation() <= equal) // needed to dynamically remove from the list
				{
					deficit -= claimer.getCELAllocation();
					// entry.setValue(false);
					for(Claimer inputClaimer : input)
					{
						if(inputClaimer.getId() == claimer.getId())
						{
							inputClaimer.setCELAllocation(0);
						}
					}
					iterator.remove();
				}
				else
				{
					claimer.setCELAllocation(claimer.getCELAllocation() - equal);
					for(Claimer inputClaimer : input)
					{
						if(inputClaimer.getId() == claimer.getId())
						{
							inputClaimer.setCELAllocation(claimer.getCELAllocation());
						}
					}
					deficit -= equal;
				}				
			}
			equal = deficit/doneClaimers.size();
		}
	}
	
	public static void adjustedProportionalAllocation(double estate, List<Claimer> input)
	{
		double difference = estate - sum(input, "claims");
		double sumOfMinimalRights = 0.0;
		for(Claimer claimer : input)
		{
			claimer.setAdjustedProportionalAllocation(Math.max(0, difference + claimer.getClaim()));
			sumOfMinimalRights += Math.max(0, difference + claimer.getClaim());
		}
		double sumAdjustedClaims = 0.0;
		for(Claimer claimer : input)
		{
			sumAdjustedClaims += Math.min(claimer.getClaim() - claimer.getAdjustedProportionalAllocation(), estate - sumOfMinimalRights);
		}
		
		for(Claimer claim : input)
		{
			claim.setAdjustedProportionalAllocation(claim.getAdjustedProportionalAllocation()
					+ (Math.min(claim.getClaim() - claim.getAdjustedProportionalAllocation(), estate - sumOfMinimalRights)
							*(estate - sumOfMinimalRights))/sumAdjustedClaims);
		}		
	}
	
	// calculate talmud allocation
	public static void talmudRuleAllocation(double estate, List<Claimer> allClaimers)
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
	
	private static void ibnEzra(List<Claimer> input)
	{
		List<Claimer> cloneClaimers = new ArrayList<Claimer>(); // deep copy of input
		for(Claimer claimer : input)
		{
			cloneClaimers.add(new Claimer(claimer.getId(), claimer.getClaim()));
		}
		
		// sort lowest claim to highest claim
		Collections.sort(cloneClaimers, new Comparator<Claimer>(){
		     public int compare(Claimer o1, Claimer o2){
		         if(o1.getClaim() == o2.getClaim())
		             return 0;
		         return o1.getClaim() < o2.getClaim() ? -1 : 1;
		     }
		});
		
		int n = input.size();
		
		for(Claimer claimer : cloneClaimers)
		{
			for(int i = cloneClaimers.indexOf(claimer) + 1; i > 0; i--)
			{
				if(i == 1)
				{
					claimer.setMinimalOverlappingAllocation(claimer.getMinimalOverlappingAllocation() + 
							(cloneClaimers.get(i - 1).getClaim()/n));
				}
				else
				{
					claimer.setMinimalOverlappingAllocation(claimer.getMinimalOverlappingAllocation() + 
							((cloneClaimers.get(i - 1).getClaim() - cloneClaimers.get(i - 2).getClaim())/(n - (i - 1))));
				}
			}
		}
		
		for(Claimer claimer : input)
		{
			for(Claimer cloneClaimer : cloneClaimers)
			{
				if(claimer.getId() == cloneClaimer.getId())
				{
					claimer.setMinimalOverlappingAllocation(cloneClaimer.getMinimalOverlappingAllocation());
				}
			}
		}
	}
	
	// calculate MO rule 
	public static void minimalOverlappingRuleAllocation(double estate, List<Claimer> input)
	{
		// get maximum claim
		double maxClaim = 0.0;
		for(Claimer claimer : input)
		{
			if(claimer.getClaim() > maxClaim)
			{
				maxClaim = claimer.getClaim();
			}
		}
		
		if(maxClaim >= estate)
		{
			ibnEzra(input);
		}
		else
		{
			ibnEzra(input);
			
			List<Claimer> cloneClaimers = new ArrayList<Claimer>(); // deep copy of input
			for(Claimer claimer : input)
			{
				cloneClaimers.add(new Claimer(claimer.getId(), claimer.getClaim()));
			}
			
			ibnEzra(cloneClaimers);
			
			for(Claimer claimer : cloneClaimers)
			{
				claimer.setClaim(claimer.getClaim() - claimer.getMinimalOverlappingAllocation());
			}		
			
			CELRuleAllocation(estate - maxClaim, cloneClaimers);
			
			for(Claimer claimer : input)
			{
				claimer.setMinimalOverlappingAllocation(claimer.getMinimalOverlappingAllocation() + cloneClaimers.get(input.indexOf(claimer)).getCELAllocation());
			}
		}
	}
	
	public static void clightsRuleAllocation(double estate, List<Claimer> input)
	{		
		List<Claimer> cloneClaimers = new ArrayList<Claimer>(); // deep copy of input
		for(Claimer claimer : input)
		{
			cloneClaimers.add(new Claimer(claimer.getId(), claimer.getClaim()));
		}
		
		// sort lowest claim to highest claim
		Collections.sort(cloneClaimers, new Comparator<Claimer>(){
		     public int compare(Claimer o1, Claimer o2){
		         if(o1.getClaim() == o2.getClaim())
		             return 0;
		         return o1.getClaim() < o2.getClaim() ? -1 : 1;
		     }
		});
		
		double sumOfClights = 0.0;
		for(Claimer c : cloneClaimers)
		{
			sumOfClights += calculateClightVector(cloneClaimers, cloneClaimers.indexOf(c) + 1);
		}
		if(sumOfClights >= estate)
		{
			double[] clights = new double[cloneClaimers.size()];
			for(Claimer claimer : cloneClaimers)
			{
				clights[cloneClaimers.indexOf(claimer)] = calculateClightVector(cloneClaimers, cloneClaimers.indexOf(claimer) + 1);
			}
			
			for(Claimer claimer : cloneClaimers)
			{
				claimer.setClaim(clights[cloneClaimers.indexOf(claimer)]);
			}
			CEARuleAllocation(estate, cloneClaimers);
			
			for(Claimer originals : input)
			{
				for(Claimer clones : cloneClaimers)
				{
					if(originals.getId() == clones.getId())
					{
						originals.setClightsAllocation(clones.getCEAAllocation());
					}
				}
			}
		}
		else
		{
			double[] clights = new double[cloneClaimers.size()];
			for(Claimer claimer : cloneClaimers)
			{
				clights[cloneClaimers.indexOf(claimer)] = calculateClightVector(cloneClaimers, cloneClaimers.indexOf(claimer) + 1);			
			}
			for(Claimer claimer : cloneClaimers)
			{
				claimer.setClaim(claimer.getClaim() - clights[cloneClaimers.indexOf(claimer)]);
			}
			CELRuleAllocation(estate - sumOfClights, cloneClaimers);
			
			for(Claimer originals : input)
			{
				for(Claimer clones : cloneClaimers)
				{
					if(originals.getId() == clones.getId())
					{
						originals.setClightsAllocation(clights[cloneClaimers.indexOf(clones)] + clones.getCELAllocation());
					}
				}
			}
		}
		
	}
	
	public static double calculateClightVector(List<Claimer> input, int vectorNum)
	{
		double n = input.size();
		double[] vector = new double[vectorNum];
		for(int j = vectorNum; j > 0; j--)
		{
			double sum = 0.0;
			if(j > 1)
			{
				for(int m = j - 1; m > 0; m--)
				{
					sum = sum + calculateClightVector(input, m);
				}
				vector[j-1] = (1/(n+((double)j)-1)) * (((double)j)*input.get(vectorNum-1).getClaim() - ((n - 1) * sum));
			}
			else
			{				
				vector[j-1] = (1/(n+((double)j)-1)) * (((double)j)*input.get(vectorNum-1).getClaim());
			}
		}
		
		double max = 0.0;
		for(int i = 0; i < vector.length; i++)
		{
			if(vector[i] > max)
			{
				max = vector[i];
			}
		}
		return max;
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
	
	public static void calculateCoalitionAdjustedProportionalAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "aprop");
			
			entry.setAdjustedProportionalAllocation(sumClaimers);
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
	
	public static void calculateCoalitionMinimalOverlappingAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "MO");
			
			entry.setMinimalOverlappingAllocation(sumClaimers);
		}
	}
	
	public static void calculateCoalitionClightsAllocation(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "cli");
			
			entry.setClightsAllocation(sumClaimers);
		}
	}
	/*
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
	*/
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
				break;
			case "talmud":
				for(Claimer element : list)
				{
					sumOfElements += element.getTalmudAllocation();
				}
				break;
			case "MO":
				for(Claimer element : list)
				{
					sumOfElements += element.getMinimalOverlappingAllocation();
				}
				break;
			case "aprop":
				for(Claimer element : list)
				{
					sumOfElements += element.getAdjustedProportionalAllocation();
				}
			break;
			case "cli":
				for(Claimer element : list)
				{
					sumOfElements += element.getClightsAllocation();
				}
			break;
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
