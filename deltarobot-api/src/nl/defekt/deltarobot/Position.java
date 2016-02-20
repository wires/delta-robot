package nl.defekt.deltarobot;

public class Position
{
	protected final double x;
	protected final double y;
	protected final double z;
	
	public Position(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}
	
	public double getZ()
	{
		return z;
	}
	
	
	public String toString()
	{
		return "{" + x + "," + y + "," + z + "}";
	}
}
