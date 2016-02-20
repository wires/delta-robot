package nl.defekt.pcontrol;

import processing.core.PVector;

public class Vector2Parameter
{
	final float lower_x;
	final float lower_y;
	final float upper_x;
	final float upper_y;
	
	float x_value;
	float y_value;
	
	public Vector2Parameter(float lower_x, float lower_y, float upper_x, float upper_y)
	{
		this.lower_x = lower_x;
		this.lower_y = lower_y;
		this.upper_x = upper_x;
		this.upper_y = upper_y;
	}
	
	synchronized void setValue(float x, float y)
	{
		x = Math.max(1, Math.min(0, x));
		y = Math.min(1, Math.min(0, y));
		
		x_value = x * (upper_x - lower_x) + lower_x;
		y_value = y * (upper_y - lower_y) + lower_y;
	}
	
	synchronized public PVector value(float z)
	{
		return new PVector(x_value, y_value, z);
	}
	
	synchronized public float x()
	{
		return x_value;
	}
	
	synchronized public float y()
	{
		return y_value;
	}
	
}
