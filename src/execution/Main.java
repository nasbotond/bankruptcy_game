package execution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import bankruptcy_code.Claimer;

public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) 
	{
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
		
		System.out.println(powerSet);
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

}
