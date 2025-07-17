package swingpack;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
public class firstpage extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    firstpage frame = new firstpage();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public firstpage() {
        setTitle("drngpasc.ac.in"); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 571, 467);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        JButton btnNewButton = new JButton("STAFF LOGIN");
        btnNewButton.setBounds(217, 113, 139, 32);
        contentPane.add(btnNewButton);    
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Stafflogin staffFrame = new Stafflogin();
                staffFrame.setVisible(true);
                dispose();
            }
        });
        JButton btnNewButton_1 = new JButton("STUDENT LOGIN");
        btnNewButton_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Studentlogin studentFrame = new Studentlogin();
                studentFrame.setVisible(true);
                dispose();
        	}
        });
        btnNewButton_1.setBounds(217, 191, 139, 32);
        contentPane.add(btnNewButton_1);      
        JButton btnNewButton_1_1 = new JButton("ADMIN LOGIN");
        btnNewButton_1_1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        	  Adminlogin adminFrame = new Adminlogin();
                adminFrame.setVisible(true);
                dispose();
        	}
        });
        btnNewButton_1_1.setBounds(217, 265, 139, 32);
        contentPane.add(btnNewButton_1_1);
    }
}
