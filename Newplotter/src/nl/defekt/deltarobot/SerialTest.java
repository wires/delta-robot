package nl.defekt.deltarobot;

import gnu.io.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import nl.defekt.deltarobot.DeltaRobotAngular.TimedAngle;
import nl.defekt.deltarobot.impl.ArduinoRobot;
import nl.defekt.deltarobot.impl.RobotEventListener;

class A implements TimedAngle
{
	final int t;
	final double[] theta = new double[3];
	
	public A(int t, int seed)
	{
		final Random rnd = new Random(seed);
		this.t = t;
		for (int i = 0; i < 3; i++)
			theta[i] = Math.PI * (2 * rnd.nextDouble() - 1);
	}
	
	public int getMilliTime()
	{
		return t;
	}
	
	public double getTheta(int i)
	{
		return theta[i];
	}
}

public class SerialTest
{
	public static void main(String[] args)
	{
		final Random rnd = new Random(1234);
		
		// callbacks
		final RobotEventListener listener = new RobotEventListener() {
			public void ready(ArduinoRobot arduino)
			{
				System.out.println("recv: robot ready");
			}
			
			public void ack(int n)
			{
				System.out.println("ack: robot acknowledges receiption of " + n + " positions");
			}
			
			public void message(String message)
			{
				System.err.println("msg: robot send message '" + message + "'");
			}
		};
		
		try
		{
			System.out.println("connecting to robot");
			final ArduinoRobot arduino = new ArduinoRobot(listener);
			
			for (int j = 0; j < 10; j++)
			{
				
				System.out.println("connected, building a list of events");
				final ArrayList<TimedAngle> list = new ArrayList<TimedAngle>();
				for (int i = 0; i < 1000 / (j + 1); i++)
				{
					list.add(new A(3000 + i * 25 * (j + 1), i));
				}
				
				
				try
				{
					Thread.sleep(6000);
					
					// communication
					System.out.println("sending: " + list.size() + " positions to robot");
					System.out.flush();
					arduino.sendEvent(list);
					System.out.println("done");
					
					Thread.sleep(2000);
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		catch (NoSuchPortException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedCommOperationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (PortInUseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}