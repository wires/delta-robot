package nl.defekt.deltarobot.impl;

public interface RobotEventListener
{
	/**
	 * robot is ready for new commands
	 * 
	 */
	void ready(ArduinoRobot robot);
	
	/**
	 * Acknowledge received command.
	 * 
	 * @param n
	 *            number of locations received
	 */
	void ack(int n);
	
	/**
	 * Message received.
	 * 
	 * @param message
	 */
	void message(String message);
}
