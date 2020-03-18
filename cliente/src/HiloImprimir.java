import java.io.BufferedReader;
import java.io.IOException;

public class HiloImprimir extends Thread {
	private BufferedReader in;

	public HiloImprimir(BufferedReader in) {
		this.in = in;
	}
	
	public void run() {
		String line;
		try {
			while ((line = in.readLine()) != null) {
			    System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
