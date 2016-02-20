package nl.defekt.deltarobot;


public class IKRobotAdapter implements DirectPositionalRobot
{
	protected final DirectAngularRobot deltaRobot;
	protected final Kinematics kinematics;
	
	public IKRobotAdapter(DirectAngularRobot deltaRobot, Kinematics kinematics)
	{
		this.deltaRobot = deltaRobot;
		this.kinematics = kinematics;
	}
	
	@Override
	public void setPosition(Position position)
	{
		final Angle angle = kinematics.calcInverse(position);
		deltaRobot.setAngle(angle);
	}
}