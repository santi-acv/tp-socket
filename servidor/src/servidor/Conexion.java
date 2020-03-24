package servidor;

public class Conexion {
	
	public enum Estado {
		IDLE, CALL
	}
	
	public String ip;
	public int puerto;
	public Estado estado;
	public HiloUsuario hilo;
	public InterfazJSON json;
	
	Conexion(String ip, int puerto, HiloUsuario hilo, InterfazJSON json) {
		this.ip = ip;
		this.puerto = puerto;
		this.estado = Estado.IDLE;
		this.hilo = hilo;
		this.json = json;
	}
}
