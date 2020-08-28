package data_elements;

public class CoalitionWithRankingDifference 
{
	private Coalition coalition;
	private double rank;
	private double rankingDifference;
	
	public Coalition getCoalition() 
	{
		return coalition;
	}
	public void setCoalition(Coalition coalition) 
	{
		this.coalition = coalition;
	}
	public double getRank() 
	{
		return rank;
	}
	public void setRank(double rank) 
	{
		this.rank = rank;
	}
	public double getRankingDifference() 
	{
		return rankingDifference;
	}
	public void setRankingDifference(double rankingDifference) 
	{
		this.rankingDifference = rankingDifference;
	}
	
	public CoalitionWithRankingDifference(Coalition coalition, double rank)
	{
		this.coalition = coalition;
		this.rank = rank;
	}
}
