package hilos;

import recursos.Registro;
import recursos.InterfazJSON;
import recursos.CodigoEstado;
import recursos.Conexion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class HiloSocketUDP extends Thread {
	private DatagramSocket socket;
	
	public HiloSocketUDP (DatagramSocket socketUDP) {
		this.socket = socketUDP;
	}
	
	public void run () {
		
		while (MainServidor.abierto) {
			
			// recibe un datagrama
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
			
			System.out.println(new String(packet.getData())); // TODO borrar
			
			// obtiene la IP y el puerto
			InetAddress ip = packet.getAddress();
			int puerto = packet.getPort();
			
			// convierte a un objeto JSON
			JSONObject mensaje;
			try {
				mensaje = new JSONObject(new String(packet.getData()));
			} catch (JSONException ex) {
				mensaje = null;
			}
			
			// prepara la respuesta
			JSONObject respuesta = new JSONObject();
			CodigoEstado estado = CodigoEstado.OK;
			
			// valida el mensaje
			if (mensaje == null) {
				estado = CodigoEstado.JSON_INVALIDO;
			} else if (!mensaje.has("tipo_operacion")) {
				estado = CodigoEstado.FALTA_TIPO;
			} else {
				
				// determina el tipo de operacion
				int tipo_operacion = mensaje.optInt("tipo_operacion", -1);
				respuesta.put("tipo_operacion", tipo_operacion);
				switch (tipo_operacion) {
				
				// lista de usuarios
				case 1:
					JSONArray lista_clientes = new JSONArray();
					Registro.tabla.forEachValue(1, InterfazJSON.agregar(lista_clientes));
					respuesta.put("lista_clientes", lista_clientes);
					break;
				
				// desconectar
				case 4:
					Conexion origen, destino;
					String nombre = mensaje.optString("origen");
					if (nombre == null) {
						estado = CodigoEstado.FALTA_ORIGEN;
					} else if ((origen = Registro.tabla.get(nombre)) == null) {
						estado = CodigoEstado.USUARIO_INVALIDO;
					} else if (!ip.equals(origen.ip)) {
						estado = CodigoEstado.IP_INVALIDA;
					} else if ((destino = origen.hilo.enlace) != null &&
						Registro.terminarLlamada(origen, destino)) {
						origen.json.enviarEstado(CodigoEstado.UDP_FIN_LLAMADA);
						destino.json.enviarEstado(CodigoEstado.LLAMADA_CORTADA);
					} else {
						estado = CodigoEstado.NO_HAY_LLAMADA;
					}
					break;
				
				// no existe operacion
				default:
					estado = CodigoEstado.TIPO_INVALIDO;
					break;
				}
			}
			
			// envia el datagrama
			respuesta.put("estado", estado.estado);
			respuesta.put("mensaje", estado.mensaje);
			buffer = respuesta.toString().getBytes();
			packet = new DatagramPacket(buffer, buffer.length, ip, puerto);
			System.out.println(new String(packet.getData())); // TODO borrar
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		socket.close();
	}
}
