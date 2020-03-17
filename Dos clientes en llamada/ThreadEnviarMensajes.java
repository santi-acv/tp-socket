import java.io.*;

// se encarga de enviar los mensajes
public class ThreadEnviarMensajes extends Thread
{
	private PrintWriter out = null;
	private BufferedReader teclado = null;

	// recibir la entrada del socket y del teclado
	public ThreadEnviarMensajes(BufferedReader teclado, PrintWriter out)
	{
		super("ThreadEnviarMensajes");
		this.out = out;
		this.teclado = teclado;
	}

	// leer del teclado y enviar mensajes
	public void run()
	{
		try {
				// recibir y enviar mensajes al servidor
				String fromUser;
				while ( (fromUser = teclado.readLine()) != null )
				{
					// enviar al server la respuesta del usuario
					out.println(fromUser);

					// si es el codigo de finalizar llamada
					if ( fromUser.equals("Bye") ) { break; }
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}