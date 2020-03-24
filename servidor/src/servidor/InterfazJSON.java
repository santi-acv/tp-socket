package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

public class InterfazJSON {
	private PrintWriter out;
	private BufferedReader in;
	private JSONObject mensaje;
	private int tipo_operacion = -1;

	public InterfazJSON (Socket socket) throws IOException {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	public int leerMensaje() throws IOException {
		String linea;
		while ((linea = in.readLine()) != null) {
			System.out.println(linea);
			try {
				mensaje = new JSONObject(linea);
				tipo_operacion = mensaje.getInt("tipo_operacion");
				return tipo_operacion;
			} catch (JSONException ex) {
				enviarError(-1, "El mensaje no corresponde a un objeto JSON");
			}
		}
		return -1;
	}

	public String obtenerDestino() {
		return mensaje.optString("destino");
	}

	public void enviarError(int codigo, String mensaje) {
		JSONObject respuesta = new JSONObject();
		respuesta.put("estado", codigo);
		respuesta.put("mensaje", mensaje);
		respuesta.put("tipo_operacion", tipo_operacion);
		out.println(respuesta.toString());
	}
	
	public void enviarOk() {
		JSONObject respuesta = new JSONObject();
		respuesta.put("estado", 0);
		respuesta.put("mensaje", "ok");
		respuesta.put("tipo_operacion", tipo_operacion);
		out.println(respuesta.toString());
	}

	public void redirigirMensaje(InterfazJSON json) {
		json.out.println(mensaje);
	}
}
