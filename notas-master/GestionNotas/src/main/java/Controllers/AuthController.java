/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Helpers.BcryptHelper;
import Interface.ILogin;
import Models.AuthModel;
import Views.Login;
import Views.Profesor.HomeProfesor;
import Views.Rector.HomeRector;
import configuracion.ConexionLocal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author NEW PC MDMS
 */
public class AuthController implements ILogin<AuthModel> {

    private final ConexionLocal conn = new ConexionLocal();

    @Override
    public boolean iniciar_sesion(AuthModel objeto) {

        try {
            conn.conectar();

            String sql = "SELECT id_usuario, nombres, apellidos, clave, id_perfil FROM tbl_usuarios WHERE n_doc = ? AND id_estado = 1";

            PreparedStatement sta = conn.getConexion().prepareStatement(sql);
            sta.setString(1, objeto.getDocumento());

            ResultSet res = sta.executeQuery();
            if (res.next()) {
                boolean esValido = BcryptHelper.verifyPassword(objeto.getClave(), res.getString("clave"));
                if (!esValido) {
                    JOptionPane.showMessageDialog(null, "Documento o contraseña invalidos", "Error", JOptionPane.INFORMATION_MESSAGE);
                    return false;
                }

                String nombreCompleto = "" + res.getString("nombres") + " " + res.getString("apellidos") + "";
                
                AuthModel autenticado = new AuthModel();
                autenticado.setNombreUsuario(nombreCompleto);
                autenticado.setIdUsuario(res.getInt("id_usuario"));
                autenticado.setIdPerfil(res.getInt("id_perfil"));

                switch (res.getInt("id_perfil")) {
                    case 1:
                        //RECTOR
                        HomeRector hRec = new HomeRector();
                        hRec.obtAuthUser(autenticado);
                        hRec.setVisible(true);
                        break;
                    case 2:
                        //PROFESOR
                        HomeProfesor hPro = new HomeProfesor();
                        hPro.setVisible(true);
                        break;
                    default:
                        //ALUMNO
                        break;
                }
                        
                return true;

            } else {
                JOptionPane.showMessageDialog(null, "Documento o contraseña invalidos", "Error", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

}
