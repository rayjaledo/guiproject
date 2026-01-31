package my_package;

import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.*; // Import para sa SQL classes

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
            if (row == -1) return; // Siguraduha nga naay napili nga row

            // Kuhaon ang ID gikan sa Column 0
            String id = table.getValueAt(row, 0).toString();
            my_config.config conf = new my_config.config();

            if (action.equals("Delete")) {
                int confirm = JOptionPane.showConfirmDialog(null, 
                    "Are you sure you want to delete ID: " + id + "?", "Delete", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // SQL para ma-delete sa database
                    String sql = "DELETE FROM products WHERE p_id = '" + id + "'";
                    conf.updateData(sql); // Gamiton ang updateData method
                    
                    ((DefaultTableModel) table.getModel()).removeRow(row);
                    JOptionPane.showMessageDialog(null, "Product Deleted Successfully");
                }
            }

            if (action.equals("Edit")) {
                // Kuhaon ang bag-ong data gikan sa table cells
                String name = table.getValueAt(row, 1).toString();
                String category = table.getValueAt(row, 2).toString();
                String price = table.getValueAt(row, 3).toString();

                // SQL Query para ma-save ang changes
                String sql = "UPDATE products SET p_name = '" + name + "', "
                           + "p_category = '" + category + "', "
                           + "p_price = '" + price + "' "
                           + "WHERE p_id = '" + id + "'";

                // I-save sa database
                int result = conf.updateData(sql);
                
                if(result > 0) {
                    JOptionPane.showMessageDialog(null, "Product ID " + id + " updated successfully!");
                }
            }
            
            fireEditingStopped(); // I-refresh ang cell status
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