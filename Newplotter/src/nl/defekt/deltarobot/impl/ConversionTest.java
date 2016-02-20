package nl.defekt.deltarobot.impl;

public class ConversionTest
{
	public static void main(String[] args)
	{
		final double U = Math.PI;
		final double L = -U;
		
		double max = Double.MIN_VALUE;
		double min = Double.MAX_VALUE;
		
		for (double d = L; d < U; d += 0.00001)
		{
			final int i = ArduinoRobot.doubleToIntConversion(L, d, U);
			final double e = ArduinoRobot.intToDoubleConversion(L, i, U);
			
			// track minimum and maximum epsilon
			max = Math.max(max, (e - d));
			min = Math.min(min, (e - d));
		}
		
		System.out.println("minimum error epsilon during float<->int conversion ~ " + min);
		System.out.println("maximum error epsilon during float<->int conversion ~ " + max);
		
		for (double d = L; d < U; d += 0.00001)
		{
			final int i = ArduinoRobot.doubleToIntConversion(L, d, U);
			final double e = ArduinoRobot.intToDoubleConversion(L, i, U);
			
			if ((e - d) == max)
			{
				final int j = ArduinoRobot.doubleToIntConversion(L, d, U);
				final double f = ArduinoRobot.intToDoubleConversion(L, j, U);
				System.out.println("max @ " + d + " --> " + j + " --> " + f);
			}
			
			if ((e - d) == min)
				System.out.println("min @ " + d + " --> " + i + " --> " + e);
		}
	}
}
