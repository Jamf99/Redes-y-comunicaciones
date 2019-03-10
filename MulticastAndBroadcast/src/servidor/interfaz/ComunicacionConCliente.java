package servidor.interfaz;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ComunicacionConCliente extends Thread {

	public final static String COMMAND = ":";
	public final static String LISTA_USUARIOS = "l_usuarios";
	public final static String LISTA_GRUPOS = "l_grupos";
	public final static String ENTRAR_GRUPO = "join_g";
	public final static String SALIR_GRUPO = "leave_g";

	private Comunicacion principal;
	private Socket s;
	private String nombre;
	private boolean conectado;

	private DataInputStream sIn;
	private DataOutputStream sOut;

	public ComunicacionConCliente(Comunicacion com, Socket s) {
		principal = com;
		this.s = s;
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
			sOut.close();
			sIn.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			VentanaServidor.LOG("Se ha perdido la conexión con", nombre);
			principal.desconectarUsuario(this);
		}
	}

	/**
	 * Recibe los mensajes de un cliente y los muestra en el log.
	 * 
	 * @throws IOException
	 */
	public void recibirMensajes() throws IOException {
		String mensaje = sIn.readUTF();
		switch (mensaje) {
		case ENTRAR_GRUPO:
			principal.agregarEliminarUsuarioAGrupo(sIn.readUTF(), true);
			break;
		case SALIR_GRUPO:
			principal.agregarEliminarUsuarioAGrupo(sIn.readUTF(), false);
			break;

		default:
			VentanaServidor.LOG(nombre, ", recibido por Unicast:", mensaje);
			break;
		}
	}

	/**
	 * Envía un mensaje al cliente
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

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}

	public boolean isConectado() {
		return conectado;
	}
}
