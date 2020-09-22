package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class RenderCeldas extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	protected int columna ;
	
	protected static final String colorDisponible="#20A500";
	protected static final String fondoDisponible="#DEFFDD";
	
	protected static final String colorNoDisponible="#6A6A6A";
	protected static final String fondoNoDisponible="#E8E8E8";
	
	protected static final String fondoSeleccion="#ACB6EA";


	public RenderCeldas(int Colpatron)
	{
	    this.columna = Colpatron;
	}

	@Override
	public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column)
	{        
	    setBackground(Color.white);
	    table.setForeground(Color.black);
	    super.getTableCellRendererComponent(table, value, selected, focused, row, column);
	    
	    if(table.getValueAt(row,columna).equals("Sí"))
	    {
	        this.setForeground(Color.decode(colorDisponible));
	        this.setBackground(Color.decode(fondoDisponible));
	    }
	    else
	    {
	        this.setForeground(Color.decode(colorNoDisponible));
	        this.setBackground(Color.decode(fondoNoDisponible));
	    }
	    if(selected)
	    {
	        this.setBackground(Color.decode(fondoSeleccion));
	    }


	    return this;
	  }
}
