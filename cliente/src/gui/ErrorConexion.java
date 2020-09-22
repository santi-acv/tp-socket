package gui;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import app.HiloEnlace;

public class ErrorConexion extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JLabel texto;
	private JButton boton;
	
	public ErrorConexion(FrameInicio frame, HiloEnlace enlace) {
		super();
		setPreferredSize(frame.size);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		texto = new JLabel("Error de conexión con el servidor");
		texto.setAlignmentX(Component.CENTER_ALIGNMENT);
		texto.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		boton = new JButton("Reintentar");
		boton.setAlignmentX(Component.CENTER_ALIGNMENT);
		boton.setAlignmentY(Component.CENTER_ALIGNMENT);
		boton.addActionListener(e -> {
			if (enlace.conectar())
				frame.reemplazar(this, new VistaPrincipal(frame, enlace));
		});
		
		add(Box.createVerticalGlue());
		add(texto);
		add(Box.createRigidArea(new Dimension(10, 10)));
		add(boton);
		add(Box.createVerticalGlue());
	}
}
