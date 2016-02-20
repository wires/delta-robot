package nl.defekt.pcontrol;

public class BoundFloatPVectorAdapter extends FloatParam
{
	protected final PVectorParameter p;
	
	public BoundFloatPVectorAdapter(PVectorParameter p)
	{
		super(p.lowerBounds.z, p.upperBounds.z);
		
		this.p = p;
	}
	
	@Override
	synchronized void setValue(float v)
	{
		final float x = (p.value().x - p.lowerBounds.x) / (p.upperBounds.x - p.lowerBounds.x);
		final float y = (p.value().y - p.lowerBounds.y) / (p.upperBounds.y - p.lowerBounds.y);
		
		p.setValue(x, y, v);
	}
	
	@Override
	public synchronized float value()
	{
		return p.value().z;
	}
	
	
}
