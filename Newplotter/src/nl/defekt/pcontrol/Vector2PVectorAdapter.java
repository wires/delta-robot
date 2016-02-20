package nl.defekt.pcontrol;

import processing.core.PVector;

/**
 * Turn a PVector into a Vector2Paramter
 * 
 * @author wires
 * 
 */
public class Vector2PVectorAdapter extends Vector2Parameter
{
	protected final PVectorParameter p;
	
	public Vector2PVectorAdapter(PVectorParameter p)
	{
		super(p.lowerBounds.x, p.lowerBounds.y, p.upperBounds.x, p.upperBounds.y);
		this.p = p;
	}
	
	@Override
	synchronized void setValue(float x, float y)
	{
		final float z = (p.value.z - p.lowerBounds.z) / (p.upperBounds.z - p.lowerBounds.z);
		p.setValue(x, y, z);
	}
	
	@Override
	public synchronized PVector value(float z)
	{
		return new PVector(p.value.x, p.value.y, z);
	}
	
	@Override
	public synchronized float x()
	{
		return p.value.x;
	}
	
	@Override
	public synchronized float y()
	{
		return p.value.y;
	}
}