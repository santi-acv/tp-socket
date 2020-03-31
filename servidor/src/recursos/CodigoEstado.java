package recursos;

public enum CodigoEstado {
	// general
	OK               (0, "ok"),
	JSON_INVALIDO   (-3, "El mensaje no corresponde a un objeto JSON."),
	FALTA_TIPO      (-2, "El campo \"tipo_operacion\" está ausente."),
	TIPO_INVALIDO   (-1, "No existe una operación con este código."),
	
	// llamar
	ORIGEN_OCUPADO	 (1, "El usuario ya se encuentra en una llamada."),
	FALTA_DESTINO    (2, "El campo \"destino\" está ausente."),
	DESTINO_INVALIDO (3, "No existe un usuario con ese nombre."),
	DESTINO_OCUPADO  (4, "El destino de la llamada está ocupado."),
	
	// esperando llamada
	NO_CONTESTA	     (5, "La llamada no fue contestada."),
	
	// enviar mensaje
	FALTA_CUERPO     (6, "El campo \"cuerpo\" está ausente."),
	
	// cambiar nombre
	FALTA_NOMBRE     (7, "El campo \"nombre\" está ausente."),
	NOMBRE_DUPLICADO (8, "Ya existe un usuario con ese nombre."),
	
	// contestar o cortar
	NO_HAY_LLAMADA   (9, "El usuario no se encuentra en una llamada");
		
	final int estado;
	final String mensaje;
	
	private CodigoEstado(int estado, String mensaje) {
		this.estado = estado;
		this.mensaje = mensaje;
	}
}
