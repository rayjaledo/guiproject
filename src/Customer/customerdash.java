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
    String customerName;

    /**
     * Creates new form customerdash
     */
    public customerdash(String name) {
        this.customerName = name;
        // 1. Siguroha nga ang components ma-initialize una
    initComponents();
   
    // 1. Paghimo sa component
    scrollProducts = new javax.swing.JScrollPane();
    jPanel_products = new javax.swing.JPanel();
    
    // ... generated code ...

    // 2. I-wrap ang product panel sa scroll pane
    scrollProducts.setViewportView(jPanel_products);
    // Pag-add og border sa food area aron mo-match sa Order Summary
    scrollProducts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 1));
    
    // Pagamyan ang width sa Vertical Scroll Bar (8 pixels)
    scrollProducts.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(8, 0));
    
    // I-set ang scroll speed para nindot ang pag-scroll
    scrollProducts.getVerticalScrollBar().setUnitIncrement(16);
    
    // I-set ang background sa viewport para puti ang palibot sa food
    scrollProducts.getViewport().setBackground(java.awt.Color.WHITE);

    // 3. I-add ang scrollProducts sa frame
    getContentPane().add(scrollProducts, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 600, 530));
    // Pag-add og padding sa sulod sa panel aron dili pilit sa border
    jPanel_products.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // I-setup ang Grid Layout (3 columns, 15px gap)
    jPanel_products.setLayout(new java.awt.GridLayout(0, 3, 15, 15));
    // 2. I-check ang session
    new my_config.config().checkSession(this);
    if (!this.isDisplayable()) {
        return; 
    }
    
    // 3. FORCE DISPLAY SETTINGS
    this.setExtendedState(javax.swing.JFrame.NORMAL); // Siguroha nga dili kini minimized
    this.setSize(1000, 700); // Manually set size para sigurado
    this.setLocationRelativeTo(null); // I-center sa screen
    
    // 4. I-load ang products
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
// 1. I-SETUP ANG SCROLLPANE SA PRODUCT PANEL
// Imbis setBounds sa jPanel_products, i-set ang view sa scrollProducts
scrollProducts.getViewport().setBackground(java.awt.Color.WHITE);
scrollProducts.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 1));

// 2. I-SET ANG LAYOUT PARA SA GRID
// Importante kini para ang products mo-flow sa 3 columns
jPanel_products.setLayout(new java.awt.GridLayout(0, 3, 15, 15)); 

// 3. I-REFRESH ANG DISPLAY
jPanel_products.revalidate();
jPanel_products.repaint();
    }
public void loadProducts() {
// 1. Limpyohan ang panel sa dili pa mag-load aron dili magsapaw
    jPanel_products.removeAll(); 
    
    // 2. I-setup ang Grid Layout (3 columns)
    jPanel_products.setLayout(new java.awt.GridLayout(0, 3, 15, 15)); 
    
    try {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:project.db");
        String sql = "SELECT p_name, p_price, p_image FROM products";
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            String name = rs.getString("p_name");
            String price = rs.getString("p_price");
            String path = rs.getString("p_image");

            // 3. Paghimo og Product Box
            JPanel productBox = new JPanel();
            productBox.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
            productBox.setBackground(java.awt.Color.WHITE);
            productBox.setBorder(BorderFactory.createLineBorder(new java.awt.Color(230, 230, 230)));
            productBox.setPreferredSize(new java.awt.Dimension(180, 210));

            // 4. Image Setup (Centered)
            JLabel lblImg = new JLabel();
            lblImg.setHorizontalAlignment(SwingConstants.CENTER);
            if (path != null && !path.isEmpty()) {
                ImageIcon icon = new ImageIcon(path);
                Image img = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(img));
            }
            productBox.add(lblImg, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 100, 80));

            // 5. Name ug Price (Centered text alignment)
            JLabel lblName = new JLabel(name);
            lblName.setFont(new java.awt.Font("Tahoma", 1, 12));
            lblName.setHorizontalAlignment(SwingConstants.CENTER); 
            productBox.add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 95, 180, 20));

            JLabel lblPrice = new JLabel("₱ " + price);
            lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
            productBox.add(lblPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 115, 180, 20));

            // 6. ADD TO CART BUTTON LOGIC
            JButton btnAdd = new JButton("Add to Cart");
            btnAdd.setBackground(new java.awt.Color(255, 165, 0)); // Orange
            btnAdd.setForeground(java.awt.Color.WHITE);           // Puti ang text
            btnAdd.setFocusPainted(false);                        // Para limpyo tan-awon inig click
            btnAdd.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
            btnAdd.addActionListener(e -> {
                DefaultTableModel model = (DefaultTableModel) jTable_orders.getModel();
                boolean exists = false;
                
                // I-check kung naa na ang item sa table
                for (int i = 0; i < model.getRowCount(); i++) {
                    if (model.getValueAt(i, 0).toString().equals(name)) {
                        int currentQty = Integer.parseInt(model.getValueAt(i, 2).toString());
                        model.setValueAt(currentQty + 1, i, 2);
                        exists = true;
                        break;
                    }
                }
                
                if (!exists) {
                    model.addRow(new Object[]{name, price, 1});
                }

                calculateTotal(); 

                // FIX: Refresh lang ang UI, ayaw i-removeAll()
                jPanel_products.revalidate();
                jPanel_products.repaint();
            });

            // I-add ang button sa box (Centered: X=40)
            productBox.add(btnAdd, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 100, 30));
            
            // I-add ang box sa main panel
            jPanel_products.add(productBox);
        }
        conn.close();
        
        // 7. Katapusang refresh human sa loop
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
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_orders = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel_products.setBackground(new java.awt.Color(255, 255, 255));
        jPanel_products.setLayout(new java.awt.GridLayout(0, 3));
        getContentPane().add(jPanel_products, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, -1, 10));

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

        jPanel1.setBackground(new java.awt.Color(204, 0, 0));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("\"WELCOME TO RAY'S FASTFOOD\"");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 10, -1, 30));

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 600, 50));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel2.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 10, -1, -1));

        jPanel4.setBackground(new java.awt.Color(204, 0, 0));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Order Summary");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, -1, -1));

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 280, 50));

        jTable_orders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Price", "Qty"
            }
        ));
        jScrollPane1.setViewportView(jTable_orders);

        jPanel2.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 240, 450));

        getContentPane().add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 50, 280, 530));

        jLabel3.setText("LogOut");
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 590, -1, -1));

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

    // --- GI-APPLY: Auto-kuha sa ngalan gikan sa Login session ---
    // Siguroha nga husto ang package name (my_package) sa imong Login class
    String customerName = my_package.Login.loggedInFullName; 

    // Default value kon walay nakit-an nga session
    if (customerName == null || customerName.trim().isEmpty()) {
    customerName = "Guest Customer";
    }

    // 2. I-setup ang Database Connection
    Connection conn = DriverManager.getConnection("jdbc:sqlite:project.db");

    // 3. SQL Query - GIDUGANG ANG 'customer_name'
    // Siguroha nga nag-ALTER TABLE na ka sa SQLiteStudio aron naay 'customer_name' column
    String sql = "INSERT INTO orders (customer_name, item_name, item_price, qty, total_price) VALUES (?, ?, ?, ?, ?)";
    PreparedStatement pst = conn.prepareStatement(sql);

    // 4. Paghimo og Receipt StringBuilder
    StringBuilder receipt = new StringBuilder();
    receipt.append("          --- RAY'S FAST FOOD RECEIPT ---\n\n");
    
    // --- DISPLAY: Ngalan sa receipt nga gikan sa login session ---
    receipt.append("Customer: ").append(customerName).append("\n");
    receipt.append("------------------------------------------------------------\n");
    
    receipt.append(String.format("%-15s %-10s %-8s %-10s\n", "Item", "Price", "Qty", "Subtotal"));
    receipt.append("------------------------------------------------------------\n");

    // 5. I-loop ang matag row aron i-save sa DB
    for (int i = 0; i < model.getRowCount(); i++) {
        String name = model.getValueAt(i, 0).toString();
        double price = Double.parseDouble(model.getValueAt(i, 1).toString());
        int qty = Integer.parseInt(model.getValueAt(i, 2).toString());
        double subtotal = price * qty;

        receipt.append(String.format("%-15s %-10.2f %-8d %-10.2f\n", name, price, qty, subtotal));
        
        // I-save sa database matag row
        pst.setString(1, customerName);
        pst.setString(2, name);
        pst.setDouble(3, price);
        pst.setInt(4, qty);
        pst.setDouble(5, subtotal);
        pst.executeUpdate();
    }

    receipt.append("------------------------------------------------------------\n");
    receipt.append(lbl_total.getText() + "\n");
    receipt.append("\nThank you for your purchase! Please wait for your order.");

    // 6. I-display ang Receipt pop-up
    javax.swing.JLabel label = new javax.swing.JLabel("<html><pre>" + receipt.toString() + "</pre></html>");
    label.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 12));
    JOptionPane.showMessageDialog(this, label, "Order Success", JOptionPane.INFORMATION_MESSAGE);

    // 7. Limpyohan ang Table ug Label
    model.setRowCount(0);
    lbl_total.setText("Total: ₱ 0.00");
    
    // TANGTANGON ang txt_custName.setText("") kay dili na kinahanglan ang text field

    // 8. Isira ang connection
    pst.close();
    conn.close();

} catch (Exception e) {
    JOptionPane.showMessageDialog(this, "Error sa Checkout: " + e.getMessage());
}
    }//GEN-LAST:event_btn_checkoutActionPerformed

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
                  // 1. Confirmation Dialog
    int a = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
    
    if (a == JOptionPane.YES_OPTION) { // Mas safe gamiton ang YES_OPTION kaysa manual nga '0'
        // 2. Paghimo og instance sa Login frame
        // Siguraduha nga ang spelling ni-match sa imong Login file
        my_package.Login loginFrame = new my_package.Login(); 
        
        // 3. I-pakita ang login screen
        loginFrame.setVisible(true); 
        
        // 4. I-close ang karaan nga dashboard
        this.dispose(); 
    }
    }//GEN-LAST:event_jLabel3MouseClicked

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
            // I-pass ang "Guest" o blangko nga string para sa testing
            new customerdash("Guest").setVisible(true); 
        
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
// I-add ni dapit sa ubang variables sa ubos
private javax.swing.JScrollPane scrollProducts;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_checkout;
    private javax.swing.JButton btn_remove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel_products;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable_orders;
    private javax.swing.JLabel lbl_total;
    // End of variables declaration//GEN-END:variables
}

