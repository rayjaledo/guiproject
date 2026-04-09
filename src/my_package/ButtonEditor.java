package my_package;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.sql.*;

public class ButtonEditor extends DefaultCellEditor {

    private JButton button;
    private String action;
    private JTable table;

    public ButtonEditor(JTable table, String action) {
        super(new JTextField());
        this.table = table;
        this.action = action;
        this.button = new JButton(action);

        button.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return; 

            // Kuhaon ang ID gikan sa Column 0
            String id = table.getValueAt(row, 0).toString();
            my_config.config conf = new my_config.config();

            // --- LOGIC PARA SA UPDATE ---
            if (action.equals("Update")) { 
                String name = table.getValueAt(row, 1).toString();
                String category = table.getValueAt(row, 2).toString();
                String price = table.getValueAt(row, 3).toString();

                String sql = "UPDATE products SET p_name = '" + name + "', "
                           + "p_category = '" + category + "', "
                           + "p_price = '" + price + "' "
                           + "WHERE p_id = '" + id + "'";

                int result = conf.updateData(sql);
                if(result > 0) {
                    JOptionPane.showMessageDialog(null, "Product ID " + id + " updated successfully!");
                }
            }

            // --- LOGIC PARA SA DELETE (Kini ang bag-o) ---
            if (action.equals("Delete")) {
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Are you sure you want to delete Product ID " + id + "?", 
                    "Delete Confirmation", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    String sql = "DELETE FROM products WHERE p_id = '" + id + "'";
                    conf.deleteData(sql); // Siguroha nga naa kay deleteData sa imong config
                    
                    JOptionPane.showMessageDialog(null, "Product deleted successfully!");
                    
                    // I-refresh ang table sa admindash frame
                    Admin.admindash dash = (Admin.admindash) SwingUtilities.getWindowAncestor(table);
                    dash.refreshTable();
                }
            }
            
            fireEditingStopped(); 
        });
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table, Object value, boolean isSelected, int row, int column) {
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        return action;
    }
}