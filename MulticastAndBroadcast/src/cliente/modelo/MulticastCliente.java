package cliente.modelo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import cliente.interfaz.VentanaCliente;

public class MulticastCliente extends Thread {
	public final static int TIEMPO_ESPERA_MULTICAST = 10000;
	public final static String IP = "239.1.2.2";
	private MulticastSocket ms;
	private InetAddress grupo;
	private boolean escuchar;

	public MulticastCliente(String ip) {
		escuchar = true;
		conectar(ip);
		start();
	}

	private void conectar(String ip) {
		if (CambiarGrupo(ip))
			VentanaCliente.LOG("Conectado a Multicast en: ", ip);
		else
			VentanaCliente.LOG("No se ha podido conectar a Multicast en: ", ip);
	}

	public boolean CambiarGrupo(String ip) {
		boolean cambio = false;
		try {
			if (grupo != null)
				ms.leaveGroup(grupo);
			grupo = InetAddress.getByName(ip);
			ms = new MulticastSocket(5000);
			ms.joinGroup(grupo);
			cambio = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cambio;
	}

	@Override
	public void run() {
		try {
			while (escuchar) {
				recibir();
				// System.out.println("Recibiendo multicast...");
				sleep(500);
			}
		} catch (InterruptedException e) {
			System.err.println(e);
		}
	}

	private void recibir() {
		try {

			byte[] buf = new byte[6000];
			DatagramPacket recibe = new DatagramPacket(buf, buf.length);
			ms.setSoTimeout(TIEMPO_ESPERA_MULTICAST);
			ms.receive(recibe);
			String filename = "data/txt/" + grupo.getHostName() + "_" + System.currentTimeMillis() + "_"
					+ (Math.random() * 3000) + ".txt";
			File o = new File(filename);

			FileOutputStream oo = new FileOutputStream(o);
			// ByteArrayOutputStream out = new ByteArrayOutputStream();

			oo.write(buf);
			oo.flush();
			oo.close();
			VentanaCliente.LOG("Se ha recibido el archivo por multicast: ", filename);
		} catch (SocketTimeoutException e) {

		} catch (IOException e) {
			System.err.println(e);
			VentanaCliente.LOG("Error al recibir un archivo txt.");
		}

	}

	public String getIP() {
		return grupo.getHostAddress();
	}

}
