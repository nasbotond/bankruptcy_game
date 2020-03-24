package bankruptcy_code;

public class Claimer 
{
	private double claim;
	private char id;
	private double proportionalAllocation; // proportional rule
	private double CEAAllocation; // constrained equal awards rule
	private double CELAllocation; // constrained equal losses rule
	private double shapleyValue; // shapley values of the claimer
	
	public double getClaim() 
	{
		return claim;
	}
	
	public void setClaim(double claim) 
	{
		this.claim = claim;
	}

	public Character getId() 
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

	public double getCEAAllocation() 
	{
		return CEAAllocation;
	}

	public void setCEAAllocation(double CEAAllocation) 
	{
		this.CEAAllocation = CEAAllocation;
	}

	public double getCELAllocation() 
	{
		return CELAllocation;
	}

	public void setCELAllocation(double CELAllocation) 
	{
		this.CELAllocation = CELAllocation;
	}

	public double getShapleyValue() 
	{
		return shapleyValue;
	}

	public void setShapleyValue(double shapleyValue) 
	{
		this.shapleyValue = shapleyValue;
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
