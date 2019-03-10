package cliente.modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import cliente.interfaz.VentanaCliente;

public class ComunicacionConServidor extends Thread {
	public final static String COMMAND = ":";
	public final static String LISTA_USUARIOS = "l_usuarios";
	public final static String LISTA_GRUPOS = "l_grupos";
	public final static String ENTRAR_GRUPO = "join_g";
	public final static String SALIR_GRUPO = "leave_g";

	public final static int PUERTO = 8080;
	public final static String IP_DEFAULT = "127.0.0.1";

	private Socket s;

	private DataInputStream sIn;
	private DataOutputStream sOut;

	private Comunicacion principal;
	private boolean conectado;

	/**
	 * Inicializa lla comunicación TCP para manejar con eventos el servidor.
	 * 
	 * @param principal
	 *            Comunicación principal
	 */
	public ComunicacionConServidor(Comunicacion principal) {
		this.principal = principal;
		conectado = true;
		crearComunicacion();
	}

	/**
	 * Inicializa la comunicación con el servidor y crea el hilo que espera los
	 * mensajes.
	 */
	public void crearComunicacion() {
		try {
			s = new Socket(IP_DEFAULT, PUERTO);
			sOut = new DataOutputStream(s.getOutputStream());
			sIn = new DataInputStream(s.getInputStream());

			// Ciclo para obtener información desde el servidor
			start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			while (conectado) {
				recibirMensajes();
				Thread.sleep(500);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Recibe un mensaje del servidor y lo escribe en la consola.
	 * 
	 * @throws IOException
	 */
	public void recibirMensajes() throws IOException {
		String mensaje = nextLineServer();
		switch (mensaje) {
		case LISTA_USUARIOS:
			String[] usuarios = nextLineServer().split(COMMAND);
			principal.setUsuarios(usuarios);
			break;
		case LISTA_GRUPOS:
			String nl = nextLineServer();
			String[] grupos = "".equals(nl) ? new String[0] : nl.split(COMMAND);
			principal.setGrupos(grupos);
			break;
		default:
			VentanaCliente.LOG("recibido del servidor por Unicast:", mensaje);
			break;
		}
	}

	/**
	 * Lee una línea obteinda del servidoe
	 * 
	 * @return String recibido del servidor
	 * @throws IOException
	 */
	public String nextLineServer() throws IOException {
		return sIn.readUTF();
	}

	/**
	 * Envía un mensaje al servidor
	 * 
	 * @param mensaje
	 *            Mensaje a enviar
	 * @throws IOException
	 */
	public void enviarMensaje(String mensaje) throws IOException {
		sOut.writeUTF(mensaje);
		sOut.flush();
	}

}
