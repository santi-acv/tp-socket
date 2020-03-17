import java.io.*;
import java.net.*;

// un cliente
public class Cliente
{
	public static void main(String[] args) throws IOException
	{
		Socket socketLlamada = null;
        BufferedReader in = null;
		PrintWriter out = null;		// dato a enviar al server

		// dar conocimiento al usuario que su pedido está en proceso
		System.out.println("Iniciando llamada...");
		
		// conectarme al server
		try {
			// conectarme al server
			socketLlamada = new Socket("localhost", 4444);

			// obtener entrada y salida del socket
            out = new PrintWriter(socketLlamada.getOutputStream(), true);
			in = new BufferedReader( new InputStreamReader(socketLlamada.getInputStream()) );
			
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error de I/O en la conexion al host");
            System.exit(1);
		}
		
		// la entrada del teclado
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

		// recibir y enviar mensajes al servidor
		ThreadRecibirMensajes threadRM = new ThreadRecibirMensajes(in);
		ThreadEnviarMensajes threadEM = new ThreadEnviarMensajes(teclado, out);
		threadEM.start();
		threadRM.start();

		// esperar a que finalicen los procesos para no cerrar el cliente antes de eso
		try {
			threadEM.join();	// sólo con un join basta para dar a enteder que ya no queremos conexión
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// sí o sí alguien va a enviar el Bye y eso va a cerrar el socket
	}
}