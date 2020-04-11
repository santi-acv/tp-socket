package recursos;

import hilos.HiloLlamada;
import recursos.Conexion.Estado;

import java.util.concurrent.ConcurrentHashMap;

public class Registro {
	
	public static ConcurrentHashMap<String, Conexion> tabla = new ConcurrentHashMap<String, Conexion>();

	public static CodigoEstado cambiarNombre(String nombre, Conexion conexion) {
		synchronized (conexion) {
			if (conexion.estado != Estado.IDLE) {
				return CodigoEstado.ORIGEN_OCUPADO;
			} else if (tabla.putIfAbsent(nombre, conexion) != null) {
				return CodigoEstado.NOMBRE_DUPLICADO;
			} else {
				tabla.remove(conexion.nombre);
				conexion.nombre = nombre;
    			BaseDatos.cambioNombre(conexion);
				return CodigoEstado.OK;
			}
		}
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
				} else {
					return null;
				}
			}
		}
		destino.id_llamada = origen.id_llamada = BaseDatos.intentoLlamada(origen, destino);
		return new HiloLlamada(origen, destino);
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
				} else {
					return false;
				}
			}
		}
		BaseDatos.inicioLlamada(origen.id_llamada);
		return true;
	}

	public static boolean terminarLlamada(Conexion origen, Conexion destino) {
		// para evitar un deadlock bloqua primero la conexion con ip menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.compareTo(lock2) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		int id_llamada;
		synchronized (lock1) {
			synchronized (lock2) {
				if (!origen.estado.equals(Estado.IDLE) &&
					!destino.estado.equals(Estado.IDLE)) {
					origen.reiniciar();
					destino.reiniciar();
					id_llamada = origen.id_llamada;
				} else {
					return false;
				}
			}
		}
		BaseDatos.finLlamada(id_llamada);
		return true;
	}
}
