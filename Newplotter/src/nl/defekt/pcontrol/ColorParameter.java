package nl.defekt.pcontrol;

import java.awt.Color;

public class ColorParameter
{
	protected Color color;
	
	public ColorParameter(Color defaultColor)
	{
		this.color = defaultColor;
	}
	
	synchronized void setColor(Color color)
	{
		this.color = color;
	}
	
	synchronized public Color value()
	{
		return color;
	}
}
