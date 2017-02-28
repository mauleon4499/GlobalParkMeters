package com.example.semauleo.globalparkmeters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by jaayala on 28/02/2017.
 */

public class AccesoBD {

    private static final String DRIVER = ""; // Rellenar
    private static final String CONEXION = ""; // Rellenar

    static {
        try {
            Class.forName(AccesoBD.DRIVER);
            System.out.println("Driver cargado.");
        }
        catch(ClassNotFoundException cnfe) {
            System.out.println("No se pudo cargar el driver.");
            cnfe.printStackTrace();
        }
    }

    public void anadirUsuario(String usuario) {
        Connection con = null;
        try {
            con = DriverManager.getConnection(AccesoBD.CONEXION);
            con.setAutoCommit(false);
            String insertar = "INSERT INTO Usuario VALUES (?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertar);
            ps.setString(1, usuario);
            ps.setString(2, usuario);
            ps.setString(3, usuario);
            ps.executeUpdate();
            ps.close();
            con.commit();
        }
        catch(SQLException sqle) {
            System.out.println("Se produjo un error.");
            try {
                if(con != null) con.rollback();
            }
            catch(SQLException sqle2) {
                System.out.println("Se produjo un error al hacer rollback.");
            }
        }
        finally {
            try {
                if(con != null) con.close();
            }
            catch(SQLException sqle2) {
                System.out.println("Se produjo un error al cerrar la conexión.");
            }
        }
    }
}
