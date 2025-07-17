package swingpack;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class A2 extends JFrame { 
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField nameField, classField;
    private JLabel lblTotalHours, lblAttendedHours, lblAttendancePercent;
    
    public A2() { 
        setTitle("Student Attendance Summary");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 500, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new GridLayout(6, 2, 10, 10));
        setContentPane(contentPane);

        contentPane.add(new JLabel("Student Name:"));
        nameField = new JTextField();
        contentPane.add(nameField);

        contentPane.add(new JLabel("Class:"));
        classField = new JTextField();
        contentPane.add(classField);

        contentPane.add(new JLabel("Total Hours:"));
        lblTotalHours = new JLabel("-");
        contentPane.add(lblTotalHours);

        contentPane.add(new JLabel("Attended Hours:"));
        lblAttendedHours = new JLabel("-");
        contentPane.add(lblAttendedHours);

        contentPane.add(new JLabel("Attendance Percentage:"));
        lblAttendancePercent = new JLabel("-");
        contentPane.add(lblAttendancePercent);
9
        JButton btnFetch = new JButton("Get Attendance");
        contentPane.add(btnFetch);
        btnFetch.addActionListener(e -> fetchAttendance());

        JButton btnExit = new JButton("Exit");
        contentPane.add(btnExit);
        btnExit.addActionListener(e -> System.exit(0));
    }


    private void fetchAttendance() {
        String studentName = nameField.getText();
        String studentClass = classField.getText();
        if (studentName.isEmpty() || studentClass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all details!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tamil", "root", "TAMILSELVAN@2006")) {     
            String studentId = getStudentId(con, studentName, studentClass);
            if (studentId == null) {
                JOptionPane.showMessageDialog(this, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String query = """
                SELECT 
                    COUNT(*) AS total_hours,
                    SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS attended_hours
                FROM attendance
                WHERE student_id = ?
            """;
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, studentId);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        int totalHours = rs.getInt("total_hours");
                        int attendedHours = rs.getInt("attended_hours");
                        double attendancePercentage = (totalHours > 0) ? (attendedHours * 100.0 / totalHours) : 0.0;

                        lblTotalHours.setText(String.valueOf(totalHours));
                        lblAttendedHours.setText(String.valueOf(attendedHours));
                        lblAttendancePercent.setText(String.format("%.2f %%", attendancePercentage));
                    } else {
                        JOptionPane.showMessageDialog(this, "No attendance records found!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private String getStudentId(Connection con, String studentName, String studentClass) throws SQLException {
        String query = "SELECT student_id FROM students WHERE student_name = ? AND class = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, studentName);
            pst.setString(2, studentClass);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next() ? rs.getString("student_id") : null;
            }
        }
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                A2 frame = new A2();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
