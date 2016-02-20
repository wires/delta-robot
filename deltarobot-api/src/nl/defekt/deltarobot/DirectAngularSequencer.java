package nl.defekt.deltarobot;

import java.util.ArrayList;

import nl.defekt.deltarobot.DeltaRobotAngular.TimedAngle;


public class DirectAngularSequencer implements DirectAngularRobot
{
	protected final DeltaRobotAngular robot;
	
	public DirectAngularSequencer(DeltaRobotAngular robot)
	{
		this.robot = robot;
	}
	
	@Override
	public void setAngle(final Angle angle)
	{
		final ArrayList<TimedAngle> s = new ArrayList<TimedAngle>();
		s.add(new TimedAngle() {
			@Override
			public double getTheta(int i)
			{
				return angle.getAngle(i);
			}
			
			@Override
			public int getMilliTime()
			{
				return 0;
			}
		});
		robot.moveAngles(s);
	}
}