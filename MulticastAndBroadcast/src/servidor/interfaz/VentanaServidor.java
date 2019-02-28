package servidor.interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public class VentanaServidor extends JFrame {

	public static VentanaServidor REF;
	private Comunicacion com;

	private JButton enviarMulticast;
	private JButton enviarBroadcast;
	private JPanel panelUsuarios;
	private JPanel panelMulticast;
	private JPanel panelLogs;
	private JTextArea txtLog;

	public VentanaServidor() {
		com = new Comunicacion(this);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		enviarBroadcast = new JButton("Enviar por Broadcast");
		enviarBroadcast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				enviarBroadcast();
			}
		});
		enviarMulticast = new JButton("Enviar por Multicast");
		enviarMulticast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				enviarMulticast();
			}
		});

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
		panelLogs.setPreferredSize(new Dimension(100, 200));
		txtLog.setEditable(false);
		add(enviarBroadcast, BorderLayout.EAST);
		add(enviarMulticast, BorderLayout.WEST);
		add(topInfoCOntaier, BorderLayout.NORTH);
		add(panelLogs, BorderLayout.SOUTH);

		setBounds(250, 150, 600, 400);
		pack();

		refreshUsuarios();
		refreshGroups();
	}

	public static void main(String[] args) {
		REF = new VentanaServidor();
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
		String[] usrs = com.getUsuarios();
		int ng = usrs.length + 1;

		panelMulticast.setLayout(new GridLayout(ng, 1));
		if (ng <= 10) {
			JButton btnCrearGrupo = new JButton("Crear Grupo");
			btnCrearGrupo.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					crearGrupo();
				}
			});
			panelMulticast.add(btnCrearGrupo);
		}

		if (ng > 1) {
			for (String u : usrs) {
				panelMulticast.add(new JLabel(u));
			}
		}
		pack();
	}

	public void crearGrupo() {

	}

	public void enviarBroadcast() {

	}

	public void enviarMulticast() {

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
