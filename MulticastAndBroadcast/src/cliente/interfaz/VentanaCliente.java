package cliente.interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

public class VentanaCliente extends JFrame {
	public static VentanaCliente REF;

	private JPanel panelUsuarios;
	private JPanel panelMulticast;
	private JPanel panelLogs;
	private JTextArea txtLog;

	private Comunicacion com;

	public VentanaCliente() {
		com = new Comunicacion(this);
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
		panelLogs.setBorder(new TitledBorder("Log de la aplicación"));
		panelLogs.setLayout(new GridLayout(1, 1));
		panelLogs.add(new JScrollPane(txtLog));
		panelLogs.setPreferredSize(new Dimension(300, 200));
		txtLog.setEditable(false);

		add(topInfoCOntaier, BorderLayout.NORTH);
		add(panelLogs, BorderLayout.SOUTH);

		setBounds(250, 150, 600, 400);
		pack();

		refreshUsuarios();
		refreshGroups();

	}

	public static void main(String[] args) {
		REF = new VentanaCliente();
		REF.setVisible(true);
	}

	/**
	 * Actualiza la visualización del panel de usuarios
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
	 * Actualiza la visualización del panel de de grupos multicast
	 */
	public void refreshGroups() {
		panelMulticast.removeAll();
		String[] grs = com.getGrupos();
		int ng = grs.length + 1;
		panelMulticast.setLayout(new GridLayout(ng, 1));
		if (ng > 1) {
			for (String g : grs) {
				panelMulticast.add(new JLabel(g));
			}
		}
		pack();
	}

	public void log(String... params) {
		for (String str : params) {
			txtLog.append(str + " ");
		}
		txtLog.append("\n");
	}

	public void logln(String... params) {
		for (String str : params) {
			txtLog.append(str + "\n");
		}
	}

	public final static void LOG(String... params) {
		if (REF != null)
			REF.log(params);
		else
			System.out.println("Error al encontrar el log de a interfaz");
	}

	public final static void LOGLN(String... params) {
		if (REF != null)
			REF.logln(params);
		else
			System.out.println("Error al encontrar el log de a interfaz");
	}
}
