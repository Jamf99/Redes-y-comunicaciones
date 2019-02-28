package cliente.interfaz;

import servidor.interfaz.VentanaServidor;

public class Comunicacion {

	private VentanaServidor interfaz;
	private String[] usuarios;
	private String[] grupos;

	public Comunicacion(VentanaCliente ventanaCliente) {
		usuarios = new String[0];
		grupos = new String[0];
	}

	public String[] getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(String[] usuarios) {
		this.usuarios = usuarios;
		interfaz.refreshUsuarios();
	}

	public String[] getGrupos() {
		return grupos;
	}

	public void setGrupos(String[] grupos) {
		this.grupos = grupos;
		interfaz.refreshGroups();
	}
}
