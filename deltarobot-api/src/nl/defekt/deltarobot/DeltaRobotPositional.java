package nl.defekt.deltarobot;

import java.util.List;

public interface DeltaRobotPositional
{
	/**
	 * Coordinate of target.
	 * 
	 * World center is at robot base center, with Z pointing upwards (ie. target
	 * is always in <code>z < 0</code> subspace).
	 */
	interface TimedPosition
	{
		double getX();
		
		double getY();
		
		double z();
		
		int getNanoTime();
		
	}
	
	void movePositions(List<TimedPosition> sequence);
}
