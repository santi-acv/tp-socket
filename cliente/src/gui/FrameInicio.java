package gui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import app.HiloEnlace;

public class FrameInicio extends JFrame {
	private static final long serialVersionUID = 1L;
	public final Dimension size = new Dimension(500, 400);
	
	public FrameInicio() {
		super("TP Socket - Cliente");
		HiloEnlace enlace = new HiloEnlace();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				enlace.desconectar();
				System.exit(0);
			}
		});
		
		add(enlace.conectar()
			? new VistaPrincipal(this, enlace)
			: new ErrorConexion(this, enlace));
		
		pack();
		setResizable(false);
	}
	
	public void reemplazar(JPanel panel_viejo, JPanel panel_nuevo) {
		remove(panel_viejo);
		add(panel_nuevo);
		panel_viejo.setVisible(false);
		panel_nuevo.setVisible(true);
		setVisible(true);
	}
}
