//package nl.defekt.deltarobot.impl;
//
//import java.util.List;
//
//import nl.defekt.deltarobot.RobotEventListener;
//
//public class CommProtocol
//{
//	protected final RobotEventListener listener;
//
//	public void sendEvent(List<Angle> event) throws IOException
//	{
//		// write nr of events
//		output.write(event.size());
//
//		// write all events
//		for (Angle p : event)
//		{
//			output.write(p.getNanoTime());
//			for (int i = 0; i < 3; i++)
//			{
//				// clip to [-1,1]
//				final double clipped = clip(-1.0, p.getTheta(i) / Math.PI, 1.0);
//
//				// clip output to integer range
//				final int theta = (int) clip(-INTEGER_SYMMETRIC_MAX, clipped, INTEGER_SYMMETRIC_MAX);
//
//				output.write(theta);
//			}
//		};
//
//		output.flush();
//	}
//
//
//	public CommProtocol(RobotEventListener listener) throws NoSuchPortException, IOException,
//	        UnsupportedCommOperationException, PortInUseException
//	{
//		// store callback
//		this.listener = listener;
//
//		// port configuration
//		// (cannot sniff processing preferences
//		// http://code.google.com/p/arduino/issues/detail?id=103)
//		final String portdev = "/dev/tty.usbserial-A60048Bd";
//		final int portrate = 9600;
//
//
//		// open serial port
//		System.out.println("[ROBOT] opening serial port: " + portdev);
//		final CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portdev);
//		final SerialPort port = (SerialPort) portId.open("serial talk", 4000);
//		input = port.getInputStream();
//		output = port.getOutputStream();
//		port.setSerialPortParams(portrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
//		        SerialPort.PARITY_NONE);
//
//		// start reading
//		serialReadThread = new Thread(this);
//	}
//}
//
//public class ArduinoRobot implements DeltaRobotAngular, Runnable
//{
//	final public static int INTEGER_SYMMETRIC_MAX = 2147483647;
//
//	protected final Thread serialReadThread;
//	protected final RobotEventListener listener;
//	protected final OutputStream output;
//	protected final InputStream input;
//	protected final ConcurrentLinkedQueue<List<Angle>> events = new ConcurrentLinkedQueue<List<Angle>>();
//
//	public void moveSequence(List<Angle> sequence)
//	{
//		// store event in thread safe buffer queue
//		events.offer(sequence);
//	}
//
//	public ArduinoRobot(RobotEventListener listener) throws NoSuchPortException, IOException,
//	UnsupportedCommOperationException, PortInUseException
//	{
//		// store callback
//		this.listener = listener;
//
//		// port configuration
//		// (cannot sniff processing preferences
//		// http://code.google.com/p/arduino/issues/detail?id=103)
//		final String portdev = "/dev/tty.usbserial-A60048Bd";
//		final int portrate = 9600;
//
//
//		// open serial port
//		System.out.println("[ROBOT] opening serial port: " + portdev);
//		final CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portdev);
//		final SerialPort port = (SerialPort) portId.open("serial talk", 4000);
//		input = port.getInputStream();
//		output = port.getOutputStream();
//		port.setSerialPortParams(portrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
//				SerialPort.PARITY_NONE);
//
//		// start reading
//		serialReadThread = new Thread(this);
//	}
//
//	public void run()
//	{
//		while (true)
//		{
//			try
//			{
//				// wait for data
//				while (input.available() > 0)
//				{
//					// print it received message
//					System.out.print((char) (input.read()));
//
//					// transfer event
//					try
//					{
//						// send first event from queue
//						final List<Angle> event = events.peek();
//						sendEvent(event);
//					}
//					catch (IOException writeException)
//					{
//						// error during sending,
//						writeException.printStackTrace();
//
//						// wait for next ready and retry send
//						continue;
//					}
//					finally
//					{
//						// succes, commit
//						events.poll();
//					}
//
//					// if ready and queue empty notify listener to fill queue
//					if (events.isEmpty())
//						listener.readyForSent();
//				}
//			}
//			catch (IOException readException)
//			{
//				// failure during reading
//				readException.printStackTrace();
//
//				// stop processing
//				// TODO reconnect?
//				return;
//			}
//		}
//	}
//
//
//	/**
//	 * Clip value to closed domain <code>[lower, upper]</code>
//	 *
//	 * @param lower
//	 * @param value
//	 * @param upper
//	 * @return
//	 */
//	public final static double clip(double lower, double value, double upper)
//	{
//		return Math.min(upper, Math.max(lower, value));
//	}
// }
