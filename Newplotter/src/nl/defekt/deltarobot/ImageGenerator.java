package nl.defekt.deltarobot;

import processing.core.PApplet;

public class ImageGenerator extends PApplet
{
	public void setup()
	{
		size(600, 400);
	}
	
	public void draw()
	{
		pushMatrix();
		// translate(width / 2, height / 2);
		for (int i = 0; i < 10; i++)
		{
			beginShape();
			vertex(0, 0);
			for (int j = 0; j < 10; j++)
			{
				final float cx1 = (float) (Math.random() * width);
				final float cy1 = (float) (Math.random() * height);
				final float cx2 = (float) (Math.random() * width);
				final float cy2 = (float) (Math.random() * height);
				final float px = (float) (Math.random() * width);
				final float py = (float) (Math.random() * height);
				bezierVertex(cx1, cy1, cx2, cy2, px, py);
			}
			endShape();
		}
		popMatrix();
	}
}
