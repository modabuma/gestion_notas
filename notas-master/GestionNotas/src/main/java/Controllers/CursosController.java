/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Helpers.CursosCBox;
import Helpers.ProfesoresCBox;
import Interface.ICursos;
import Models.CursoModel;
import configuracion.ConexionLocal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author NEW PC MDMS
 */
public class CursosController implements ICursos<CursoModel> {

    private ConexionLocal conn = new ConexionLocal();

    public void cargarProfesoresFiltro(JComboBox comboBox) {
        conn.conectar();

        try {
            String sql = "SELECT tu.id_usuario, CONCAT(tu.nombres, ' ', tu.apellidos) AS nombre FROM tbl_usuarios tu WHERE tu.id_perfil = 2 AND tu.id_estado = 1";
            PreparedStatement sta = conn.getConexion().prepareStatement(sql);

            ResultSet res = sta.executeQuery();

            List<ProfesoresCBox> tipos = new ArrayList<>();
            tipos.add(new ProfesoresCBox(0, "Ninguno"));
            while (res.next()) {
                int id = res.getInt("id_usuario");
                String nombre = res.getString("nombre");
                tipos.add(new ProfesoresCBox(id, nombre));
            }

            // Llenar el JComboBox
            for (ProfesoresCBox tipo : tipos) {
                comboBox.addItem(tipo);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void obtenerCursos(JTable table, int idProfesor) {
        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> OrdenarTabla = new TableRowSorter<>(modelo);
        table.setRowSorter(OrdenarTabla);
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");
        modelo.addColumn("Profesor");

        table.setModel(modelo);

        try {
            String[] data = new String[3];

            conn.conectar();

            String sql = "SELECT tc.id_curso, tc.nombre, CONCAT(tu.nombres, ' ', tu.apellidos) as profesor "
                    + "FROM tbl_cursos tc "
                    + "INNER JOIN tbl_usuarios_cursos tuc ON tuc.id_curso = tc.id_curso "
                    + "INNER JOIN tbl_usuarios tu ON tu.id_usuario = tuc.id_usuario "
                    + "WHERE tu.id_perfil = 2 AND (? = 0 OR tu.id_usuario= ?) AND tc.id_estado = 1 AND tu.id_estado = 1; ";

            PreparedStatement sta = conn.getConexion().prepareStatement(sql);
            sta.setInt(1, idProfesor);
            sta.setInt(2, idProfesor);

            ResultSet res = sta.executeQuery();

            while (res.next()) {
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);

                modelo.addRow(data);
            }

            table.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los cursos... " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void obtenerCursosProfesor(int idProfesor, JTable tablaProfesor) {
        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> OrdenarTabla = new TableRowSorter<>(modelo);
        tablaProfesor.setRowSorter(OrdenarTabla);
        modelo.addColumn("ID");
        modelo.addColumn("Nombre");

        tablaProfesor.setModel(modelo);
        try {
            String[] data = new String[2];

            conn.conectar();
            String sql = "SELECT idCurso, nombre FROM curso WHERE idProfesor = ? ORDER BY idCurso DESC";
            PreparedStatement sta = conn.getConexion().prepareStatement(sql);

            sta.setInt(1, idProfesor);

            ResultSet res = sta.executeQuery();

            while (res.next()) {
                data[0] = res.getString(1);
                data[1] = res.getString(2);

                modelo.addRow(data);
            }

            tablaProfesor.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los cursos del profesor... " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public CursoModel buscarCurso(JTable table) {
        CursoModel curso = new CursoModel();
        String idCurso;
        try {
            int fila = table.getSelectedRow();
            if (fila >= 0) {
                idCurso = (String) ((table.getValueAt(fila, 0)));

                conn.conectar();

                String sql = "SELECT tc.id_curso, tc.nombre, tuc.id_usuario AS id_profesor, CONCAT(tu.nombres, ' ', tu.apellidos) as profesor "
                        + "FROM tbl_usuarios_cursos tuc "
                        + "INNER JOIN tbl_cursos tc ON tc.id_curso = tuc.id_curso  "
                        + "INNER JOIN tbl_usuarios tu ON tu.id_usuario = tuc.id_usuario "
                        + "WHERE tuc.id_curso = ? AND tc.id_estado = 1;";

                PreparedStatement sta = conn.getConexion().prepareStatement(sql);
                sta.setInt(1, Integer.parseInt(idCurso));
                ResultSet res = sta.executeQuery();

                if (res.next()) {
                    curso.setIdCurso(res.getInt("id_curso"));
                    curso.setNombre(res.getString("nombre"));
                    curso.setIdProfesor(res.getInt("id_profesor"));
                    curso.setNombreProfesor(res.getString("profesor"));
                }else{
                    JOptionPane.showMessageDialog(null, "No se ha encontrado el curso", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return curso;
                }

            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el curso " + e.toString(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        return curso;
    }

    public void actualizarCurso(CursoModel curso) {
        try {
            conn.conectar();
            String cursoProfesor = "SELECT tuc.id_usuario_curso FROM tbl_usuarios_cursos tuc INNER JOIN tbl_usuarios tu ON tu.id_usuario = tuc.id_usuario WHERE id_curso = ? AND tu.id_perfil = 2";
            PreparedStatement sta0 = conn.getConexion().prepareStatement(cursoProfesor);
            sta0.setInt(1, curso.getIdCurso());

            ResultSet resCursoProfe = sta0.executeQuery();

            if (resCursoProfe.next()) {

                String sql1 = "UPDATE tbl_cursos SET nombre = ? WHERE id_curso = ?";
                PreparedStatement sta1 = conn.getConexion().prepareStatement(sql1);
                sta1.setString(1, curso.getNombre());
                sta1.setInt(2, curso.getIdCurso());

                int upCurso = sta1.executeUpdate();
                if (upCurso == 1) {
                    String sql2 = "UPDATE tbl_usuarios_cursos SET id_usuario = ? WHERE id_usuario_curso = ?";
                    PreparedStatement sta2 = conn.getConexion().prepareStatement(sql2);
                    sta2.setInt(1, curso.getIdProfesor());
                    sta2.setInt(2, resCursoProfe.getInt("id_usuario_curso"));

                    int resup = sta2.executeUpdate();
                    if (resup != 1) {
                        JOptionPane.showMessageDialog(null, "Error al actualizar el curso", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    JOptionPane.showMessageDialog(null, "Curso editado con exito", "Exito", JOptionPane.INFORMATION_MESSAGE);

                } else {
                    JOptionPane.showMessageDialog(null, "Error al encontrar el curso", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el curso " + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void obtenerEstudiantesCurso(int idCurso, JTable tablaDinamica) {
        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> OrdenarTabla = new TableRowSorter<>(modelo);
        tablaDinamica.setRowSorter(OrdenarTabla);
        modelo.addColumn("ID");
        modelo.addColumn("Nombres");
        modelo.addColumn("Apellidos");
        modelo.addColumn("Tipo Documento");
        modelo.addColumn("No Documento");
        modelo.addColumn("Correo");
        modelo.addColumn("Telefono");

        tablaDinamica.setModel(modelo);
        try {
            String[] data = new String[7];

            conn.conectar();

            String sql = "SELECT tu.id_usuario, tu.nombres,tu.apellidos, ttd.nombre AS tipodocumento, tu.n_doc, tu.correo, tu.telefono, tu.id_tipo_doc "
                    + "FROM tbl_usuarios tu "
                    + "INNER JOIN tbl_tipo_doc ttd ON ttd.id_tipo_doc = tu.id_tipo_doc "
                    + "INNER JOIN tbl_usuarios_cursos tuc ON tuc.id_usuario = tu.id_usuario "
                    + "WHERE tu.id_perfil = 3 AND tuc.id_curso = ? AND tu.id_estado = 1;";
            PreparedStatement sta = conn.getConexion().prepareStatement(sql);
            sta.setInt(1, idCurso);

            ResultSet res = sta.executeQuery();

            while (res.next()) {
                data[0] = res.getString(1);
                data[1] = res.getString(2);
                data[2] = res.getString(3);
                data[3] = res.getString(4);
                data[4] = res.getString(5);
                data[5] = res.getString(6);
                data[6] = res.getString(7);

                modelo.addRow(data);
            }

            tablaDinamica.setModel(modelo);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los estudiantes" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean crearCurso(CursoModel curso) {
        try {
            conn.conectar();

            String sql = "INSERT INTO tbl_cursos(`nombre`) VALUES (?);";
            PreparedStatement sta = conn.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            sta.setString(1, curso.getNombre());

            int affectedRows = sta.executeUpdate();

            if (affectedRows > 0) {
                ResultSet res = sta.getGeneratedKeys();

                if (res.next()) {

                    String saveCurso = "INSERT INTO tbl_usuarios_cursos(`id_usuario`, `id_curso`) VALUES(?,?)";
                    PreparedStatement sta1 = conn.getConexion().prepareStatement(saveCurso);
                    sta1.setInt(1, curso.getIdProfesor());
                    sta1.setInt(2, res.getInt(1));

                    int resCurso = sta1.executeUpdate();
                    if (resCurso != 1) {
                        JOptionPane.showMessageDialog(null, "No se ha podido guardar el curso del profesor", "Error", JOptionPane.ERROR_MESSAGE);
                        return false;
                    }

                    JOptionPane.showMessageDialog(null, "¡Se ha creado el curso!", "Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    return true;

                } else {
                    JOptionPane.showMessageDialog(null, "Error al crear el curso", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }

            } else {
                JOptionPane.showMessageDialog(null, "Error al crear el curso", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al crear el curso" + e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public void loadDataCBox(JComboBox comboBox) {
        conn.conectar();

        try {
            String sql = "SELECT id_curso, nombre FROM tbl_cursos WHERE id_estado = 1;";
            PreparedStatement sta = conn.getConexion().prepareStatement(sql);

            ResultSet res = sta.executeQuery();

            List<CursosCBox> tipos = new ArrayList<>();
            tipos.add(new CursosCBox(0, "Seleccione"));
            while (res.next()) {
                int id = res.getInt("id_curso");
                String nombre = res.getString("nombre");
                tipos.add(new CursosCBox(id, nombre));
            }

            // Llenar el JComboBox
            for (CursosCBox tipo : tipos) {
                comboBox.addItem(tipo);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean eliminarCurso(int id) {
        try {
            conn.conectar();
            
            String sql = "UPDATE tbl_cursos SET id_estado = 2 WHERE id_curso = ?;";
            
            PreparedStatement sta = conn.getConexion().prepareStatement(sql);
            sta.setInt(1, id);
            
            int res = sta.executeUpdate();
            if(res == 1){
                JOptionPane.showMessageDialog(null, "Se ha eliminado el curso", "Exitoso", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }else{
                 JOptionPane.showMessageDialog(null, "No se ha podido eliminado el curso", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
