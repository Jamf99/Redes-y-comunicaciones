package cliente.modelo;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

public class MulticastCliente extends Thread {

	private MulticastSocket socket = null;
	private byte[] buf = new byte[256];
	private Socket socket1;

	public void run() {
		try {
			socket = new MulticastSocket(5000);

			InetAddress group = InetAddress.getByName("239.1.2.2");
			socket.joinGroup(group);
			// --------------------------
			// RECIBIR MENSAJE
			// --------------------------
			BufferedInputStream bis = new BufferedInputStream(socket1.getInputStream());
			FileOutputStream fos = new FileOutputStream(new File("ArchivoClientes"));

			int byteCounter = 0;
			byte[] buffer = new byte[1024];

			while ((byteCounter = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, byteCounter);
				fos.flush();
			}

			fos.close();
			// ------------------------------------------------------
			while (true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String received = new String(packet.getData(), 0, packet.getLength());
				if ("end".equals(received)) {
					break;
				}
			}
			// socket.leaveGroup(group);
			// socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}
}
