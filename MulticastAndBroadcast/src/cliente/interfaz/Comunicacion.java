package cliente.interfaz;

public class Comunicacion {

	private VentanaCliente interfaz;
	private String[] usuarios;
	private String[] grupos;
	
	private ComunicacionConServidor com;

	public Comunicacion(VentanaCliente ventanaCliente) {
		interfaz = ventanaCliente;
		usuarios = new String[0];
		grupos = new String[0];
		com = new ComunicacionConServidor(this);
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
