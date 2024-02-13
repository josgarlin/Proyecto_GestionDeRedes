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
import javax.swing.Timer;

import mdlaf.shadows.DropShadowBorder;

public class Guisondea extends JPanel {
	private JTextArea sondeo, alarmas;
	private Timer timer;
	private long[] out, in;
	private long umbIN, umbOUT;
	private int desde, hasta;
	
	// GUI
	
	public Guisondea() {
		super();
		setBackground(Color.decode("#c8e6c9"));
		setBounds(0, 0, 800, 600);
		setLayout(null);
		
		sondeo = new JTextArea();
		sondeo.setText("Sondeando...");
		sondeo.setBackground(Color.decode("#c8e6c9"));
		sondeo.setFont(new Font("Monospaced", Font.PLAIN, 13));
		add(sondeo);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 50, 400,250);
		scrollPane.setBackground(Color.decode("#c8e6c9"));
		scrollPane.setBorder(new DropShadowBorder());
		scrollPane.setViewportView(sondeo);
		add(scrollPane);
		
		JTextField umbralOUT = new JTextField();
		umbralOUT.setHorizontalAlignment(SwingConstants.CENTER);
		umbralOUT.setText("0");
		umbralOUT.setToolTipText("Umbral alarma out. En octetos. 0 para no alarma");
		umbralOUT.setBounds(500, 60, 100, 26);
		umbralOUT.setColumns(10);	
		add(umbralOUT);
		
		JLabel lblNewLabel1 = new JLabel("Umbral OCT OUT");
		lblNewLabel1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel1.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel1.setBounds(605, 60, 220, 26);
		add(lblNewLabel1);
		
		JTextField umbralIN = new JTextField();
		umbralIN.setHorizontalAlignment(SwingConstants.CENTER);
		umbralIN.setText("0");
		umbralIN.setToolTipText("Umbral alarma in. En octetos. 0 para no alarma");
		umbralIN.setBounds(500, 100, 100, 26);
		umbralIN.setColumns(10);	
		add(umbralIN);
		
		JLabel lblNewLabel2 = new JLabel("Umbral OCT IN");
		lblNewLabel2.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel2.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel2.setBounds(605, 100, 220, 26);
		add(lblNewLabel2);
		
		JTextField desde = new JTextField();
		desde.setHorizontalAlignment(SwingConstants.CENTER);
		desde.setText("0");
		desde.setBounds(500, 150, 100, 26);
		desde.setColumns(10);	
		add(desde);		
		
		JTextField hasta = new JTextField();
		hasta.setHorizontalAlignment(SwingConstants.CENTER);
		hasta.setText("255");
		hasta.setBounds(605, 150, 100, 26);
		hasta.setColumns(10);	
		add(hasta);		
		
		alarmas = new JTextArea();
		alarmas.setText("Alarmas...");
		alarmas.setBackground(Color.decode("#c8e6c9"));
		alarmas.setFont(new Font("Monospaced", Font.PLAIN, 13));
		add(alarmas);
		
		JScrollPane scrollPane2 = new JScrollPane();
		scrollPane2.setBounds(10, 300, 770,250);
		scrollPane2.setBackground(Color.decode("#c8e6c9"));
		scrollPane2.setBorder(new DropShadowBorder());
		scrollPane2.setViewportView(alarmas);
		add(scrollPane2);
		
		JButton buttonstart = new JButton ("COMENZAR");
		buttonstart.setBounds(500, 200, 100, 30);
		add(buttonstart);
		
		buttonstart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				alarmas.setText("");
				umbIN = Long.parseLong(umbralIN.getText());
				umbOUT = Long.parseLong(umbralOUT.getText());
				checkandstart(Integer.parseInt(desde.getText()), Integer.parseInt(hasta.getText()));				
			}
		});	
		
		JButton buttonstop = new JButton ("PARAR");
		buttonstop.setBounds(620, 200, 100, 30);
		add(buttonstop);
		
		buttonstop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});	
						
		timer = new Timer(50,new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				update();
			}
		});
		timer.setRepeats(true);
		
		out = new long[255];		
		in = new long[255];
		
		for (int i = 0; i < out.length; i++)  {
			out[i] = 0;
			in[i] = 0;
		}
		
		umbIN = 0;
		umbOUT = 0;
	}
	
	// LOGIC
	private String ip;
	private int id = 0;
	
	public void start(String ip) {
		this.ip = ip.replace("0", "");
		sondeo.setText("Sondeando "+ip+"...");
		alarmas.setText("");
		id = desde;
	}
	
	public void stop() {
		timer.stop();
	}
	
	private void checkandstart(int desde, int hasta) {
		if (desde >= 0 && desde <= 255 && hasta >= 0 && hasta <= 255) {
			if (desde < hasta) {
				this.desde = desde;
				this.hasta = hasta;
				start(ip);
				timer.start();		
			} else {
				Guimain.LOG.output("El primer valor tiene que ser menor que el segundo valor", true);
			}
		} else {
			Guimain.LOG.output("Los valores deben estar entre 0 y 255", true);
		}		
	}
	
	private void update() {
		String resul = "";
		String selectedAddress = "";
		
		id++;
		
		if (id == hasta) {
			//sondeo.setText("");
			id = desde+1;
		}
		
		selectedAddress = ip + id;
		
		sondeo.setText(sondeo.getText()+"\nSondeando " +selectedAddress);

		if (Guimain.SNMP.getNext("1", selectedAddress)!="ERROR") {
			if (!Guimain.SNMP.getInOctects(selectedAddress).equals("noSuchInstance")) {
				long in = Long.parseLong(Guimain.SNMP.getInOctects(selectedAddress));
		
				if (this.in[id] == 0) {
					this.in[id] = in;
				}
	
				long resin = in - this.in[id];				
				resul += "        OCT in: "+resin;
				
				if (umbIN != 0 && umbIN < resin) {
					alarmas.setText(alarmas.getText() + ""
							+ "\nALARMA para "+selectedAddress+" superó OCTETOS IN: "+resin+ " ("+umbIN+")");
				}
	
				this.in[id] = in;
	
				long out = Long.parseLong(Guimain.SNMP.getOutOctects(selectedAddress));
				
				if (this.out[id] == 0) {
					this.out[id] = out;
				}
	
				long resout = out - this.out[id];				
				resul += "\n        OCT out: "+resout;
				
				if (umbOUT != 0 && umbOUT < resout) {
					alarmas.setText(alarmas.getText() + ""
							+ "\nALARMA para "+selectedAddress+" superó OCTETOS OUT: "+resout+ " ("+umbOUT+")");
				}
	
				this.out[id] = out;
				sondeo.setText(sondeo.getText()+"\n"+resul);
			}
		}		
	}
}
