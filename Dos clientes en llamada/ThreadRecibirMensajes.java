import java.io.*;

// se encarga de recibir y mostrar los mensajes
public class ThreadRecibirMensajes extends Thread
{
	private BufferedReader in = null;

	// recibir la entrada del socket
	public ThreadRecibirMensajes(BufferedReader in)
	{
		super("ThreadRecibirMensajes");
		this.in = in;
	}

	// recibir y mostrar mensajes
	public void run()
	{
		try {
			String fromServer;

			// leer del server
			while ( (fromServer = in.readLine()) != null )
			{
				// imprimir lo que leí del server
				System.out.println("Mensaje: " + fromServer);
				
				// si es el codigo de finalizar llamada
				if ( fromServer.equals("Bye") ) { break; }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}