package nl.defekt.deltarobot;

import java.util.List;

/**
 * Interface to real robot hardware.
 * 
 * E.g. arduino + firmware, rendering of virtual robot
 * 
 * The user of a DeltaRobot implementation should be able to pass a
 * {@link RobotEventListener} to it (ie. via constructor).
 * 
 * @author wires
 * 
 */
public interface DeltaRobotAngular
{
	interface TimedAngle
	{
		/**
		 * Angle between horizontal plane and i-th arm.
		 * 
		 * Up is positive angle.
		 * 
		 * @param i
		 *            i-th arm, i=0,1,..
		 * 
		 * @return Angle from -π to positive π
		 */
		double getTheta(int i);
		
		int getMilliTime();
	}
	
	void moveAngles(List<TimedAngle> sequence);
}