import java.awt.*;
import java.io.File;
import javax.swing.*;

/**
 * Main GUI class for the Course Management System.
 */
public class CourseManagementGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Panels for different screens
    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private AdminPanel adminPanel;
    private InstructorPanel instructorPanel;
    private StudentPanel studentPanel;
    
    // Data models
    private Admin admin;
    private CourseManager courseManager;
    
    /**
     * Constructor for the CourseManagementGUI.
     */
    public CourseManagementGUI() {
        // Set up the frame
        super("Course Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Ensure data directory exists
        if (!DataManager.ensureDataDirectoryExists()) {
            JOptionPane.showMessageDialog(this, 
                "Error: Could not create data directory. Exiting...",
                "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Fix corrupted grades file if it exists
        File gradesFile = new File("data/grades.txt");
        if (gradesFile.exists()) {
            try {
                // Try to convert existing file to UTF-8
                if (!DataManager.convertGradeFileToUTF8()) {
                    // If conversion fails, reset the file
                    DataManager.resetGradeFile();
                    JOptionPane.showMessageDialog(this,
                        "The grades file was corrupted and has been reset.",
                        "File Correction", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                // If any errors occur, reset the file
                DataManager.resetGradeFile();
                JOptionPane.showMessageDialog(this,
                    "The grades file was corrupted and has been reset.",
                    "File Correction", JOptionPane.WARNING_MESSAGE);
            }
        }
        
        // Initialize admin and course manager
        admin = new Admin("admin", "123", "Admin Name");
        courseManager = new CourseManager();
        
        // Load initial admin-created users from file (if any)
        try {
            admin.loadUserData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Warning: Could not load user data. Starting with empty user list.",
                "Warning", JOptionPane.WARNING_MESSAGE);
        }
        
        // Set up card layout for switching between panels
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Initialize panels
        loginPanel = new LoginPanel(this);
        registerPanel = new RegisterPanel(this);
        adminPanel = new AdminPanel(this);
        instructorPanel = new InstructorPanel(this);
        studentPanel = new StudentPanel(this);
        
        // Add panels to card layout
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(registerPanel, "Register");
        cardPanel.add(adminPanel, "Admin");
        cardPanel.add(instructorPanel, "Instructor");
        cardPanel.add(studentPanel, "Student");
        
        // Show login panel initially
        cardLayout.show(cardPanel, "Login");
        
        // Add card panel to frame
        add(cardPanel);
        
        // Display the frame
        setVisible(true);
    }
    
    /**
     * Switch to a different panel.
     * 
     * @param panelName The name of the panel to switch to.
     */
    public void switchPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
    }
    
    /**
     * Get the admin instance.
     * 
     * @return The admin instance.
     */
    public Admin getAdmin() {
        return admin;
    }
    
    /**
     * Get the course manager instance.
     * 
     * @return The course manager instance.
     */
    public CourseManager getCourseManager() {
        return courseManager;
    }
    
    /**
     * Update the instructor panel with a specific instructor.
     * 
     * @param instructor The instructor to update the panel with.
     */
    public void updateInstructorPanel(Instructor instructor) {
        instructorPanel.setInstructor(instructor);
    }
    
    /**
     * Update the admin panel with the admin name.
     * 
     * @param admin The admin instance.
     */
    public void updateAdminPanel(Admin admin) {
        adminPanel.setAdminName(admin.getAdminName());
    }
    
    /**
     * Update the student panel with a specific student.
     * 
     * @param student The student to update the panel with.
     */
    public void updateStudentPanel(Student student) {
        studentPanel.setStudent(student);
    }
    
    /**
     * Main method to start the application.
     * 
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Use SwingUtilities to ensure GUI is created on EDT
        SwingUtilities.invokeLater(() -> {
            new CourseManagementGUI();
        });
    }
} 