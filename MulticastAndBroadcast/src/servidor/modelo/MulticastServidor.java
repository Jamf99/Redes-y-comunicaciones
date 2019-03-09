package servidor.modelo;

import java.io.*;
import java.net.*;

import javax.swing.JFileChooser;

public class MulticastServidor {

	private static byte[] readBytesFromFile(String filePath) {

		FileInputStream fileInputStream = null;
		byte[] bytesArray = null;

		try {

			File file = new File(filePath);
			bytesArray = new byte[(int) file.length()];

			// read file into bytes[]
			fileInputStream = new FileInputStream(file);
			fileInputStream.read(bytesArray);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return bytesArray;

	}

	public static void enviar() throws IOException {
		File f = new File("ArchivoClientes.txt");
		DatagramSocket dSock = new DatagramSocket();
		DatagramPacket dPacket = null;
		InetAddress dirGrupo = InetAddress.getByName("239.1.2.2");

		// ----------------------
		// MANDAR MENSJAE
		// ----------------------
		byte[] buf = readBytesFromFile(f.getPath());
		// --------------------------

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
