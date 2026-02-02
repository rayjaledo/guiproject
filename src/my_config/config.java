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
   public boolean authenticateUser(String username, String password) {
    String hashedInput = hashPassword(password); // This uses the method we just fixed!
    String sql = "SELECT * FROM sign_up WHERE full_name = ? AND password = ?";
    
    try (java.sql.Connection conn = connectDB();
         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, username);
        pstmt.setString(2, hashedInput);
        java.sql.ResultSet rs = pstmt.executeQuery();
        
        return rs.next(); // Returns true if a match is found
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
   public String getUserRole(String username, String password) {
        String hashedInput = hashPassword(password); 
        // I-add kini para makita nimo sa Output window kung unsa ang gi-generate nga hash
        
        
        String sql = "SELECT u_type FROM sign_up WHERE full_name = ? AND password = ? AND u_status = 'Active'";
        
        try (Connection conn = connectDB(); 
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, hashedInput);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("u_type"); 
                }
            }
        } catch (SQLException e) {
            System.out.println("Role Error: " + e.getMessage());
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
   
    
}