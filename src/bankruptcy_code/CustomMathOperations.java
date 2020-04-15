package bankruptcy_code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CustomMathOperations 
{
	private CustomMathOperations()
	{
		
	}
	
	public static double generateUniformRandom(double min, double max)
	{
		if (min > max)
		{
	        throw new IllegalArgumentException("max must be greater than min");
	    }
		
	    Random r = new Random();
	    
	    return min + (max - min) * r.nextDouble();
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
	
	// generate all permutations of the input list
	public static <E> List<List<E>> generatePermutation(List<E> original) 
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
