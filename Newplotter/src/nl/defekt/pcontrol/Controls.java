package nl.defekt.pcontrol;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class BoundedFloatControl extends JPanel
{
	public BoundedFloatControl(final FloatParam param)
	{
		this(param, true);
	}
	
	public BoundedFloatControl(final FloatParam param, boolean horizontal)
	{
		final JSlider slider = new JSlider((horizontal ? JSlider.HORIZONTAL : JSlider.VERTICAL), 0,
				100000, 100000 / 2);
		
		slider.setMajorTickSpacing(100000 / 5);
		slider.setMinorTickSpacing(100000 / 35);
		
		final JTextField lower = new JTextField(Float.toString(param.getLowerBound()));
		final JTextField upper = new JTextField(Float.toString(param.getUpperBound()));
		lower.setHorizontalAlignment(SwingConstants.LEFT);
		upper.setHorizontalAlignment(SwingConstants.RIGHT);
		
		final JTextField value = new JTextField(Float.toString(param.value()));
		value.setHorizontalAlignment(SwingConstants.CENTER);
		value.setEditable(false);
		lower.setEditable(false);
		upper.setEditable(false);

		
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				param.setValue(((float) slider.getValue()) / 100000f);
				value.setText(Float.toString(param.value()));
			}
		});
		
		setLayout(new BorderLayout());
		add(upper, BorderLayout.EAST);
		add(lower, BorderLayout.WEST);
		add(slider, BorderLayout.NORTH);
		add(value, BorderLayout.SOUTH);
	}
}

class XYControl extends JPanel
{
	public float x = 0.5f;
	public float y = 0.5f;
	
	public XYControl(final Vector2Parameter param)
	{
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e)
			{
				x = (float) e.getPoint().x / (float) getWidth();
				y = (float) e.getPoint().y / (float) getHeight();
				repaint();
				
				param.setValue(x, y);
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		final int w = getWidth();
		final int h = getHeight();
		final int px = Math.round(w * x);
		final int py = Math.round(h * y);
		
		g.setColor(Color.BLUE);
		g.drawRect(px - 1, py - 1, 3, 3);
		
		g.setColor(Color.GRAY);
		g.drawLine(0, h / 2, w, h / 2);
		g.drawLine(w / 2, 0, w / 2, h);
	}
	
	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(120, 120);
	}
}


class Vector3Control extends JPanel
{
	final protected BoundFloatPVectorAdapter z;
	final protected Vector2PVectorAdapter xy;
	
	public Vector3Control(PVectorParameter param)
	{
		xy = new Vector2PVectorAdapter(param);
		z = new BoundFloatPVectorAdapter(param);
		
		setLayout(new BorderLayout());
		add(new JLabel("xy = plane, z = slider"), BorderLayout.NORTH);
		add(new XYControl(xy), BorderLayout.CENTER);
		add(new BoundedFloatControl(z, false), BorderLayout.EAST);
	}
}

class ColorControl extends JColorChooser
{
	protected final ColorParameter param;
	
	public ColorControl(ColorParameter p)
	{
		this.param = p;
		
		getSelectionModel().addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e)
			{
				param.setColor(getColor());
				System.out.println("set clor");
			}
		});
	}
	
}

public class Controls extends JFrame
{
	final HashMap<String,JComponent> controls = new HashMap<String,JComponent>();
	
	public Controls(Object obj)
	{
		super("controls");
		
		setLocation(930, 10);
		
		final Container c = getContentPane();
		
		c.setLayout(new BoxLayout(c, BoxLayout.PAGE_AXIS));
		
		// reflect all fields
		for (Field f : obj.getClass().getFields())
		{
			try
			{
				if (f.getType().equals(PVectorParameter.class))
				{
					final PVectorParameter p = (PVectorParameter) f.get(obj);
					c.add(new CollapsiblePanel("[PVECTOR] " + f.getName(), new Vector3Control(p)));
				}
				
				if (f.getType().equals(Vector2Parameter.class))
				{
					final Vector2Parameter p = (Vector2Parameter) f.get(obj);
					c.add(new CollapsiblePanel("[XY] " + f.getName(), new XYControl(p)));
				}
				
				if (f.getType().equals(FloatParam.class))
				{
					final FloatParam p = (FloatParam) f.get(obj);
					c.add(new CollapsiblePanel("[FLOAT] " + f.getName(), new BoundedFloatControl(p)));
				}
				
				if (f.getType().equals(ColorParameter.class))
				{
					final ColorParameter p = (ColorParameter) f.get(obj);
					c.add(new CollapsiblePanel("[COLOR] " + f.getName(), new ColorControl(p)));
				}
			}
			catch (IllegalArgumentException e)
			{
				e.printStackTrace();
				continue;
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
				continue;
			}
			
		}
		
		c.add(Box.createGlue());
		pack();
		setVisible(true);
	}
}