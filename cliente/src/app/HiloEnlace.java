package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.SwingUtilities;

import org.json.JSONException;
import org.json.JSONObject;

import gui.ChatLlamada;
import gui.DialogoOpcion;
import gui.FrameInicio;
import gui.VistaPrincipal;

public class HiloEnlace extends Thread {
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	
	private FrameInicio frame;
	private VistaPrincipal vista;
	private ChatLlamada chat;
	private DialogoOpcion dialogo;
	private String miNuevoNombre = "";
	private String nombreDestino = "";
	
	
	public void run() {
		try {
			String line;
			while ((line = in.readLine()) != null) {
				JSONObject mensaje = new JSONObject(line);
				int tipo_operacion = mensaje.getInt("tipo_operacion");
				switch (tipo_operacion) {
				
				// cambio de nombre
				case 0:
					if (mensaje.getInt("estado") != 0)
						dialogo.mostrarError(mensaje);
					else
						frame.setTitle(miNuevoNombre);
					break;
				
				// lista de clientes
				case 1:
					vista.tabla.actualizar(
						mensaje.getJSONArray("lista_clientes"));
					break;
				
				// inicio de llamada
				case 2:
					int estado = mensaje.getInt("estado");
					if (estado == 1)
					{
						nombreDestino = mensaje.getString("origen");
						dialogo.llamadaEntrante(mensaje.getString("origen"));
					}
					else if (estado == 3)
						dialogo.llamadaTerminada(mensaje);
					else
						dialogo.mostrarError(mensaje);
					break;
				
				// envio de mensaje
				case 3:
					if (mensaje.has("cuerpo"))
						chat.agregarMensaje(mensaje.getString("cuerpo"));
					break;
				
				// fin de llamada
				case 4:
					if (mensaje.getInt("estado") != 0) {
						dialogo.llamadaTerminada(mensaje);
						frame.reemplazar(chat, vista);
						dialogo.panel = vista;
					}
					break;
				
				// llamada contestada
				case 5:
					if (mensaje.getInt("estado") == 0) {
						dialogo.cerrar();
						SwingUtilities.invokeLater(() -> {
							frame.reemplazar(vista, chat);
							chat.setNombreDestino(nombreDestino);
							chat.clsArea();
							dialogo.panel = chat;
						});
					}
					else
						dialogo.mostrarError(mensaje);
					break;
				}
			}
		} catch (SocketException ex) {
		} catch (JSONException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void cambiarNombre(String nombre) {
		miNuevoNombre = "TP Socket - Cliente: " + nombre;
		out.println("{\"tipo_operacion\":0,\"nombre\":\""+nombre+"\"}");		
	}
	
	public void actualizarListaClientes() {
		out.println("{\"tipo_operacion\":1}");
	}
	
	public void iniciarLlamada(String destino) {
		out.println("{\"tipo_operacion\":2,\"destino\":\""+destino+"\"}");
		dialogo.llamadaSaliente(destino);
		nombreDestino = destino;
	}
	
	public void aceptarLlamada() {
		out.println("{\"tipo_operacion\":5}");
	}
	
	public void cortarLlamada() {
		out.println("{\"tipo_operacion\":4}");
	}
	
	public void enviarMensaje(String cuerpo) {
		out.println("{\"tipo_operacion\":3,\"cuerpo\":\""+cuerpo+"\"}");
	}
	
	public boolean conectar() {
		try {
			socket = new Socket("localhost", 4444);
		} catch (IOException ex) {
			return false;
		}
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return true;
	}
	
	public void registrarVista(FrameInicio frame, VistaPrincipal panel) {
		this.frame = frame;
		this.vista = panel;
		dialogo = new DialogoOpcion(panel, this);
		chat = new ChatLlamada(this, frame, panel, dialogo);
	}
	
	public void desconectar() {
		if (socket == null)
			return;
		out.println("{\"tipo_operacion\":-1}");
		try {
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
		
}
