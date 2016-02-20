package nl.defekt.deltarobot.impl;

import gnu.io.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import nl.defekt.deltarobot.DeltaRobotAngular;

/**
 * Serial communication with Arduino.
 * 
 * @author wires
 * 
 */
public class ArduinoRobot implements Runnable, DeltaRobotAngular
{
	protected final Thread serialReadThread;
	protected final RobotEventListener listener;
	
	protected final OutputStream os;
	protected final Writer writer;
	
	protected final InputStream is;
	protected final Reader reader;
	
	protected final ConcurrentLinkedQueue<List<TimedAngle>> events = new ConcurrentLinkedQueue<List<TimedAngle>>();
	protected boolean ready = false;
	
	protected float shortTiming = 1000;
	protected float longTiming = 2000;
	
	public ArduinoRobot(RobotEventListener listener)
	{
		// store callback
		this.listener = listener;
		
		// port configuration
		// (cannot sniff processing preferences
		// http://code.google.com/p/arduino/issues/detail?id=103)
		final String portdev = "/dev/tty.usbserial-A60048Bd";
		final int portrate = 115200;
		
		try
		{
			// open serial port
			System.out.println("[ROBOT] opening serial port: " + portdev);
			final CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portdev);
			final SerialPort port = (SerialPort) portId.open("serial talk", 4000);
			is = port.getInputStream();
			os = port.getOutputStream();
			port.setSerialPortParams(portrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		}
		catch (NoSuchPortException e)
		{
			e.printStackTrace();
			throw new RuntimeException("failed to connect to arduino");
		}
		catch (PortInUseException e)
		{
			e.printStackTrace();
			throw new RuntimeException("failed to connect to arduino");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException("failed to connect to arduino");
		}
		catch (UnsupportedCommOperationException e)
		{
			e.printStackTrace();
			throw new RuntimeException("failed to connect to arduino");
		}
		
		// setup ASCII writer
		final Charset ascii = Charset.forName("US-ASCII");
		reader = new InputStreamReader(is, ascii);
		writer = new OutputStreamWriter(os, ascii);
		
		// start reading
		serialReadThread = new Thread(this);
		serialReadThread.start();
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				StringBuilder b = null;
				int c;
				
				// read status char
				int status = reader.read();
				// System.out.println("received status " + status);
				
				// switch accordin to status
				PARSE: switch (status)
				{
					// out of data
					case -1:
						return;
						
					case 0:
						// ready for commands
						listener.ready(ArduinoRobot.this);
						ready = true;
						break PARSE;
						
					case 1:
						// ack, followed by size
						listener.ack(reader.read());
						break PARSE;
						
					case 2:
						// messaged followed by chars, closed by \n
						b = new StringBuilder();
						
						for(;;)
						{
							c = reader.read();
							
							if ((c == -1) || (((char) c) == '\r'))
							{
								listener.message(b.toString());
								break;
							}
							
							b.append((char) c);
						}
						
					default:
						break;
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Send event (timed coordinates) to Arduino.
	 * 
	 * This method can fail or block.
	 * 
	 * @param event
	 * @throws IOException
	 */
	public void sendEvent(List<TimedAngle> event) throws IOException
	{
		// System.out.println("[ROBOT] sending stroke with " + event.size() +
		// " angles");
		
		// write all events
		for (TimedAngle p : event)
		{
			ready = false;
			
			writer.write(Integer.toString(p.getMilliTime()));
			writer.write(',');
			
			// // write nr of events
			// writer.write("0,4000,32000,-32000");
			
			for (int i = 0; i < 3; i++)
			{
				// clip output to integer range
				// final int theta = doubleToIntConversion(-Math.PI,
				// p.getTheta(i), Math.PI);
				final double pos = ((p.getTheta(i) / Math.PI) + 1.0) / 2.0;
				final int ms = (int) (shortTiming + (pos * (longTiming - shortTiming)));
				writer.write(Integer.toString(ms));
				
				if (i != 2)
					writer.write(',');
			}
			
			// end of line
			writer.write('\r');
			
			// empty buffers
			writer.flush();
			
			// wait for ready
			try
			{
				while (!ready)
					Thread.sleep(1);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				return;
			}
		}
	}
	
	public void setLowerTiming(float timing)
	{
		shortTiming = timing;
	}
	
	public void setUpperTiming(float timing)
	{
		longTiming = timing;
	}
	
	/**
	 * Clip value to closed domain <code>[lower, upper]</code>
	 * 
	 * @param lower
	 * @param value
	 * @param upper
	 * @return
	 */
	public final static double clip(double lower, double value, double upper)
	{
		return Math.min(upper, Math.max(lower, value));
	}
	
	/**
	 * Symmetric integer maximum
	 */
	// final public static int INT_SMAX = 2000000000; // 2147483647;
	final public static int INT_SMAX = 32000; // dealing with shorts
	
	public final static int doubleToIntConversion(double lower, double value, double upper)
	{
		// scale to [-1.0, 1.0 ]
		final double delta = (upper - lower) / 2.0;
		final double normalized_value = ((value - lower) / delta) - 1.0;
		
		// clip to [-1.0, 1.0 ]
		final double v = clip(-1.0, normalized_value, 1.0);
		
		// scale, clip and cast to integer
		return (int) clip(-INT_SMAX, v * INT_SMAX, INT_SMAX);
	}
	
	public final static double intToDoubleConversion(double lower, int value, double upper)
	{
		// clip input
		final int clipped_value = (int) clip(-INT_SMAX, value, INT_SMAX);
		
		// scale to [-1.0, 1.0 ]
		final double normalized_value = ((double) clipped_value) / INT_SMAX;
		
		// clip to [-1.0, 1.0 ]
		final double delta = (upper - lower) / 2.0;
		final double rescaled = (normalized_value * delta);
		
		return clip(lower, rescaled, upper);
	}
	
	public void moveAngles(List<TimedAngle> sequence)
	{
		try
		{
			sendEvent(sequence);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}