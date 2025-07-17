package swingpack;
import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.EmptyBorder;
public class Studentlogin extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField nameField;
    private JTextField classField;
    private Connection conn;
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Studentlogin frame = new Studentlogin();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public Studentlogin() {
        setTitle("Student Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 642, 347);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JLabel lblNewLabel = new JLabel("STUDENT NAME");
        lblNewLabel.setBounds(171, 95, 120, 35);
        contentPane.add(lblNewLabel);
        JLabel lblClass = new JLabel("CLASS");
        lblClass.setBounds(171, 155, 120, 35);
        contentPane.add(lblClass);
        nameField = new JTextField();
        nameField.setBounds(287, 102, 150, 20);
        contentPane.add(nameField);
        nameField.setColumns(10);
        classField = new JTextField();
        classField.setBounds(287, 162, 150, 20);
        contentPane.add(classField);
        classField.setColumns(10);
        JButton btnLogin = new JButton("LOGIN");
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verifyStudentLogin();
            }
        });
        btnLogin.setBounds(187, 219, 89, 23);
        contentPane.add(btnLogin);
        JButton btnCancel = new JButton("CANCEL");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                firstpage firstPage = new firstpage();
                firstPage.setVisible(true);
            }
        });
        btnCancel.setBounds(305, 219, 89, 23);
        contentPane.add(btnCancel);
        connectDatabase();
    }
    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tamil", "root", "TAMILSELVAN@2006");
            if (conn != null) {
                System.out.println(" Database Connection Successful!");
            } else {
                System.out.println(" Database Connection Failed!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database Connection Failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void verifyStudentLogin() {
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Database connection is not established!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String studentName = nameField.getText().trim();
        String studentClass = classField.getText().trim();
        if (studentName.isEmpty() || studentClass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter Student Name and Class!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String query = "SELECT * FROM students WHERE student_name = ? AND class = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, studentName);
            pst.setString(2, studentClass);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Login Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                S1 nextPage = new S1(studentName, studentClass); 
                nextPage.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Name or Class!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
