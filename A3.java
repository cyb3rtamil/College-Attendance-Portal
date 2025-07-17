package swinbngpack;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
public class A3 extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JDateChooser fromDateChooser, toDateChooser;
    private JComboBox<String> classDropdown;
    private JTable table;
    private DefaultTableModel model;
    public A3() {
        setTitle("Class Attendance Report");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 700, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        JPanel inputPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        inputPanel.add(new JLabel("From Date:"));
        fromDateChooser = createRestrictedDateChooser();
        inputPanel.add(fromDateChooser);
        inputPanel.add(new JLabel("To Date:"));
        toDateChooser = createRestrictedDateChooser();
        inputPanel.add(toDateChooser);
        inputPanel.add(new JLabel("Class:"));
        classDropdown = new JComboBox<>(new String[]{"I BSc. CSCY", "II BSc. CSCY"});
        inputPanel.add(classDropdown);
        contentPane.add(inputPanel, BorderLayout.NORTH);
        String[] columnNames = {"Student ID", "Student Name", "Total Hours", "Attended Hours", "Attendance (%)"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        JButton btnFetch = new JButton("Get Report");
        btnFetch.addActionListener(e -> fetchAttendanceReport());
        buttonPanel.add(btnFetch);
        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));
        buttonPanel.add(btnExit);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
    private JDateChooser createRestrictedDateChooser() {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        Calendar minDate = Calendar.getInstance();
        minDate.set(2024, Calendar.DECEMBER, 2);
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2025, Calendar.APRIL, 2);
        dateChooser.setMinSelectableDate(minDate.getTime());
        dateChooser.setMaxSelectableDate(maxDate.getTime());
        dateChooser.getDateEditor().addPropertyChangeListener("date", evt -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(selectedDate);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    JOptionPane.showMessageDialog(this, "Weekends are not allowed!", "Invalid Date", JOptionPane.WARNING_MESSAGE);
                    dateChooser.setDate(null);
                }
            }
        });
        return dateChooser;
    }
    private void fetchAttendanceReport() {
        Date fromDate = fromDateChooser.getDate();
        Date toDate = toDateChooser.getDate();
        String selectedClass = (String) classDropdown.getSelectedItem();
        if (fromDate == null || toDate == null || selectedClass == null) {
            JOptionPane.showMessageDialog(this, "Please select all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fromDateStr = sdf.format(fromDate);
        String toDateStr = sdf.format(toDate);
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tamil", "root", "TAMILSELVAN@2006")) {
            String query = """
                SELECT s.student_id, s.student_name, 
                       COUNT(a.id) AS total_hours,
                       SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) AS attended_hours
                FROM students s
                LEFT JOIN attendance a ON s.student_id = a.student_id
                WHERE s.class = ? AND a.date BETWEEN ? AND ?
                GROUP BY s.student_id, s.student_name
                ORDER BY s.student_id
            """;
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setString(1, selectedClass);
                pst.setString(2, fromDateStr);
                pst.setString(3, toDateStr);
                try (ResultSet rs = pst.executeQuery()) {
                    boolean hasData = false;
                    while (rs.next()) {
                        hasData = true;
                        String studentId = rs.getString("student_id");
                        String studentName = rs.getString("student_name");
                        int totalHours = rs.getInt("total_hours");
                        int attendedHours = rs.getInt("attended_hours");
                        double attendancePercentage = (totalHours > 0) ? (attendedHours * 100.0 / totalHours) : 0.0;
                        model.addRow(new Object[]{studentId, studentName, totalHours, attendedHours, String.format("%.2f %%", attendancePercentage)});
                    }
                    if (!hasData) {
                        JOptionPane.showMessageDialog(this, "No attendance records found for this class and date range!", "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                A3 frame = new A3();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
