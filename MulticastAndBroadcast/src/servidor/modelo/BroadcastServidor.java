package servidor.modelo;

import java.net.*;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import cliente.interfaz.VentanaCliente;

import java.awt.image.BufferedImage;
import java.io.*;

public class BroadcastServidor  {

	private File leerArchivo() {
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

	public void enviarArchivo() throws Exception{

		File imagen = leerArchivo();

		if (imagen != null) {
			Socket socket = new Socket("localhost", 13085);
			OutputStream outputStream = socket.getOutputStream();

			BufferedImage image = ImageIO.read(imagen);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", byteArrayOutputStream);

			byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
			outputStream.write(size);
			outputStream.write(byteArrayOutputStream.toByteArray());
			outputStream.flush();
			socket.close();
		} else {
			VentanaCliente.LOG("No se pudo leer la imagen");
		}

	}

}