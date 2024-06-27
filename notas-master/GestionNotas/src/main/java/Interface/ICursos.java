/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

import Models.CursoModel;
import javax.swing.JTable;

/**
 *
 * @author NEW PC MDMS
 * @param <T>
 */
public interface ICursos<T> {

    void obtenerCursos(JTable tabla, int id);

    void obtenerCursosProfesor(int idProfesor, JTable tabla);

    boolean eliminarCurso(int id);
}
