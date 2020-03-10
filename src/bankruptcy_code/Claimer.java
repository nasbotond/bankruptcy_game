package bankruptcy_code;

public class Claimer 
{
	private double claim;
	private char id;
	
	public double getClaim() 
	{
		return claim;
	}

	public char getId() 
	{
		return id;
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
