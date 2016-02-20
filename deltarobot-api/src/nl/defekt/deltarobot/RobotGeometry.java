package nl.defekt.deltarobot;

/**
 * Robot Geometry
 * 
 * @author wires
 */
public class RobotGeometry
{
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(e);
		result = prime * result + Float.floatToIntBits(f);
		result = prime * result + Float.floatToIntBits(re);
		result = prime * result + Float.floatToIntBits(rf);
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RobotGeometry other = (RobotGeometry) obj;
		if (Float.floatToIntBits(e) != Float.floatToIntBits(other.e))
			return false;
		if (Float.floatToIntBits(f) != Float.floatToIntBits(other.f))
			return false;
		if (Float.floatToIntBits(re) != Float.floatToIntBits(other.re))
			return false;
		if (Float.floatToIntBits(rf) != Float.floatToIntBits(other.rf))
			return false;
		return true;
	}
	
	public RobotGeometry(RobotGeometry geometry)
	{
		this(geometry.e, geometry.f, geometry.re, geometry.rf);
	}
	
	public float getE()
	{
		return e;
	}
	
	public float getF()
	{
		return f;
	}
	
	public float getRf()
	{
		return rf;
	}
	
	public float getRe()
	{
		return re;
	}
	
	/** Edge-length of end effector */
	private float e;
	
	/** Edge-length of base */
	private float f;
	
	/** Arm-length of upper arm */
	private float rf;
	
	/** Arm-length of lower arm */
	private float re;
	
	public RobotGeometry(float e, float f, float re, float rf)
	{
		this.e = e;
		this.f = f;
		this.re = re;
		this.rf = rf;
	}
	
	/**
	 * Some defaults: <code>this(10, 130, 150, 150);</code>
	 * 
	 */
	public RobotGeometry()
	{
		this(7, 30, 80, 150);
	}
	
	public void setE(float e)
	{
		this.e = e;
	}
	
	public void setF(float f)
	{
		this.f = f;
	}
	
	public void setRe(float re)
	{
		this.re = re;
	}
	
	public void setRf(float rf)
	{
		this.rf = rf;
	}
}
