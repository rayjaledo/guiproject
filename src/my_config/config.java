package my_config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Gikinahanglan para sa Statement stmt
import javax.swing.JOptionPane; // Para sa pop-up messages
import javax.swing.JTable; // Para makaila ang Java sa imong table
import net.proteanit.sql.DbUtils; // Para sa resultSetToTableModel
import my_package.Login;
/**
 *
 * @author USER41
 */
public class config {
    

    // Connection Method to SQLITE
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
            con = DriverManager.getConnection("jdbc:sqlite:project.db"); // Establish connection
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e);
        }
        return con;
        
    }

    // Pamaagi para sa Sign Up (Original Code)
    public void addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) values[i]);
                } else if (values[i] instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) values[i]);
                } else if (values[i] instanceof Float) {
                    pstmt.setFloat(i + 1, (Float) values[i]);
                } else if (values[i] instanceof Long) {
                    pstmt.setLong(i + 1, (Long) values[i]);
                } else if (values[i] instanceof Boolean) {
                    pstmt.setBoolean(i + 1, (Boolean) values[i]);
                } else if (values[i] instanceof java.util.Date) {
                    pstmt.setDate(i + 1, new java.sql.Date(((java.util.Date) values[i]).getTime()));
                } else if (values[i] instanceof java.sql.Date) {
                    pstmt.setDate(i + 1, (java.sql.Date) values[i]);
                } else if (values[i] instanceof java.sql.Timestamp) {
                    pstmt.setTimestamp(i + 1, (java.sql.Timestamp) values[i]);
                } else {
                    pstmt.setString(i + 1, values[i].toString());
                }
            }

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    
  
    // This is your hashing method
    
   public String hashPassword(String password) {
    try {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (java.security.NoSuchAlgorithmException e) {
        return null;
    }
}
  public boolean authenticateUser(String email, String password) {
    String hashedInput = hashPassword(password); 
    // Gi-fix: 'email' na ang basehan, dili 'full_name'
    String sql = "SELECT * FROM sign_up WHERE email = ? AND password = ?";
    
    try (java.sql.Connection conn = connectDB();
         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, email.trim());
        pstmt.setString(2, hashedInput);
        java.sql.ResultSet rs = pstmt.executeQuery();
        
        return rs.next(); 
    } catch (java.sql.SQLException e) {
        System.out.println("Auth Error: " + e.getMessage());
        return false;
    }
}

    public boolean isEmailTaken(String email) {
        // Mogamit ta og '?' para sa seguridad (SQL Injection prevention)
        String sql = "SELECT email FROM sign_up WHERE email = ?";
        try (Connection conn = connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                // Kon naay mugawas nga resulta, pasabot Taken ang email
                return rs.next(); 
            }
        } catch (SQLException e) {
            System.out.println("Error sa pag-check sa email: " + e.getMessage());
            return false;
        }
}
// I-insert kini sa ubos sa imong isEmailTaken method
   public String[] getUserData(String email, String password) {
    String hashedInput = hashPassword(password); 
    // Get both u_type and u_status
    String sql = "SELECT u_type, u_status FROM sign_up WHERE email = ? AND password = ?";
    
    try (Connection conn = connectDB(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, email.trim());
        pstmt.setString(2, hashedInput);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                // Return both type and status as an array
                return new String[]{rs.getString("u_type"), rs.getString("u_status")};
            }
        }
    } catch (SQLException e) {
        System.out.println("Login Error: " + e.getMessage());
    }
    return null; 
}
   public void displayData(String sql, javax.swing.JTable table) {
    try (Connection conn = connectDB();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        // This line automatically maps the Resultset to your JTable
        table.setModel(DbUtils.resultSetToTableModel(rs));
        
    } catch (SQLException e) {
        System.out.println("Error displaying data: " + e.getMessage());
    }
}

   // FIXED: Gigamit ang connectDB() para makuha ang connection
   public int updateData(String sql) {
    int num = 0;
    try (Connection conn = connectDB(); 
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        num = pst.executeUpdate(); // I-run ang update
        if (num > 0) {
            JOptionPane.showMessageDialog(null, "Updated Successfully!");
        }
    } catch (SQLException e) {
        System.out.println("Update Error: " + e.getMessage());
    }
    return num;
}
   public void recordSession(String sql, Object... values) {
    try (Connection conn = connectDB(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        for (int i = 0; i < values.length; i++) {
            pstmt.setString(i + 1, values[i].toString());
        }
        pstmt.executeUpdate();
    } catch (SQLException e) {
        // Ayaw i-type og manual ang error message, gamita ang e.getMessage()
        System.out.println("SQL Error sa Record Session: " + e.getMessage());
    }
}

    public java.sql.Connection getConnection() {
    return connectDB();
    }
   
    // Sa sulod sa imong config class
// Sa sulod sa imong config.java
// Sa sulod sa imong my_config.config class
  public void checkSession(java.awt.Window currentFrame) {
      // 1. Kon ang frame kay Login, ayaw pag-execute sa security check

    if (currentFrame instanceof my_package.Login) {
        return;
    }

    // Siguroha nga husto ang spelling sa 'isLoggedIn'
    if (my_package.Login.isLoggedIn == false) { 
        currentFrame.setVisible(false);
        javax.swing.JOptionPane.showMessageDialog(null, "Security Alert: Please log in first!");
        
        // I-dispose ang dashboard sa dili pa ablihan ang login
        currentFrame.dispose();
        new my_package.Login().setVisible(true);
    } else {
        currentFrame.setVisible(true); 
    }
}

   public void deleteData(String sql) {
    // Gamita ang connectDB() ug try-with-resources
    try (Connection conn = connectDB();
         Statement stmt = conn.createStatement()) {
        
        stmt.executeUpdate(sql);
        // Awtomatiko na kining mo-close bisan mag-error
        
    } catch (Exception e) {
        System.out.println("Error sa pag-delete: " + e.getMessage());
    }
}
 public String getFullName(String email) {
    String fullname = "";
    String sql = "SELECT full_name FROM sign_up WHERE email = ?"; 
    try (Connection conn = connectDB(); // Gigamit ang connectDB()
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, email.trim());
        try (ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                fullname = rs.getString("full_name");
            }
        }
    } catch (Exception e) {
        System.out.println("Error: " + e.getMessage());
    }
    return fullname;
}
public String getTotalOrdersToday() {
    String total = "0";
    String sql = "SELECT COUNT(*) FROM orders WHERE date(order_date) = date('now')";
    // I-wrap sa try-with-resources
    try (Connection conn = connectDB(); 
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        if (rs.next()) {
            total = rs.getString(1);
        }
    } catch (Exception e) {
        System.out.println("Error Order Count: " + e.getMessage());
    }
    return total;
}

public String getTotalSalesToday() {
    String total = "₱ 0.00";
    String sql = "SELECT SUM(total_price) FROM orders WHERE date(order_date) = date('now')";
    // I-wrap sa try-with-resources
    try (Connection conn = connectDB();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        if (rs.next()) {
            double sum = rs.getDouble(1);
            total = String.format("₱ %,.2f", sum); 
        }
    } catch (Exception e) {
        System.out.println("Error Total Sales: " + e.getMessage());
    }
    return total;
}
// KINI NGA METHOD MO-KUHA SA LISTAHAN SA SALES MATAG ADLAW
public void getDailySalesReport(javax.swing.JTable table) {
    try {
        // Ang query nga imong gihatag
        String sql = "SELECT date(order_date) AS 'Date', " +
                     "COUNT(*) AS 'Total Orders', " +
                     "SUM(total_price) AS 'Total Sales' " +
                     "FROM orders " +
                     "GROUP BY date(order_date) " +
                     "ORDER BY date(order_date) DESC";
        
        // Gamita ang imong existing nga displayData method
        displayData(sql, table);
        
    } catch (Exception e) {
        System.out.println("Error Daily Report: " + e.getMessage());
    }
}
public void displayProducts(javax.swing.JTable table) {
    try {
        // Gigamit ang saktong column names gikan sa imong screenshot
        String sql = "SELECT p_id AS 'ID', p_name AS 'Product Name', "
                   + "p_category AS 'Category', p_price AS 'Price', "
                   + "p_status AS 'Status' FROM products";
        
        displayData(sql, table);
    } catch (Exception e) {
        System.out.println("Error displayProducts: " + e.getMessage());
    }
}
// CODE PARA SA CONFIG.JAVA
public void updateOrderStatus(String orderId, String newStatus) {
    String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
    try (java.sql.Connection conn = connectDB(); 
         java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, newStatus);
        pst.setString(2, orderId);
        pst.executeUpdate();
        
    } catch (Exception e) {
        System.out.println("Update Error: " + e.getMessage());
    }
}
public void viewOrders(javax.swing.JTable table) {
    // Siguroha nga ang column names (order_id, customer_name, etc.) 
    // match sa imong nakita sa SQLiteStudio/DB Browser.
   String sql = "SELECT order_id, customer_name, item_name, qty, item_price, "
               + "total_price, order_date, status FROM orders ORDER BY order_id ASC";
    
    // Kon 'no such column: items' gihapon, i-check kon 'product_name' ba ang column name sa database
    displayData(sql, table);
}
// Method para mo-view sa Menu/Products
public void viewMenu(javax.swing.JTable table) {
    // Siguroha nga husto ang SQL base sa imong 'products' table
    String sql = "SELECT p_id AS 'ID', p_name AS 'Product Name', "
               + "p_category AS 'Category', p_price AS 'Price', "
               + "p_status AS 'Status' FROM products";
    displayData(sql, table);
}

// Method para mo-update sa Status sa Product
public void updateProductStatus(String productId, String newStatus) {
    // Kinahanglan 'p_status' ug 'p_id' ang gamiton
    String sql = "UPDATE products SET p_status = ? WHERE p_id = ?";
    try (java.sql.Connection conn = connectDB(); 
         java.sql.PreparedStatement pst = conn.prepareStatement(sql)) {
        
        pst.setString(1, newStatus);
        pst.setString(2, productId);
        pst.executeUpdate();
        
    } catch (Exception e) {
        System.out.println("Database Error: " + e.getMessage());
    }
}
public void viewSalesReport(javax.swing.JTable table, String date) {
    try {
        // Ang query nga imong gihatag
        String sql = "SELECT date(order_date) AS 'Date', " +
                     "COUNT(*) AS 'Total Orders', " +
                     "SUM(total_price) AS 'Total Sales' " +
                     "FROM orders " +
                     "GROUP BY date(order_date) " +
                     "ORDER BY date(order_date) DESC";
        
        // Gamita ang imong existing nga displayData method
        displayData(sql, table);
        
    } catch (Exception e) {
        System.out.println("Error Daily Report: " + e.getMessage());
    }
}
}