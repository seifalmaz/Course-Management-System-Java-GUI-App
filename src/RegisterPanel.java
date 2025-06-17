import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Registration panel for the Course Management System GUI.
 */
public class RegisterPanel extends JPanel {
    private CourseManagementGUI mainFrame;
    private JComboBox<String> userTypeCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JComboBox<String> specializationCombo;
    private JPanel specializationPanel;
    private JButton registerButton;
    private JButton backButton;
    
    /**
     * Constructor for the RegisterPanel.
     * 
     * @param mainFrame The main GUI frame.
     */
    public RegisterPanel(CourseManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create header
        JLabel headerLabel = new JLabel("Course Management System - Registration", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // User type selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("User Type:"), gbc);
        
        gbc.gridx = 1;
        String[] userTypes = {"Student", "Instructor", "Admin"};
        userTypeCombo = new JComboBox<>(userTypes);
        formPanel.add(userTypeCombo, gbc);
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Full Name:"), gbc);
        
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);
        
        // Specialization field (for instructors only)
        specializationPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbcSpec = new GridBagConstraints();
        gbcSpec.fill = GridBagConstraints.HORIZONTAL;
        
        gbcSpec.gridx = 0;
        gbcSpec.gridy = 0;
        specializationPanel.add(new JLabel("Specialization:"), gbcSpec);
        
        gbcSpec.gridx = 1;
        specializationCombo = new JComboBox<>();
        specializationCombo.setPreferredSize(new Dimension(200, specializationCombo.getPreferredSize().height));
        specializationPanel.add(specializationCombo, gbcSpec);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        formPanel.add(specializationPanel, gbc);
        specializationPanel.setVisible(false);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        
        // Add components to main panel
        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        userTypeCombo.addActionListener(e -> {
            String selectedType = (String) userTypeCombo.getSelectedItem();
            boolean isInstructor = selectedType.equals("Instructor");
            specializationPanel.setVisible(isInstructor);
            
            if (isInstructor) {
                populateSpecializationCombo();
            }
        });
        
        registerButton.addActionListener(e -> handleRegistration());
        backButton.addActionListener(e -> {
            resetFields();
            mainFrame.switchPanel("Login");
        });
    }
    
    /**
     * Populate the specialization combo box with course names.
     */
    private void populateSpecializationCombo() {
        specializationCombo.removeAllItems();
        specializationCombo.addItem("None");
        
        CourseManager courseManager = mainFrame.getCourseManager();
        ArrayList<Course> courses = courseManager.getCourses();
        
        if (!courses.isEmpty()) {
            for (Course course : courses) {
                specializationCombo.addItem(course.getName());
            }
        }
    }
    
    /**
     * Handle registration button click.
     */
    private void handleRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText();
        String userType = (String) userTypeCombo.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "All fields are required",
                "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Admin admin = mainFrame.getAdmin();
        
        try {
            if (userType.equals("Admin")) {
                Admin newAdmin = new Admin(username, password, name);
                admin.addUser(newAdmin);
                JOptionPane.showMessageDialog(this, "Admin registered successfully!");
                resetFields();
                mainFrame.switchPanel("Login");
            } else if (userType.equals("Instructor")) {
                String specialization = (String) specializationCombo.getSelectedItem();
                
                Instructor newInstructor = new Instructor(username, password, name, specialization);
                admin.addUser(newInstructor);
                JOptionPane.showMessageDialog(this, "Instructor registered successfully!");
                resetFields();
                mainFrame.switchPanel("Login");
            } else if (userType.equals("Student")) {
                Student newStudent = new Student(username, password, name);
                admin.addUser(newStudent);
                JOptionPane.showMessageDialog(this, "Student registered successfully!");
                resetFields();
                mainFrame.switchPanel("Login");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                "Registration failed: " + e.getMessage(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Reset the registration form fields.
     */
    private void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        nameField.setText("");
        specializationCombo.removeAllItems();
        userTypeCombo.setSelectedIndex(0);
        specializationPanel.setVisible(false);
    }
} 