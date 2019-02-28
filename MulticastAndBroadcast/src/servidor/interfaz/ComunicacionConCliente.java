package servidor.interfaz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ComunicacionConCliente extends Thread{

	public final static String COMMAND = ":";
	public final static String LISTA_USUARIOS = "-_usuarios";
	public final static String LISTA_GRUPOS = "l_grupos";
	
	private Comunicacion principal;
	private Socket s;
	private String nombre;
	private boolean conectado;

	private DataInputStream sIn;
	private DataOutputStream sOut;
	
	
	public ComunicacionConCliente(Comunicacion com, Socket s) {
		com = principal;
		this.s= s;
		this.conectado = true;
	}
	
	@Override
	public void run() {
		try {
			sOut = new DataOutputStream(s.getOutputStream());
			sIn = new DataInputStream(s.getInputStream());
			while (conectado) {
				recibirMensajes();
				sleep(500);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			VentanaServidor.LOG("Se ha perdido la conexi�n con", nombre);
			principal.desconectarUsuario(this);
		}
	}
	
	/**
	 * Recibe los mensajes de un cliente y los muestra en el log.
	 * @throws IOException
	 */
	public void recibirMensajes() throws IOException {
		String mensaje = sIn.readUTF();
		VentanaServidor.LOG(nombre, ", recibido por Unicast:", mensaje);
	}
	
	/**
	 * Env�a un mensaje al cliente
	 * 
	 * @param mensaje
	 *            mensaje a enviar
	 * @throws IOException
	 */
	public void enviarMensaje(String mensaje) throws IOException {
		sOut.writeUTF(mensaje);
		sOut.flush();
	}
	
	/**
	 * Actualiza el nombre e informa al cliente
	 * 
	 * @param nombre
	 *            Nuevo nombre del cliente
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
		try {
			enviarMensaje("Su usuario es: " + nombre);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return Nombre del cliente
	 */
	public String getNombre() {
		return nombre;
	}
}