import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainCliente {

	public static void main(String[] args) throws IOException {
		ClienteAPI cliente;
        PrintWriter out = null;
        BufferedReader in = null;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            //socket = new Socket("localhost", 4444);
        	cliente = new ClienteAPI("localhost",4444);
            // enviamos nosotros
            out = cliente.obtenerStreamSalida();
            //viene del servidor
            in = cliente.obtenerStreamEntrada();
            //Empezamos
            new HiloImprimir(in).start();
            String line;
            while ((line = stdIn.readLine()) != null) {
    		    out.println(line);
    		}
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error de I/O en la conexion al host");
            System.exit(1);
        }
        
        
	}

}
