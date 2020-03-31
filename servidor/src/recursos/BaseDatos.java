package recursos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDatos {

    private static final String url = "jdbc:postgresql://localhost:5432/sd";
    private static final String user = "postgres";
    private static final String password = "postgres";
 
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
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
            ps.setString(3, origen.ip.toString());
            ps.setInt(4, origen.puerto);
            ps.setString(5, destino.ip.toString());
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