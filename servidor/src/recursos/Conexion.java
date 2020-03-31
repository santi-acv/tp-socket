package recursos;

import hilos.HiloLlamada;
import hilos.HiloUsuario;

import java.net.Socket;
import java.net.InetAddress;

public class Conexion {
	
	public enum Estado {
		IDLE, CALL, RING, TALK
	}
	
	public String nombre;
	public HiloUsuario hilo;
	public InterfazJSON json;
	
	public Estado estado;
	public HiloLlamada llamada;
	
	public InetAddress ip;
	public int puerto;
	private int pl;
	
	public Conexion(String nombre, HiloUsuario hilo, InterfazJSON json, Socket socket) {
		
		this.nombre = nombre;
		this.hilo = hilo;
		this.json = json;
		
		this.estado = Estado.IDLE;
		
		this.ip = socket.getInetAddress();
		this.puerto = socket.getPort();
		this.pl = socket.getLocalPort();
	}
	
	public void reiniciar() {
		estado = Estado.IDLE;
		hilo.enlace = null;
		llamada = null;
	}
	
	public int compareTo(Conexion other) {
		return this.pl - other.pl;
	}
}
