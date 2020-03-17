import java.io.*;

// se encarga de leer lo que viene del cliente1 y lo envía al 2
public class ThreadUnoADos extends Thread
{
	private BufferedReader in1 = null;
	private PrintWriter out2 = null;

	public ThreadUnoADos(BufferedReader in1, PrintWriter out2)
	{
		super("ThreadUnoADos");
		this.in1 = in1;
		this.out2 = out2;
	}

	public void run()
	{
		try {

			// escuchar al cliente 1
			String entrada1;
			while ( (entrada1 = in1.readLine()) != null )
			{
				// envío al cliente 2 el mensaje
				out2.println(entrada1);
				
				// si es el código de finalizar llamada
				if ( entrada1.equals("Bye") ) { break; }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}