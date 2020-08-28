package data_elements;

public class Claimer 
{
	private double claim;
	private char id;
	private double proportionalAllocation; // proportional rule
	private double CEAAllocation; // constrained equal awards rule
	private double CELAllocation; // constrained equal losses rule
	private double shapleyValue; // shapley values of the claimer
	private double talmudAllocation; // talmud rule allocation 
	private double adjustedProportionalAllocation; // adjusted proportional rule allocation
	private double minimalOverlappingAllocation; // MO rule allocation
	private double clightsAllocation; // per capita nucleolus (clight's rule)
	private double equalAllocation; // control rule #1, all get equal amounts
	private double uniformRandomAllocation; // control rule #2
	
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

	public double getTalmudAllocation() 
	{
		return talmudAllocation;
	}

	public void setTalmudAllocation(double talmudAllocation) 
	{
		this.talmudAllocation = talmudAllocation;
	}

	public double getAdjustedProportionalAllocation() 
	{
		return adjustedProportionalAllocation;
	}

	public void setAdjustedProportionalAllocation(double adjustedProportionalAllocation) 
	{
		this.adjustedProportionalAllocation = adjustedProportionalAllocation;
	}

	public double getMinimalOverlappingAllocation() 
	{
		return minimalOverlappingAllocation;
	}

	public void setMinimalOverlappingAllocation(double minimalOverlappingAllocation) 
	{
		this.minimalOverlappingAllocation = minimalOverlappingAllocation;
	}

	public double getClightsAllocation()
	{
		return clightsAllocation;
	}

	public void setClightsAllocation(double clightsAllocation) 
	{
		this.clightsAllocation = clightsAllocation;
	}

	public double getEqualAllocation() 
	{
		return equalAllocation;
	}

	public void setEqualAllocation(double equalAllocation) 
	{
		this.equalAllocation = equalAllocation;
	}

	public double getUniformRandomAllocation() 
	{
		return uniformRandomAllocation;
	}

	public void setUniformRandomAllocation(double uniformRandomAllocation) 
	{
		this.uniformRandomAllocation = uniformRandomAllocation;
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
