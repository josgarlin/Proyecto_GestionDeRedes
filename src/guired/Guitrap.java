package guired;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.shadows.DropShadowBorder;
import mdlaf.utils.MaterialColors;

public class Guitrap extends JFrame implements Runnable {
	private Point initialClick;
	private JTextArea traps;
	private String ip;
	
	// GUI
	
	public Guitrap(String ip) {
		super();
		this.ip = ip;
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
	    
		JLabel title = new JLabel("Gestión de red - Trap listener");
		title.setBounds(10, 0, 300, 40);
		top.add(title);
		
		JButton topButton = new JButton ("X");
		topButton.setBounds(760, 2, 35, 35);
		top.add(topButton);
		
		topButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});		
		
		MaterialUIMovement.add (topButton, MaterialColors.GRAY_100);
		
		add(top);

		setLocationRelativeTo(null);
		
		traps = new JTextArea();
		traps.setText("Escuchando traps...");
		traps.setBackground(Color.decode("#c8e6c9"));
		traps.setFont(new Font("Monospaced", Font.PLAIN, 13));
		add(traps);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 50, 770,500);
		scrollPane.setBackground(Color.decode("#c8e6c9"));
		scrollPane.setBorder(new DropShadowBorder());
		scrollPane.setViewportView(traps);
		add(scrollPane);
        
        pack();
        setVisible(true);
					
	}
	
	@Override
	public void run() {
		try {
			Guimain.SNMP.listen(traps, ip);
		} catch (IOException e) {
			// e.printStackTrace();
			Guimain.LOG.output("ERROR EN TRAPLISTEN", true);
		}
	}	
	
	public void exit() {
		Guimain.t = null;
		Guimain.SNMP.listenStop();
		dispose();
	}
}


// sysuptime
// sudo snmptrap -v 2c -c public 192.168.1.91 '' .1.3.6.1.2.1.1.3
// se recibe oid.0 y el value
// se recibe snmptrapoid y el oid