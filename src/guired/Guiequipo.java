package guired;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import mdlaf.shadows.DropShadowBorder;
import mdlaf.shadows.RoundedCornerBorder;

public class Guiequipo extends JPanel {
	private JLabel title, timeOn;	
	private JTextField txtname, txtequipo, txtcontact;
	private JTextArea txtroute;
	private String ip;

	// GUI
	
	public Guiequipo() {
		super();
		setBackground(Color.decode("#c8e6c9"));
		setBounds(0, 0, 800, 600);
		setLayout(null);
		
		JPanel pane = new JPanel();
		pane.setBackground(Color.decode("#ffffff"));
		pane.setBounds(50, 75, 700, 450);
		pane.setLayout(null);
		pane.setBorder(new RoundedCornerBorder(Color.decode("#96b397")));

		title = new JLabel("EQUIPO ");
		title.setHorizontalAlignment(SwingConstants.LEFT);
		title.setFont(new Font("Tahoma", Font.BOLD, 18));
		title.setBounds(10, 0, 423, 30);
		pane.add(title);
		
		JLabel name = new JLabel("Nombre: ");
		name.setHorizontalAlignment(SwingConstants.LEFT);
		name.setBounds(30, 35, 100, 30);
		pane.add(name);
		
		txtname = new JTextField();
		txtname.setHorizontalAlignment(SwingConstants.LEFT);
		txtname.setText("ej: 192.168.1.X");
		txtname.setBounds(135, 35, 500, 30);
		txtname.setColumns(10);	
		pane.add(txtname);
		
		txtname.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setName();
			}
		});
		
		JLabel equipo2 = new JLabel("Equipo: ");
		equipo2.setHorizontalAlignment(SwingConstants.LEFT);
		equipo2.setBounds(30, 70, 100, 30);
		pane.add(equipo2);
		
		txtequipo = new JTextField();
		txtequipo.setHorizontalAlignment(SwingConstants.LEFT);
		txtequipo.setText("ej: 192.168.1.X");
		txtequipo.setBounds(135, 70, 500, 30);
		txtequipo.setColumns(10);	
		pane.add(txtequipo);
		
		txtequipo.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setDescr();
			}
		});
		
		JLabel contact = new JLabel("Contacto: ");
		contact.setHorizontalAlignment(SwingConstants.LEFT);
		contact.setBounds(30, 105, 100, 30);
		pane.add(contact);
		
		txtcontact = new JTextField();
		txtcontact.setHorizontalAlignment(SwingConstants.LEFT);
		txtcontact.setText("ej: 192.168.1.X");
		txtcontact.setBounds(135, 105, 500, 30);
		txtcontact.setColumns(10);	
		pane.add(txtcontact);
		
		txtcontact.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				setContact();
			}
		});
		
		timeOn = new JLabel("Tiempo encendido: ");
		timeOn.setHorizontalAlignment(SwingConstants.LEFT);
		timeOn.setBounds(30, 140, 500, 30);
		pane.add(timeOn);
		
		JButton cpuButton = new JButton ("CPU info linux");
		cpuButton.setBounds(20, 185, 140, 30);
		cpuButton.setToolTipText("Click para ver valores");
		pane.add(cpuButton);
		
		cpuButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cpuButton.setToolTipText(
						"Cpu: " +
						Guimain.SNMP.getNextConfirma(Guimain.SNMP.cpuName, ip) +
						" - Uso: " +
						Guimain.SNMP.getNextConfirma(Guimain.SNMP.cpuUse, ip)+ " %"			
				);
			}
		});	
		
		JButton hddButton = new JButton ("HDD ºc windows");
		hddButton.setBounds(190, 185, 140, 30);
		hddButton.setToolTipText("Click para ver temperatura");
		pane.add(hddButton);
		
		hddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hddButton.setToolTipText(Guimain.SNMP.get(Guimain.SNMP.hddTemperature, ip, 2000) + " ºC");
			}
		});	
		
		JButton oinButton = new JButton ("Gfx OCTECTS IN");
		oinButton.setBounds(370, 185, 140, 30);
		pane.add(oinButton);
		
		oinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Guigraph().dibujaIN(title.getText());
			}
		});	
		
		JButton oout = new JButton ("Gfx OCTECTS OUT");
		oout.setBounds(540, 185, 140, 30);
		pane.add(oout);
		
		oout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Guigraph().dibujaOUT(title.getText());
			}
		});	
		
		txtroute = new JTextArea();
		txtroute.setText("route");
		txtroute.setBorder(new DropShadowBorder());
		txtroute.setFont(new Font("Monospaced", Font.PLAIN, 13));
		pane.add(txtroute);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 230, 680, 200);
		scrollPane.setViewportView(txtroute);
		pane.add(scrollPane);
				
		add(pane);
	}
	
	// LOGIC
	
	public void start(String ip) {
		this.ip = ip;
		title.setText(ip);
		txtname.setText(Guimain.SNMP.get(Guimain.SNMP.sysName, ip));
		txtequipo.setText(Guimain.SNMP.get(Guimain.SNMP.sysDescr, ip));
		txtcontact.setText(Guimain.SNMP.get(Guimain.SNMP.sysContact, ip));
		timeOn.setText("Tiempo encendido:    "+Guimain.SNMP.get(Guimain.SNMP.sysUpTime, ip));
		txtroute.setText("Route table: (4 indirecto, 3 directo, 2 invalid, 1 other)\n"+Guimain.SNMP.getRouteTable(ip));
	}
	
	public void stop() {		
	}
	
	private void setName() {
		Guimain.SNMP.set(Guimain.SNMP.sysName, title.getText(), txtname.getText());		
	}
	
	private void setDescr() {
		Guimain.SNMP.set(Guimain.SNMP.sysDescr, title.getText(), txtequipo.getText());				
	}
	
	private void setContact() {
		Guimain.SNMP.set(Guimain.SNMP.sysContact, title.getText(), txtcontact.getText());		
	}
}
