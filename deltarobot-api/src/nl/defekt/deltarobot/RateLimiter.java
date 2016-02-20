package nl.defekt.deltarobot;

public class RateLimiter implements DirectAngularRobot
{
	final protected DirectAngularRobot robot;
	final protected long eventDistanceMs;
	protected long dt = 0;
	
	boolean finished = true;
	
	public RateLimiter(long eventDistanceMs, DirectAngularRobot robot)
	{
		this.robot = robot;
		this.eventDistanceMs = eventDistanceMs;
	}
	
	@Override
	public void setAngle(final Angle angle)
	{
		if (!finished)
			return;
		
		final long now = System.currentTimeMillis();
		if (now - dt > eventDistanceMs)
		{
			finished = false;
			
			final Thread t = new Thread(new Runnable() {
				@Override
				public void run()
				{
					robot.setAngle(angle);
					finished = true;
				}
			});
			
			dt = now;
			t.start();
		}
	}
}
