package nl.defekt.deltarobot;

import java.util.ArrayList;

import nl.defekt.deltarobot.DeltaRobotPositional.TimedPosition;

public class DirectPositionalSequencer implements DirectPositionalRobot
{
	protected final DeltaRobotPositional robot;
	
	public DirectPositionalSequencer(DeltaRobotPositional robot)
	{
		this.robot = robot;
	}
	
	public void setPosition(final Position position)
	{
		final ArrayList<TimedPosition> s = new ArrayList<TimedPosition>();
		s.add(new TimedPosition() {
			public double z()
			{
				return position.getZ();
			}
			
			public double getY()
			{
				return position.getY();
			}
			
			public double getX()
			{
				return position.getX();
			}
			
			public int getNanoTime()
			{
				return 0;
			}
		});
		robot.movePositions(s);
	}
}