package nl.defekt.deltarobot;


public class FKRobotAdapter implements DirectAngularRobot
{
	protected final DirectPositionalRobot deltaRobot;
	protected final Kinematics kinematics;
	
	public FKRobotAdapter(DirectPositionalRobot deltaRobot, Kinematics kinematics)
	{
		this.deltaRobot = deltaRobot;
		this.kinematics = kinematics;
	}
	
	@Override
	public void setAngle(Angle angle)
	{
		deltaRobot.setPosition(kinematics.calcForward(angle));
	}
}
