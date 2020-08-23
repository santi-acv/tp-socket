package gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import app.HiloEnlace;

public class ChatLlamada extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTextArea area;
	private JTextField field;
	private JScrollPane scrollpane;
	private JButton botonEnviar;
	private JButton botonCortar;
	private String nombreDes = "";
	
	public ChatLlamada(HiloEnlace enlace, FrameInicio frame, VistaPrincipal vista, DialogoOpcion dialogo) {
		super(new GridBagLayout());
		setPreferredSize(frame.size);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		area = new JTextArea(5, 20);
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		field = new JTextField(20);
		
		field.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		field.setToolTipText("Escribe un mensaje aquí");
		scrollpane = new JScrollPane(area);
		botonEnviar = new JButton("Enviar");
		botonCortar = new JButton("Cortar");
		
		GridBagConstraints c = new GridBagConstraints();
		Insets i = new Insets(0, 0, 0, 0);
		c.insets = i;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		add(scrollpane, c);
		
		i.top = 5;
		i.left = 3;
		i.right = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = 1;
		c.weighty = 0;
		add(field, c);
		
		c.weightx = 0;
		add(botonEnviar, c);
		add(botonCortar, c);
		
		
		// envia el mensaje
		ActionListener listener = e -> {
			String mensaje = field.getText();
			field.setText(null);
			enlace.enviarMensaje(mensaje);
			area.append("[Yo]: " + mensaje + "\n\n");
		};
		field.addActionListener(listener);
		botonEnviar.addActionListener(listener);
		
		// corta la llamada
		botonCortar.addActionListener(e -> {
			enlace.cortarLlamada();
			frame.reemplazar(this, vista);
			dialogo.panel = vista;
		});
		
	}
	
	public void setNombreDestino(String destino)
	{
		nombreDes = destino;
	}
	
	public void agregarMensaje(String mensaje) {
		SwingUtilities.invokeLater(() -> 
			area.append("[" + nombreDes + "]: " + mensaje+"\n\n"));
	}
	
	/**para limpiar el area luego de una llamada*/
	public void clsArea()
	{
		area.setText("");
	}
}
