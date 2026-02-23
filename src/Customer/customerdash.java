/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Customer;

import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author james
 */
public class customerdash extends javax.swing.JFrame {

    /**
     * Creates new form customerdash
     */
    public customerdash() {
        new my_config.config().checkSession(this);
        if (!this.isDisplayable()) {
        return; // Hunongon ang constructor dinhi kon wala naka-login
    }
        initComponents();
        loadProducts();
// I-set ang 'Qty' column (Index 2) para mogamit og Spinner
jTable_orders.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()) {
    private JSpinner spinner;

    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // 1. Safety check para sa sulod sa cell
        int startValue = 1;
        if (value != null && !value.toString().trim().isEmpty()) {
            try {
                startValue = Integer.parseInt(value.toString().trim());
            } catch (NumberFormatException e) {
                startValue = 1;
            }
        }

        // 2. Paghimo sa Spinner
        spinner = new JSpinner(new SpinnerNumberModel(startValue, 1, 100, 1));

        // 3. Importante: Inig usab sa arrow, i-save dayon sa table
        spinner.addChangeListener(e -> {
            if (table.isEditing()) {
                table.setValueAt(spinner.getValue(), row, column); // I-update ang table value
                calculateTotal(); // I-compute ang bag-ong total dayon
            }
        });

        return spinner;
    }

    @Override
    public Object getCellEditorValue() {
        return spinner.getValue(); // Siguroha nga ang bag-ong number maoy ma-save
    }
});
// I-set ang panel sa center-top (X=200, Y=20, Width=700, Height=600)
// Usba ang 200 kon gusto nimo mas i-center pa
jPanel_products.setBounds(200, 20, 700, 600); 

// Siguroha nga ang iyang layout dili Absolute para mo-follow sa Grid
jPanel_products.setLayout(new java.awt.GridLayout(0, 3, 10, 10));

// I-refresh
jPanel_products.revalidate();
jPanel_products.repaint();
    }
public void loadProducts() {
        // Limpyohan ang panel sa dili pa mag-load aron dili magsapaw
        jPanel_products.removeAll(); 
        
        try {
            // Siguroha nga ang project.db naa sa husto nga folder
            Connection conn = DriverManager.getConnection("jdbc:sqlite:project.db");
            String sql = "SELECT p_name, p_price, p_image FROM products";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String name = rs.getString("p_name");
                String price = rs.getString("p_price");
                String path = rs.getString("p_image");

                // 1. Paghimo og Box (Panel) para sa matag produkto
                JPanel productBox = new JPanel();
                productBox.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
                productBox.setBackground(java.awt.Color.WHITE);
                productBox.setBorder(BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));

                // 2. Image Label setup
                JLabel lblImg = new JLabel();
                lblImg.setHorizontalAlignment(SwingConstants.CENTER);
                if (path != null && !path.isEmpty()) {
                    ImageIcon icon = new ImageIcon(path);
                    Image img = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                    lblImg.setIcon(new ImageIcon(img));
                }
                productBox.add(lblImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 10, 100, 80));

                // 3. Name Label
                JLabel lblName = new JLabel(name);
                lblName.setFont(new java.awt.Font("Tahoma", 1, 12));
                lblName.setHorizontalAlignment(SwingConstants.CENTER);
                productBox.add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 95, 140, 20));

                // 4. Price Label
                JLabel lblPrice = new JLabel("₱ " + price);
                lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
                productBox.add(lblPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 115, 140, 20));

                // 1. Paghimo sa button sa sulod sa loop
JButton btnAdd = new JButton("Add to Cart");
btnAdd.addActionListener(e -> {
    DefaultTableModel model = (DefaultTableModel) jTable_orders.getModel();
    boolean exists = false;
    int rowCount = model.getRowCount();

    for (int i = 0; i < rowCount; i++) {
        // Siguroha nga naay sulod ang cell 0 (Item Name)
        Object tableItem = model.getValueAt(i, 0);
        
        if (tableItem != null && tableItem.toString().equals(name)) {
            // Mao kini ang safety check para sa Quantity
            Object qtyValue = model.getValueAt(i, 2);
            int currentQty;
            
            // Kon ang qtyValue kay null o blangko, himoa kining 0
            if (qtyValue == null || qtyValue.toString().isEmpty()) {
                currentQty = 0;
            } else {
                currentQty = Integer.parseInt(qtyValue.toString());
            }
            
            model.setValueAt(currentQty + 1, i, 2);
            exists = true;
            break;
        }
    }

    if (!exists) {
        model.addRow(new Object[]{name, price, 1});
    }

    calculateTotal(); 
});
// 3. I-add ang button sa imong productBox
productBox.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 145, 100, 25));
                // I-add ang box sa GridLayout panel
                jPanel_products.add(productBox);
            }
            conn.close();
            
            // Refresh ang display
            jPanel_products.revalidate();
            jPanel_products.repaint();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    // Ang initComponents() i-generate ra ni sa NetBeans Design

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel_products = new javax.swing.JPanel();
        btn_checkout = new javax.swing.JButton();
        lbl_total = new javax.swing.JLabel();
        btn_remove = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_orders = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel_products.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_products.setLayout(new java.awt.GridLayout(0, 3));
        getContentPane().add(jPanel_products, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 560, 540));

        btn_checkout.setBackground(new java.awt.Color(102, 255, 102));
        btn_checkout.setText("Checkout");
        btn_checkout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_checkoutActionPerformed(evt);
            }
        });
        getContentPane().add(btn_checkout, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 510, -1, -1));

        lbl_total.setText("Total:");
        getContentPane().add(lbl_total, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 520, -1, -1));

        btn_remove.setText("Remove Item");
        btn_remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_removeActionPerformed(evt);
            }
        });
        getContentPane().add(btn_remove, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 460, -1, -1));

        jTable_orders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Price", "Qty"
            }
        ));
        jScrollPane1.setViewportView(jTable_orders);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 80, 240, 480));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Order Summary");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 40, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_removeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_removeActionPerformed
        // 1. Kuhaa ang index sa gi-select nga row sa table
int selectedRow = jTable_orders.getSelectedRow();

// 2. I-check kon naay gi-select ang user
if (selectedRow != -1) {
    DefaultTableModel model = (DefaultTableModel) jTable_orders.getModel();
    
    // 3. Tangtangon ang row
    model.removeRow(selectedRow);
    
    // 4. I-recalculate ang total aron ma-subtract ang presyo
    calculateTotal(); 
    
    JOptionPane.showMessageDialog(this, "Item removed.");
} else {
    // Kon walay gi-select nga row
    JOptionPane.showMessageDialog(this, "Please select an item from the table to remove.");
}
    }//GEN-LAST:event_btn_removeActionPerformed

    private void btn_checkoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_checkoutActionPerformed
try {
    // 1. Kuhaa ang Table Model ug i-check kon naay sulod
    DefaultTableModel model = (DefaultTableModel) jTable_orders.getModel();
    if (model.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "Ang imong cart blangko pa! Palihug pagpili og pagkaon.");
        return;
    }

    // 2. I-setup ang Database Connection
    // Siguroha nga ang project.db naa sa root folder sa imong project
    Connection conn = DriverManager.getConnection("jdbc:sqlite:project.db");

    // 3. SQL Query para sa pag-save sa orders
    String sql = "INSERT INTO orders (item_name, item_price, qty, total_price) VALUES (?, ?, ?, ?)";
    PreparedStatement pst = conn.prepareStatement(sql);

    // 4. Paghimo og Receipt String para sa Pop-up
    StringBuilder receipt = new StringBuilder();
    receipt.append("--- RAY'S FAST FOOD RECEIPT ---\n\n");
    receipt.append(String.format("%-15s %-10s %-5s %-10s\n", "Item", "Price", "Qty", "Subtotal"));
    receipt.append("------------------------------------------\n");

    // 5. I-loop ang matag row sa table aron i-save sa DB ug i-add sa receipt
    for (int i = 0; i < model.getRowCount(); i++) {
        String name = model.getValueAt(i, 0).toString();
        double price = Double.parseDouble(model.getValueAt(i, 1).toString());
        int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
        double subtotal = price * qty;

        // I-save sa Database
        pst.setString(1, name);
        pst.setDouble(2, price);
        pst.setInt(3, qty);
        pst.setDouble(4, subtotal);
        pst.executeUpdate();

        // I-add sa Receipt
        receipt.append(String.format("%-15s %-10.2f %-5d %-10.2f\n", name, price, qty, subtotal));
    }

    // 6. I-pakita ang Receipt ug Success Message
    receipt.append("------------------------------------------\n");
    receipt.append(lbl_total.getText() + "\n");
    receipt.append("\nThank you for your purchase! Please wait for your order.");

    JOptionPane.showMessageDialog(this, receipt.toString(), "Order Success", JOptionPane.INFORMATION_MESSAGE);

    // 7. Limpyohan ang Table ug Label human sa malampuson nga pag-save
    model.setRowCount(0);
    lbl_total.setText("Total: ₱ 0.00");

    // 8. Isira ang connection
    pst.close();
    conn.close();

} catch (Exception e) {
    // I-pakita ang error kon naay problema sa database (e.g. walay table nga 'orders')
    JOptionPane.showMessageDialog(this, "Error sa Checkout: " + e.getMessage());
}
    }//GEN-LAST:event_btn_checkoutActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(customerdash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(customerdash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(customerdash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(customerdash.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               customerdash dash = new customerdash();
            if (dash.isDisplayable()) {
                    dash.setVisible(true);
                }
            } // Kini ang closing sa run() method
        }); // Kini ang closing sa invokeLater
    }
public void calculateTotal() {
    double grandTotal = 0;
    DefaultTableModel model = (DefaultTableModel) jTable_orders.getModel();

    for (int i = 0; i < model.getRowCount(); i++) {
        try {
            // Siguroha nga husto ang columns: 1 (Price) ug 2 (Qty)
            double price = Double.parseDouble(model.getValueAt(i, 1).toString());
            int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
            
            grandTotal += (price * qty);
        } catch (Exception e) {
            // Mo-skip kon naay blangko nga row
        }
    }

    // I-update ang display label
    lbl_total.setText("Total: ₱ " + String.format("%.2f", grandTotal));
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_checkout;
    private javax.swing.JButton btn_remove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel_products;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_orders;
    private javax.swing.JLabel lbl_total;
    // End of variables declaration//GEN-END:variables
}
