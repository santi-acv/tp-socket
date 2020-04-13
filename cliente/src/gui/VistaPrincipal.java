package gui;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import app.HiloEnlace;

public class VistaPrincipal extends JPanel {
	private static final long serialVersionUID = 1L;
	
	public TablaClientes tabla;
	public JButton botonNombre;
	public JButton botonActualizar;
	public JButton botonLlamar;
	private JScrollPane scrollpane;
	private JPanel botones;
	
	public VistaPrincipal(FrameInicio frame, HiloEnlace enlace) {
		super();
		setPreferredSize(frame.size);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		tabla = new TablaClientes(this);
		scrollpane = new JScrollPane(tabla);
		scrollpane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		botonNombre = new JButton("Cambiar nombre");
		botonActualizar = new JButton("Actualizar");
		botonLlamar = new JButton("Llamar");
		botonLlamar.setEnabled(false);
		
		// activa el boton al seleccionar una fila
		tabla.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				botonLlamar.setEnabled(true);
				tabla.getSelectionModel().removeListSelectionListener(this);
			}
		});
		
		// llama al cliente seleccionado
		botonLlamar.addActionListener(e -> {
			int row = tabla.getSelectedRow();
			Object cliente = tabla.getModel().getValueAt(row, 0);
			enlace.iniciarLlamada((String) cliente);
		});
		
		// actualiza la lista de clientes
		botonActualizar.addActionListener(e ->{
			enlace.actualizarListaClientes();
			botonActualizar.setEnabled(false);
		});
		
		// cambia el nombre del usuario
		botonNombre.addActionListener(e -> {
			String nombre = JOptionPane.showInputDialog(this,
				"Introduzca el nuevo nombre",
				"Cambiar nombre", JOptionPane.QUESTION_MESSAGE);
			if (nombre == null)
				return;
			enlace.cambiarNombre(nombre);
			enlace.actualizarListaClientes();
		});
		
		botones = new JPanel();
		botones.setLayout(new FlowLayout(FlowLayout.CENTER));
		botones.add(botonNombre);
		botones.add(botonActualizar);
		botones.add(botonLlamar);
		
		add(scrollpane);
		add(botones);
		
		enlace.registrarVista(frame, this);
		enlace.actualizarListaClientes();
		enlace.start();
	}
}
