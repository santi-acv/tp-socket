package gui;

import javax.swing.SwingConstants;

public class RenderCeldasCentrado extends RenderCeldas {
	private static final long serialVersionUID = 1L;
	
	public RenderCeldasCentrado(int Colpatron)
	{
		super(Colpatron);
	    this.setHorizontalAlignment(SwingConstants.CENTER);
	}
}