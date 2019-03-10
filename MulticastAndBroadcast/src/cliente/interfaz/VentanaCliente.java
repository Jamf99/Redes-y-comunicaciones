package cliente.interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import cliente.modelo.Comunicacion;
import servidor.interfaz.VentanaServidor;

public class VentanaCliente extends JFrame {
	public static VentanaCliente REF;

	private JPanel panelUsuarios;
	private JPanel panelMulticast;
	private JPanel panelLogs;
	private JTextArea txtLog;
	private PanelImage panelImagen;
	private Comunicacion com;

	/**
	 * Inicializa la intefaz de la aplicaci�n.
	 */
	public VentanaCliente() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		JPanel puContainer = new JPanel();
		puContainer.setBorder(new TitledBorder("Info Usuarios"));

		JPanel muContainer = new JPanel();
		muContainer.setBorder(new TitledBorder("Info Canales"));

		panelUsuarios = new JPanel();

		panelMulticast = new JPanel();

		puContainer.add(new JScrollPane(panelUsuarios));
		muContainer.add(new JScrollPane(panelMulticast));

		JPanel topInfoCOntaier = new JPanel();
		topInfoCOntaier.setLayout(new GridLayout(1, 2));
		topInfoCOntaier.add(puContainer);
		topInfoCOntaier.add(muContainer);
		// topInfoCOntaier.setPreferredSize(new Dimension(100, 100));

		panelLogs = new JPanel();
		txtLog = new JTextArea();
		panelLogs.setBorder(new TitledBorder("Log de la aplicaci�n"));
		panelLogs.setLayout(new GridLayout(1, 1));
		panelLogs.add(new JScrollPane(txtLog));
		panelLogs.setPreferredSize(new Dimension(300, 200));
		txtLog.setEditable(false);

		panelImagen = new PanelImage();
		panelImagen.setPreferredSize(new Dimension(250, 100));

		add(topInfoCOntaier, BorderLayout.NORTH);
		add(panelImagen, BorderLayout.CENTER);
		add(panelLogs, BorderLayout.SOUTH);

		setBounds(250, 150, 600, 400);
		pack();

	}

	/**
	 * Inicializa la conexi�n de la aplicaci�n.
	 */
	public void iniciar() {
		com = new Comunicacion(this);
		refreshUsuarios();
		refreshGroups();
	}

	/**
	 * Main de la aplicaci�n.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		REF = new VentanaCliente();
		REF.iniciar();
		REF.setVisible(true);
	}

	/**
	 * Actualiza la visualizaci�n del panel de usuarios
	 */
	public void refreshUsuarios() {
		panelUsuarios.removeAll();
		String[] usrs = com.getUsuarios();
		int nu = usrs.length + 1;
		panelUsuarios.setLayout(new GridLayout(nu, 1));
		if (nu == 1) {
			panelUsuarios.add(new JLabel("No hay usuarios conectados"));
		} else {
			for (String u : usrs) {
				panelUsuarios.add(new JLabel(u));
			}
		}
		pack();
	}

	/**
	 * Actualiza la visualizaci�n del panel de de grupos multicast
	 */
	public void refreshGroups() {
		panelMulticast.removeAll();
		String[] grs = com.getGrupos();
		int ng = grs.length + 1;
		panelMulticast.setLayout(new GridLayout(ng, 1));
		if (ng > 1) {
			for (String g : grs) {
				String[] gInfo = g.split(" ");
				panelMulticast.add(new MCGButton(gInfo[0], Integer.parseInt(gInfo[1])));
			}
		}
		pack();
	}

	/**
	 * Cambbia el grupo muticast
	 * 
	 * @param ip
	 *            IP del nuevo grupo.
	 */
	public void cambiarGrupo(String ip) {
		com.cambiarGrupoMulticast(ip);
	}

	/**
	 * Muestra los mensajes en la interfaz separados por espacios.
	 * 
	 * @param params
	 *            texto a imprimir.
	 */
	public void log(String... params) {
		for (String str : params) {
			txtLog.append(str + " ");
		}
		txtLog.append("\n");
	}

	/**
	 * Muestra los mensajes en la interfaz separados por saltos de l�nea.
	 * 
	 * @param params
	 *            texto a imprimir.
	 */
	public void logln(String... params) {
		for (String str : params) {
			txtLog.append(str + "\n");
		}
	}

	/**
	 * Llama el m�todo log.
	 * 
	 * @param paramstexto
	 *            a imprimir.
	 */
	public final static void LOG(String... params) {
		if (REF != null)
			REF.log(params);
		else
			System.out.println("Error al encontrar el log de a interfaz");
	}

	/**
	 * Llama el m�todo logln.
	 * 
	 * @param params
	 *            texto a imprimir.
	 */
	public final static void LOGLN(String... params) {
		if (REF != null)
			REF.logln(params);
		else
			System.out.println("Error al encontrar el log de a interfaz");
	}

	/**
	 * Actualiza la imagen a mostrar.
	 * 
	 * @param bf
	 *            Buffer con la imagen recibida.
	 */
	public final static void SET_BF(BufferedImage bf) {
		if (REF != null && REF.panelImagen != null)
			REF.panelImagen.setBf(bf);
	}

	/**
	 * Panel en el que se pinta una iimagen recibida del servidor.
	 */
	private class PanelImage extends JPanel {
		private BufferedImage bf;

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponents(g);
			if (bf != null) {
				// Calcula el tama�o de la imagen a pintar
				double s, w, h;
				w = Math.max(bf.getWidth(), 1);
				h = Math.max(bf.getHeight(), 1);
				s = Math.min(Math.min(this.getWidth() / w, this.getHeight() / h), 1);
				// pinta la imagen recibida
				g.drawImage(bf, 0, 0, (int) (w * s), (int) (h * s), this);
			}

		}

		/**
		 * @param bf
		 *            Buffer con el contenido de la imagen
		 */
		public void setBf(BufferedImage bf) {
			this.bf = bf;
			this.repaint();
		}

	}

	/**
	 * Bot�n para cambiar de grupo multicast.
	 */
	private class MCGButton extends JButton {
		public MCGButton(String ip, int count) {
			super(ip + " " + count + "/5");
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (count < 5) {
						cambiarGrupo(ip);
					} else {
						VentanaServidor.LOG("El grupo ya est� lleno");
					}
				}
			});
		}

	}

}
