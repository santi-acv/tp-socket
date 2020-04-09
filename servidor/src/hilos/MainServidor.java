package hilos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.DatagramSocket;

public class MainServidor {
	// indica si el servidor sigue aceptando conexiones
	protected static boolean abierto = true;

	public static void main(String[] args) throws IOException {
		int puerto = 0;
		ServerSocket socketTCP = null;
		DatagramSocket socketUDP = null;
		
		// crea un socket servidor TCP
		try {
			puerto = 4444;
			socketTCP = new ServerSocket(puerto);
			puerto = 9876;
			socketUDP = new DatagramSocket(puerto);
	    } catch (IOException e) {
	        System.err.println("No se puede abrir el puerto: " + puerto);
	        System.exit(1);
	    }
		
		// crea un hilo socket UDP
		new HiloSocketUDP(socketUDP).start();
		
		// acepta conexiones con los clientes
		while (abierto) {
			new HiloUsuario(socketTCP.accept()).start();
		}
		
		socketTCP.close();
	}
}
