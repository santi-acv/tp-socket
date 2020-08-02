package recursos;

import recursos.Conexion.Estado;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class InterfazJSON {
	private PrintWriter out;
	private BufferedReader in;
	private JSONObject mensaje;
	private int tipo_operacion = 0;
	private JSONArray lista_clientes;

	public InterfazJSON(Socket socket) throws IOException {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public int leerMensaje() throws IOException {
		String linea;
		tipo_operacion = -1;
		while ((linea = in.readLine()) != null) {
			try {
				mensaje = new JSONObject(linea);
			} catch (JSONException ex) {
				enviarEstado(CodigoEstado.JSON_INVALIDO);
				continue;
			}
			if (mensaje.has("tipo_operacion"))
				return tipo_operacion = mensaje.getInt("tipo_operacion");
			enviarEstado(CodigoEstado.FALTA_TIPO);
		}
		return -1;
	}

	public String obtenerNombre() {
		return mensaje.optString("nombre");
	}

	public String obtenerDestino() {
		return mensaje.optString("destino");
	}
	
	public void enviarLlamada(String nombre, InterfazJSON json) {
		JSONObject llamada = new JSONObject();
		llamada.put("estado", CodigoEstado.LLAMADA_ENTRANTE.estado);
		llamada.put("mensaje", CodigoEstado.LLAMADA_ENTRANTE.mensaje);
		llamada.put("tipo_operacion", tipo_operacion);
		llamada.put("origen", nombre);
		json.out.println(llamada.optString("origen") + " quiere iniciar una llamada.");
		enviarEstado(CodigoEstado.OK);
	}

	public void redirigirMensaje(InterfazJSON json) {
		if (mensaje.has("cuerpo")) {
			out.println(mensaje.optString("nombre") + ": " + mensaje.optString("cuerpo"));
			json.out.println(mensaje.optString("nombre") + ": " + mensaje.optString("cuerpo"));
			enviarEstado(CodigoEstado.OK);
		} else {
			enviarEstado(CodigoEstado.FALTA_CUERPO);
		}
	}
	
	public void enviarEstado(CodigoEstado codigo) {
		JSONObject respuesta = new JSONObject();
		respuesta.put("estado", codigo.estado);
		respuesta.put("mensaje", codigo.mensaje);
		respuesta.put("tipo_operacion", tipo_operacion);
		if(codigo == CodigoEstado.OK) {
			switch(respuesta.getInt("tipo_operacion")) {
				case 2:
					out.println("Llamando a " + mensaje.optString("destino") + "...");
					break;
				case 4:
					out.println("Se ha terminado la llamada.");
					break;
				case 5:
					out.println("Has contestado la llamada.");
					break;
			}
		}else {
			out.println(respuesta.optString("mensaje"));
		}
	}
	
	public Consumer<Conexion> agregarLista() {
		lista_clientes = new JSONArray();
		return agregar(lista_clientes);
	}
	
	public static Consumer<Conexion> agregar(JSONArray lista_clientes) {
		return conexion -> {
			JSONObject objeto = new JSONObject();
			objeto.put("nombre", conexion.nombre);
			objeto.put("ip", conexion.ip);
			objeto.put("puerto", conexion.puerto);
			objeto.put("disponible", conexion.estado == Estado.IDLE);
			lista_clientes.put(objeto);
		};
	}

	public void enviarLista() {
		int i;
		JSONObject respuesta = new JSONObject();
		respuesta.put("estado", 0);
		respuesta.put("mensaje", "ok");
		respuesta.put("tipo_operacion", tipo_operacion);
		respuesta.put("lista_clientes", lista_clientes);
		out.println("CLIENTES CONECTADOS");
		for(i = 0; i < lista_clientes.length(); i++){
			out.println(lista_clientes.getJSONObject(i).optString("nombre"));
		}
		lista_clientes = null;
	}

	public void cerrar() throws IOException {
		out.close();
		in.close();
	}
}
