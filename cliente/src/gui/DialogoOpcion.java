package gui;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import app.HiloEnlace;

public class DialogoOpcion {
	public JPanel panel;
	private JDialog dialog;
	private JOptionPane entrante;
	private JOptionPane saliente;
	private HiloEnlace enlace;
	String[] opcionesIn = {"Contestar", "Rechazar"};
	String[] opcionesOut = {"Cancelar"};
	
	public DialogoOpcion(JPanel panel, HiloEnlace enlace) {
		this.panel = panel;
		this.enlace = enlace;
		
		entrante = new JOptionPane();
		entrante.setOptions(opcionesIn);
		entrante.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		
		saliente = new JOptionPane();
		saliente.setOptions(opcionesOut);
		saliente.setMessageType(JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void llamadaEntrante(String cliente) {
		SwingUtilities.invokeLater(() -> {
			entrante.setMessage("Esta recibiendo una llamada de "+cliente+".");
			
			dialog = entrante.createDialog(panel, "Llamada");
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.pack();
			dialog.setVisible(true);
			if (entrante.getValue().equals(opcionesIn[0])) {
				enlace.aceptarLlamada();
			} else if (entrante.getValue().equals(opcionesIn[1])) {
				enlace.cortarLlamada();
			}
		});
	}
	
	public void llamadaSaliente(String cliente) {
		SwingUtilities.invokeLater(() -> {
			saliente.setMessage("Llamando a "+cliente+"...");
			
			dialog = saliente.createDialog(panel, "Llamada");
			dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
			dialog.pack();
			dialog.setVisible(true);
			if (saliente.getValue().equals(opcionesOut[0]))
				enlace.cortarLlamada();
		});
	}
	
	public void llamadaTerminada(JSONObject mensaje) {
		cerrar();
		JOptionPane.showMessageDialog(panel,
				mensaje.getString("mensaje"),
				"Llamada", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void mostrarError(JSONObject mensaje) {
		cerrar();
		JOptionPane.showMessageDialog(panel,
				mensaje.getString("mensaje"),
				"Error", JOptionPane.ERROR_MESSAGE);
	}
	
	public void cerrar() {
		SwingUtilities.invokeLater(() -> dialog.setVisible(false));
	}
}
