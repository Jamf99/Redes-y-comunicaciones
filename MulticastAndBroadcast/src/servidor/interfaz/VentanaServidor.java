package servidor.interfaz;

import javax.swing.JFrame;

public class VentanaServidor extends JFrame {
	
	public VentanaServidor() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}	

	public static void main(String[] args) {
		(new VentanaServidor()).setVisible(true);
	}
	


}
