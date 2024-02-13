package guired;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.shadows.RoundedCornerBorder;
import mdlaf.utils.MaterialColors;

public class Guigraph extends JFrame {
	private Point initialClick;
	private JLabel title;
	private Graphic graphicPanel;
	private Timer time;

	public Guigraph() {
		setMinimumSize (new Dimension (800, 600));
		setResizable(false);
		setUndecorated(true);
		getContentPane().setBackground(Color.decode("#c8e6c9"));
		setBounds(0, 0, 800, 600);
		getContentPane().setLayout(null);
		
		JPanel top = new JPanel();
		top.setBackground(Color.decode("#96b397"));
		top.setBounds(0, 0, 800, 40);
		top.setLayout(null);		
			
		top.addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            initialClick = e.getPoint();
	            getComponentAt(initialClick);
	        }
	    });
    
		top.addMouseMotionListener(new MouseMotionListener() {		
			@Override
			public void mouseMoved(MouseEvent e) {				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
	            int thisX = getLocation().x;
	            int thisY = getLocation().y;
	
	            int xMoved = e.getX() - initialClick.x;
	            int yMoved = e.getY() - initialClick.y;
	
	            int X = thisX + xMoved;
	            int Y = thisY + yMoved;
	            setLocation(X, Y);
			}
		});   
	    
		title = new JLabel("Gestión de red - OCTECTS (Y) / TIME (X)");
		title.setBounds(10, 0, 500, 40);
		top.add(title);
		
		JButton topButton = new JButton ("X");
		topButton.setBounds(760, 2, 35, 35);
		top.add(topButton);
		
		topButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				time.stop();
				time = null;
				dispose();
			}
		});		
		
		MaterialUIMovement.add (topButton, MaterialColors.GRAY_100);
		
		add(top);

		setLocationRelativeTo(null);
		
		graphicPanel = new Graphic();
		graphicPanel.setBounds(10, 50, 770, 530);
		graphicPanel.setBorder(new RoundedCornerBorder(Color.decode("#96b397")));
		
		add(graphicPanel);
        
        pack();
        setVisible(true);
	}
	
	public void dibujaIN(String address) {		
		title.setText("Gestión de red - "+address+" OCTECTS IN (Y) / TIME (X)");
		try {
			time = new Timer(1000,new Graphredupdate(address, true));
		} catch (NumberFormatException | IOException e) {
			// 			e.printStackTrace();
		}
		time.setRepeats(true);
		time.start();
	}
	
	public void dibujaOUT(String address) {		
		title.setText("Gestión de red - "+address+" OCTECTS OUT (Y) / TIME (X)");
		try {
			time = new Timer(1000,new Graphredupdate(address, false));
		} catch (NumberFormatException | IOException e) {
			// 			e.printStackTrace();
		}
		time.setRepeats(true);
		time.start();
	}
	
	public class Graphredupdate implements ActionListener {
		long i = 0;
		long j = 0;		
		long resul = 0;
		String address;
		boolean in;
		
		public Graphredupdate(String address, boolean in) throws NumberFormatException, IOException {
			this.address = address;
			this.in = in;
			i = 0;
			j = 0;		
			resul = 0;
			
			graphicPanel.actualiza((double) 0, 1000);
			
			if (in) {
				i = j = Long.parseLong(Guimain.SNMP.getInOctects(address));				
			} else {
				i = j = Long.parseLong(Guimain.SNMP.getOutOctects(address));
			}
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			paint(getGraphics());
			try {
				if (in) {
					j = Long.parseLong(Guimain.SNMP.getInOctects(address));					
				} else {
					j = Long.parseLong(Guimain.SNMP.getOutOctects(address));
				}
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			resul = j - i;
						
			graphicPanel.actualiza((double) resul, 1000);			
			i = j;				
		}
	}
}
