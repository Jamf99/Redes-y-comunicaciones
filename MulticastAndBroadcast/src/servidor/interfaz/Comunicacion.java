package servidor.interfaz;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import servidor.modelo.BroadcastServidor;
import servidor.modelo.MulticastServidor;

public class Comunicacion {

	public final int PUERTO = 8080;
	public final String BASE_USUARIO_KEY = "user";

	private HashMap<String, ComunicacionConCliente> usuarios;
	private VentanaServidor interfaz;

	private ServerSocket ss;

	private MulticastServidor multicast;
	private HashMap<String, AtomicInteger> gruposMulticast;
	private BroadcastServidor broadCast;

	public Comunicacion(VentanaServidor ventanaServidor) {
		interfaz = ventanaServidor;
		usuarios = new HashMap<String, ComunicacionConCliente>();
		aceptarClientes();
		multicast = new MulticastServidor();
		broadCast = new BroadcastServidor();
		gruposMulticast = new HashMap<String, AtomicInteger>();
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
					interfaz.logln("Servidor Iniciado ...");
					while (true) {
						try {
							Socket s = ss.accept();
							ComunicacionConCliente con = new ComunicacionConCliente(Comunicacion.this, s);
							con.start();
							sleep(500);
							if (usuarios.size() < 10) {
								con.setNombre(agregarUsuario(con));
								actualizarUsuarios();
								actualizarGruposMulticast();
								interfaz.log("Usuario agregado: ", con.getNombre());
							} else {
								con.enviarMensaje("El servidor está lleno");
								con.setConectado(false);
								interfaz.logln("Se ha rechazado la conexión a un nuevo usuario");
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

	/**
	 * Actualiza la lista de usuarios en la interfaz, y envía los datos a los
	 * clientes para que también actualicen.
	 */
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

	/**
	 * @return Arreglo con los nombres de los usuarios
	 */
	public String[] getUsuarios() {
		return usuarios.keySet().toArray(new String[usuarios.size()]);
	}

	/**
	 * Crea un nuevo grupo para multicast.
	 * 
	 * @param ip
	 *            IP del grupo
	 * @return true si se pudo crear el grupo, false en caso contrario.
	 */
	public boolean crearGrupoMulticast(String ip) {
		if (MulticastServidor.IP.equals(ip) || gruposMulticast.containsKey(ip))
			return false;
		try {
			InetAddress.getByName(ip);
			gruposMulticast.put(ip, new AtomicInteger(0));
		} catch (IOException e) {
			e.printStackTrace();
			VentanaServidor.LOG("No se pudo conectar con la IP: ", ip);
			return false;
		}
		actualizarGruposMulticast();
		return true;
	}

	public void agregarEliminarUsuarioAGrupo(String ip, boolean agregar) {
		int sum = agregar ? 1 : -1;
		AtomicInteger s = gruposMulticast.get(ip);
		if (s != null)
			s.addAndGet(sum);
		actualizarGruposMulticast();
	}

	public void actualizarGruposMulticast() {
		StringBuilder sb = new StringBuilder();
		for (String ip : gruposMulticast.keySet()) {
			sb.append(ip).append(" ").append(gruposMulticast.get(ip).get()).append(ComunicacionConCliente.COMMAND);
		}
		String lista = sb.toString();
		interfaz.refreshGroups("".equals(lista) ? new String[0] : lista.split(ComunicacionConCliente.COMMAND));
		for (ComunicacionConCliente user : usuarios.values()) {
			try {
				user.enviarMensaje(ComunicacionConCliente.LISTA_GRUPOS);
				user.enviarMensaje(lista);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public void cambiarGrupoMulticast(String ip) {
		if (multicast.CambiarGrupo(ip))
			interfaz.log("Multicast grupo: ", ip);
		else
			interfaz.log("Error al conectar en multicast: ", ip);
	}

	/**
	 * LLama el método de enviar de multicast
	 */
	public void enviarMulticast() {
		try {
			multicast.enviar();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * LLama el método de enviar de broadcast
	 */
	public void EnviarBroadCast() {
		try {
			broadCast.enviarArchivo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
