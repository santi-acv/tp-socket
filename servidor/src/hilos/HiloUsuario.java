package hilos;

import recursos.Conexion;
import recursos.Conexion.Estado;
import recursos.InterfazJSON;
import recursos.Registro;

import java.io.IOException;
import java.net.Socket;
import java.net.InetSocketAddress;

public class HiloUsuario extends Thread {

	private Socket socket;
	private Conexion conexion;
	public Conexion enlace;
	
	public HiloUsuario(Socket socket) {
		this.socket = socket;
		this.conexion = null;
		this.enlace = null;
	}

	public void run() {
		try {
			// crea una interfaz para recibir y enviar mensajes JSON
			InterfazJSON json = new InterfazJSON(socket);
			
			// agrega el cliente al registro de conexiones
			String ip = ((InetSocketAddress)socket.getRemoteSocketAddress()).toString();
            conexion = new Conexion(ip, this, json, socket);
            Registro.agregarConexion(conexion.nombre, conexion);
            System.out.println(ip);
            
            // recibe un mensaje del socket y lo procesa segun el tipo
            while (true) {
            	switch (json.leerMensaje()) {
            	
            	// el socket esta cerrado
            	case 0:
            		return;
            	
            	// ver clientes conectados
            	case 1:
            		// TODO mostrar lista de clientes
            		break;
            	
            	// iniciar llamada
            	case 2:
            		Conexion destino;
        			HiloLlamada hilo;
            		if (!conexion.estado.equals(Estado.IDLE)) {
            			json.enviarError(3, "El cliente se encuentra ocupado.");
            		} else if ((ip = json.obtenerDestino()) == null) {
            			json.enviarError(1, "El mensaje no especifica un destino.");
            		} else if ((destino = Registro.solicitarConexion(ip)) == null) {
            			json.enviarError(2, "No se encuentra al cliente de destino.");
            		} else if ((hilo = Registro.establecerLlamada(conexion, destino)) == null) {
            			json.enviarError(3, "El destino se encuentra ocupado.");
            		} else {
            			json.enviarLlamada(enlace.json);
                    	hilo.start();
            			System.out.println(conexion.nombre+"  --> "+enlace.nombre);
            			//BaseDatos.insertar(conexion, enlace);
            		}
            		break;
            	
            	// conversar
            	case 3:
            		if (enlace != null &&
            			conexion.estado.equals(Estado.TALK) &&
            			enlace.estado.equals(Estado.TALK)) {
            			json.redirigirMensaje(enlace.json);
            		} else {
            			json.enviarError(2, "El usuario no se encuentra en una llamada");
            		}
            		break;
            	
            	// terminar llamada
            	case 4:
            		if (enlace != null && Registro.terminarLlamada(conexion, enlace)) {
            			json.enviarOk();
            			System.out.println(conexion.nombre+" |--> ");
            		} else {
            			json.enviarError(2, "El usuario no se encuentra en una llamada");
            		}
            		break;
            	
            	// contestar llamada
            	case 5:
            		if (Registro.contestarLLamada(enlace, conexion)) {
            			json.enviarOk();
                		System.out.println(conexion.nombre+" <--> "+enlace.nombre);
            		} else {
            			json.enviarError(2, "El usuario no esta recibiendo una llamada.");
            		}
            		break;
            	
            	default:
            		json.enviarError(-1, "No existe una operación con ese código");
            		break;
            	}
            }
		} catch (IOException e) {
			System.out.println("Ha ocurrido un error I/O.");
			e.printStackTrace();
		}
	}
}
