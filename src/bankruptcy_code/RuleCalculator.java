package bankruptcy_code;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class RuleCalculator
{
	public RuleCalculator()
	{
		
	}
	
	/*
	// calculates the Shapley values of the claimers and puts them in a double array
	public static double[] calculateShapleyValues(double estate, List<Claimer> claimers, List<Coalition> coalitions, boolean isVersionB)
	{
		
		List<Coalition> coalitionsClone = new ArrayList<Coalition>(); // deep copy of coalitions
		for(Coalition coalition : coalitions)
		{
			coalitionsClone.add(new Coalition(coalition.getClaimers()));
		}
		
		calculateCharacteristicFunction(estate, coalitionsClone, claimers, false);
		
		List<Claimer> claimersClone = new ArrayList<Claimer>(claimers);
		List<List<Claimer>> permutations = CustomMathOperations.generatePermutation(claimersClone);
		
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
				
				for(Coalition coalition : coalitionsClone)
				{
					if(coalition.getClaimers().equals(currentCoalition)) 
					{
						if(currentCoalition.size() == 1)
						{
							shapleys[claimers.indexOf(permutation.get(i))] += coalition.getCharacteristicFunction();
						}
						else
						{
							shapleys[claimers.indexOf(permutation.get(i))] += (coalition.getCharacteristicFunction() - previousReference);
						}
						previousReference = coalition.getCharacteristicFunction();
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
	*/ 
	
	public static ArrayList<Double> shap(double estate, List<Claimer> claimers, List<Coalition> coalitions, boolean isVersionB)
	{
		int n = claimers.size();		
		ArrayList<Double> v = calculateCharacteristicFunction(estate, coalitions, claimers, isVersionB);

		long startTime = System.nanoTime();
		double N = n;
		ArrayList<Double> w = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		w.set(0,  1/N);
		ArrayList<Integer> expo = new ArrayList<Integer>(Collections.nCopies(n, 1));
		for(int j = 1; j < n; j++)
		{
			w.set(j, w.get(j-1)*j / (n - j));
			expo.set(j, (int) Math.pow(2, j));
		}

		int s = 2 * expo.get(n-1) - 2;
		// get array v
		ArrayList<Double> shapl = new ArrayList<Double>(Collections.nCopies(n, 0.0));
		ArrayList<Boolean> a = new ArrayList<Boolean>(Collections.nCopies(n, false));
		for(int i = 0; i < n - 1; i++)
		{
			shapl.set(i, v.get(expo.get(i) - 1)/n);
		}
		for(int i = 0; i < s; i++)
		{
			// int k = 0;
			Collections.fill(a, false);
			int k = de2bi_card(i, a, n);
			for(int j = 0; j < n - 1; j++)
			{
				if(!a.get(j))
				{
					shapl.set(j, shapl.get(j) + w.get(k)*(v.get(i + expo.get(j)) - v.get(i)));
				}
			}
		}
		shapl.set(n-1, v.get(s));
		for(int j = 0; j < n - 1; j++)
		{
			shapl.set(n-1, shapl.get(n-1) - shapl.get(j));
		}
		
		for(int j = 0; j < shapl.size(); j++)
		{
			claimers.get(j).setShapleyValue(shapl.get(j)); // and update the Shapley value field for each claimer in the input list
		}
		long endTime = System.nanoTime();
		long elapsedTime = endTime - startTime;
		// System.out.println("shap time: " + ((double) elapsedTime/1_000_000_000) + " seconds");
		return shapl;
	}
	
	private static int de2bi_card(int i, ArrayList<Boolean> a, int n)
	{
		int k = 0;
		int x = 2;
		for(int c = 0; c < n-2; c++)
		{
			x += x;
		}
		int j = i + 1;
		int l = n - 1;
		while(j > 0)
		{
			if(j >= x)
			{
				a.set(l, true);
				k = k + 1;
				j -= x;
			}
			x /= 2;
			l--;
		}
		return k;
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
		while(surplus > 0.00001)
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
	
	private static void ibnEzra(double estate, List<Claimer> input)
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
							(Math.min(cloneClaimers.get(i - 1).getClaim(), estate)/n));
				}
				else
				{
					claimer.setMinimalOverlappingAllocation(claimer.getMinimalOverlappingAllocation() + 
							((Math.min(cloneClaimers.get(i - 1).getClaim(), estate) - Math.min(cloneClaimers.get(i - 2).getClaim(), estate))
									/(n - (i - 1))));
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
			ibnEzra(estate, input);
		}
		else
		{
			ibnEzra(estate, input);
			
			List<Claimer> cloneClaimers = new ArrayList<Claimer>(); // deep copy of input
			for(Claimer claimer : input)
			{
				cloneClaimers.add(new Claimer(claimer.getId(), claimer.getClaim()));
			}
			
			ibnEzra(estate, cloneClaimers);
			
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
	
	public static void equalAllocation(double estate, List<Claimer> input)
	{
		for(Claimer claimer : input)
		{
			claimer.setEqualAllocation(estate/input.size());
		}
	}
	
	public static void uniformRandomAllocation(double estate, List<Claimer> input)
	{
		double sum = 0.0;
		double[] randomDoubles = new double[input.size()];
		for(int i = 0; i < randomDoubles.length; i++) 
		{
			double rand = CustomMathOperations.generateUniformRandom(0, 1);
			randomDoubles[i] = rand;
			sum += rand;
		}
		
		double factor = 1/sum;
		for(int i = 0; i < randomDoubles.length; i++)
		{
			randomDoubles[i] = randomDoubles[i]*factor;
		}
		
		for(Claimer claimer : input)
		{
			claimer.setUniformRandomAllocation(estate*randomDoubles[input.indexOf(claimer)]);
		}
	}
	
	public static ArrayList<Double> calculateCharacteristicFunction(double estate, List<Coalition> coalitions, List<Claimer> claimers, boolean isVersionB)
	{
		double sumOfAllClaims = sum(claimers, "claims");
		ArrayList<Double> v = new ArrayList<Double>(Collections.nCopies(coalitions.size(), 0.0));
		for(Coalition entry : coalitions)
		{
			double sumCurrentCoalition = sum(entry.getClaimers(), "claims");			
			
			double maxUpperBound = estate - (sumOfAllClaims - sumCurrentCoalition);
			v.set(coalitions.indexOf(entry), Math.max(0, maxUpperBound));	
			
			if(isVersionB)
			{
				entry.setCharacteristicFunction(Math.max(0, maxUpperBound)/entry.getClaimers().size());
			}
			else
			{
				entry.setCharacteristicFunction(Math.max(0, maxUpperBound));
			}			
		}
		return v;
	}
	
	/*
	public static void calculateReferenceOLD(double estate, List<Coalition> coalitions, List<Claimer> allClaimers, boolean isVersionB)
	{
		double sumOfAllClaims = sum(allClaimers, "claims");
		
		for(Coalition entry : coalitions)
		{
			double sumCurrentCoalition = sum(entry.getClaimers(), "claims");			
			
			double maxUpperBound = estate - (sumOfAllClaims - sumCurrentCoalition);
			if(isVersionB)
			{
				entry.setReference(Math.max(0, maxUpperBound)/entry.getClaimers().size());
			}
			else
			{
				entry.setReference(Math.max(0, maxUpperBound));
			}			
		}
	}
	*/
	
	// v(s) function: used as reference point for SRD
	public static void calculateReference(String reference, List<Coalition> coalitions)
	{
		switch(reference)
		{
		case("characteristic function"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getCharacteristicFunction());
			}
		break;
		case("average of rules"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference((coalition.getProportionalAllocation() + coalition.getCEAAllocation()
				+ coalition.getCELAllocation() + coalition.getTalmudAllocation() + coalition.getAdjustedProportionalAllocation() 
				+ coalition.getClightsAllocation() + coalition.getMinimalOverlappingAllocation() + coalition.getShapleyValueAllocation())/8);
			}
		break;
		case("proportional rule"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getProportionalAllocation());
			}
		break;
		case("CEA rule"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getCEAAllocation());
			}
		break;
		case("CEL rule"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getCELAllocation());
			}
		break;
		case("Talmud rule"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getTalmudAllocation());
			}
		break;
		case("adjusted proportional rule"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getAdjustedProportionalAllocation());
			}
		break;
		case("per capita nucleolous rule"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getClightsAllocation());
			}
		break;
		case("minimal overlapping rule"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getMinimalOverlappingAllocation());
			}
		break;
		case("Shapley values"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getShapleyValueAllocation());
			}
		break;
		case("equal allocation"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getEqualAllocation());
			}
		break;
		case("uniform random allocation"):
			for(Coalition coalition : coalitions)
			{
				coalition.setReference(coalition.getUniformRandomAllocation());
			}
		break;
		}
	}
	
	public static void calculateCoalitionProportionalAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "prop");
			if(isVersionB)
			{
				entry.setProportionalAllocation(sumClaimers/(entry.getClaimers().size()));
			}
			else
			{
				entry.setProportionalAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionCEAAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "CEA");
			
			if(isVersionB)
			{
				entry.setCEAAllocation(sumClaimers/entry.getClaimers().size());
			}
			else
			{
				entry.setCEAAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionCELAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "CEL");
			
			if(isVersionB)
			{
				entry.setCELAllocation(sumClaimers/entry.getClaimers().size());	
			}
			else
			{
				entry.setCELAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionAdjustedProportionalAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "aprop");
			
			if(isVersionB)
			{
				entry.setAdjustedProportionalAllocation(sumClaimers/entry.getClaimers().size());
			}
			else
			{
				entry.setAdjustedProportionalAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionShapleyAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "shap");
			
			if(isVersionB)
			{
				entry.setShapleyValueAllocation(sumClaimers/entry.getClaimers().size());
			}
			else
			{
				entry.setShapleyValueAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionTalmudAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "talmud");
			
			if(isVersionB)
			{
				entry.setTalmudAllocation(sumClaimers/entry.getClaimers().size());
			}
			else
			{
				entry.setTalmudAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionMinimalOverlappingAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "MO");
			
			if(isVersionB)
			{
				entry.setMinimalOverlappingAllocation(sumClaimers/entry.getClaimers().size());
			}
			else
			{
				entry.setMinimalOverlappingAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionClightsAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "cli");
			
			if(isVersionB)
			{
				entry.setClightsAllocation(sumClaimers/entry.getClaimers().size());
			}
			else
			{
				entry.setClightsAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionEqualAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "eq");
			
			if(isVersionB)
			{
				entry.setEqualAllocation(sumClaimers/entry.getClaimers().size());
			}
			else
			{
				entry.setEqualAllocation(sumClaimers);
			}
		}
	}
	
	public static void calculateCoalitionUniformRandomAllocation(List<Coalition> coalitions, boolean isVersionB)
	{
		for(Coalition entry : coalitions)
		{
			double sumClaimers = sum(entry.getClaimers(), "unirand");
			
			if(isVersionB)
			{
				entry.setUniformRandomAllocation(sumClaimers/entry.getClaimers().size());
			}
			else			
			{
				entry.setUniformRandomAllocation(sumClaimers);
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
			case "eq":
				for(Claimer element : list)
				{
					sumOfElements += element.getEqualAllocation();
				}
				break;
			case "unirand":
				for(Claimer element : list)
				{
					sumOfElements += element.getUniformRandomAllocation();
				}
				break;
		}
				
		return sumOfElements;
	}
		
}
