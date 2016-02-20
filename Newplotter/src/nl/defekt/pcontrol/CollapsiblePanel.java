package nl.defekt.pcontrol;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class CollapsiblePanel extends JPanel
{
	public CollapsiblePanel(String text, final JComponent content)
	{
		super(new BorderLayout());
		
		// set background color
		
		// add content and make invisible
		add(content, BorderLayout.CENTER);
		content.setVisible(false);
		
		// add header
		final JLabel header = new JLabel(text);
		header.setBackground(new Color(200, 200, 220));
		header.setOpaque(true);
		header.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				content.setVisible(!content.isShowing());
			}
		});
		
		add(header, BorderLayout.NORTH);
	}
}
