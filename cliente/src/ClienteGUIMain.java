//package cliente;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

public class ClienteGUIMain extends JFrame {
	
	private ClienteAPI cliente;
	PrintWriter out = null;
    BufferedReader in = null;
    BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
    JTextArea pantalla;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClienteGUIMain frame = new ClienteGUIMain();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClienteGUIMain() {
		
		try {
            //socket = new Socket("localhost", 4444);
        	cliente = new ClienteAPI("localhost",4444);
            // enviamos nosotros
            out = cliente.obtenerStreamSalida();
            //viene del servidor
            in = cliente.obtenerStreamEntrada();
            //Empezamos
            new HiloImprimir(in).start();
            
        } catch (UnknownHostException e) {
            System.err.println("Host desconocido");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error de I/O en la conexion al host");		
		    System.exit(1);
        }
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 632, 447);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);
		
		JLabel lblNewLabel = new JLabel("TP SOCKET - Cliente");
		sl_contentPane.putConstraint(SpringLayout.NORTH, lblNewLabel, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, lblNewLabel, 5, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, lblNewLabel, 140, SpringLayout.WEST, contentPane);
		contentPane.add(lblNewLabel);
		
		JButton botonDesconectar = new JButton("Desconectar");
		botonDesconectar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Desconecta el cliente
				cliente.cerrarConexion();
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, botonDesconectar, -5, SpringLayout.NORTH, lblNewLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, botonDesconectar, 466, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, botonDesconectar, -10, SpringLayout.EAST, contentPane);
		contentPane.add(botonDesconectar);
		
		JButton botonCambiarNombre = new JButton("Cambiar Nombre");
		botonCambiarNombre.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Cambia el nombre
				String nombreNuevo = JOptionPane.showInputDialog("Introduzca el nuevo nombre");
				if (nombreNuevo == null) {
					return;
				}
				if (!nombreNuevo.equals("")) {
					cliente.cambiarNombre(nombreNuevo);
				}
			}
		});
		
		
		sl_contentPane.putConstraint(SpringLayout.NORTH, botonCambiarNombre, -5, SpringLayout.NORTH, lblNewLabel);
		sl_contentPane.putConstraint(SpringLayout.EAST, botonCambiarNombre, -6, SpringLayout.WEST, botonDesconectar);
		contentPane.add(botonCambiarNombre);
		
		JButton botonClientes = new JButton("Lista de clientes");
		botonClientes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Lista los clientes
				cliente.listarClientes();
			}
		});
		sl_contentPane.putConstraint(SpringLayout.NORTH, botonClientes, 0, SpringLayout.NORTH, botonDesconectar);
		sl_contentPane.putConstraint(SpringLayout.EAST, botonClientes, -6, SpringLayout.WEST, botonCambiarNombre);
		contentPane.add(botonClientes);
		
		JButton botonLlamar = new JButton("Realizar Llamada");
		sl_contentPane.putConstraint(SpringLayout.WEST, botonLlamar, 0, SpringLayout.WEST, lblNewLabel);
		botonLlamar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Realiza una llamada
				String destino = JOptionPane.showInputDialog("Introduzca el destino");
				if (destino == null) {
					return;
				}
				if (!destino.equals("")) {
					cliente.realizarLlamada(destino);
				}
			}
		});
		contentPane.add(botonLlamar);
		
		JButton botonEnviarMensaje = new JButton("Enviar Mensaje");
		sl_contentPane.putConstraint(SpringLayout.NORTH, botonEnviarMensaje, 0, SpringLayout.NORTH, botonLlamar);
		sl_contentPane.putConstraint(SpringLayout.EAST, botonEnviarMensaje, 0, SpringLayout.EAST, contentPane);
		botonEnviarMensaje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Envia un mensaje
				String mensaje = JOptionPane.showInputDialog("Introduzca el mensaje");
				if (mensaje == null) {
					return;
				}
				if (!mensaje.equals("")) {
					cliente.enviarMensaje(mensaje);
				}
			}
		});
		contentPane.add(botonEnviarMensaje);
		
		JButton botonTerminarLlamada = new JButton("Terminar Llamada");
		sl_contentPane.putConstraint(SpringLayout.NORTH, botonTerminarLlamada, 6, SpringLayout.SOUTH, botonEnviarMensaje);
		sl_contentPane.putConstraint(SpringLayout.EAST, botonTerminarLlamada, 0, SpringLayout.EAST, botonEnviarMensaje);
		botonTerminarLlamada.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Termina una llamada
				cliente.terminarLlamada();
			}
		});
		contentPane.add(botonTerminarLlamada);
		
		JButton botonContestar = new JButton("Contestar llamada");
		sl_contentPane.putConstraint(SpringLayout.SOUTH, botonContestar, 0, SpringLayout.SOUTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, botonLlamar, -6, SpringLayout.NORTH, botonContestar);
		botonContestar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//Contesta la llamada
				cliente.contestarLlamada();
			}
		});
		sl_contentPane.putConstraint(SpringLayout.WEST, botonContestar, 0, SpringLayout.WEST, lblNewLabel);
		contentPane.add(botonContestar);
		
		
		
		
		JTextArea pantallaSalida = new JTextArea(10, 10);
		sl_contentPane.putConstraint(SpringLayout.NORTH, pantallaSalida, 9, SpringLayout.SOUTH, botonClientes);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, pantallaSalida, 528, SpringLayout.SOUTH, botonDesconectar);
		sl_contentPane.putConstraint(SpringLayout.EAST, pantallaSalida, -10, SpringLayout.EAST, botonClientes);
		pantallaSalida.setEditable(false);
		pantallaSalida.setWrapStyleWord(true);
		pantallaSalida.setLineWrap(true);
		contentPane.add(pantallaSalida);
		//Almacenar esa pantalla
		this.pantalla = pantallaSalida;
		//Agregarle scrollbar
		JScrollPane sp = new JScrollPane(pantalla, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		sl_contentPane.putConstraint(SpringLayout.NORTH, sp, 40, SpringLayout.NORTH, lblNewLabel);
		sl_contentPane.putConstraint(SpringLayout.WEST, sp, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, sp, -40, SpringLayout.SOUTH, botonLlamar);
		sl_contentPane.putConstraint(SpringLayout.EAST, sp, -10, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, pantallaSalida, 229, SpringLayout.EAST, sp);
		contentPane.add(sp);
		
	}
	
	public class HiloImprimir extends Thread {
		private BufferedReader in;

		public HiloImprimir(BufferedReader in) {
			this.in = in;
		}
		
		public void run() {
			String line;
			
			String parts[]=null;
			String dobleSplit[]=null;

			
			try {
				while ((line = in.readLine()) != null) {
				    //Append a la pantalla lo que se desea mostrar, line es el json que recibe del servidor TODO: Hacer que se imprima todo ya
					


					char operacion=line.charAt(line.length()-2);//obtengo numero operacion


					if(line.charAt(line.length()-3)=='-'){//desconectar
						operacion='9';
					}


					switch(operacion){

						case'0'://nombre

							parts=line.split(",");
							dobleSplit=parts[0].split(":");
							if(dobleSplit[1].equalsIgnoreCase("0")){	
								JOptionPane.showMessageDialog(null, "Nombre cambiado correctamente");
							}else{
								dobleSplit=parts[1].split(":");
								JOptionPane.showMessageDialog(null, dobleSplit[1]);
							}
							break;	
						case'1'://lista
					    	parts=line.split(",");
					    	
							pantalla.append("PUERTO"+"         "+"IP"+"                   "+"NOMBRE"+ "             "+"DISPONIBLE\n");
							
							
							String disponible[];
							String nombre[];
							String ip[];
							String puerto[];
							
							
							int numero;
							
							for(int c=2;c<(parts.length-1); ) {
							
							    disponible=parts[c+3].split(":");
							    disponible=disponible[1].split("}");
							    if(parts[c+2].contains("/"))
							    		nombre=parts[c+2].split("/");
							    else {
						    		nombre=parts[c+2].split(":");
						    		nombre[1]=nombre[1].substring(1);
							    }

								ip=parts[c+1].split("/");
								
								if(c==2)
									numero=2;
								else
									numero=1;
								
								puerto=parts[c].split(":");
								
								pantalla.append(puerto[numero]+"      "+ip[1].substring(0, ip[1].length()-1)+"       "+nombre[1].substring(0, nombre[1].length()-1)+"            "+ disponible[0]+"\n\n");
								c=c+4;
							}	

							break;
						
						case'2'://realizar
							parts=line.split(",");
							dobleSplit=parts[0].split(":");
							if(dobleSplit[1].equalsIgnoreCase("0")){	
								pantalla.append("***REALIZANDO LLAMADA, FAVOR ESPERE***");
								pantalla.append("\n\n");
							}else{
								dobleSplit=parts[1].split(":");
								JOptionPane.showMessageDialog(null, dobleSplit[1]);
							}
							break;
						case'3'://mensaje
							parts=line.split(",");
							if(parts.length==2){	
								dobleSplit=parts[0].split(":");	
								pantalla.append("MENSAJE RECIBIDO: " + dobleSplit[1]);
								//NO SE DE QUIEN RECIBIO EL MENSAJE Y NO PUDE HACER QUE PASE ESO 
								pantalla.append("\n");
							}else if(parts.length!=2){
								dobleSplit=parts[1].split(":");
								JOptionPane.showMessageDialog(null,"ENVIADO");
							}
							break;
						case'4'://terminar
							parts=line.split(",");
							dobleSplit=parts[0].split(":");
							if(dobleSplit[1].equalsIgnoreCase("0")){	
								pantalla.append("---LLAMADA TERMINADA---");
								pantalla.append("\n\n");
							}else{
								dobleSplit=parts[1].split(":");
								JOptionPane.showMessageDialog(null, dobleSplit[1]);
							}
							break;
						case'5'://contestar
							parts=line.split(",");
							dobleSplit=parts[0].split(":");
							if(!dobleSplit[1].equalsIgnoreCase("7")){	
								pantalla.append("<<<LLAMADA EN LINEA, PUEDE CONVERSAR>>>");
								pantalla.append("\n\n");
							}else{
								dobleSplit=parts[1].split(":");
								JOptionPane.showMessageDialog(null, dobleSplit[1]);
							}
							break;
						case'9':
							JOptionPane.showMessageDialog(null, "Se ha Desconectado");//funciona todo
							break;

					}
					
				
					
					
					
					pantalla.setCaretPosition(pantalla.getDocument().getLength());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private String parsear(String json) {
			//TODO: Parsear el json 
			return null;
		}
	}
}
