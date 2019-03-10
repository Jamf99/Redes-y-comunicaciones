package servidor.modelo;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;

import servidor.interfaz.VentanaServidor;

public class BroadcastServidor {
	private final static String IP = "255.255.255.255";
	private final static int PORT = 8888;
	private DatagramSocket ds;
	private InetAddress address;

	public BroadcastServidor() {
		try {
			address = InetAddress.getByName(IP);
			ds = new DatagramSocket();
			ds.setBroadcast(true);
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}

	}

	public static File leerArchivo() {
		JFileChooser fileChooser = new JFileChooser();
		int valor = fileChooser.showOpenDialog(fileChooser);
		if (valor == JFileChooser.APPROVE_OPTION) {
			String ruta = fileChooser.getSelectedFile().getAbsolutePath();
			File f = new File(ruta);
			return f;
		} else {
			return null;
		}
	}

	public void enviarArchivo() throws Exception {
		File imagen = leerArchivo();
		if (imagen == null) {
			VentanaServidor.LOG("No se pudo leer la imagen");
			return;
		} else if (!imagen.getName().endsWith(".jpg")) {
			VentanaServidor.LOG("Seleccione una imagen .jpg", imagen.getName());
			return;
		}
		BufferedImage bfi = ImageIO.read(imagen);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(bfi, "jpg", byteArrayOutputStream);
		ImageWriter wr = ImageIO.getImageWritersByFormatName("jpeg").next();
		ImageWriteParam iwp = wr.getDefaultWriteParam();
		iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwp.setCompressionQuality(0.5f);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageOutputStream ios = ImageIO.createImageOutputStream(out);
		wr.setOutput(ios);
		wr.write(null, new IIOImage(bfi, null, null), iwp);
		byte[] b = out.toByteArray();

		DatagramPacket dp = new DatagramPacket(b, b.length, address, PORT);
		ds.send(dp);
		VentanaServidor.LOG("Se ha enviado la imagen");

	}

}