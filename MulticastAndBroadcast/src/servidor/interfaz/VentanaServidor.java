package servidor.interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import servidor.modelo.Comunicacion;
import servidor.modelo.MulticastServidor;

public class VentanaServidor extends JFrame {

	public static VentanaServidor REF;
	private Comunicacion com;

	private JButton enviarMulticast;
	private JButton enviarBroadcast;
	private JPanel panelUsuarios;
	private JPanel panelMulticast;
	private JPanel panelLogs;
	private JTextArea txtLog;

	/**
	 * Inicializa la interfaz de la aplicación.
	 */
	public VentanaServidor() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		enviarBroadcast = new JButton("Enviar por Broadcast (JPG)");
		enviarBroadcast.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				enviarBroadcast();
			}
		});
		enviarMulticast = new JButton("Enviar por Multicast (TXT)");
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
	}

	/**
	 * Inicializa la comunicación y actualiza la interfaz.
	 */
	public void iniciar() {
		com = new Comunicacion(this);
		refreshUsuarios();
		refreshGroups(new String[0]);
	}

	/**
	 * Método Main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		REF = new VentanaServidor();
		REF.iniciar();
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
	public void refreshGroups(String[] groups) {
		panelMulticast.removeAll();

		int ng = groups.length + 2;

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
		JButton btnGrupoDefecto = new JButton(MulticastServidor.IP + " (Grupo por defecto)");
		btnGrupoDefecto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cambiarGrupo(MulticastServidor.IP);
			}
		});
		panelMulticast.add(btnGrupoDefecto);
		if (ng > 2) {
			for (String g : groups) {
				String[] gInfo = g.split(" ");
				System.out.println(g);
				panelMulticast.add(new MCGButton(gInfo[0], Integer.parseInt(gInfo[1])));
			}
		}
		pack();
	}

	/**
	 * Crea un nuevo grupo multicast
	 */
	public void crearGrupo() {
		String ipGrupo = JOptionPane.showInputDialog("Ingrese la IP del grupo. (Multicast)");
		if (ipGrupo == null || "".equals(ipGrupo)) {
			log("No se ha ingresado una IP");
			return;
		}
		com.crearGrupoMulticast(ipGrupo);
	}

	/**
	 * Cambia el grupo multicast al que se envían los archivos.
	 * 
	 * @param ip
	 *            Ip del nuevo grupo
	 */
	public void cambiarGrupo(String ip) {
		com.cambiarGrupoMulticast(ip);
	}

	/**
	 * Envía un archivo por broadcast.
	 */
	public void enviarBroadcast() {
		com.EnviarBroadCast();
	}

	/**
	 * Envía un archivo por multicast.
	 */
	public void enviarMulticast() {
		com.enviarMulticast();
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
	 * Muestra los mensajes en la interfaz separados por saltos de línea.
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
	 * LLama el método log.
	 * 
	 * @param params
	 *            texto a imprimir.
	 */
	public final static void LOG(String... params) {
		if (REF != null)
			REF.log(params);
		else
			System.out.println("Error al encontrar el log de a interfaz");
	}

	/**
	 * Llama el método logln
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
	 * Botón para cambiar de grupo multicast.
	 */
	private class MCGButton extends JButton {
		public MCGButton(String ip, int count) {
			super(ip + " " + count + "/5");
			this.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					cambiarGrupo(ip);
				}
			});
		}

	}

}
