package swingpack;

import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Date;

public class T2 extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JComboBox<String> classComboBox;
    private JDateChooser dateChooser;
    private JButton submitButton;
    private JRadioButton[][] presentButtons, absentButtons;
    private JPanel studentPanel;
    private JTable timetableTable;
    private JScrollPane timetableScrollPane;
    private JLabel subjectLabel;

   
    private Set<String> markedDates = new HashSet<>();

    private String[][] students = {
            {   
                "232CY001", "Afsar",
                "232CY002", "Barath",
                "232CY003", "Kathir",
                "232CY004", "Mathi",
                "232CY005", "Naveen",
                "232CY006", "Purusothaman",
                "232CY007", "Sachin",
                "232CY008", "Sakthi",
                "232CY009", "Soumith",
                "232CY010", "Thirumalai"
            },
            {   
                "231CY001", "Kumaresh Karthic",
                "231CY002", "Ravi Shankar",
                "231CY003", "Sakthi Prasath",
                "231CY004", "Srijith",
                "231CY005", "Sujan",
                "231CY006", "Sudharsan",
                "231CY007", "Tamilselvan",
                "231CY008", "Thanigaivel",
                "231CY009", "Vadivelkumaran",
                "231CY010", "Vinoth Kumar"
            }
        };

    private String[][] ibscTimetable = {
        {"Monday", "3,4"},
        {"Tuesday", "1,2"},
        {"Wednesday", "5,6"},
        {"Thursday", "1,2"},
        {"Friday", "5,6"}
    };

    private String[][] iibscTimetable = {
        {"Monday", "1,2"},
        {"Tuesday", "3,4"},
        {"Wednesday", "3,4"},
        {"Thursday", "5,6"},
        {"Friday", "1,2"}
    };

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                T2 frame = new T2();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private String dbURL = "jdbc:mysql://localhost:3306/tamil";
    private String dbUser = "root";
    private String dbPassword = "TAMILSELVAN@2006";
    private JButton btnCancel;


    public T2() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1100, 554);
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(contentPane);
        setContentPane(scrollPane);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        contentPane.add(mainPanel);

        JLabel lblClass = new JLabel("Select Class:");
        lblClass.setBounds(50, 20, 100, 25);
        mainPanel.add(lblClass);

        classComboBox = new JComboBox<>(new String[]{"I BSc. CSCY", "II BSc. CSCY"});
        classComboBox.setBounds(160, 20, 200, 25);
        classComboBox.addActionListener(e -> {
            updateStudentList();
            updateTimetable();
            updateSubjectLabel();
        });
        mainPanel.add(classComboBox);

        subjectLabel = new JLabel();
        subjectLabel.setBounds(400, 20, 300, 25);
        mainPanel.add(subjectLabel);
        updateSubjectLabel();

        JLabel lblDate = new JLabel("Select Date:");
        lblDate.setBounds(50, 60, 100, 25);
        mainPanel.add(lblDate);

        dateChooser = new JDateChooser();
        dateChooser.setBounds(160, 60, 150, 25);
        mainPanel.add(dateChooser);

        Calendar minDate = Calendar.getInstance();
        minDate.set(2024, Calendar.DECEMBER, 2);
        dateChooser.setMinSelectableDate(minDate.getTime());

        Calendar maxDate = Calendar.getInstance();
        maxDate.set(2025, Calendar.APRIL, 2);
        dateChooser.setMaxSelectableDate(maxDate.getTime());

        JLabel lblStudents = new JLabel("Mark Attendance:");
        lblStudents.setBounds(50, 140, 200, 25);
        mainPanel.add(lblStudents);

        studentPanel = new JPanel();
        studentPanel.setLayout(new GridLayout(10, 3, 10, 5));
        studentPanel.setBounds(50, 170, 600, 250);
        mainPanel.add(studentPanel);

        presentButtons = new JRadioButton[2][10];
        absentButtons = new JRadioButton[2][10];
        updateStudentList();

        submitButton = new JButton("SUBMIT");
        submitButton.setBounds(160, 450, 100, 30);
        mainPanel.add(submitButton);

        submitButton.addActionListener(e -> submitAttendance());

        timetableTable = new JTable();
        timetableScrollPane = new JScrollPane(timetableTable);
        timetableScrollPane.setBounds(700, 20, 250, 150);
        mainPanel.add(timetableScrollPane);
        
        btnCancel = new JButton("CANCEL");
        btnCancel.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		dispose();
                Stafflogin stafflogin = new Stafflogin();
                stafflogin.setVisible(true);
        	}
        });
        btnCancel.setBounds(302, 450, 100, 30);
        mainPanel.add(btnCancel);

        updateTimetable();
    }

    private void updateStudentList() {
        studentPanel.removeAll();
        int classIndex = classComboBox.getSelectedIndex();
        ButtonGroup[] buttonGroups = new ButtonGroup[10];

        for (int i = 0; i < 10; i++) {
            buttonGroups[i] = new ButtonGroup();
            JLabel studentLabel = new JLabel(students[classIndex][i * 2] + " - " + students[classIndex][i * 2 + 1]);
            studentPanel.add(studentLabel);

            presentButtons[classIndex][i] = new JRadioButton("Present");
            absentButtons[classIndex][i] = new JRadioButton("Absent");

            buttonGroups[i].add(presentButtons[classIndex][i]);
            buttonGroups[i].add(absentButtons[classIndex][i]);

            studentPanel.add(presentButtons[classIndex][i]);
            studentPanel.add(absentButtons[classIndex][i]);
        }

        studentPanel.revalidate();
        studentPanel.repaint();
    }

    private void updateTimetable() {
        int classIndex = classComboBox.getSelectedIndex();
        String[][] timetable = (classIndex == 0) ? ibscTimetable : iibscTimetable;

        String[] columnNames = {"Day", "Hours"};
        DefaultTableModel model = new DefaultTableModel(timetable, columnNames);
        timetableTable.setModel(model);
    }

    private void updateSubjectLabel() {
        int classIndex = classComboBox.getSelectedIndex();
        String subject = (classIndex == 0) ? "OBJECT ORIENTED PROGRAM WITH C++" : "PYTHON PROGRAMMING";
        subjectLabel.setText("Subject: " + subject);
    }
    private void submitAttendance() {
        Connection conn = null;
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");

            
            conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
            System.out.println("Database Connected Successfully!");

            String query = "INSERT INTO attendance (class, student_id, student_name, date, status, subject) VALUES (?, ?, ?, ?, ?, ?) " +
                           "ON DUPLICATE KEY UPDATE status = VALUES(status), subject = VALUES(subject)";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(dateChooser.getDate());

                int classIndex = classComboBox.getSelectedIndex();
                String className = (classIndex == 0) ? "I BSc. CSCY" : "II BSc. CSCY";
                String subject = (classIndex == 0) ? "OBJECT ORIENTED PROGRAM WITH C++" : "PYTHON PROGRAMMING";

                for (int i = 0; i < students[classIndex].length / 2; i++) {
                    stmt.setString(1, className);
                    stmt.setString(2, students[classIndex][i * 2]); 
                    stmt.setString(3, students[classIndex][i * 2 + 1]); 
                    stmt.setString(4, formattedDate);
                    stmt.setString(5, presentButtons[classIndex][i].isSelected() ? "Present" : "Absent");
                    stmt.setString(6, subject); 
                    stmt.addBatch();
                }

                stmt.executeBatch();
                JOptionPane.showMessageDialog(this, "Attendance submitted successfully!");
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "JDBC Driver not found: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}