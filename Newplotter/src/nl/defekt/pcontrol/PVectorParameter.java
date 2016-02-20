package nl.defekt.pcontrol;

import processing.core.PVector;

public class PVectorParameter
{
	final protected PVector lowerBounds;
	final protected PVector upperBounds;
	
	final protected PVector value;
	
	public PVectorParameter(PVector lowerBounds, PVector upperBounds)
	{
		this.lowerBounds = lowerBounds;
		this.upperBounds = upperBounds;
		
		final float hx = (lowerBounds.x + upperBounds.x) / 2;
		final float hy = (lowerBounds.y + upperBounds.y) / 2;
		final float hz = (lowerBounds.z + upperBounds.z) / 2;
		
		this.value = new PVector(hx, hy, hz);
	}
	
	synchronized void setValue(float x, float y, float z)
	{
		// bound to [0,1]
		x = Math.min(1, Math.max(0, x));
		y = Math.min(1, Math.max(0, y));
		z = Math.min(1, Math.max(0, z));
		
		value.x = x * (upperBounds.x - lowerBounds.x) + lowerBounds.x;
		value.y = y * (upperBounds.y - lowerBounds.y) + lowerBounds.y;
		value.z = z * (upperBounds.z - lowerBounds.z) + lowerBounds.z;
	}
	
	public synchronized PVector value()
	{
		return value;
	}
}
