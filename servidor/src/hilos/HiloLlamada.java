package hilos;

import recursos.CodigoEstado;
import recursos.Conexion;
import recursos.Conexion.Estado;

public class HiloLlamada extends Thread {
	private Conexion origen;
	private Conexion destino;
	
	public HiloLlamada(Conexion origen, Conexion destino) {
		this.origen = origen;
		this.destino = destino;
		origen.llamada = this;
		destino.llamada = this;
	}
	
	
	public void run() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException ex) {
			return;
		}
		
		// para evitar un deadlock bloqua primero la conexion con ip menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.compareTo(lock2) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		synchronized (lock1) {
			synchronized (lock2) {
				if (origen.estado.equals(Estado.CALL) &&
					destino.estado.equals(Estado.RING)) {
					origen.reiniciar();
					destino.reiniciar();
				} else {
					return;
				}
			}
		}
		origen.json.enviarEstado(CodigoEstado.NO_CONTESTA, 2);
		destino.json.enviarEstado(CodigoEstado.NO_CONTESTA, 2);
	}
}
