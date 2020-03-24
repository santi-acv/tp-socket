package servidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import servidor.Conexion.Estado;

public class Registro {
	
	public static ConcurrentHashMap<String, Conexion> tabla;
	
	public static void agregarConexion (String id, Conexion conexion) {
		tabla.put(id, conexion);
	}
	
	public static Conexion solicitarConexion(String id) {
		return tabla.get(id);
	}

	public static boolean establecerConexion(Conexion origen, Conexion destino) {
		// para evitar un deadlock bloqua primero la conexion con ip menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.ip.compareTo(lock2.ip) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		synchronized (lock1) {
			synchronized (lock2) {
				if (origen.estado.equals(Estado.IDLE) &&
					destino.estado.equals(Estado.IDLE)) {
					origen.estado = Estado.CALL;
					destino.estado = Estado.CALL;
					destino.hilo.destino = origen;
					return true;
				} else {
					return false;
				}
			}
		}
	}

	public static void terminarConexion(Conexion origen, Conexion destino) {
		// para evitar un deadlock bloqua primero la conexion con ip menor
		Conexion lock1 = origen, lock2 = destino;
		if (lock1.ip.compareTo(lock2.ip) > 0) {
			lock1 = destino;
			lock2 = origen;
		}
		
		synchronized (lock1) {
			synchronized (lock2) {
				origen.estado = Estado.IDLE;
				destino.estado = Estado.IDLE;
			}
		}
	}
	
	public static long insertar(Conexion origen, Conexion destino) {

        String SQL = "INSERT INTO conexiones_realizadas(fecha,hora,ip_origen,puerto_origen,ip_destino,puerto_destino) " + "VALUES(?,?,?,?,?,?)";
        java.util.Date date = new java.util.Date();
        	
        long id = 0;
        Connection conn = null;
        
        try 
        {
        	conn = BaseDatos.connect();
        	PreparedStatement ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setDate(1, new java.sql.Date(date.getTime()));
            ps.setTime(2, new java.sql.Time(date.getTime()));
            ps.setString(3, origen.ip);
            ps.setInt(4, origen.puerto);
            ps.setString(5, destino.ip);
            ps.setInt(6, destino.puerto);
 
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        id = rs.getLong(1);
                    }
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error en la insercion: " + ex.getMessage());
        }
        
        finally  {
        	try{
        		conn.close();
        	}catch(Exception ef){
        		System.out.println("No se pudo cerrar la conexion a BD: "+ ef.getMessage());
        	}
        }
        return id;
    }
}
