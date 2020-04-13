package gui;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class TablaClientes extends JTable {
	private static final long serialVersionUID = 1L;
	private static final String[] columnas =
		{"Nombre", "IP", "Puerto", "Disponible"};
	
	private VistaPrincipal panel;
	private ListSelectionListener listener;
	
	TablaClientes(VistaPrincipal panel) {
		super();
		this.panel = panel;
		
		ListSelectionModel model = this.getSelectionModel();
		listener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				panel.botonLlamar.setEnabled(true);
				model.removeListSelectionListener(this);
			}
		};
		model.addListSelectionListener(listener);
		
		setFillsViewportHeight(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
	}
	
	public void actualizar(JSONArray array) {
		Object[][] data = new Object[array.length()][4];
		int i = 0;
		for (Object object : array) {
			JSONObject o = (JSONObject) object;
			data[i][0] = o.opt("nombre");
			data[i][1] = o.opt("ip");
			data[i][2] = o.opt("puerto");
			data[i][3] = o.opt("disponible");
			i++;
		}
		SwingUtilities.invokeLater(() -> {
				setModel(new ModeloTabla(data, columnas));
				panel.botonActualizar.setEnabled(true);
				panel.botonLlamar.setEnabled(false);
				this.getSelectionModel().addListSelectionListener(listener);
		});
	}
	
	private class ModeloTabla extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		
		ModeloTabla(Object[][] data, String[] columnNames) {
			super(data, columnNames);
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
