package cliente.modelo;

import java.io.IOException;

import cliente.interfaz.VentanaCliente;

public class Comunicacion {

	private VentanaCliente interfaz;
	private String[] usuarios;
	private String[] grupos;

	private ComunicacionConServidor com;
	private MulticastCliente multicast;
	private BroadcastCliente broadcast;

	/**
	 * Comunicación principal de la aplicación.
	 * 
	 * @param ventanaCliente
	 *            Interfaz de la aplicación
	 */
	public Comunicacion(VentanaCliente ventanaCliente) {
		interfaz = ventanaCliente;
		usuarios = new String[0];
		grupos = new String[0];
		com = new ComunicacionConServidor(this);
		multicast = new MulticastCliente(MulticastCliente.IP);
		broadcast = new BroadcastCliente();
	}

	/**
	 * @return Usuarios activos.
	 */
	public String[] getUsuarios() {
		return usuarios;
	}

	/**
	 * Actualiza los usuarios activos.
	 * 
	 * @param usuarios
	 *            Nuevos usuarios activos.
	 */
	public void setUsuarios(String[] usuarios) {
		this.usuarios = usuarios;
		interfaz.refreshUsuarios();
	}

	/**
	 * @return Grupos activos
	 */
	public String[] getGrupos() {
		return grupos;
	}

	/**
	 * Actualiza los grupos activos en la aplicación.
	 * 
	 * @param grupos
	 *            nuevos grupos activos.
	 */
	public void setGrupos(String[] grupos) {
		this.grupos = grupos;
		interfaz.refreshGroups();
	}

	/**
	 * Cambia de grupo multicast para recibir archivos del servidor. abandona el
	 * grupo actual para poder unirse al nuevo frupo.
	 * 
	 * @param ip
	 *            IP del nuevo grupo.
	 */
	public void cambiarGrupoMulticast(String ip) {
		String prevIP = multicast.getIP();
		if (multicast.CambiarGrupo(ip)) {
			interfaz.log("Multicast grupo: ", ip);
			try {
				if (prevIP != null) {
					com.enviarMensaje(ComunicacionConServidor.SALIR_GRUPO);
					com.enviarMensaje(prevIP);
				}
				com.enviarMensaje(ComunicacionConServidor.ENTRAR_GRUPO);
				com.enviarMensaje(ip);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			interfaz.log("Error al conectar en multicast: ", ip);
	}
}
