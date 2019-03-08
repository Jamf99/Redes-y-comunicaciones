package cliente.modelo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import javax.swing.JOptionPane;

public class BroadcastCliente {
	
	public InetAddress direccion;
	public byte[] buffer;
	
	public String str, str2;
	
	public DatagramPacket packet;
	
	public MulticastSocket socket;
	
	public BroadcastCliente() throws Exception{
		socket = new MulticastSocket(1502);
		run();
	}
	
	public void run() {
		try {
			direccion = InetAddress.getByName("172.30.194.68");
			socket.joinGroup(direccion);
			while(true) {
				buffer = new byte[256];
				packet = new DatagramPacket(buffer, buffer.length);
				socket.receive(packet);
				str = new String(packet.getData());
				JOptionPane.showMessageDialog(null, "Imágen recibida");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.leaveGroup(direccion);
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
