/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package configuracion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NEW PC MDMS
 */
public class Conexion {

    private Connection cnn;
    private String cadenaConexion, usuarioDB, clave;

    public Conexion(String cadenaConexion, String usuarioDB, String clave) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.cadenaConexion = cadenaConexion;
            this.usuarioDB = usuarioDB;
            this.clave = clave;

            cnn = DriverManager.getConnection(this.cadenaConexion, this.usuarioDB, this.clave);
            System.out.println("Conexion a BD correcta");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean testearConexion() {
        try {
            return !cnn.isClosed();
        } catch (SQLException e) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public void abrirConexion() {
        try {
            cnn = DriverManager.getConnection(cadenaConexion, "root", "");
        } catch (SQLException e) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public void cerrarConexion(){
        try {
            cnn.close();
        } catch (SQLException e) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
