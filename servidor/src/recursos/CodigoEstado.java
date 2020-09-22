package recursos;

public enum CodigoEstado {
	// control de llamadas
	OK               (0, "ok"),
	LLAMADA_ENTRANTE (1, "Está recibiendo una llamada."),
	LLAMADA_CORTADA  (2, "El otro cliente ha terminado la llamada."),
	NO_CONTESTA	     (3, "La llamada no fue contestada a tiempo."),
	UDP_FIN_LLAMADA  (4, "La llamada se ha cortado por el canal UDP."),
	CAMBIO_NOMBRE   (11, "El cliente conectado ha cambiado su nobre."),
	
	// operacion invalida
	ORIGEN_OCUPADO	 (5, "El usuario se encuentra ocupado."),
	DESTINO_OCUPADO  (6, "El destino de la llamada está ocupado."),
	NO_HAY_LLAMADA   (7, "El usuario no se encuentra en una llamada."),
	USUARIO_INVALIDO (8, "No existe un usuario con ese nombre."),
	NOMBRE_DUPLICADO (9, "Ya existe un usuario con ese nombre."),
	IP_INVALIDA     (10, "La IP del datagrama no está autorizada."),
	AUTOLLAMADA     (11,"No es posible la autollamada"),
	
	// errores de formato
	TIPO_INVALIDO   (-1, "No existe una operación con este código."),
	JSON_INVALIDO   (-2, "El mensaje no corresponde a un objeto JSON."),
	FALTA_TIPO      (-3, "El campo \"tipo_operacion\" está ausente."),
	FALTA_NOMBRE    (-4, "El campo \"nombre\" está ausente."),
	FALTA_CUERPO    (-5, "El campo \"cuerpo\" está ausente."),
	FALTA_ORIGEN    (-6, "El campo \"origen\" está ausente."),
	FALTA_DESTINO   (-7, "El campo \"destino\" está ausente."),
	;		
	public final int estado;
	public final String mensaje;
	
	private CodigoEstado(int estado, String mensaje) {
		this.estado = estado;
		this.mensaje = mensaje;
	}
}
