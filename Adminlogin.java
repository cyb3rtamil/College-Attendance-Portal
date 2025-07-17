package swingpack;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class Adminlogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField nameField;
    private JPasswordField passwordField;
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Adminlogin frame = new Adminlogin();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public Adminlogin() {
        setTitle("Admin Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblName = new JLabel("User ID:");
        lblName.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblName.setBounds(50, 50, 100, 25);
        contentPane.add(lblName);

        nameField = new JTextField();
        nameField.setBounds(150, 50, 250, 25);
        contentPane.add(nameField);
        nameField.setColumns(10);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblPassword.setBounds(50, 100, 100, 25);
        contentPane.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 250, 25);
        contentPane.add(passwordField);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(100, 170, 100, 30);
        contentPane.add(btnLogin);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                authenticateUser();
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBounds(250, 170, 100, 30);
        contentPane.add(btnCancel);
        btnCancel.addActionListener(e -> System.exit(0));
    }
    private void authenticateUser() {
        String userId = nameField.getText();
        String password = new String(passwordField.getPassword());
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tamil", "root", "TAMILSELVAN@2006")) {
            String query = "SELECT * FROM admin WHERE user_id = ? AND password = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, userId);
                pst.setString(2, password);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        new A1().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
