import java.io.*;

// se encarga de leer lo que viene del cliente2 y lo envía al 1
public class ThreadDosAUno extends Thread
{
	private BufferedReader in2 = null;
	private PrintWriter out1 = null;

	public ThreadDosAUno(BufferedReader in2, PrintWriter out1)
	{
		super("ThreadDosAUno");
		this.in2 = in2;
		this.out1 = out1;

	}

	public void run()
	{
		try {
			
			// escuchar al cliente 2
			String entrada2;
			while ( (entrada2 = in2.readLine()) != null )
			{
				// enviar respuesta al cliente1
				out1.println(entrada2);
				
				// si es el código de finalizar llamada
				if ( entrada2.equals("Bye") ) { break; }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}