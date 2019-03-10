package servidor.modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import servidor.interfaz.VentanaServidor;

public class MulticastServidor {
	public final static String IP = "239.1.2.2"; // IP base (todos pueden comenzar en este grupo)
	private MulticastSocket ms;
	private InetAddress grupo;

	public MulticastServidor() {
		conectar(IP);
	}

	private void conectar(String ip) {
		if (CambiarGrupo(ip))
			VentanaServidor.LOG("Conectado a Multicast en: ", ip);
		else
			VentanaServidor.LOG("No se ha podido conectar a Multicast en: ", ip);
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

	public void enviar() throws IOException {
		File f = BroadcastServidor.leerArchivo();// = new File("ArchivoClientes.txt");
		if (f == null) {
			VentanaServidor.LOG("No seleccionó ningún archivo");
			return;
		} else if (!f.getName().endsWith(".txt")) {
			VentanaServidor.LOG("EL arcivo debe ser .txt");
			return;
		}

		byte[] b = new byte[(int) f.length()];
		FileInputStream fis = new FileInputStream(f);
		fis.read(b);
		fis.close();

		DatagramPacket dPacket = new DatagramPacket(b, b.length, grupo, 5000);
		ms.send(dPacket);

	}

}
