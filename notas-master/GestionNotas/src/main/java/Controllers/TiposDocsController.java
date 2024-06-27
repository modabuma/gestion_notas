/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers;

import Models.TiposDocsModel;
import configuracion.ConexionLocal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

/**
 *
 * @author NEW PC MDMS
 */
public class TiposDocsController {

    private ConexionLocal conn = new ConexionLocal();

    public TiposDocsController() {
    }

    public void loadData(JComboBox comboBox) {
        conn.conectar();

        try {
            String sql = "SELECT id_tipo_doc, nombre FROM tbl_tipo_doc";
            PreparedStatement sta = conn.getConexion().prepareStatement(sql);

            ResultSet res = sta.executeQuery();

            List<TiposDocsModel> tipos = new ArrayList<>();
            tipos.add(new TiposDocsModel(0, "Seleccione"));
            while (res.next()) {
                int id = res.getInt("id_tipo_doc");
                String nombre = res.getString("nombre");
                tipos.add(new TiposDocsModel(id, nombre));
            }

            // Llenar el JComboBox
            for (TiposDocsModel tipo : tipos) {
                comboBox.addItem(tipo);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void seleccionarItemPorId(JComboBox<TiposDocsModel> comboBox, int idTipoDoc) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            TiposDocsModel item = comboBox.getItemAt(i);
            if (item.getId() == idTipoDoc) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
    }

}
