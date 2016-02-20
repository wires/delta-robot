package nl.defekt.deltarobot;


import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import nl.defekt.deltarobot.impl.ArduinoRobot;
import nl.defekt.deltarobot.impl.RobotEventListener;
import nl.defekt.pcontrol.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PVector;
import toxi.geom.Vec3D;
import toxi.volume.*;


public class Gui extends PApplet
{
	public class ProcessingRobot implements DirectPositionalRobot, DirectAngularRobot
	{
		protected PVector target = new PVector();
		protected PVector theta = new PVector();
		protected PVector eff[] = new PVector[3];
		protected final RobotGeometry geometry;
		
		public ProcessingRobot(RobotGeometry geometry)
		{
			this.geometry = geometry;
		}
		
		public void draw()
		{
			// draw target and run IK
			if (target != null)
			{
				for (int i = 0; i < 3; i++)
					if (eff[i] != null)
					{
						// draw top arm positions
						pushMatrix();
						translate(eff[i]);
						box(4);
						popMatrix();
					}
			}
			
			// draw effectors and run FK
			if (theta != null)
			{
				final float e = geometry.getF();
				final float re = geometry.getRf();
				final float boxSide = 6;
				
				for (int i = 0; i < 3; i++)
				{
					pushMatrix();
					
					// i-th arm
					rotateZ(i * 2 * PI / 3 - HALF_PI);
					
					// i-th angle
					rotateY(component(theta, i));
					
					// draw arm
					pushMatrix();
					translate(e + (re / 2), 0);
					scale(re / boxSide, 1, 1);
					stroke(0);
					fill(200);
					box(boxSide);
					popMatrix();
					
					// draw ball joint
					translate(e + re, 0);
					// rotateY(-component(theta, i));
					// rotateZ(-i * 2 * PI / 3);
					stroke(255);
					noFill();
					box(10);
					
					// eff[i] = modelXYZ();
					
					popMatrix();
				}
				
				// // run
				// // FK
				// final PVector v = kinematics.calcForward(theta0.value(),
				// theta1.value(), theta2
				// .value());
				// if (v != null)
				// {
				// pushMatrix();
				// translate(v);
				// fill(fkTargetColor.value());
				// box(boxSize.value());
				// popMatrix();
				// }
			}
		}
		
		void rod(PVector from, PVector to)
		{
			// pushMatrix();
			// translate(from);
			// box(10);
			// popMatrix();
			
			fill(255);
			stroke(255);
			strokeWeight(2);
			beginShape(TRIANGLES);
			vertex(from.x, from.y, from.z - 2);
			vertex(from.x, from.y, from.z + 3);
			vertex(to.x, to.y, to.z);
			endShape();
		}
		
		
		/**
		 * modelX/Y/Z(0,0,0) with identity transform is not equal to (0,0,0).
		 * 
		 * I'd say this is a bug in processing and this method fixes this.
		 */
		PVector modelXYZ()
		{
			// get origin transform
			pushMatrix();
			resetMatrix();
			final float ox = modelX(0, 0, 0);
			final float oy = modelY(0, 0, 0);
			final float oz = modelZ(0, 0, 0);
			popMatrix();
			
			final float x = modelX(0, 0, 0);
			final float y = modelY(0, 0, 0);
			final float z = modelZ(0, 0, 0);
			
			return new PVector(x - ox, y - oy, z - oz);
			// return new PVector(x, y, z);
		}
		
		public void setPosition(nl.defekt.deltarobot.Position position)
		{
			target = new PVector((float) position.getX(), (float) position.getY(), (float) position
					.getZ());
		}
		
		public void setAngle(Angle angle)
		{
			theta.x = (float) angle.getAngle(0);
			theta.y = (float) angle.getAngle(1);
			theta.z = (float) angle.getAngle(2);
		}
	}
	
	// callbacks
	final RobotEventListener listener = new RobotEventListener() {
		public void ready(ArduinoRobot arduino)
		{
			// System.out.println("recv: robot ready");
		}
		
		public void ack(int n)
		{
			// System.out.println("ack: robot acknowledges receiption of " + n +
			// " positions");
		}
		
		public void message(String message)
		{
			// System.err.println("msg: robot send message '" + message + "'");
		}
	};
	
	final protected RobotGeometry geometry = new RobotGeometry();
	final protected Kinematics kinematics = new Kinematics(geometry);
	
	
	final protected ProcessingRobot plotter = new ProcessingRobot(geometry);
	
	// final protected ArduinoRobot arduino = new ArduinoRobot(listener);
	// final protected DirectAngularRobot arduinoDirect = new
	// DirectAngularSequencer(arduino);
	// final protected RateLimiter arduinoLimited = new RateLimiter(10,
	// arduinoDirect);
	
	final protected CompositeAngularRobot composite = new CompositeAngularRobot(plotter);
	// , arduinoLimited);
	
	final protected DirectPositionalRobot target = new IKRobotAdapter(composite, kinematics);
	
	final protected WorkArea wa = new WorkArea(kinematics);
	
	PeasyCam cam;
	Controls controls;
	
	public FloatParam servoShortTiming = new FloatParam(700, 2500, 1000);
	public FloatParam servoLongTiming = new FloatParam(700, 2500, 2000);
	
	public FloatParam boxSize = new FloatParam(1, 12);
	public ColorParameter fkTargetColor = new ColorParameter(Color.RED);
	public ColorParameter ikSourceColor = new ColorParameter(Color.GREEN);
	
	class WorkArea implements Runnable
	{
		protected final ArrayList<Position> positions = new ArrayList<Position>();
		protected final Random random = new Random(1234);
		protected boolean working = false;
		
		public int pointCount = 10000;
		public float weight = 12;
		public float stroke = 3;
		
		final Kinematics kinematics;
		
		protected RobotGeometry geometry = null;
		
		public WorkArea(final Kinematics kinematics)
		{
			this.kinematics = kinematics;
			
			volume = new VolumetricSpace(SCALE, DIMX, DIMY, DIMZ);
			brush = new RoundBrush(volume, SCALE.x / 2);
			surface = new IsoSurface(volume);
			
			update();
		}
		
		int DIMX = 96;
		int DIMY = 96;
		int DIMZ = 96;
		
		float ISO_THRESHOLD = 0.1f;
		Vec3D SCALE = new Vec3D(2, 2, 4).scaleSelf(200);
		
		VolumetricSpace volume;
		VolumetricBrush brush;
		IsoSurface surface;
		
		public void run()
		{
			final long start = System.currentTimeMillis();
			System.out.println("Calculating working area");
			positions.clear();
			for (int i = 0; i < pointCount * 10; i++)
			{
				final double t0 = (random.nextDouble() * 4 - 2) * Math.PI;
				final double t1 = (random.nextDouble() * 4 - 2) * Math.PI;
				final double t2 = (random.nextDouble() * 4 - 2) * Math.PI;
				
				final Position p = kinematics.calcForward(new Angle(t0, t1, t2));
				if (p != null)
					positions.add(p);
				
				if (positions.size() > pointCount)
					break;
			};
			
			
			System.out.println("Finished, took " + (float) (System.currentTimeMillis() - start)
					/ 1000.0
					+ " seconds");
			
			if (method == 1)
			{
				final long start2 = System.currentTimeMillis();
				System.out.println("Calculating volume");
				
				// volumetric
				volume.clear();
				for (Position pos : positions)
				{
					brush.setSize(weight);
					brush.drawAtAbsolutePos(new Vec3D((float) pos.getX(), (float) pos.getY(),
							(float) pos.getZ()), density);
				}
				volume.closeSides();
				surface.reset();
				surface.computeSurface(ISO_THRESHOLD);
				
				System.out.println("Finished volume, took "
						+ (float) (System.currentTimeMillis() - start2) / 1000.0 + " seconds");
				
			}
			
			geometry = new RobotGeometry(kinematics.geometry);
			working = false;
			
			
		}
		
		int opacity = 80;
		int method = 1;
		
		public void draw()
		{
			if (working)
			{
				// noStroke();
				stroke(0, 255, 255, 40);
				box(300);
				return;
			}
			
			switch (method)
			{
				// draw flat points
				case 0:
					noFill();
					stroke(0, 255, 255, opacity);
					if (weight != 0)
						strokeWeight(stroke);
					for (Position pos : positions)
						point((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
					break;
					
					// draw spheres
				case 2:
					fill(0, 255, 255, opacity);
					noStroke();
					// stroke(0, 0, 0, opacity);
					for (Position pos : positions)
					{
						pushMatrix();
						translate((float) pos.getX(), (float) pos.getY(), (float) pos.getZ());
						sphere(weight);
						popMatrix();
					}
					break;
					
				case 1:
					hint(DISABLE_DEPTH_TEST);
					
					lightSpecular(230, 230, 230);
					
					beginShape(TRIANGLES);
					
					noStroke();
					fill(0, 255, 255, opacity);
					
					// noFill();
					// stroke(0, 255, 255, opacity);
					
					// draw all faces of the computed mesh
					int num = surface.getNumFaces();
					Vec3D[] verts = null;
					for (int i = 0; i < num; i++)
					{
						verts = surface.getVerticesForFace(i, verts);
						vertex(verts[2].x, verts[2].y, verts[2].z);
						vertex(verts[1].x, verts[1].y, verts[1].z);
						vertex(verts[0].x, verts[0].y, verts[0].z);
					}
					endShape();
					hint(ENABLE_DEPTH_TEST);
					
					break;
			}
			
		}
		
		boolean isWireframe = false;
		int lpointCount = 0;
		float ldensity = 0;
		float lweight = 0;
		public float density = 0.1f;
		
		public void update()
		{
			// working, don't interrupt
			if (working)
				return;
			
			if ((!kinematics.geometry.equals(geometry)) || lpointCount != pointCount
					|| lweight != weight || ldensity != density)
			{
				lpointCount = pointCount;
				lweight = weight;
				ldensity = density;
				
				working = true;
				final Thread t = new Thread(this);
				t.start();
			}
		}
	}
	
	public void setup()
	{
		size(900, 750, OPENGL);
		cam = new PeasyCam(this, 100);
		cam.lookAt(0, 0, -100);
		cam.setMinimumDistance(50);
		cam.setMaximumDistance(600);
		
		controls = new Controls(this);
		controls.setSize(250, 600);
	}
	
	// end effector domain, lower and upper bounds
	public final PVector domainLower = new PVector(-100, -100, -300);
	public final PVector domainUpper = new PVector(100, 100, -20);
	
	public PVectorParameter targetPosition = new PVectorParameter(domainLower, domainUpper);
	public PVectorParameter jampotPosition = new PVectorParameter(domainLower, domainUpper);
	
	public FloatParam theta0 = new FloatParam(-180, 180);
	public FloatParam theta1 = new FloatParam(-180, 180);
	public FloatParam theta2 = new FloatParam(-180, 180);
	
	public FloatParam f_baseSide = new FloatParam(1, 300, geometry.getF());
	public FloatParam rf_upperArm = new FloatParam(1, 300, geometry.getRf());
	
	public FloatParam e_endEffectorSide = new FloatParam(1, 300, geometry.getE());
	public FloatParam re_lowerArm = new FloatParam(1, 300, geometry.getRe());
	
	public FloatParam stroke = new FloatParam(1, 16, wa.stroke);
	public FloatParam weight = new FloatParam(1, 16, wa.weight);
	public FloatParam density = new FloatParam(0, 3, wa.density);
	public FloatParam pointCount = new FloatParam(100, 100000, wa.pointCount);
	public FloatParam opacity = new FloatParam(0, 255, 80);
	public FloatParam method = new FloatParam(0, 2, 1);
	
	public void draw()
	{
		lights();
		background(20);
		
		geometry.setE(e_endEffectorSide.value());
		geometry.setF(f_baseSide.value());
		geometry.setRe(re_lowerArm.value());
		geometry.setRf(rf_upperArm.value());
		wa.pointCount = (int) pointCount.value();
		wa.weight = weight.value();
		wa.stroke = stroke.value();
		wa.opacity = (int) opacity.value();
		wa.method = (int) method.value();
		wa.density = density.value();
		wa.update();
		
		// final float r = 200;
		// final float eyeX = r * cos(map(mouseX,0,width,-TWO_PI,TWO_PI));
		// final float eyeY = r * sin(map(mouseY, 0, width, -TWO_PI, TWO_PI));
		// final float eyeZ = r;
		
		// camera(eyeX, eyeY, eyeZ, 0, 0, 0, 0, 0, 1);
		// rotateX()
		
		pushMatrix();
		translate(0, 0, -200);
		// grid
		stroke(255, 255, 255, 64);
		final int s = 16;
		for (int i = -10; i < 10; i++)
			line(i * s, -10 * s, i * s, 10 * s);
		
		for (int i = -10; i < 10; i++)
			line(-10 * s, i * s, 10 * s, i * s);
		popMatrix();
		
		pushMatrix();
		translate(0, 0, -100);
		
		// world axis
		stroke(255, 0, 0);
		line(0, 0, 0, 10, 0, 0);
		stroke(0, 255, 0);
		line(0, 0, 0, 0, 10, 0);
		stroke(0, 0, 255);
		line(0, 0, 0, 0, 0, 10);
		popMatrix();
		
		
		strokeWeight(1);
		
		// paint supply
		pushMatrix();
		translate(jampotPosition.value());
		final int k = 4;
		for (int i = 0; i < k; i++)
			rotateZ((PI / (float) (k * 4)) * i);
		scale(2, 2, 4);
		box(4);
		popMatrix();
		
		
		// IK
		final PVector ik = targetPosition.value();
		pushMatrix();
		translate(ik);
		fill(ikSourceColor.value());
		box(boxSize.value());
		popMatrix();
		
		
		target.setPosition(p(ik));
		
		/*
		 * final PVector t = kinematics.calcInverse(ik.x, ik.y, ik.z); if (t !=
		 * null) { plotter.target = ik; plotter.theta = map(t, -360, 360,
		 * -TWO_PI, TWO_PI); }
		 */
		
		plotter.draw();
		
		// work area
		wa.draw();
		
		// arduino.setLowerTiming(servoShortTiming.value());
		// arduino.setUpperTiming(servoLongTiming.value());
	}
	
	public Position p(PVector v)
	{
		return new Position(v.x, v.y, v.z);
	}
	
	
	public void fill(Color color)
	{
		fill(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public void translate(PVector v)
	{
		translate(v.x, v.y, v.z);
	}
	
	/** like {@link PApplet.map()}, but for PVectors in bounding cube */
	public static PVector map(PVector v, float from_min, float from_max, float to_min, float to_max)
	{
		final float x = map(v.x, from_min, from_max, to_min, to_max);
		final float y = map(v.y, from_min, from_max, to_min, to_max);
		final float z = map(v.z, from_min, from_max, to_min, to_max);
		return new PVector(x, y, z);
	}
	
	/** get i'th component from vector */
	public static float component(PVector v, int i)
	{
		switch (i)
		{
			case 0:
				return v.x;
			case 1:
				return v.y;
			case 2:
			default:
				return v.z;
		}
	}
	
	
}
