package nl.defekt.deltarobot;

public class CompositeAngularRobot implements DirectAngularRobot
{
	protected final DirectAngularRobot robots[];
	
	public CompositeAngularRobot(DirectAngularRobot... robots)
	{
		this.robots = robots;
	}
	
	@Override
	public void setAngle(Angle angle)
	{
		for (DirectAngularRobot robot : robots)
			robot.setAngle(angle);
	}
	
}
