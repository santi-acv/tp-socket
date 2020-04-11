package recursos;

import hilos.HiloLlamada;
import hilos.HiloUsuario;

import java.net.Socket;
import java.io.IOException;
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
	protected int id_cliente;
	protected int id_llamada;
	
	public Conexion(String nombre, HiloUsuario hilo, InterfazJSON json, Socket socket) {
		
		this.nombre = nombre;
		this.hilo = hilo;
		this.json = json;
		
		this.estado = Estado.IDLE;
		
		this.ip = socket.getInetAddress();
		this.puerto = socket.getPort();
		this.pl = socket.getLocalPort();
		
		this.id_cliente = BaseDatos.inicioConexion(ip.toString(), puerto);
	}
	
	public void reiniciar() {
		estado = Estado.IDLE;
		hilo.enlace = null;
		llamada = null;
	}
	
	public int compareTo(Conexion other) {
		return this.pl - other.pl;
	}

	public void cerrar() throws IOException {
		BaseDatos.finConexion(this);
		json.cerrar();
	}
}
