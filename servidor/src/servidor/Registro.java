package servidor;

import java.util.concurrent.ConcurrentHashMap;
import servidor.HiloUsuario.Estado;

public class Registro {
	
	public static ConcurrentHashMap<String, Conexion> tabla;
	
	public static void agregarConexion (String id, Conexion conexion) {
		tabla.put(id, conexion);
	}
	
	public static boolean validarUsuario(String id) {
		return true;
	}
	
	public static Conexion solicitarConexion(String id) {
		return tabla.get(id);
	}

	public static boolean establecerConexion(Conexion origen, Conexion destino) {
		
		// para evitar un deadlock bloqua primero la conexion con ip menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.ip.compareTo(lock2.ip) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		synchronized (lock1) {
			synchronized (lock2) {
				if (origen.estado.equals(Estado.IDLE) &&
					destino.estado.equals(Estado.IDLE)) {
					origen.estado = Estado.CALL;
					destino.estado = Estado.CALL;
					destino.hilo.destino = origen;
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
