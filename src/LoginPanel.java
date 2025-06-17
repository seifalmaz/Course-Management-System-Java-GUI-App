import java.awt.*;
import javax.swing.*;

/**
 * Login panel for the Course Management System GUI.
 */
public class LoginPanel extends JPanel {
    private CourseManagementGUI mainFrame;
    private JComboBox<String> userTypeCombo;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    
    /**
     * Constructor for the LoginPanel.
     * 
     * @param mainFrame The main GUI frame.
     */
    public LoginPanel(CourseManagementGUI mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create header
        JLabel headerLabel = new JLabel("Course Management System - Login", JLabel.CENTER);
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
        gbc.gridy = 0;
        String[] userTypes = {"Admin", "Instructor", "Student"};
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
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        // Add components to main panel
        add(headerLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> mainFrame.switchPanel("Register"));
    }
    
    /**
     * Handle login button click.
     */
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeCombo.getSelectedItem();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Username and password cannot be empty",
                "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Admin admin = mainFrame.getAdmin();
        
        if (userType.equals("Admin")) {
            // Check main admin
            if (admin.authenticate(username, password)) {
                mainFrame.updateAdminPanel(admin);
                mainFrame.switchPanel("Admin");
                return;
            }
            
            // Check other admin users
            for (User user : admin.getUsers()) {
                if (user instanceof Admin && user.authenticate(username, password)) {
                    mainFrame.updateAdminPanel((Admin) user);
                    mainFrame.switchPanel("Admin");
                    return;
                }
            }
        } else if (userType.equals("Instructor")) {
            for (User user : admin.getUsers()) {
                if (user instanceof Instructor && user.authenticate(username, password)) {
                    mainFrame.updateInstructorPanel((Instructor) user);
                    mainFrame.switchPanel("Instructor");
                    return;
                }
            }
        } else if (userType.equals("Student")) {
            for (User user : admin.getUsers()) {
                if (user instanceof Student && user.authenticate(username, password)) {
                    mainFrame.updateStudentPanel((Student) user);
                    mainFrame.switchPanel("Student");
                    return;
                }
            }
        }
        
        JOptionPane.showMessageDialog(this, 
            "Invalid credentials or user not found",
            "Login Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Reset the login form fields.
     */
    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        userTypeCombo.setSelectedIndex(0);
    }
} 