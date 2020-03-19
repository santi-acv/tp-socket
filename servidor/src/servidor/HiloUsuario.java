package servidor;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.net.InetSocketAddress;


public class HiloUsuario extends Thread {
	
	public enum Estado {
		IDLE, CALL
	}

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
			// obtiene la ip del cliente
			String ip = ((InetSocketAddress)socket.getRemoteSocketAddress()).toString();
			int puerto = socket.getPort();
            System.out.println("conexion establecida con: " + ip);
			
			// obtiene los streams de entrada y salida
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader( 
                    socket.getInputStream()));
            
            // TODO buscar el cliente en la base de datos
            if (!Registro.validarUsuario(ip)) {
            	out.close();
            	in.close();
            	return;
            }
            
            // agrega el cliente al registro de conexiones
            origen = new Conexion(ip, puerto, Estado.IDLE, this, out);
            Registro.agregarConexion(ip, origen);
            
            String line;
            
            // escucha el socket y procesa la entrada 
            while ((line = in.readLine()) != null) {
            	switch (origen.estado) {
            	
            	// el cliente esta disponible para llamadas
            	case IDLE:
            		
            		// TODO convertir line en el id del destino
            		
            		// obtiene la informacion del cliente destino
            		destino = Registro.solicitarConexion(line);
            		
            		// TODO enviar respuesta a traves de la API JSON
            		if (destino == null)
            			out.println("el destino de llamada no existe");
            		else if (!Registro.establecerConexion(origen, destino))
            			out.println("el destino se encuentra ocupado");
                	else {
                    	out.println("llamando a " + line);
                    	destino.out.println("recibiendo llamada de " + ip);
                    	try {
							Registro.insertar(origen, destino);
						} catch (SQLException e) {
							e.printStackTrace();
						}
                	}
            		break;
            	
            	// el cliente esta en una llamada
            	case CALL:
            		
            		// TODO convertir line en el mensaje a enviar
            		
            		// envia el mensaje al destinatario
            		destino.out.println(line);
            		break;
            	}
            }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
