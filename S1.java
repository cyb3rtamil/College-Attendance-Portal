package swingpack;

import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class S1 extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTable table;
    private DefaultTableModel model;

    public S1(String studentName, String studentClass) {
        setTitle("Student Attendance Summary");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        String studentId = getStudentId(studentName, studentClass);
        if (studentId == null) {
            JOptionPane.showMessageDialog(this, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JLabel lblDetails = new JLabel("Name: " + studentName + "  |  Roll No: " + studentId + "  |  Class: " + studentClass);
        lblDetails.setFont(new Font("Arial", Font.BOLD, 14));
        contentPane.add(lblDetails, BorderLayout.NORTH);

        String[] columnNames = {"Subject", "Total Hours", "Attended Hours", "Attendance (%)"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        fetchAttendanceSummary(studentId, studentClass);
    }

    private String getStudentId(String studentName, String studentClass) {
        String studentId = null;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tamil", "root", "TAMILSELVAN@2006")) {
            String query = "SELECT student_id FROM students WHERE student_name = ? AND class = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, studentName);
                pst.setString(2, studentClass);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        studentId = rs.getString("student_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentId;
    }

    private void fetchAttendanceSummary(String studentId, String studentClass) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tamil", "root", "TAMILSELVAN@2006")) {
            String query = """
                SELECT a.subject, 
                       COUNT(a.id) AS total_hours,
                       SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS attended_hours
                FROM attendance a
                WHERE a.student_id = ? AND a.class = ?
                GROUP BY a.subject
                """;
            
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, studentId);
                pst.setString(2, studentClass);
                try (ResultSet rs = pst.executeQuery()) {
                    boolean hasData = false;
                    while (rs.next()) {
                        hasData = true;
                        String subject = rs.getString("subject");
                        int totalHours = rs.getInt("total_hours");
                        int attendedHours = rs.getInt("attended_hours");
                        double attendancePercentage = (totalHours > 0) ? (attendedHours * 100.0 / totalHours) : 0.0;
                        
                        model.addRow(new Object[]{subject, totalHours, attendedHours, String.format("%.2f %%", attendancePercentage)});
                    }
                    if (!hasData) {
                        JOptionPane.showMessageDialog(this, "No attendance records found for this student.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
