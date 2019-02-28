package cliente.modelo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastCliente {

	
	public static void main(String[] args) {
		boolean escuchar = true;
		try {
			System.setProperty("java.net.preferIPv4Stack", "true");
			//para unir el cliente al grupo
			MulticastSocket mSock = new MulticastSocket(5000);
			InetAddress dirGrupo = InetAddress.getByName("239.1.2.2");
			mSock.joinGroup(dirGrupo);
			
			while (escuchar) {
				//para recibir mensajes dirigidos al grupo
				byte[] buf = new byte[1000];
				DatagramPacket recibe = new DatagramPacket(buf, buf.length);
				mSock.receive(recibe);
				
				//para obtener los datos del mensaje
				
			
				String datos = new String(recibe.getData(),0,recibe.getLength());
				if(!datos.equals("FIN"))
					System.out.println(datos);
				else
					escuchar = false;
				
				//retirar cliente del grupo
				
				mSock.leaveGroup(dirGrupo);
				
				//cerrar multicast
				mSock.close();
			}
			
			
		} catch (IOException e) {
			System.err.println(e);
		}
	}
}
