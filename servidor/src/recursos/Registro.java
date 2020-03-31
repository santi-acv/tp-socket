package recursos;

import hilos.HiloLlamada;
import recursos.Conexion.Estado;

import java.util.concurrent.ConcurrentHashMap;

public class Registro {
	
	public static ConcurrentHashMap<String, Conexion> tabla = new ConcurrentHashMap<String, Conexion>();

	public static boolean cambiarNombre(String nombre, Conexion conexion) {
		boolean ausente = tabla.putIfAbsent(nombre, conexion) == null;
		if (ausente) {
				tabla.remove(conexion.nombre);
				conexion.nombre = nombre;
		}
		return ausente;
	}

	public static HiloLlamada establecerLlamada(Conexion origen, Conexion destino) {
		// para evitar un deadlock bloqua primero la conexion con puerto menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.compareTo(lock2) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		synchronized (lock1) {
			synchronized (lock2) {
				if (origen.estado.equals(Estado.IDLE) &&
					destino.estado.equals(Estado.IDLE)) {
					origen.estado = Estado.CALL;
					destino.estado = Estado.RING;
					origen.hilo.enlace = destino;
					destino.hilo.enlace = origen;
					return new HiloLlamada(origen, destino);
				} else {
					return null;
				}
			}
		}
	}
	
	public static boolean contestarLLamada(Conexion origen, Conexion destino) {
		// para evitar un deadlock bloqua primero la conexion con puerto menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.compareTo(lock2) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		synchronized (lock1) {
			synchronized (lock2) {
				if (origen.estado.equals(Estado.CALL) &&
					destino.estado.equals(Estado.RING)) {
					origen.estado = Estado.TALK;
					destino.estado = Estado.TALK;
					origen.llamada.interrupt();
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public static boolean terminarLlamada(Conexion origen, Conexion destino) {
		// para evitar un deadlock bloqua primero la conexion con ip menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.compareTo(lock2) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		synchronized (lock1) {
			synchronized (lock2) {
				if (!origen.estado.equals(Estado.IDLE) &&
					!destino.estado.equals(Estado.IDLE)) {
					origen.reiniciar();
					destino.reiniciar();
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
