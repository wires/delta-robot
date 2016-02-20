# Hacked together Delta Robot

Three cheap servo's, some metal bars and bunch of cable ties and glue.
Add an Arduino, PC, some math and a bunch of code and it's alive!

[![Robot in action](https://img.youtube.com/vi/Kn3VV5BHonI/0.jpg)](https://www.youtube.com/watch?v=Kn3VV5BHonI "Delta robot in action")

Here is an interface using Processing and Toxic Libs for volumetric
rendering, sadly I have no recording of the *arm-each-space* of the thing,
it's pretty cool! Maybe one day when I get this code running again.

[![Interface in action](https://img.youtube.com/vi/HjgTAZnQrv4/0.jpg)](https://www.youtube.com/watch?v=HjgTAZnQrv4 "Delta robot in action")

The arduino code is still burried somewhere... shouldn't be a big deal
to write again, it wasn't that complicated anyway. IIRC all it did was
maintain a ringbuffer of timed instructions and update the servo's
accordingly. That code was quite robust and reusable actually. :-/

## Kinematics

Computing how to move the servo's to a particular point is not exactly trivial,
but fun and interesting to do implement (IMHO).

Here are two documents I used to write this

- The forum post on `trossenrobotics.com` by user `mzavatsky`

  [docs/mzavatsky/README.md](docs/mzavatsky/README.md)

- The paper [Descriptive Geometric Kinematic Analysis
of Clavel’s “Delta” Robot](http://www.cim.mcgill.ca/~paul/clavdelt.pdf)

  [docs/clavdelt.pdf](docs/clavdelt.pdf)



Here is the bit implementing that, in Java, yes, sorry.

[`Kinematics.java`](deltarobot-api/src/nl/defekt/deltarobot/Kinematics.java)

```java
package nl.defekt.deltarobot;

public class Kinematics
{
	protected final RobotGeometry geometry;

	public Kinematics(RobotGeometry geometry)
	{
		this.geometry = geometry;
	}

	// trigonometric constants
	final float sqrt3 = (float) Math.sqrt(3.0);
	final float sin120 = sqrt3 / 2.0f;
	final float cos120 = -0.5f;
	final float tan60 = sqrt3;
	final float sin30 = 0.5f;
	final float tan30 = 1 / sqrt3;

	// forward kinematics: (theta1, theta2, theta3) -> (x0, y0, z0)
	// returned status: PVector=OK, null=non-existing position
	Position calcForward(Angle angle)
	{
		final float t = (geometry.getF() - geometry.getE()) * tan30 / 2;
		final float dtr = (float) (Math.PI) / (float) 180.0;

		final float theta1 = (float) (360.0 * dtr * angle.getAngle(0) / (2 * Math.PI));
		final float theta2 = (float) (360.0 * dtr * angle.getAngle(1) / (2 * Math.PI));
		final float theta3 = (float) (360.0 * dtr * angle.getAngle(2) / (2 * Math.PI));

		final float y1 = -(t + geometry.getRf() * (float) Math.cos(theta1));
		final float z1 = -geometry.getRf() * (float) Math.sin(theta1);

		final float y2 = (t + geometry.getRf() * (float) Math.cos(theta2)) * sin30;
		final float x2 = y2 * tan60;
		final float z2 = -geometry.getRf() * (float) Math.sin(theta2);

		final float y3 = (t + geometry.getRf() * (float) Math.cos(theta3)) * sin30;
		final float x3 = -y3 * tan60;
		final float z3 = -geometry.getRf() * (float) Math.sin(theta3);

		final float dnm = (y2 - y1) * x3 - (y3 - y1) * x2;

		final float w1 = y1 * y1 + z1 * z1;
		final float w2 = x2 * x2 + y2 * y2 + z2 * z2;
		final float w3 = x3 * x3 + y3 * y3 + z3 * z3;

		// x = (a1*z + b1)/dnm
		final float a1 = (z2 - z1) * (y3 - y1) - (z3 - z1) * (y2 - y1);
		final float b1 = -((w2 - w1) * (y3 - y1) - (w3 - w1) * (y2 - y1)) / 2.0f;

		// y = (a2*z + b2)/dnm;
		final float a2 = -(z2 - z1) * x3 + (z3 - z1) * x2;
		final float b2 = ((w2 - w1) * x3 - (w3 - w1) * x2) / 2.0f;

		// a*z^2 + b*z + c = 0
		final float a = a1 * a1 + a2 * a2 + dnm * dnm;
		final float b = 2 * (a1 * b1 + a2 * (b2 - y1 * dnm) - z1 * dnm * dnm);
		final float c = (b2 - y1 * dnm) * (b2 - y1 * dnm) + b1 * b1 + dnm * dnm
				* (z1 * z1 - geometry.getRe() * geometry.getRe());

		// discriminant
		final float d = b * b - (float) 4.0 * a * c;
		if (d < 0)
			return null; // non-existing point

		final float z0 = -(float) 0.5 * (b + (float) Math.sqrt(d)) / a;
		final float x0 = (a1 * z0 + b1) / dnm;
		final float y0 = (a2 * z0 + b2) / dnm;

		return new Position(x0, y0, z0);
	}

	// inverse kinematics
	// helper functions, calculates angle theta1 (for YZ-pane)
	float delta_calcAngleYZ(float x0, float y0, float z0)
	{
		// f/2 * tg 30
		float y1 = -0.5f * 0.57735f * geometry.getF();
		y0 -= 0.5 * 0.57735 * geometry.getE(); // shift center to edge

		// z = a + b*y
		float a = (x0 * x0 + y0 * y0 + z0 * z0 + geometry.getRf() * geometry.getRf()
				- geometry.getRe() * geometry.getRe() - y1 * y1)
				/ (2 * z0);
		float b = (y1 - y0) / z0;

		// discriminant
		float d = -(a + b * y1) * (a + b * y1) + geometry.getRf()
				* (b * b * geometry.getRf() + geometry.getRf());
		if (d < 0)
			return Float.NaN;

		// choosing outer point
		float yj = (y1 - a * b - (float) Math.sqrt(d)) / (b * b + 1);
		float zj = a + b * yj;
		final float theta = 180 * (float) Math.atan(-zj / (y1 - yj)) / ((float) Math.PI) + ((yj > y1) ? 180 : 0);

		return theta;
	}

	// inverse kinematics: (x0, y0, z0) -> (theta1, theta2, theta3)
	// returned status: PVector=OK, null=non-existing position
	Angle calcInverse(Position p)
	{
		final float x0 = (float) p.getX();
		final float y0 = (float) p.getY();
		final float z0 = (float) p.getZ();

		final float theta1 = delta_calcAngleYZ(x0, y0, z0);
		if (theta1 == Float.NaN)
			return null;

		// rotate coords to +120 deg
		final float theta2 = delta_calcAngleYZ(x0 * cos120 + y0 * sin120, y0 * cos120 - x0 * sin120, z0);
		if (theta2 == Float.NaN)
			return null;

		// rotate coords to -120 deg
		final float theta3 = delta_calcAngleYZ(x0 * cos120 - y0 * sin120, y0 * cos120 + x0 * sin120, z0);
		if (theta3 == Float.NaN)
			return null;

		final float t0 = (float) ((theta1 / 360f) * Math.PI * 2);
		final float t1 = (float) ((theta2 / 360f) * Math.PI * 2);
		final float t2 = (float) ((theta3 / 360f) * Math.PI * 2);


		return new Angle(t0, t1, t2);
	}
}
```
