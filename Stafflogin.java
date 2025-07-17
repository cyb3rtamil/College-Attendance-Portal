package swingpack;
import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.*;
public class Stafflogin extends JFrame {	
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tamil";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "TAMILSELVAN@2006"; 
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Stafflogin frame = new Stafflogin();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public Stafflogin() {
        setTitle("Staff Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(598, 314);
        setLocationRelativeTo(null); 
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JLabel lblUser = new JLabel("USER ID");
        lblUser.setBounds(133, 92, 89, 30);
        contentPane.add(lblUser);
        JLabel lblPassword = new JLabel("PASSWORD");
        lblPassword.setBounds(133, 148, 76, 23);
        contentPane.add(lblPassword);
        textField = new JTextField();
        textField.setBounds(232, 97, 190, 20);
        contentPane.add(textField);
        textField.setColumns(10);
        passwordField = new JPasswordField();
        passwordField.setBounds(232, 149, 190, 20);
        contentPane.add(passwordField);
        JButton btnLogin = new JButton("LOGIN");
        btnLogin.setBounds(149, 214, 111, 23);
        contentPane.add(btnLogin);
        JButton btnCancel = new JButton("CANCEL");
        btnCancel.setBounds(289, 214, 117, 23);
        contentPane.add(btnCancel);
        btnLogin.addActionListener(e -> loginAction());
        btnCancel.addActionListener(e -> {
            dispose();
            firstpage firstPage = new firstpage();
            firstPage.setVisible(true);
        });
    }
    private void loginAction() {
        String userId = textField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        boolean isValid = validateLogin(userId, password);
        if (isValid) {
            openUserFrame(userId);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            passwordField.setText(""); 
        }
    }
    private boolean validateLogin(String userId, String password) {
        String query = "SELECT * FROM users WHERE user_id=? AND password=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }   }
    private void openUserFrame(String userId) {
        switch (userId.toLowerCase()) {
            case "shobana@123":
                new T1().setVisible(true);
                dispose();
                break;
            case "malathi@123":
                new T2().setVisible(true);
                dispose();
                break;
            case "vinetha@123":
                new T3().setVisible(true);
                dispose();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unauthorized user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
