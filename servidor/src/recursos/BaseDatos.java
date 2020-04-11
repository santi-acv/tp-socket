package recursos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class BaseDatos {

    private static final String url = "jdbc:postgresql://localhost:5432/tp-socket";
    private static final String user = "postgres";
    private static final String password = "postgres";
    
    private static Connection conn;
    private static PreparedStatement insertarConexion;
    private static PreparedStatement terminarConexion;
    private static PreparedStatement cambiarNombre;
    private static PreparedStatement insertarLlamada;
    private static PreparedStatement contestarLlamada;
    private static PreparedStatement terminarLlamada;

    public static void iniciar() {
    	synchronized (BaseDatos.class) {
			try {
				conn = DriverManager.getConnection(url, user, password);
			} catch (SQLException ex) {
				System.err.println("No se ha podido conectar a la base de datos.");
				System.err.println(ex.getMessage());
				return;
			}
			try {
				insertarConexion = conn.prepareStatement(
						"INSERT INTO conexiones (dir_ip, puerto, inicio)" + "VALUES (?,?,?);",
						Statement.RETURN_GENERATED_KEYS);
				terminarConexion = conn.prepareStatement("UPDATE conexiones SET fin = ? " + "WHERE numero = ("
						+ "SELECT max(numero) " + "FROM conexiones " + "WHERE dir_ip = ? AND puerto = ?)");
				cambiarNombre = conn.prepareStatement(
						"INSERT INTO nombres (tiempo, viejo, nuevo, dir_ip, puerto)" + "VALUES (?,?,?,?,?);");
				insertarLlamada = conn.prepareStatement(
						"INSERT INTO llamadas (origen, destino, inicio)"
								+ "VALUES (ROW(?,?,?)::usuario, ROW(?,?,?)::usuario, ?);",
						Statement.RETURN_GENERATED_KEYS);
				contestarLlamada = conn.prepareStatement("UPDATE llamadas SET contestada = ? " + "WHERE numero = ?;");
				terminarLlamada = conn.prepareStatement("UPDATE llamadas SET terminada = ? " + "WHERE numero = ?;");
			} catch (SQLException ex) {
				System.err.println("No se han podido preparar las declaraciones.");
				System.err.println(ex.getMessage());
				cerrar();
				return;
			}
		}
    }

	public static int inicioConexion(String ip, int puerto) {
    	if (conn == null)
    		return 0;
    	try {
    		synchronized (BaseDatos.class) {
				PreparedStatement ps = insertarConexion;
				ps.setString(1, ip);
				ps.setInt(2, puerto);
				ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException ex) {
			System.err.println("Base de datos: Error registrando la conexión.");
			System.err.println(ex.getMessage());
			return 0;
		}
	}

	public static void finConexion(Conexion cliente) {
		if (conn == null)
			return;
		try {
    		synchronized (BaseDatos.class) {
				PreparedStatement ps = terminarConexion;
				ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				ps.setString(2, cliente.ip.toString());
				ps.setInt(3, cliente.puerto);
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			System.err.println("Base de datos: Error registrando la desconexión.");
			System.err.println(ex.getMessage());
		}
	}

	public static void cambioNombre(Conexion cliente, String nombre_viejo) {
		if (conn == null)
    		return;
    	try {
    		synchronized (BaseDatos.class) {
				PreparedStatement ps = cambiarNombre;
				ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				ps.setString(2, nombre_viejo);
				ps.setString(3, cliente.nombre);
				ps.setString(4, cliente.ip.toString());
				ps.setInt(5, cliente.puerto);
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			System.err.println("Base de datos: Error registrando el cambio de nombre.");
			System.err.println(ex.getMessage());
		}
	}

	public static int intentoLlamada(Conexion origen, Conexion destino) {
    	if (conn == null)
    		return 0;
    	try {
    		synchronized (BaseDatos.class) {
				PreparedStatement ps = insertarLlamada;
				ps.setString(1, origen.nombre);
				ps.setString(2, origen.ip.toString());
				ps.setInt(3, origen.puerto);
				ps.setString(4, destino.nombre);
				ps.setString(5, destino.ip.toString());
				ps.setInt(6, destino.puerto);
				ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
				ps.executeUpdate();
				ResultSet rs = ps.getGeneratedKeys();
				rs.next();
				return rs.getInt(1);
			}
		} catch (SQLException ex) {
			System.err.println("Base de datos: Error registrando el intento de llamada.");
			System.err.println(ex.getMessage());
			return 0;
		}
	}
	
	public static void inicioLlamada(int id_llamada) {
		if (conn == null)
    		return;
    	try {
    		synchronized (BaseDatos.class) {
				PreparedStatement ps = contestarLlamada;
				ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				ps.setInt(2, id_llamada);
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			System.err.println("Base de datos: Error registrando el inicio de la llamada.");
			System.err.println(ex.getMessage());
		}
	}

	public static void finLlamada(int id_llamada) {
		if (conn == null)
    		return;
    	try {
    		synchronized (BaseDatos.class) {
				PreparedStatement ps = terminarLlamada;
				ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				ps.setInt(2, id_llamada);
				ps.executeUpdate();
			}
		} catch (SQLException ex) {
			System.err.println("Base de datos: Error registrando el inicio de la llamada.");
			System.err.println(ex.getMessage());
		}
	}
    
    public static void cerrar() {
    	synchronized (BaseDatos.class) {
			try {
				conn.close();
			} catch (SQLException ex) {
				System.err.println("No se ha podido cerrar la conexión a la base de datos.");
				System.err.println(ex.getMessage());
			}
			conn = null;
		}
    }
}