package hilos;

import recursos.Conexion;
import recursos.Conexion.Estado;
import recursos.CodigoEstado;
import recursos.InterfazJSON;
import recursos.Registro;
import recursos.BaseDatos;

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
			String nombre = ((InetSocketAddress)socket.getRemoteSocketAddress()).toString();
            conexion = new Conexion(nombre, this, json, socket);
            Registro.tabla.put(nombre, conexion);
            
            // recibe un mensaje del socket y lo procesa segun el tipo
            while (true) {
            	switch (json.leerMensaje()) {
            	
            	// cerrar sesion
            	case -1:
            		if (enlace != null)
            			Registro.terminarLlamada(conexion, enlace);
            		Registro.tabla.remove(nombre);
            		json.enviarEstado(CodigoEstado.OK);
            		json.cerrar();
            		socket.close();
            		return;
            	
            	// cambiar nombre
            	case 0:
            		if ((nombre = json.obtenerNombre()) == null) {
            			json.enviarEstado(CodigoEstado.FALTA_NOMBRE);
            		} else if (!Registro.cambiarNombre(nombre, conexion)) {
            			json.enviarEstado(CodigoEstado.NOMBRE_DUPLICADO);
            		} else {
            			json.enviarEstado(CodigoEstado.OK);
            		}
            		break;
            	
            	// ver clientes conectados
            	case 1:
            		Registro.tabla.forEachValue(1, json.agregarLista());
            		json.enviarLista();
            		break;
            	
            	// iniciar llamada
            	case 2:
            		Conexion destino;
        			HiloLlamada hilo;
            		if (conexion.estado != Estado.IDLE) {
            			json.enviarEstado(CodigoEstado.ORIGEN_OCUPADO);
            		} else if ((nombre = json.obtenerDestino()) == null) {
            			json.enviarEstado(CodigoEstado.FALTA_DESTINO);
            		} else if ((destino = Registro.tabla.get(nombre)) == null) {
            			json.enviarEstado(CodigoEstado.USUARIO_INVALIDO);
            		} else if ((hilo = Registro.establecerLlamada(conexion, destino)) == null) {
            			json.enviarEstado(CodigoEstado.DESTINO_OCUPADO);
            		} else {
            			json.enviarLlamada(conexion.nombre, enlace.json);
                    	hilo.start();
            		}
            		break;
            	
            	// conversar
            	case 3:
            		if (enlace != null &&
            			conexion.estado.equals(Estado.TALK) &&
            			enlace.estado.equals(Estado.TALK)) {
            			json.redirigirMensaje(enlace.json);
            		} else {
            			json.enviarEstado(CodigoEstado.NO_HAY_LLAMADA);
            		}
            		break;
            	
            	// terminar llamada
            	case 4:
            		destino = enlace;
            		if (enlace != null && Registro.terminarLlamada(conexion, enlace)) {
            			json.enviarEstado(CodigoEstado.OK);
            			destino.json.enviarEstado(CodigoEstado.LLAMADA_CORTADA, 4);
            		} else {
            			json.enviarEstado(CodigoEstado.NO_HAY_LLAMADA);
            		}
            		break;
            	
            	// contestar llamada
            	case 5:
            		if (Registro.contestarLLamada(enlace, conexion)) {
            			json.enviarEstado(CodigoEstado.OK);
            			enlace.json.enviarEstado(CodigoEstado.OK, 5);
            		} else {
            			json.enviarEstado(CodigoEstado.NO_HAY_LLAMADA);
            		}
            		break;
            	
            	// operacion indeterminada
            	default:
            		json.enviarEstado(CodigoEstado.TIPO_INVALIDO);
            		break;
            	}
            }
		} catch (IOException e) {
			System.out.println("Ha ocurrido un error I/O.");
			e.printStackTrace();
		}
	}
}
