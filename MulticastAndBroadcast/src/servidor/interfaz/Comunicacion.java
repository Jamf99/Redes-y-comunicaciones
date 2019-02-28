package servidor.interfaz;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Comunicacion {

	public final int PUERTO = 8080;
	public final String BASE_USUARIO_KEY = "user";

	private HashMap<String, ComunicacionConCliente> usuarios;
	private VentanaServidor interfaz;

	private ServerSocket ss;

	public Comunicacion(VentanaServidor ventanaServidor) {
		interfaz = ventanaServidor;
		usuarios = new HashMap<String, ComunicacionConCliente>();
		aceptarClientes();
	}

	/**
	 * Inicializa el servidor y acepta usuarios en un ciclo que dura mientras la
	 * aplicación corre.
	 */
	public void aceptarClientes() {
		try {
			System.out.println("Esperando usuarios ...");
			ss = new ServerSocket(PUERTO);

			new Thread() {
				@Override
				public void run() {
					interfaz.log("Servidor Iniciado ...");
					while (true) {
						try {
							Socket s = ss.accept();
							ComunicacionConCliente con = new ComunicacionConCliente(Comunicacion.this, s);

							con.start();
							sleep(500);
							if(usuarios.size() < 10) {
								con.setNombre(agregarUsuario(con));
								actualizarUsuarios();
								interfaz.log("Usuario agregado: ", con.getNombre());	
							}else {
								con.enviarMensaje("El servidor está lleno");
								con.setConectado(false);
								interfaz.log("Se ha rechazado la conexión a un nuevo usuario");	
								
							}
								
								
							
							sleep(500);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				}
			}.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Registra un nuevo usuario a la apicación.
	 * 
	 * @param con
	 *            Conexión con el usuario que se registrará
	 * @return Nombre del usuario registrado
	 */
	public String agregarUsuario(ComunicacionConCliente con) {
		int num = 1;
		while (usuarios.containsKey(BASE_USUARIO_KEY + num)) {
			num++;
		}
		String k = BASE_USUARIO_KEY + num;
		usuarios.put(k, con);

		return k;
	}

	public void desconectarUsuario(ComunicacionConCliente con) {
		usuarios.remove(con.getNombre());
		actualizarUsuarios();
	}

	public void actualizarUsuarios() {
		interfaz.refreshUsuarios();
		StringBuilder sb = new StringBuilder();
		for (String user : usuarios.keySet()) {
			sb.append(user).append(ComunicacionConCliente.COMMAND);
		}
		String lista = sb.toString();
		for (ComunicacionConCliente user : usuarios.values()) {
			try {
				user.enviarMensaje(ComunicacionConCliente.LISTA_USUARIOS);
				user.enviarMensaje(lista);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String[] getUsuarios() {
		return usuarios.keySet().toArray(new String[usuarios.size()]);
	}
}
