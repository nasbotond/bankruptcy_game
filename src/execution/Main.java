package execution;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import bankruptcy_code.Claimer;
import bankruptcy_code.Coalition;

public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) 
	{
		
		EventQueue.invokeLater(() -> {
			UserInterfaceFrame frame = new UserInterfaceFrame();
			
			frame.setVisible(true);
		});
		
		
		List<Claimer> input = new ArrayList<Claimer>();
		
		input.add(new Claimer('a', 100));
		input.add(new Claimer('b', 180));
		input.add(new Claimer('c', 300));
		input.add(new Claimer('d', 400));
		
		List<LinkedList<Claimer>> powerSet = new LinkedList<LinkedList<Claimer>>();
		
		for(int i = 1; i <= input.size(); i++)
		{
			powerSet.addAll((Collection<? extends LinkedList<Claimer>>) combination(input, i));
		}
		
		List<Coalition> coalitions = new ArrayList<Coalition>();
		
		for(LinkedList<Claimer> entry : powerSet)
		{
			coalitions.add(new Coalition(entry));
		}
		
		System.out.println(coalitions);
		
		// proportional rule (claimers)
		double estate = 850.0;
		double sum = 0.0;
		
		for(Claimer entry : input)
		{
			sum += entry.getClaim();
		}
		
		for(Claimer entry : input)
		{
			entry.setProportionalAllocation(proportionalRuleClaimers(estate, sum, entry.getClaim()));
		}
		
		CEARuleClaimers(estate, input);
		
		for(Claimer entry : input)
		{
			System.out.println(entry.getId() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getConstrainedEAAllocation());
		}
		
		calculateReference(estate, coalitions, sum);
		calculateProportional(coalitions);
		calculateCEA(coalitions);
		
		for(Coalition entry : coalitions)
		{
			System.out.println(entry.getId() + " ref: " + entry.getReference() + " prop: " + entry.getProportionalAllocation() + " CEA: " + entry.getConstrainedEAAllocation());
		}
		
	}
	
	public static <T> List<List<T>> combination(List<T> values, int size)
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
	
	public static double proportionalRuleClaimers(double estate, double sumOfClaims, double claim)
	{
		return (claim*estate)/sumOfClaims;
	}
	
	public static void CEARuleClaimers(double estate, List<Claimer> input)
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
				claimer.setConstrainedEAAllocation(claimer.getClaim());
				remainingAmount += (equalAmount-claimer.getClaim());
			}
			
			if (claimer.getClaim() >= equalAmount)
			{
				claimer.setConstrainedEAAllocation(equalAmount);
			}
			
			equalAmount = remainingAmount/iterator;
		}
	}
	
	public static void calculateReference(double estate, List<Coalition> coalitions, double sum)
	{
		for(Coalition entry : coalitions)
		{
			double sumCurrentCoalition = 0;
			for(Claimer claimer : entry.getClaimers())
			{
				sumCurrentCoalition += claimer.getClaim();
			}
			
			double maxUpperBound = estate - (sum - sumCurrentCoalition);
			entry.setReference(Math.max(0, maxUpperBound));
		}
	}
	
	// calculateReference must be called before this method to ensure we have the correct reference values
	public static void calculateProportional(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumPropClaimers = 0;
			for(Claimer claimer : entry.getClaimers())
			{
				sumPropClaimers += claimer.getProportionalAllocation();
			}
			
			entry.setProportionalAllocation(sumPropClaimers - entry.getReference());		
		}
	}
	
	public static void calculateCEA(List<Coalition> coalitions)
	{
		for(Coalition entry : coalitions)
		{
			double sumCEAClaimers = 0;
			for(Claimer claimer : entry.getClaimers())
			{
				sumCEAClaimers += claimer.getConstrainedEAAllocation();
			}
			
			double cea = sumCEAClaimers - entry.getReference();
			if(cea < 0)
			{
				entry.setConstrainedEAAllocation(0);
			}
			else
			{
				entry.setConstrainedEAAllocation(cea);
			}	
		}
	}

}
