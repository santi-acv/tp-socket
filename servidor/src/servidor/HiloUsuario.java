package servidor;

import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;

public class HiloUsuario extends Thread {

	private Socket socket;
	private Conexion origen;
	public Conexion destino;
	
	public HiloUsuario(Socket socket) {
		this.socket = socket;
		this.origen = null;
		this.destino = null;
	}

	public void run() {
		try {
			// crea una interfaz para recibir y enviar mensajes JSON
			InterfazJSON json = new InterfazJSON(socket);
			
			// obtiene la ip del cliente
			// TODO reemplazar por nombre de usuario
			String ip = ((InetSocketAddress)socket.getRemoteSocketAddress()).toString();
			int puerto = socket.getPort();
            System.out.println("conexion establecida con: " + ip);
            
            // agrega el cliente al registro de conexiones
            origen = new Conexion(ip, puerto, this, json);
            Registro.agregarConexion(ip, origen);
            
            // escucha el socket y lee el siguiente mensaje
            int tipo_mensaje;
            while ((tipo_mensaje = json.leerMensaje()) != -1) {
            	switch (tipo_mensaje) {
            	
            	// ver clientes conectados
            	case 1:
            		// TODO mostrar lista de clientes
            		break;
            	
            	// iniciar llamada
            	case 2:
            		String ip_destino;
            		if ((ip_destino = json.obtenerDestino()) == null) {
            			json.enviarError(2, "El mensaje no especifica un destino.");
            		} else if ((destino = Registro.solicitarConexion(ip_destino)) == null) {
            			json.enviarError(3, "No se encuentra al cliente de destino.");
            		} else if (!Registro.establecerConexion(origen, destino)) {
            			json.enviarError(4, "El destino se encuentra ocupado.");
            			destino = null;
            		} else {
            			// TODO notificar al destino de la llamada
            			json.enviarOk();
                    	Registro.insertar(origen, destino);
                    	System.out.println(ip + " llamando a " + ip_destino);
            		}
            		break;
            	
            	// conversar
            	case 3:
            		json.redirigirMensaje(destino.json);
            		break;
            	
            	// terminar llamada
            	case 4:
            		Registro.terminarConexion(origen, destino);
            		break;
            	}
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
