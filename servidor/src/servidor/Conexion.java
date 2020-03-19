package servidor;

import java.io.PrintWriter;

import servidor.HiloUsuario.Estado;

public class Conexion {
	public String ip;
	public int puerto;
	public Estado estado;
	public HiloUsuario hilo;
	public PrintWriter out;
	
	Conexion(String ip, int puerto, Estado estado, HiloUsuario hilo, PrintWriter out) {
		this.ip = ip;
		this.puerto = puerto;
		this.estado = estado;
		this.hilo = hilo;
		this.out = out;
	}
}
