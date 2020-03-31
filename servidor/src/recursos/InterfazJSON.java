package recursos;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONException;
import org.json.JSONObject;

public class InterfazJSON {
	private PrintWriter out;
	private BufferedReader in;
	private JSONObject mensaje;
	private int tipo_operacion = 0;

	public InterfazJSON(Socket socket) throws IOException {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public int leerMensaje() throws IOException {
		String linea;
		while ((linea = in.readLine()) != null) {
			try {
				mensaje = new JSONObject(linea);
			} catch (JSONException ex) {
				enviarError(-3, "El mensaje no corresponde a un objeto JSON.");
				continue;
			}
			if (mensaje.has("tipo_operacion"))
				return tipo_operacion = mensaje.getInt("tipo_operacion");
			enviarError(-2, "Se debe especificar un tipo de operación.");
		}
		return 0;
	}

	public String obtenerDestino() {
		return mensaje.optString("destino");
	}
	
	public void enviarLlamada(InterfazJSON json) {
		JSONObject llamada = new JSONObject();
		llamada.put("estado", 0);
		llamada.put("mensaje", "ok");
		llamada.put("tipo_operacion", tipo_operacion);
		json.out.println(llamada.toString());
		enviarOk();
	}

	public void redirigirMensaje(InterfazJSON json) {
		if (mensaje.has("cuerpo")) {
			json.out.println(mensaje);
			enviarOk();
		} else {
			enviarError(1, "El mensaje debe tener un cuerpo.");
		}
	}
	
	public void enviarOk() {
		JSONObject respuesta = new JSONObject();
		respuesta.put("estado", 0);
		respuesta.put("mensaje", "ok");
		respuesta.put("tipo_operacion", tipo_operacion);
		out.println(respuesta.toString());
	}

	public void enviarError(int codigo, String mensaje) {
		JSONObject respuesta = new JSONObject();
		respuesta.put("estado", codigo);
		respuesta.put("mensaje", mensaje);
		respuesta.put("tipo_operacion", tipo_operacion);
		out.println(respuesta.toString());
	}
}
