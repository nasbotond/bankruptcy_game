package exceptions;

public class InvalidEstateException extends Exception
{
	public InvalidEstateException()
	{
		
	}
	
	public InvalidEstateException(String msg) 
	{
		super(msg);
	}
}
