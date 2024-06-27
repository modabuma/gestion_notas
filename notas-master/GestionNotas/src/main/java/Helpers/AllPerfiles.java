/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Helpers;

/**
 *
 * @author NEW PC MDMS
 */
public class AllPerfiles {

    private int obtenerPerfilID(String nomPerfil) {
        int perfil_id;
        switch (nomPerfil) {
            case "RECTOR":
                perfil_id = 1;
                break;
            case "PROFESOR":
                perfil_id = 2;
                break;

            default:
                perfil_id = 3;
                break;
        }
        return perfil_id;
    }

    private String obtenerPerfilNombre(int perfilID) {
        String perfil_nombre;
        switch (perfilID) {
            case 1:
                perfil_nombre = "RECTOR";
                break;
            case 2:
                perfil_nombre = "PROFESOR";
                break;

            default:
                perfil_nombre = "ALUMNO";
                break;
        }
        return perfil_nombre;
    }
}
