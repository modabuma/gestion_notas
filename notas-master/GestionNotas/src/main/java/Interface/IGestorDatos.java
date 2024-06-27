/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interface;

/**
 *
 * @author NEW PC MDMS
 * @param <T>
 */
public interface IGestorDatos<T> {
       void creacion(T objecto);
       T lectura(int id);
       void actualizar(T objecto);
       void eliminar(int id);
}
