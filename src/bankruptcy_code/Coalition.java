package bankruptcy_code;

import java.util.List;

public class Coalition 
{
	private List<Claimer> claimers;
	private String id;
	private double reference; // characteristic function
	private double proportionalAllocation; // proportional rule
	private double constrainedEAAllocation; // constrained equal awards rule
	private double constrainedELAllocation; // constrained equal losses rule
	
	private double averagePropVariation;
	private double averageCEAVariation;
	private double averageCELVariation;

	public List<Claimer> getClaimers() 
	{
		return claimers;
	}

	public String getId() 
	{
		return id;
	}
	
	public double getReference() 
	{
		return reference;
	}

	public void setReference(double reference) {
		this.reference = reference;
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

	public double getAveragePropVariation() 
	{
		return averagePropVariation;
	}

	public void setAveragePropVariation(double averagePropVariation) 
	{
		this.averagePropVariation = averagePropVariation;
	}

	public double getAverageCEAVariation() 
	{
		return averageCEAVariation;
	}

	public void setAverageCEAVariation(double averageCEAVariation) 
	{
		this.averageCEAVariation = averageCEAVariation;
	}

	public double getAverageCELVariation() 
	{
		return averageCELVariation;
	}

	public void setAverageCELVariation(double averageCELVariation) 
	{
		this.averageCELVariation = averageCELVariation;
	}

	public Coalition(List<Claimer> claimers) 
	{
		String concat = "";
		for (Claimer claimer : claimers) 
		{
			concat = concat + claimer.getId();
		}
		id = concat;
		
		this.claimers = claimers;
	}

	@Override
	public String toString() {
		return "Coalition [claimers=" + claimers + ", id=" + id + "]";
	}	
}
