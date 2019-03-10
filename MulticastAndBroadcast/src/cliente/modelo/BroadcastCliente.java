package cliente.modelo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import cliente.interfaz.VentanaCliente;

public class BroadcastCliente extends Thread {

	private final static String IP = "255.255.255.255";
	private final static int PORT = 8888;
	private DatagramSocket ds;

	private DatagramPacket dp;
	private BufferedImage bf;

	private InetAddress address;

	private boolean escuchar;

	public BroadcastCliente() {
		escuchar = true;
		try {
			address = InetAddress.getByName(IP);
			ds = new DatagramSocket(PORT);
			ds.setBroadcast(true);
			// VentanaCliente.LOG(ds.getInetAddress().getHostName());
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
			escuchar = false;
			VentanaCliente.LOG(
					"No se ha podico conectar al broadcast, en este equipo ya está ocupado el puerto con otro cliente.");
			System.out.println(
					"No se ha podico conectar al broadcast, en este equipo ya está ocupado el puerto con otro cliente.");
		}
		start();
	}

	public void run() {
		dp = new DatagramPacket(new byte[66507], 66507, address, PORT);
		try {

			while (escuchar) {
				recibir();
				sleep(500);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recibir() {
		try {
			ds.receive(dp);
			byte[] b = dp.getData();

			ByteArrayInputStream bis = new ByteArrayInputStream(b);
			ImageInputStream iis = ImageIO.createImageInputStream(bis);
			ImageReader re = ImageIO.getImageReadersByFormatName("jpeg").next();
			re.setInput(iis);
			bf = re.read(0);
			VentanaCliente.SET_BF(bf);
			String filename = "data/img/imagen_" + System.currentTimeMillis() + "_" + (Math.random() * 3000) + ".jpg";
			File o = new File(filename);
			ImageOutputStream ios = ImageIO.createImageOutputStream(o);
			ios.write(b);
			ios.flush();
			ios.close();
			VentanaCliente.LOG("Se ha recibido el la imagen por broadcast: ", filename);
		} catch (Exception e) {
			System.err.println(e);
			VentanaCliente.LOG("Error al recibir por broadcast.");
		}
	}
}
