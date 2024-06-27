/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Models.UsuarioModel;
import javax.swing.JTable;

/**
 *
 * @author NEW PC MDMS
 * @param <T>
 */
public interface IUsuario <T> {
    boolean crearUsuario(T objecto, int curso_id);
    T buscarUsuario(JTable table);
    boolean actualizarUsuario(T objecto);
    boolean eliminarUsuario(int id);
}
