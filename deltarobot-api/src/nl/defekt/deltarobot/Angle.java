package nl.defekt.deltarobot;

public class Angle
{
	protected final double t0;
	protected final double t1;
	protected final double t2;
	
	public Angle(double t0, double t1, double t2)
	{
		this.t0 = t0;
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public int size()
	{
		return 3;
	}
	
	public double getAngle(int i)
	{
		switch (i)
		{
			case 0:
				return t0;
			case 1:
				return t1;
			case 2:
				return t2;
			default:
				throw new IllegalArgumentException("Illegal argument i = " + i);
		}
	}
	
	public String toString()
	{
		return "(" + t0 + "," + t1 + "," + t2 + ")";
	}
	
	public double distance(Angle angle)
	{
		return ((angle.t0 - t0) * (angle.t0 - t0) + (angle.t1 - t1) * (angle.t1 - t1) + (angle.t2 - t2)
		        * (angle.t2 - t2));
	}
}
