package hilos;

import recursos.Conexion;
import recursos.Registro;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ConcurrentHashMap;

public class MainServidor {
	// indica si el servidor sigue aceptando conexiones
	protected static boolean abierto = true;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;
		Registro.tabla = new ConcurrentHashMap<String, Conexion>();
		
		// crea un socket para el servidor
		try {
			serverSocket = new ServerSocket(4444);
	    } catch (IOException e) {
	        System.err.println("No se puede abrir el puerto: 4444.");
	        System.exit(1);
	    }
		
		// acepta conexiones con los clientes
		while (abierto) {
			new HiloUsuario(serverSocket.accept()).start();
		}
		
		serverSocket.close();
	}
}
