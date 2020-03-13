package bankruptcy_code;

public class Claimer 
{
	private double claim;
	private char id;
	private double proportionalAllocation; // proportional rule
	private double constrainedEAAllocation; // constrained equal awards rule
	private double constrainedELAllocation; // constrained equal losses rule
	
	public double getClaim() 
	{
		return claim;
	}
	
	public void setClaim(double claim) 
	{
		this.claim = claim;
	}

	public char getId() 
	{
		return id;
	}	
	
	public double getProportionalAllocation() 
	{
		return proportionalAllocation;
	}

	public void setProportionalAllocation(double proportionalAllocation) 
	{
		this.proportionalAllocation = proportionalAllocation;
	}

	public double getConstrainedEAAllocation() 
	{
		return constrainedEAAllocation;
	}

	public void setConstrainedEAAllocation(double constrainedEAAllocation) 
	{
		this.constrainedEAAllocation = constrainedEAAllocation;
	}

	public double getConstrainedELAllocation() 
	{
		return constrainedELAllocation;
	}

	public void setConstrainedELAllocation(double constrainedELAllocation) 
	{
		this.constrainedELAllocation = constrainedELAllocation;
	}

	public Claimer(char id, double claim)
	{
		this.id = id;
		this.claim = claim;
	}

	@Override
	public String toString() {
		// return "Claimer [claim=" + claim + ", id=" + id + "]";
		return ""+ id;
	}
}
