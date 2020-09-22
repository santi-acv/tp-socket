package app;

import javax.swing.SwingUtilities;

import gui.FrameInicio;

public class AppCliente {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new FrameInicio().setVisible(true);
		});
	}
}
