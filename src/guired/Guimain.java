package guired;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import mdlaf.animation.MaterialUIMovement;
import mdlaf.shadows.RoundedCornerBorder;
import mdlaf.utils.MaterialColors;

public class Guimain {
	public static GLog LOG;
	public static Usnmp SNMP;
	private Point initialClick;
	private JLabel title;
	private JButton topButton;
	private JPanel menuPrincipal;
	private JTextField menu1, menu2, menu3;

	private Guisondea guisondea;
	private Guiequipo guiequipo;
	
	// GUI
	
	public Guimain() {
		// Crea LOG OUTPUT y USNMP
		LOG = new GLog();
		SNMP = new Usnmp();
		
		// Crea principal frame
		JFrame frame = new JFrame ("Gestión de red");
		frame.setMinimumSize (new Dimension (800, 600));
		frame.setBounds(0, 0, 800, 600);
		frame.setResizable(false);
		frame.setUndecorated(true);
		frame.setBackground(Color.decode("#c8e6c9"));
		frame.setLayout(null);	
		frame.setLocationRelativeTo(null);

		// Crea panel superior
		JPanel top = new JPanel();
		top.setBackground(Color.decode("#96b397"));
		top.setBounds(0, 0, 800, 40);
		top.setLayout(null);		
				    
		title = new JLabel("Gestión de red");
		title.setBounds(10, 0, 300, 40);
		top.add(title);
		
		topButton = new JButton ("X");
		topButton.setBounds(760, 2, 35, 35);
		top.add(topButton);
		
		topButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pulsaBotonSuperior();
			}
		});		
		
		MaterialUIMovement.add (topButton, MaterialColors.GRAY_100);
		
		frame.add(top);
		
		// Crea panel inferior
		JPanel bottom = new JPanel();
		bottom.setBackground(Color.decode("#96b397"));
		bottom.setBounds(0, 570, 800, 30);
		bottom.setLayout(null);		 
	    
		JLabel log = new JLabel("LOG: ");
		log.setBounds(10, 0, 300, 40);
		bottom.add(log);
		
		bottom.add(LOG);		
		
		frame.add(bottom);
		
		// Add movilidad a ventana
		MouseAdapter movable = new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            initialClick = e.getPoint();
	            frame.getComponentAt(initialClick);
	        }
	    };
	    
	    MouseMotionListener movableListener = new MouseMotionListener() {		
			@Override public void mouseMoved(MouseEvent e) {}
			
			@Override
			public void mouseDragged(MouseEvent e) {
	            int thisX = frame.getLocation().x;
	            int thisY = frame.getLocation().y;
	
	            int xMoved = e.getX() - initialClick.x;
	            int yMoved = e.getY() - initialClick.y;
	
	            int X = thisX + xMoved;
	            int Y = thisY + yMoved;
	            frame.setLocation(X, Y);
			}
		};
		
		top.addMouseListener(movable);
		top.addMouseMotionListener(movableListener);
		

		
		// Add menuPrincipal
		menuPrincipal = new JPanel();
		menuPrincipal.setBackground(Color.decode("#c8e6c9"));
		menuPrincipal.setBounds(0, 0, 800, 600);
		menuPrincipal.setLayout(null);
		
		// Option1
		JPanel pane1 = new JPanel();
		pane1.setBackground(Color.decode("#ffffff"));
		pane1.setBounds(50, 150, 200, 350);
		pane1.setLayout(null);
		pane1.setBorder(new RoundedCornerBorder(Color.decode("#96b397")));

		JLabel lblNewLabel1 = new JLabel("SONDEO");
		lblNewLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel1.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel1.setBounds(39, 156, 123, 25);
		pane1.add(lblNewLabel1);
		
		JLabel lblNewLabel1_1 = new JLabel("");
		lblNewLabel1_1.setIcon(new ImageIcon(Guimain.class.getResource("/images/Imagen 1.png")));
		lblNewLabel1_1.setBounds(39, 21, 123, 124);
		pane1.add(lblNewLabel1_1);
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);		
		
		JTextPane txtinfo = new JTextPane();
		txtinfo.setText("Monitoriza el tr\u00E1fico de los distintos equipos que se encuentran en la red\r\n");
		txtinfo.setEditable(false);
		txtinfo.setBounds(24, 181, 156, 106);
		txtinfo.getStyledDocument().setParagraphAttributes(0, txtinfo.getStyledDocument().getLength(), center, false);;
		txtinfo.setBackground(Color.decode("#ffffff"));
		pane1.add(txtinfo);
		
		menu1 = new JTextField();
		menu1.setHorizontalAlignment(SwingConstants.CENTER);
		menu1.setText("ej: 192.168.1.0");
		menu1.setBounds(10, 296, 180, 26);
		menu1.setColumns(10);	
		pane1.add(menu1);
		
		menu1.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				menu1Selected();
			}
		});
		
		menuPrincipal.add(pane1);
		
		// Option2
		JPanel pane2 = new JPanel();
		pane2.setBackground(Color.decode("#ffffff"));
		pane2.setBounds(300, 150, 200, 350);
		pane2.setLayout(null);
		pane2.setBorder(new RoundedCornerBorder(Color.decode("#96b397")));

		JLabel lblNewLabel2 = new JLabel("VER EQUIPO");
		lblNewLabel2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel2.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel2.setBounds(39, 156, 123, 25);
		pane2.add(lblNewLabel2);
		
		JLabel lblNewLabel1_2 = new JLabel("");
		lblNewLabel1_2.setIcon(new ImageIcon(Guimain.class.getResource("/images/Imagen 2.png")));
		lblNewLabel1_2.setBounds(39, 21, 123, 124);
		pane2.add(lblNewLabel1_2);
				
		JTextPane txtinfo2 = new JTextPane();
		txtinfo2.setText("Muestra información del\r\n"
				+ "sistema e información de red\r\n"
				+ "de un determinado equipo\r\n"
				+ "\r\n");
		txtinfo2.setEditable(false);
		txtinfo2.setBounds(24, 181, 156, 106);
		txtinfo2.getStyledDocument().setParagraphAttributes(0, txtinfo2.getStyledDocument().getLength(), center, false);;
		txtinfo2.setBackground(Color.decode("#ffffff"));
		pane2.add(txtinfo2);
		
		menu2 = new JTextField();
		menu2.setHorizontalAlignment(SwingConstants.CENTER);
		menu2.setText("ej: 192.168.1.8");
		menu2.setBounds(10, 296, 180, 26);
		menu2.setColumns(10);	
		pane2.add(menu2);
		
		menu2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menu2Selected();
			}
		});
		
		menuPrincipal.add(pane2);
		
		// Option3
		JPanel pane3 = new JPanel();
		pane3.setBackground(Color.decode("#ffffff"));
		pane3.setBounds(550, 150, 200, 350);
		pane3.setLayout(null);
		
		pane3.setBorder(new RoundedCornerBorder(Color.decode("#96b397")));
		
		JLabel lblNewLabel3 = new JLabel("TRAP LISTEN");
		lblNewLabel3.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel3.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel3.setBounds(39, 156, 123, 25);
		pane3.add(lblNewLabel3);
		
		JLabel lblNewLabel1_3 = new JLabel("");
		lblNewLabel1_3.setIcon(new ImageIcon(Guimain.class.getResource("/images/Imagen 3.png")));
		lblNewLabel1_3.setBounds(39, 21, 123, 124);
		pane3.add(lblNewLabel1_3);
		
		JTextPane txtinfo3 = new JTextPane();
		txtinfo3.setText("Escucha los traps \r\n"
				+ "generados por los distintos\r\n"
				+ "equipos que los generen\r\n"
				+ "\r\n");
		txtinfo3.setEditable(false);
		txtinfo3.setBounds(24, 181, 156, 106);
		txtinfo3.getStyledDocument().setParagraphAttributes(0, txtinfo3.getStyledDocument().getLength(), center, false);;
		txtinfo3.setBackground(Color.decode("#ffffff"));
		pane3.add(txtinfo3);
		
		menu3 = new JTextField();
		menu3.setHorizontalAlignment(SwingConstants.CENTER);
		menu3.setText("ej: 192.168.1.9");
		menu3.setBounds(10, 296, 180, 26);
		menu3.setColumns(10);	
		pane3.add(menu3);
		
		menu3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				menu3Selected();
			}
		});
		
		menuPrincipal.add(pane3);
		
		frame.add(menuPrincipal);
		
		
		guisondea = new Guisondea();
		guisondea.setVisible(false);
		frame.add(guisondea);
		
		guiequipo = new Guiequipo();
		guiequipo.setVisible(false);
		frame.add(guiequipo);
		
		frame.pack();
		frame.setVisible(true);
	}
	
	// LOGIC
	
	private void pulsaBotonSuperior() {
		if (menuPrincipal.isVisible()) {
			exit();
		} else {
			title.setText("Gestión de red");
			topButton.setText("X");
			
			guisondea.stop();
			guisondea.setVisible(false);
			
			guiequipo.stop();
			guiequipo.setVisible(false);
			
			menuPrincipal.setVisible(true);
		}
	}
	
	private void menu1Selected() {
		if (compruebaCampos(true)) {
			menuPrincipal.setVisible(false);
			topButton.setText("<");
			title.setText("Gestión de red - Sondeo");		
			guisondea.start(menu1.getText());
			guisondea.setVisible(true);
		}
	}
	
	private void menu2Selected() {
		if (compruebaCampos(false)) {
			menuPrincipal.setVisible(false);
			topButton.setText("<");
			title.setText("Gestión de red - Informe de equipo");		
			guiequipo.start(menu2.getText());
			guiequipo.setVisible(true);
		}
	}
	
	public static Thread t = null;
	
	private void menu3Selected() {
		if (compruebaCamposMenu3()) {
			if (t == null) {
				t = new Thread(new Guitrap(menu3.getText()));
				t.start();
			} else {
				LOG.output("NO SE PUEDE ABRIR DOS TRAPS LISTENER!!", true);
			}
		}
	}
	
	private void exit() {
		LOG = null;
		SNMP = null;
		System.exit(0);
	}
	
	private boolean compruebaCampos(boolean lastValueZero) {
		String message = "OK";
		String ip = (lastValueZero?menu1:menu2).getText().replaceAll("[^\\d.]", "");
		String[] campos = ip.split("\\.");
		
		if (campos.length != 4) {
			message = "IP debe contener al menos 4 campos (ej: 10.10.10.0) - Actual: "+campos.length;
		} else {
			for (int i = 0; i < campos.length; i++) {
				int number = Integer.parseInt(campos[i]);
				if (!(number >= 0 && number < 255)) {
					message = "Los campos deben estar entre 0 y 255";
				} else if (lastValueZero) {
					if (i == 3 && number != 0) {
						message = "El último campo obligatoriamente tiene que ser 0";
					}
				}
			}
		}	
		
		if (!lastValueZero) {
			if (message.equals("OK") && Guimain.SNMP.getNext(".1", ip)=="ERROR") {
				message = "No se ha podido establecer conexión";
			}
		}
		
		Guimain.LOG.output(message, message!="OK");
		
		(lastValueZero?menu1:menu2).setText(ip);
				
		return message=="OK";
	}
	
	private boolean compruebaCamposMenu3() {
		String message = "OK";
		String ip = menu3.getText().replaceAll("[^\\d.]", "");
		String[] campos = ip.split("\\.");
		
		if (campos.length != 4) {
			message = "IP debe contener al menos 4 campos (ej: 10.10.10.0) - Actual: "+campos.length;
		} else {
			for (int i = 0; i < campos.length; i++) {
				int number = Integer.parseInt(campos[i]);
				if (!(number >= 0 && number < 255)) {
					message = "Los campos deben estar entre 0 y 255";
				}
			}
		}	
		
		Enumeration e;
		boolean result = false;
		try {
			e = NetworkInterface.getNetworkInterfaces();
			while(e.hasMoreElements())
			{
			    NetworkInterface n = (NetworkInterface) e.nextElement();
			    Enumeration ee = n.getInetAddresses();
			    while (ee.hasMoreElements() && !result)
			    {
			        InetAddress i = (InetAddress) ee.nextElement();
			        result = ip.equals(i.getHostAddress());
			    }
			}
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		if (!result) {
			message = "¡La dirección ip debe coincidir con la tuya!";
		}
		
		Guimain.LOG.output(message, message!="OK");
		
		menu3.setText(ip);
				
		return message=="OK";
	}
}

