package bankruptcy_code;

import java.util.ArrayList;
import java.util.List;

public class RankCalculator 
{
	public RankCalculator() 
	{
		
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnReference(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getReference() > coalitionSecond.getReference())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getReference() == coalitionSecond.getReference())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnProportionalAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getProportionalAllocation() > coalitionSecond.getProportionalAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getProportionalAllocation() == coalitionSecond.getProportionalAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnCEAAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getCEAAllocation() > coalitionSecond.getCEAAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getCEAAllocation() == coalitionSecond.getCEAAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnCELAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getCELAllocation() > coalitionSecond.getCELAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getCELAllocation() == coalitionSecond.getCELAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnAdjustedProportionalAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getAdjustedProportionalAllocation() > coalitionSecond.getAdjustedProportionalAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getAdjustedProportionalAllocation() == coalitionSecond.getAdjustedProportionalAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnShapleyAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getShapleyValueAllocation() > coalitionSecond.getShapleyValueAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getShapleyValueAllocation() == coalitionSecond.getShapleyValueAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnTalmudAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getTalmudAllocation() > coalitionSecond.getTalmudAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getTalmudAllocation() == coalitionSecond.getTalmudAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnMinimalOverlappingAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getMinimalOverlappingAllocation() > coalitionSecond.getMinimalOverlappingAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getMinimalOverlappingAllocation() == coalitionSecond.getMinimalOverlappingAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnClightsAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getClightsAllocation() > coalitionSecond.getClightsAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getClightsAllocation() == coalitionSecond.getClightsAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnEqualAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getEqualAllocation() > coalitionSecond.getEqualAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getEqualAllocation() == coalitionSecond.getEqualAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	public static List<CoalitionWithRankingDifference> rankingBasedOnUniformRandomAllocation(List<Coalition> coalitions)
	{
		List<Coalition> copyWithoutFullCoalition = new ArrayList<Coalition>(coalitions);
		
		copyWithoutFullCoalition.remove(coalitions.size() - 1);
		
		List<CoalitionWithRankingDifference> coalitionsRanked = new ArrayList<CoalitionWithRankingDifference>();
		
		for(Coalition coalitionFirst : copyWithoutFullCoalition)
		{
			double r = 1.0, s = 1.0;
			
			for(Coalition coalitionSecond : copyWithoutFullCoalition)
			{
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getUniformRandomAllocation() > coalitionSecond.getUniformRandomAllocation())
				{
					r += 1;
				}
				if(!coalitionFirst.equals(coalitionSecond) && coalitionFirst.getUniformRandomAllocation() == coalitionSecond.getUniformRandomAllocation())
				{
					s += 1;
				}			
			}
			
			coalitionsRanked.add(new CoalitionWithRankingDifference(coalitionFirst, (r + (s - 1) / 2)));
		}
		
		return coalitionsRanked;
	}
	
	// find the difference between a rank and the reference rank
	public static void compareRanks(List<CoalitionWithRankingDifference> coalitions, List<CoalitionWithRankingDifference> reference)
	{
		for(CoalitionWithRankingDifference coalition : coalitions)
		{
			double referenceRank = 0.0;
			
			for(CoalitionWithRankingDifference referenceCoalition : reference) // find the reference rank of this coalition
			{
				if(coalition.getCoalition().equals(referenceCoalition.getCoalition()))
				{
					referenceRank = referenceCoalition.getRank();
				}
			}
			coalition.setRankingDifference(Math.abs(coalition.getRank() - referenceRank));
		}
	}

}
