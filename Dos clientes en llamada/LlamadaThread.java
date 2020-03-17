import java.io.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// esta clase maneja la comunicacion durante la llamada entre dos clientes
public class LlamadaThread extends Thread
{
	// los sockets de ambos clientes
	private Socket cliente1 = null;
	private Socket cliente2 = null;

	// recibo el socket de ambos clientes
	public LlamadaThread(Socket cliente1, Socket cliente2)
	{
		super("LlamadaThread");
		this.cliente1 = cliente1;
		this.cliente2 = cliente2;
	}

	// realiza el intercambio de mensajes
	public void run()
	{
		try {
			// obtener la entrada y salida para enviar y recibir mensajes del y para el cliente 1
			BufferedReader in1 = new BufferedReader( new InputStreamReader(cliente1.getInputStream()) );
			PrintWriter out1 = new PrintWriter(cliente1.getOutputStream(), true);

			// obtener la entrada y salida para enviar y recibir mensajes del y para el cliente 2
			BufferedReader in2 = new BufferedReader( new InputStreamReader(cliente2.getInputStream()) );
			PrintWriter out2 = new PrintWriter(cliente2.getOutputStream(), true);

			// dar mensajes de confirmacion de llamada iniciada
			out1.println("Servidor: Llamada iniciada");
			out2.println("Servidor: Llamada iniciada");

			// manejar comunicacion entre clientes 1 y 2
			ThreadUnoADos threadUAD = new ThreadUnoADos(in1, out2);
			ThreadDosAUno threadDAU = new ThreadDosAUno(in2, out1);
			threadDAU.start();
			threadUAD.start();

			// esperar a que finalice el thread para cerrar todo
			try {
				threadUAD.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// cerrar recursos
			in1.close();
			out1.close();

			in2.close();
			out2.close();

			cliente1.close();
			cliente2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}