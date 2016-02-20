package nl.defekt.pcontrol;

public class FloatParam
{
	protected final float lower;
	protected final float upper;
	
	protected float value;
	
	public FloatParam(float lower, float upper)
	{
		this.lower = lower;
		this.upper = upper;
		this.value = (lower + upper) / 2;
	}
	
	public FloatParam(float lower, float upper, float value)
	{
		this(lower, upper);
		this.value = value;
	}
	
	// called by controls gui
	synchronized void setValue(float v)
	{
		// clip to [0,1]
		v = Math.min(1, Math.max(0, v));
		
		// map to [lower,upper]
		this.value = v * (upper - lower) + lower;
	}
	
	// called by processing
	public synchronized float value()
	{
		return value;
	}
	
	public float getLowerBound()
	{
		return lower;
	}
	
	public float getUpperBound()
	{
		return upper;
	}
}
