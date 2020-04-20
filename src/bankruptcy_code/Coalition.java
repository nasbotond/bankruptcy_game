package bankruptcy_code;

import java.util.List;

public class Coalition 
{
	private List<Claimer> claimers;
	private String id;
	private double reference; // reference value
	private double characteristicFunction; // characteristic function
	private double proportionalAllocation; // proportional rule
	private double CEAAllocation; // constrained equal awards rule
	private double CELAllocation; // constrained equal losses rule
	private double shapleyValueAllocation;
	private double talmudAllocation;
	private double adjustedProportionalAllocation; // adjusted proportional rule allocation
	private double minimalOverlappingAllocation; // MO allocation
	private double clightsAllocation;
	private double equalAllocation; // control rule #1, all get equal amounts
	private double uniformRandomAllocation; // control rule #2
	
	private double averagePropVariation;
	private double averageCEAVariation;
	private double averageCELVariation;
	private double averageShapleyValueVariation;

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

	public void setReference(double reference) 
	{
		this.reference = reference;
	}
	
	public double getCharacteristicFunction() 
	{
		return characteristicFunction;
	}

	public void setCharacteristicFunction(double characteristicFunction) 
	{
		this.characteristicFunction = characteristicFunction;
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

	public double getShapleyValueAllocation() 
	{
		return shapleyValueAllocation;
	}

	public void setShapleyValueAllocation(double shapleyValueAllocation) 
	{
		this.shapleyValueAllocation = shapleyValueAllocation;
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

	public double getAverageShapleyValueVariation() 
	{
		return averageShapleyValueVariation;
	}

	public void setAverageShapleyValueVariation(double averageShapleyValueVariation) 
	{
		this.averageShapleyValueVariation = averageShapleyValueVariation;
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
