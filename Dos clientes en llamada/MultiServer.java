import java.io.*;
import java.net.*;

// server principal. busca 2 clientes para conectarlos
public class MultiServer
{
	public static void main(String [] args)throws IOException
	{
		// socket para atender clientes
		ServerSocket serverSocket = null;

		// crear socket
		try
		{
            serverSocket = new ServerSocket(4444);
		} catch (IOException e)
		{
            System.err.println("No se puede abrir el puerto: 4444.");
            System.exit(1);
        }
		System.out.println("Puerto abierto: 4444. Esperando clientes");
		
		// recibir clientes
		Socket cliente1, cliente2;
		boolean escuchando = true;
		while (escuchando)
		{
			// sockets para ambos clientes a conectar en llamada. aceptar primer y segundo cliente
			cliente1 = serverSocket.accept();
			System.out.println("Se ha conectado un cliente");
			cliente2 = serverSocket.accept();
			System.out.println("Se ha conectado un cliente");

			//	pasarle al hilo que atenderá la llamada
			new LlamadaThread(cliente1, cliente2).start();;
		}

		// cerrar socket
        serverSocket.close();
	}
}