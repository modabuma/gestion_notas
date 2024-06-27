/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Helpers.BcryptHelper;
import Interface.IUsuario;
import Models.UsuarioModel;
import configuracion.ConexionLocal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author NEW PC MDMS
 */
public class UsuarioController implements IUsuario<UsuarioModel> {

    private int _perfil_alumno_id = 3;
    private final ConexionLocal conn = new ConexionLocal();

    public UsuarioController() {
    }

    public void obtenerUsuariosPerfil(int perfilID, JTable tablaDinamica, int curso_id) {
        int nColumnas = 0;
        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> OrdenarTabla = new TableRowSorter<>(modelo);
        tablaDinamica.setRowSorter(OrdenarTabla);
        modelo.addColumn("ID");
        modelo.addColumn("Nombres");
        modelo.addColumn("Apellidos");
        modelo.addColumn("Tipo Documento");
        modelo.addColumn("No Documento");
        if (perfilID != _perfil_alumno_id) {
            modelo.addColumn("Correo");
            modelo.addColumn("Telefono");
            nColumnas = 7;
        } else {
            nColumnas = 6;
            modelo.addColumn("Curso");
        }

        tablaDinamica.setModel(modelo);
        try {
            String[] data = new String[nColumnas];

            conn.conectar();

            String sql = "SELECT tu.id_usuario, tu.nombres, tu.apellidos, ttd.nombre AS tipodocumento, tu.n_doc, tu.correo, tu.telefono, tu.id_tipo_doc, tc.nombre AS curso "
                    + "FROM tbl_usuarios tu "
                    + "INNER JOIN tbl_tipo_doc ttd ON ttd.id_tipo_doc = tu.id_tipo_doc "
                    + "LEFT JOIN tbl_usuarios_cursos tuc ON tuc.id_usuario = tu.id_usuario "
                    + "LEFT JOIN tbl_cursos tc ON tc.id_curso = tuc.id_curso "
                    + "WHERE tu.id_perfil = ? AND (? = 0 OR tuc.id_curso = ?) AND tu.id_estado = 1;";
            PreparedStatement sta = conn.getConexion().prepareStatement(sql);
            sta.setInt(1, perfilID);
            sta.setInt(2, curso_id);
            sta.setInt(3, curso_id);

            ResultSet res = sta.executeQuery();

            while (res.next()) {
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);
                data[3] = res.getString(4);
                data[4] = res.getString(5);
                if (perfilID != _perfil_alumno_id) {
                    data[5] = res.getString(6);
                    data[6] = res.getString(7);
                } else {
                    data[5] = res.getString(9);
                }

                modelo.addRow(data);
            }

            tablaDinamica.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los registros" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean crearUsuario(UsuarioModel objecto, int curso_id) {
        try {
            conn.conectar();

            String sqlNDoc = "SELECT id_usuario FROM tbl_usuarios WHERE id_tipo_doc= ? AND n_doc = ? AND id_perfil = ?;";
            PreparedStatement staNDoc = conn.getConexion().prepareStatement(sqlNDoc);
            staNDoc.setInt(1, objecto.getId_tipo_doc());
            staNDoc.setString(2, objecto.getN_doc());
            staNDoc.setInt(3, objecto.getId_perfil());

            ResultSet resNDoc = staNDoc.executeQuery();

            if (resNDoc.next()) {
                JOptionPane.showMessageDialog(null, "Ya existe un registro con este tipo y numero de documento", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String newPassword = BcryptHelper.hashPassword(objecto.getPassword());

            String sql = "INSERT INTO `tbl_usuarios`(`nombres`, `apellidos`, `fecha_nacimiento`, `id_tipo_doc`, `n_doc`, `direccion`, `correo`, `telefono`, `clave`, `id_perfil`) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement sta = conn.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            java.sql.Date fechaNacimiento = new java.sql.Date(objecto.getFecha_nacimiento().getTime());

            sta.setString(1, objecto.getNombres());
            sta.setString(2, objecto.getApellidos());
            sta.setDate(3, fechaNacimiento);
            sta.setInt(4, objecto.getId_tipo_doc());
            sta.setString(5, objecto.getN_doc());
            sta.setString(6, objecto.getDireccion());
            sta.setString(7, objecto.getEmail());
            sta.setString(8, objecto.getTelefono());
            sta.setString(9, newPassword);
            sta.setInt(10, objecto.getId_perfil());

            int affectedRows = sta.executeUpdate();
            if (affectedRows > 0) {
                ResultSet res = sta.getGeneratedKeys();
                if (res.next()) {
                    if (curso_id != 0) {
                        String saveCurso = "INSERT INTO tbl_usuarios_cursos(`id_usuario`, `id_curso`) VALUES(?,?)";
                        PreparedStatement sta1 = conn.getConexion().prepareStatement(saveCurso);
                        sta1.setInt(1, res.getInt(1));
                        sta1.setInt(2, curso_id);

                        int resCurso = sta1.executeUpdate();
                        if (resCurso != 1) {
                            JOptionPane.showMessageDialog(null, "No se ha podido guardar el curso del registro", "Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }

                    }
                    JOptionPane.showMessageDialog(null, "¡Se ha creado el registro!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    return true;

                }
            }
            JOptionPane.showMessageDialog(null, "No se ha podido guardar el registro", "Error", JOptionPane.ERROR_MESSAGE);
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public UsuarioModel buscarUsuario(JTable table) {
        UsuarioModel usuario = new UsuarioModel();
        String idUsuario;
        try {
            int fila = table.getSelectedRow();
            if (fila >= 0) {
                idUsuario = (String) ((table.getValueAt(fila, 0)));

                conn.conectar();

                String sql = "SELECT tu.id_usuario, tu.nombres, tu.apellidos, tu.fecha_nacimiento, tu.id_tipo_doc, tu.n_doc, tu.direccion, tu.correo, tu.telefono, tuc.id_curso, tc.nombre AS nom_curso "
                        + "FROM `tbl_usuarios` tu "
                        + "LEFT JOIN tbl_usuarios_cursos tuc ON tuc.id_usuario = tu.id_usuario "
                        + "LEFT JOIN tbl_cursos tc ON tc.id_curso = tuc.id_curso "
                        + "WHERE tu.id_usuario = ? AND tu.id_estado = 1 "
                        + "LIMIT 1;";

                PreparedStatement sta = conn.getConexion().prepareStatement(sql);
                sta.setString(1, idUsuario);
                ResultSet res = sta.executeQuery();

                if (res.next()) {
                    usuario.setIdUsuario(res.getInt("id_usuario"));
                    usuario.setNombres(res.getString("nombres"));
                    usuario.setApellidos(res.getString("apellidos"));
                    usuario.setFecha_nacimiento(res.getDate("fecha_nacimiento"));
                    usuario.setId_tipo_doc(res.getInt("id_tipo_doc"));
                    usuario.setN_doc(res.getString("n_doc"));
                    usuario.setDireccion(res.getString("direccion"));
                    usuario.setEmail(res.getString("correo"));
                    usuario.setTelefono(res.getString("telefono"));
                    usuario.setId_curso(res.getInt("id_curso"));
                    usuario.setNom_curso(res.getString("nom_curso"));
                } else {
                    JOptionPane.showMessageDialog(null, "No se ha encontrado al registro", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return usuario;
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el registro " + e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return usuario;
    }

    @Override
    public boolean actualizarUsuario(UsuarioModel objecto) {
        try {
            String newPassword;
            conn.conectar();

            String sqlNDoc = "SELECT id_usuario FROM tbl_usuarios WHERE id_tipo_doc= ? AND n_doc = ? AND id_perfil = ?;";
            PreparedStatement staNDoc = conn.getConexion().prepareStatement(sqlNDoc);
            staNDoc.setInt(1, objecto.getId_tipo_doc());
            staNDoc.setString(2, objecto.getN_doc());
            staNDoc.setInt(3, objecto.getId_perfil());

            ResultSet resNDoc = staNDoc.executeQuery();

            if (resNDoc.next() && resNDoc.getInt("id_usuario") != objecto.getIdUsuario()) {
                JOptionPane.showMessageDialog(null, "Ya existe un registro con este tipo y numero de documento", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            String sqlBuscarUsuarioID = "SELECT `id_usuario`, `clave` FROM `tbl_usuarios` WHERE id_usuario = ? AND id_estado = 1;";
            PreparedStatement sta1 = conn.getConexion().prepareStatement(sqlBuscarUsuarioID);
            sta1.setInt(1, objecto.getIdUsuario());

            ResultSet res1 = sta1.executeQuery();
            if (res1.next()) {

                if (objecto.getPassword() != null) {
                    newPassword = BcryptHelper.hashPassword(objecto.getPassword());
                } else {
                    newPassword = res1.getString("clave");
                }

                String sql = "UPDATE `tbl_usuarios` SET `nombres` = ?, `apellidos` = ?, `fecha_nacimiento` = ?, `id_tipo_doc` = ?, `n_doc` = ?, `direccion` = ?, `correo` = ?, `telefono` = ?, `clave` = ?"
                        + "WHERE id_usuario = ?";

                PreparedStatement sta = conn.getConexion().prepareStatement(sql);

                java.sql.Date fechaNacimiento = new java.sql.Date(objecto.getFecha_nacimiento().getTime());

                sta.setString(1, objecto.getNombres());
                sta.setString(2, objecto.getApellidos());
                sta.setDate(3, fechaNacimiento);
                sta.setInt(4, objecto.getId_tipo_doc());
                sta.setString(5, objecto.getN_doc());
                sta.setString(6, objecto.getDireccion());
                sta.setString(7, objecto.getEmail());
                sta.setString(8, objecto.getTelefono());
                sta.setString(9, newPassword);
                sta.setInt(10, objecto.getIdUsuario());

                int res = sta.executeUpdate();
                if (res != 1) {
                    JOptionPane.showMessageDialog(null, "No se ha podido actualizar el registro", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                if (objecto.getId_curso() != 0) {
                    String sqlCurso = "SELECT tuc.id_usuario_curso FROM tbl_usuarios_cursos tuc WHERE tuc.id_usuario = ? LIMIT 1";
                    PreparedStatement staSQLCurso = conn.getConexion().prepareStatement(sqlCurso);
                    //staSQLCurso.setInt(1, objecto.getId_curso());
                    staSQLCurso.setInt(1, objecto.getIdUsuario());

                    ResultSet resCurso = staSQLCurso.executeQuery();
                    if (resCurso.next()) {
                        String sqlUPCurso = "UPDATE tbl_usuarios_cursos SET id_curso = ? WHERE id_usuario_curso = ?";
                        PreparedStatement staSQLUPCurso = conn.getConexion().prepareStatement(sqlUPCurso);
                        staSQLUPCurso.setInt(1, objecto.getId_curso());
                        staSQLUPCurso.setInt(2, resCurso.getInt("id_usuario_curso"));

                        int resup = staSQLUPCurso.executeUpdate();
                        if (resup != 1) {
                            JOptionPane.showMessageDialog(null, "Error al actualizar el curso", "Error", JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                }
                JOptionPane.showMessageDialog(null, "¡Se ha actualizado el registro!", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se ha encontrado a este registro", "Error", JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean eliminarUsuario(int id) {
        try {
            conn.conectar();

            String sql = "UPDATE tbl_usuarios SET id_estado = 2 WHERE id_usuario = ?;";

            PreparedStatement sta = conn.getConexion().prepareStatement(sql);
            sta.setInt(1, id);

            int res = sta.executeUpdate();
            if (res == 1) {
                JOptionPane.showMessageDialog(null, "Se ha eliminado el registro", "Exitoso", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "No se ha podido eliminado el registro", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void obtenerEstudiantesProfesor(JTable tabla, int idProfesor) {
        List<Integer> cursoIds = new ArrayList<>();
        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> OrdenarTabla = new TableRowSorter<>(modelo);
        tabla.setRowSorter(OrdenarTabla);

        modelo.addColumn("ID");
        modelo.addColumn("Nombres");
        modelo.addColumn("Apellidos");
        modelo.addColumn("Tipo Documento");
        modelo.addColumn("No Documento");
        modelo.addColumn("Curso");

        try {
            conn.conectar();

            String sqlCursosProfe = "SELECT id_curso FROM tbl_usuarios_cursos WHERE id_usuario = ?";
            PreparedStatement staCursosProfe = conn.getConexion().prepareStatement(sqlCursosProfe);
            staCursosProfe.setInt(1, idProfesor);
            ResultSet resCursosProfe = staCursosProfe.executeQuery();
            while (resCursosProfe.next()) {
                cursoIds.add(resCursosProfe.getInt("id_curso"));
            }

            if (!cursoIds.isEmpty()) {
                // Construir el string de parámetros para la cláusula IN
                StringBuilder inClause = new StringBuilder();
                for (int i = 0; i < cursoIds.size(); i++) {
                    inClause.append("?");
                    if (i < cursoIds.size() - 1) {
                        inClause.append(",");
                    }
                }

                String sqlEstudiantesCurso = "SELECT tu.id_usuario, tu.nombres, tu.apellidos, ttd.nombre AS tipodocumento, tu.n_doc, tu.correo, tu.telefono, tu.id_tipo_doc, tc.nombre AS curso "
                        + "FROM tbl_usuarios tu "
                        + "INNER JOIN tbl_tipo_doc ttd ON ttd.id_tipo_doc = tu.id_tipo_doc "
                        + "LEFT JOIN tbl_usuarios_cursos tuc ON tuc.id_usuario = tu.id_usuario "
                        + "LEFT JOIN tbl_cursos tc ON tc.id_curso = tuc.id_curso "
                        + "WHERE tu.id_perfil = 3 AND tuc.id_curso IN (" + inClause.toString() + ") AND tu.id_estado = 1;";

                PreparedStatement staEstudiantesCurso = conn.getConexion().prepareStatement(sqlEstudiantesCurso);

                // Establecer los parámetros de la cláusula IN
                for (int i = 0; i < cursoIds.size(); i++) {
                    staEstudiantesCurso.setInt(i + 1, cursoIds.get(i));
                }

                ResultSet resultSet = staEstudiantesCurso.executeQuery();

                // Procesar el resultado
                String[] data = new String[6]; // Inicializar data dentro del bucle
                while (resultSet.next()) {
                    data[0] = resultSet.getString(1);
                    data[1] = resultSet.getString(2);
                    data[2] = resultSet.getString(3);
                    data[3] = resultSet.getString(4);
                    data[4] = resultSet.getString(5);
                    data[5] = resultSet.getString(9);

                    modelo.addRow(data);
                }

                tabla.setModel(modelo);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
