package guired;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class GLog extends JLabel {
	
	public GLog() {
		super();
		setBounds(50, 3, 700, 30);
		//output("Bienvenido al programa", Color.white);
	}
	
	public void output(String msg, boolean error) {
		setText(msg);
		setToolTipText(msg);
		setForeground(error?Color.red:Color.white);
	}
}
