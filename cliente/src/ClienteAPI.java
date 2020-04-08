import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.*;

/**
 * API Cliente para mandar mensajes
 * 
 * @author mfidabel
 *
 */
public class ClienteAPI {
	private Socket socketCliente;
	private String hostname;
	private int puerto;
	private PrintWriter salida;
	private BufferedReader entrada;
	
	public ClienteAPI(String hostname, int puerto) throws IOException, UnknownHostException{
		//Constructor de la API
		this.hostname = hostname;
		this.puerto = puerto;	
		this.crearSocket();
	}
	
	/**
	 * Crea el Socket del cliente
	 * @throws IOException Problemas con la conexión
	 * @throws UnknownHostException Problemas desconocidos
	 */
	private void crearSocket() throws IOException, UnknownHostException {
		//Crea el socket con el hostname y el puerto dado
		this.socketCliente = new Socket(this.hostname, this.puerto);
		//Crea la salida de datos (Lo que el cliente envia al servidor)
		this.salida = new PrintWriter(this.socketCliente.getOutputStream(), true);
		//Crea la entrada de datos (Lo que el cliente recibe del servidor)
		this.entrada = new BufferedReader(new InputStreamReader(this.socketCliente.getInputStream()));
	}
	
	/**
	 * Obtiene el stream de Salida (Lo que el cliente envia al servidor)
	 * @return Stream de Salida
	 */
	public PrintWriter obtenerStreamSalida() {
		return this.salida;
	}
	
	/**
	 * Obtiene el stream de entrada (Lo que el cliente recibe del servidor)
	 * @return Stream de entrada
	 */
	public BufferedReader obtenerStreamEntrada() {
		return this.entrada;
	}
	
	// TODO: Hacer sincrono las funciones ???
	
	/**
	 *  Cierra la conexión del cliente al servidor
	 */
	
	public void cerrarConexion() {
		JSONObject mensaje = new JSONObject();
		mensaje.put("tipo_operacion",-1);
		salida.println(mensaje);
	}
	
	/**
	 * Cambia el nombre del cliente dentro del servidor
	 * @param nuevoNombre El nuevo nombre que se le asignará al usuario en el servidor
	 */
	
	public void cambiarNombre(String nuevoNombre) {
		JSONObject mensaje = new JSONObject();
		mensaje.put("nombre", nuevoNombre);
		mensaje.put("tipo_operacion",0);
		salida.println(mensaje);
	}
	
	/**
	 * Lista los clientes conectados del servidor
	 */
	
	public void listarClientes() {
		JSONObject mensaje = new JSONObject();
		mensaje.put("tipo_operacion",1);
		salida.println(mensaje);
	}
	
	/**
	 * Realiza una llamada a otro cliente en el servidor
	 * @param destino Cliente destino
	 */
	public void realizarLlamada(String destino) {
		JSONObject mensaje = new JSONObject();
		mensaje.put("destino", destino);
		mensaje.put("tipo_operacion",2);
		salida.println(mensaje);
	}
	
	
	/**
	 * Envia un mensaje 
	 * @param cuerpo Cuerpo del mensaje
	 */
	public void enviarMensaje(String cuerpo) {
		JSONObject mensaje = new JSONObject();
		mensaje.put("cuerpo", cuerpo);
		mensaje.put("tipo_operacion",3);
		salida.println(mensaje);
	}
	
	/**
	 * Termina la llamada actual
	 */
	public void terminarLlamada() {
		JSONObject mensaje = new JSONObject();
		mensaje.put("tipo_operacion", 4);
		salida.println(mensaje);
	}
	
	/**
	 * Contesta la llamada entrante
	 */
	public void contestarLlamada() {
		JSONObject mensaje = new JSONObject();
		mensaje.put("tipo_operacion", 5);
		salida.println(mensaje);
	}	
	
}