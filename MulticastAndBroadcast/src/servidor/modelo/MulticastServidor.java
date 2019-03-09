package servidor.modelo;

import java.io.*;
import java.net.*;

import javax.swing.JFileChooser;

public class MulticastServidor {
	
	public static void enviar() throws IOException {
		File f = new File("ArchivoClientes.txt");
		DatagramSocket dSock = new DatagramSocket();
		DatagramPacket dPacket = null;
		InetAddress dirGrupo = InetAddress.getByName("239.1.2.2");

		byte[] buf = new byte[(int) f.length()];
		dPacket = new DatagramPacket(buf, buf.length, dirGrupo, 5000);
		dSock.send(dPacket);
		dSock.close();

	}

	public static void main(String[] args) throws IOException {
		boolean continuar = true;
		MulticastServidor server = new MulticastServidor();
		while (continuar) {
			server.enviar();

		}

	}

}
